package hust.tools.ca.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hust.tools.ca.beamsearch.ChunkAnalysisBeamSearch;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceClassificationModel;
import hust.tools.ca.event.ChunkAnalysisBasedWordAndPOSSampleEvent;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
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
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析模型训练类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSME implements Chunker {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisBasedWordAndPOSContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private ChunkAnalysisSequenceClassificationModel<String> model;
    private SequenceValidator<String> sequenceValidator;
    private String label;

    public ChunkAnalysisBasedWordAndPOSME(String label) {
    	this.label = label;
	}

	public ChunkAnalysisBasedWordAndPOSME(ChunkAnalysisBasedWordAndPOSModel model, SequenceValidator<String> sequenceValidator, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen, String label) {
		this.sequenceValidator = sequenceValidator;
		this.label = label;
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
			String encoding, AbstractChunkAnalysisParse parse){
		ChunkAnalysisBasedWordAndPOSModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, parse);
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
            ObjectStream<Event> es = new ChunkAnalysisBasedWordAndPOSSampleEvent(sampleStream, contextGen);
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
			ChunkAnalysisBasedWordAndPOSContextGenerator contextGen, String encoding, AbstractChunkAnalysisParse parse) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		ChunkAnalysisBasedWordAndPOSModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, parse);
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
	public ChunkAnalysisBasedWordAndPOSModel readModel(File modelFile, TrainingParameters params, ChunkAnalysisBasedWordAndPOSContextGenerator contextGen,
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
	 * 返回给定词组和词性的组块类型
	 * @param words	待确定组块类型的词组
	 * @param poses	词组对应的词性数组
	 * @return		组块类型
	 */
	public String[] tag(String[] words,String[] poses){
		return tag(words, poses, null);
	}
	
	/**
	 * 返回给定词组和词性的组块类型
	 * @param words				待确定组块类型的词组
	 * @param poses				词组对应的词性数组
	 * @param additionaContext	其他上下文信息
	 * @return					组块类型
	 */
	public String[] tag(String[] words,String[] poses, Object[] additionaContext){
		bestSequence = model.bestSequence(words, poses, additionaContext, contextGenerator,sequenceValidator);
		List<String> chunks = bestSequence.getOutcomes();
		
		return chunks.toArray(new String[chunks.size()]);
	}
	
	/**
	 * 得到最优的num个组块标注结果
	 * @param k 	返回的标注结果个数
	 * @param words 待确定组块类型的词组
	 * @param pos 	词组对应的词性数组
	 * @return 		最优的k个组块标注结果
	 */
	public String[][] tag(int k, String[] words, String[] pos) {
        Sequence[] bestSequences = model.bestSequences(k, words, pos, null,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
           
        }
        return tagsandposes;
    }
	
	/**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param poses	与词组对应的词性数组
	 * @return		最优的K个标注序列
	 */
    public Sequence[] getTopKSequences(String[] characters,String[] pos) {
        return getTopKSequences(characters, pos, null);
    }

    /**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param poses	与词组对应的词性数组
	 * @param additionaContext
	 * @return		最优的K个标注序列
	 */
    public Sequence[] getTopKSequences(String[] characters, String[] pos, Object[] additionaContext) {
        return model.bestSequences(size, characters, pos, additionaContext,
        		contextGenerator, sequenceValidator);
    }

    @Override
	public Chunk[] parse(String sentence) {
    	String[] wordTags = sentence.split("//s+");
    	List<String> words = new ArrayList<>();
    	List<String> poses = new ArrayList<>();
    	
    	for(String wordTag : wordTags) {
    		words.add(wordTag.split("/")[0]);
    		poses.add(wordTag.split("/")[1]);
    	}
    	
		String[] chunkTypes = tag(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		
		AbstractChunkAnalysisSample sample = new ChunkAnalysisBasedWordAndPOSSample(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]), chunkTypes);
		sample.setLabel(label);
		
		return sample.toChunk();
	}

	@Override
	public Chunk[][] parse(String sentence, int k) {
		String[] wordTags = sentence.split("//s+");
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();
		
		for(String wordTag : wordTags) {
			words.add(wordTag.split("/")[0]);
			poses.add(wordTag.split("/")[1]);
		}
		
		String[][] chunkTypes = tag(k, words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		Chunk[][] chunks = new Chunk[chunkTypes.length][];
		for(int i = 0; i < chunkTypes.length; i++) {
			String[] chunkSequences = chunkTypes[i];
						
			AbstractChunkAnalysisSample sample = new ChunkAnalysisBasedWordAndPOSSample(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]), chunkSequences);
			sample.setLabel(label);
			chunks[i] = sample.toChunk();
		}
		
		return chunks;
	}
}

