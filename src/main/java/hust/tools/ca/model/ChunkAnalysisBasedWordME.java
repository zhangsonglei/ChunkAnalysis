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

import hust.tools.ca.event.ChunkAnalysisBasedWordSampleEventStream;
import hust.tools.ca.event.ChunkAnalysisBasedWordSampleSequenceStream;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventModelSequenceTrainer;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.SequenceTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 基于词的组块分析模型训练 类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordME implements Chunker {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
    private SequenceValidator<String> sequenceValidator;
    private String label;
    
    public ChunkAnalysisBasedWordME() {
    	
    }
    
    public ChunkAnalysisBasedWordME(ChunkAnalysisBasedWordModel model) throws IOException {
    	this(model, new ChunkAnalysisBasedWordContextGeneratorConf());
    }
    
    public ChunkAnalysisBasedWordME(ChunkAnalysisBasedWordModel model, ChunkAnalysisContextGenerator contextGen) {
    	init(model , contextGen);
    }
	
    /**
     * 构造方法
     * @param model			组块分析模型
     * @param contextGen	上下文生成器
     */
	public ChunkAnalysisBasedWordME(ChunkAnalysisBasedWordModel model, SequenceValidator<String> sequenceValidator, ChunkAnalysisContextGenerator contextGen, String label) {
		this.sequenceValidator = sequenceValidator;
		this.label = label;
		init(model , contextGen);
	}
	
    /**
     * 初始化工作
     * @param model 		组块分析模型
     * @param contextGen	上下文生成器
     */
	private void init(ChunkAnalysisBasedWordModel model, ChunkAnalysisContextGenerator contextGen) {
		int beamSize = ChunkAnalysisBasedWordME.DEFAULT_BEAM_SIZE;
        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
            beamSize = Integer.parseInt(beamSizeString);

        contextGenerator = contextGen;
        size = beamSize;
        
        if (model.getChunkAnalysisSequenceModel() != null)
            this.model = model.getChunkAnalysisSequenceModel();
        else
            this.model = new BeamSearch<String>(beamSize, model.getChunkAnalysisModel(), 0);
	}
	
	/**
	 * 训练模型
	 * @param file 			训练文件
	 * @param params 		训练
	 * @param contextGen 	特征
	 * @param encoding 		编码
	 * @return 				模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ChunkAnalysisBasedWordModel train(File file, TrainingParameters params, ChunkAnalysisContextGenerator contextGen,
			String encoding, AbstractChunkAnalysisParse parse) {
		ChunkAnalysisBasedWordModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, parse, label);
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
	 * @param languageCode 	编码
	 * @param sampleStream 	文件流
	 * @param contextGen 	特征
	 * @param encoding 		编码
	 * @return 				模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ChunkAnalysisBasedWordModel train(String languageCode, ObjectStream<AbstractChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkAnalysisBasedWordME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel chunkModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqChunkModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new ChunkAnalysisBasedWordSampleEventStream(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            chunkModel = trainer.train(es);                       
        }else if(TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)) {
        	ChunkAnalysisBasedWordSampleSequenceStream ss = new ChunkAnalysisBasedWordSampleSequenceStream(sampleStream, contextGenerator);
            EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(params.getSettings(),
                    manifestInfoEntries);
            chunkModel = trainer.train(ss);
        }else if (TrainerType.SEQUENCE_TRAINER.equals(trainerType)) {
            SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(
            		params.getSettings(), manifestInfoEntries);

            ChunkAnalysisBasedWordSampleSequenceStream ss = new ChunkAnalysisBasedWordSampleSequenceStream(sampleStream, contextGenerator);
            seqChunkModel = trainer.train(ss);
        }else
            throw new IllegalArgumentException("不支持的训练方法: " + trainerType);

        if (chunkModel != null) 
            return new ChunkAnalysisBasedWordModel(languageCode, chunkModel, beamSize, manifestInfoEntries);
        else
            return new ChunkAnalysisBasedWordModel(languageCode, seqChunkModel, manifestInfoEntries);
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 			训练的文本
	 * @param modelFile 	模型文件
	 * @param params 		训练的参数配置
	 * @param contextGen 	上下文 产生器
	 * @param encoding 		编码方式
	 * @return
	 */
	public ChunkAnalysisBasedWordModel train(File file, File modelFile, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen, String encoding, AbstractChunkAnalysisParse parse) {
		OutputStream modelOut = null;
		ChunkAnalysisBasedWordModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, parse, label);
			model = train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));           
            model.serialize(modelOut);
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
	 * @param modelFile 	模型文件
	 * @param params 		参数
	 * @param contextGen 	上下文生成器
	 * @param encoding 		编码方式
	 * @return
	 */
	public ChunkAnalysisBasedWordModel readModel(File modelFile) {
		ChunkAnalysisBasedWordModel model = null;
		
		try {
			model =  new ChunkAnalysisBasedWordModel(modelFile);
			System.out.println("读取模型成功");
            return model;
        } catch (IOException e) {
			e.printStackTrace();
		}
		
		return model;
	}
	
	/**
	 * 得到最好的numTaggings个标记序列
	 * @param numTaggings 	个数
	 * @param words 		一个个词语
	 * @return 				分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] words) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, words, null, contextGenerator, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        List<String> temp = new ArrayList<>();
        
        for (int si = 0; si < tags.length; si++) {
        	temp = bestSequences[si].getOutcomes();
        	tags[si] = temp.toArray(new String[temp.size()]);
        }
        
        return tags;
    }

	/**
	 * 返回词组的组块标注结果
	 * @param words	词组
	 * @return		词组的组块标注结果
	 */
	public String[] tag(String[] words){
		return tag(words,null);
	}
	
	/**
	 * 返回词组的组块标注结果
	 * @param words				词组
	 * @param additionaContext	其他上下文信息
	 * @return					词组的组块标注结果
	 */
	public String[] tag(String[] words, Object[] additionaContext){
		bestSequence = model.bestSequence(words, additionaContext, contextGenerator, sequenceValidator);
		List<String> temp = bestSequence.getOutcomes();
		
		return temp.toArray(new String[temp.size()]);
	}

	/**
	 * 根据给定词组，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @return		最优的K个标注序列
	 */
	public Sequence[] getTopKSequences(String[] words) {
		return getTopKSequences(words, null);
	}

	/**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param additionaContext
	 * @return		最优的K个标注序列
	 */
    public Sequence[] getTopKSequences(String[] words, Object[] additionaContext) {
        return model.bestSequences(size, words, additionaContext, contextGenerator, sequenceValidator);
    }

    @Override
	public Chunk[] parse(String sentence) {
		String[] words = sentence.split("//s+");
		String[] chunkTypes = tag(words);
		
		AbstractChunkAnalysisSample sample = new ChunkAnalysisBasedWordSample(words, chunkTypes);
		sample.setLabel(label);
		
		return sample.toChunk();
	}

	@Override
	public Chunk[][] parse(String sentence, int k) {
		String[] words = sentence.split("//s+");
		
		String[][] chunkTypes = tag(k, words);
		Chunk[][] chunks = new Chunk[chunkTypes.length][];
		for(int i = 0; i < chunkTypes.length; i++) {
			String[] chunkSequences = chunkTypes[i];
						
			AbstractChunkAnalysisSample sample = new ChunkAnalysisBasedWordSample(words, chunkSequences);
			sample.setLabel(label);
			chunks[i] = sample.toChunk();
		}
		
		return chunks;
	}
}

