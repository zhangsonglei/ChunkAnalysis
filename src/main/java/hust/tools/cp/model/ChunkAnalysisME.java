package hust.tools.cp.model;

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

import hust.tools.cp.beamsearch.ChunkAnalysisBeamSearch;
import hust.tools.cp.beamsearch.ChunkAnalysisSequenceClassificationModel;
import hust.tools.cp.beamsearch.ChunkAnalysisSequenceValidator;
import hust.tools.cp.beamsearch.DefaultChunkAnalysisSequenceValidator;
import hust.tools.cp.event.ChunkAnalysisSampleEvent;
import hust.tools.cp.feature.ChunkAnalysisContextGenerator;
import hust.tools.cp.stream.ChunkAnalysisSample;
import hust.tools.cp.stream.ChunkAnalysisSampleStream;
import hust.tools.cp.stream.FileInputStreamFactory;
import hust.tools.cp.stream.PlainTextByTreeStream;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;

/**
 * 组块模型的训练
 * @author 王馨苇
 *
 */
public class ChunkAnalysisME implements ChunkAnalysis {
	
	public static final int DEFAULT_BEAM_SIZE = 3;
	private ChunkAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private ChunkAnalysisSequenceClassificationModel<String> model;
	private ChunkAnalysisModel modelPackage;
	private List<String> characters = new ArrayList<String>();
	private List<String> tags = new ArrayList<String>();
    private ChunkAnalysisSequenceValidator<String> sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public ChunkAnalysisME(ChunkAnalysisModel model, ChunkAnalysisContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(ChunkAnalysisModel model, ChunkAnalysisContextGenerator contextGen) {
		int beamSize = ChunkAnalysisME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultChunkAnalysisSequenceValidator();
        if (model.getChunkAnalysisSequenceModel() != null) {
            this.model = model.getChunkAnalysisSequenceModel();
        } else {
            this.model = new ChunkAnalysisBeamSearch<String>(beamSize,
                    model.getChunkAnalysisModel(), 0);
        }
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
	public static ChunkAnalysisModel train(File file, TrainingParameters params, ChunkAnalysisContextGenerator contextGen,
			String encoding){
		ChunkAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<ChunkAnalysisSample> sampleStream = new ChunkAnalysisSampleStream(lineStream);
			model = ChunkAnalysisME.train("zh", sampleStream, params, contextGen);
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
	public static ChunkAnalysisModel train(String languageCode, ObjectStream<ChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkAnalysisME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        ChunkAnalysisSequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new ChunkAnalysisSampleEvent(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new ChunkAnalysisModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new ChunkAnalysisModel(languageCode, seqPosModel, manifestInfoEntries);
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
	public static ChunkAnalysisModel train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		ChunkAnalysisModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<ChunkAnalysisSample> sampleStream = new ChunkAnalysisSampleStream(lineStream);
			model = ChunkAnalysisME.train("zh", sampleStream, params, contextGen);
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
	
	public String[] tag(String[] words,String[] pos, Object[] additionaContext){
		bestSequence = model.bestSequence(words, pos, additionaContext, contextGenerator,sequenceValidator);
      //  System.out.println(bestSequence);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static ChunkAnalysisModel readModel(File modelFile, TrainingParameters params, ChunkAnalysisContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		ChunkAnalysisModel model = null;
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = ChunkAnalysisME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new ChunkAnalysisModel(encoding, abModel, beamSize,manifestInfoEntries);
	
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
	 * @param characters 一个个词语
	 * @param pos 词性标注
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] characters,String[] pos) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, characters, pos, null,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
           
        }
        return tagsandposes;
    }

	/**
	 * 最好的K个序列
	 * @param characters 一个个词语
	 * @param pos 词性标注
	 * @return
	 */
    public Sequence[] topKSequences(String[] characters,String[] pos) {
        return this.topKSequences(characters, pos, null);
    }

    /**
     * 最好的K个序列
     * @param characters 一个个词语
     * @param pos 词性标注
     * @param additionaContext
     * @return 
     */
    public Sequence[] topKSequences(String[] characters, String[] pos, Object[] additionaContext) {
        return model.bestSequences(size, characters, pos, additionaContext,
        		contextGenerator, sequenceValidator);
    }
}

