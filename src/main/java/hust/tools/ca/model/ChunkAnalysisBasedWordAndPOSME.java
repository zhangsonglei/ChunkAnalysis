package hust.tools.ca.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hust.tools.ca.beamsearch.ChunkAnalysisBeamSearch;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceClassificationModel;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceValidator;
import hust.tools.ca.beamsearch.DefaultChunkAnalysisSequenceBasedWordAndPOSValidator;
import hust.tools.ca.event.ChunkAnalysisSampleEventBasedWordAndPOS;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSampleStream;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 组块分析模型的训练 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSME implements ChunkAnalysisBasedWordAndPOS {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisBasedWordAndPOSContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private ChunkAnalysisSequenceClassificationModel<String> model;
    private ChunkAnalysisSequenceValidator<String> sequenceValidator;
    private boolean isBIEO;
    
    /**
     * 构造方法
     * @param isBIEO	组块位置标签BIEO/BIO
     */
	public ChunkAnalysisBasedWordAndPOSME(boolean isBIEO) {
		this.isBIEO = isBIEO;
	}
	
    /**
     * 构造方法
     * @param model			组块分析模型
     * @param contextGen	上下文生成器
     */
	public ChunkAnalysisBasedWordAndPOSME(ChunkAnalysisBasedWordAndPOSModel model, boolean isBIEO, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen) {
		this.isBIEO = isBIEO;
		init(model , contextGen);
	}
	
    /**
     * 初始化工作
     * @param model 		组块分析模型
     * @param contextGen	上下文生成器
     */
	private void init(ChunkAnalysisBasedWordAndPOSModel model, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen) {
		int beamSize = ChunkAnalysisBasedWordAndPOSME.DEFAULT_BEAM_SIZE;
        String beamSizeString = model.getManifestProperty(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
            beamSize = Integer.parseInt(beamSizeString);

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultChunkAnalysisSequenceBasedWordAndPOSValidator(isBIEO);
        
        if (model.getChunkAnalysisSequenceModel() != null)
            this.model = model.getChunkAnalysisSequenceModel();
        else
            this.model = new ChunkAnalysisBeamSearch<String>(beamSize, model.getChunkAnalysisModel(), 0);
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ChunkAnalysisBasedWordAndPOSModel train(File file, TrainingParameters params, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen,
			String encoding){
		ChunkAnalysisBasedWordAndPOSModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, isBIEO);
			model = train("zh", sampleStream, params, contextGen);
			return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 文件流
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ChunkAnalysisBasedWordAndPOSModel train(String languageCode, ObjectStream<AbstractChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisBasedWordAndPOSContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkAnalysisBasedWordAndPOSME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel maxentModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        ChunkAnalysisSequenceClassificationModel<String> chunkClassificationModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new ChunkAnalysisSampleEventBasedWordAndPOS(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            maxentModel = trainer.train(es);                       
        }

        if (maxentModel != null) {
            return new ChunkAnalysisBasedWordAndPOSModel(languageCode, maxentModel, beamSize, manifestInfoEntries);
        } else {
            return new ChunkAnalysisBasedWordAndPOSModel(languageCode, chunkClassificationModel, manifestInfoEntries);
        }
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param modelbinaryFile 二进制的模型文件
	 * @param modeltxtFile 文本类型的模型文件
	 * @param params 训练的参数配置
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @return
	 */
	public ChunkAnalysisBasedWordAndPOSModel train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			ChunkAnalysisBasedWordAndPOSContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		ChunkAnalysisBasedWordAndPOSModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, isBIEO);
			model = train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getChunkAnalysisModel(), modeltxtFile);
            modelWriter.persist();
            return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }	
		return null;
	}

	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static ChunkAnalysisBasedWordAndPOSModel readModel(File modelFile, TrainingParameters params, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		ChunkAnalysisBasedWordAndPOSModel model = null;
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = ChunkAnalysisBasedWordAndPOSME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new ChunkAnalysisBasedWordAndPOSModel(encoding, abModel, beamSize,manifestInfoEntries);
	
			System.out.println("读取模型成功");
            return model;
        } catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 得到最好的numTaggings个标记序列
	 * @param numTaggings 个数
	 * @param words 一个个词语
	 * @param pos 词性标注
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] words,String[] pos) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, words, pos, null,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
           
        }
        return tagsandposes;
    }
	
	@Override
	public String analysis(String[] words){
		return analysis(words, null);
	}
	
	@Override
	public String analysis(String[] words,String[] poses){
		return analysis(words, poses, null);
	}
	
	@Override
	public String analysis(String[] words,String[] poses, Object[] additionaContext){
		bestSequence = model.bestSequence(words, poses, additionaContext, contextGenerator,sequenceValidator);
		List<String> chunks = bestSequence.getOutcomes();
		String chunksResult = null;
		
		if(poses != null) {
			for(int i = 0; i < words.length; i++)
				words[i] = words[i] + "/" + poses[i];
		}
		
		for(int i = 0; i < chunks.size() - 1; i++) {
			String chunk = chunks.get(i);
			
			if(chunk.equals("O"))
				chunksResult += words[i] + "  ";
			else if(chunk.split("_")[1].equals("I")) 
				chunksResult += words[i] + "  ";	
			else {
				if(i > 1) {
					String lastChunk = chunks.get(i - 1);
					if(lastChunk.equals("O"))
						chunksResult += "[" + words[i] + "  ";
					else 
						chunksResult = chunksResult.trim() + "]" + lastChunk.split("_")[0] + "[" + words[i] + "  ";
				}else				
					chunksResult += "[" + words[i] + "  ";
			}
		}
		
		String finalChunk = chunks.get(chunks.size() - 1);
		if(finalChunk.equals("O"))
			chunksResult += words[chunks.size() - 1];
		else
			chunksResult += words[chunks.size() - 1] + "]" + finalChunk.split("_")[0];
		
		return chunksResult;
	}

	public String[] tag(String[] words){
		return tag(words,null);
	}
	
	
	public String[] tag(String[] words,String[] poses){
		return tag(words, poses, null);
	}
	
	
	public String[] tag(String[] words,String[] poses, Object[] additionaContext){
		bestSequence = model.bestSequence(words, poses, additionaContext, contextGenerator,sequenceValidator);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}
	
	
	
	
	@Override
	public Sequence[] getTopKSequences(String[] words) {
		return getTopKSequences(words, null, null);
	}

	@Override
    public Sequence[] getTopKSequences(String[] characters,String[] pos) {
        return getTopKSequences(characters, pos, null);
    }

	@Override
    public Sequence[] getTopKSequences(String[] characters, String[] pos, Object[] additionaContext) {
        return model.bestSequences(size, characters, pos, additionaContext,
        		contextGenerator, sequenceValidator);
    }
}

