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

import hust.tools.ca.beamsearch.ChunkAnalysisAndPOSSequenceValidator;
import hust.tools.ca.beamsearch.ChunkAnalysisBeamSearch;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceClassificationModel;
import hust.tools.ca.event.ChunkAnalysisSampleEventBasedWord;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSampleStream;
import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
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
 *<li>Description: 组块分析模型的训练 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisAndPOSBasedWordME implements Chunker {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisBasedWordContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
    private SequenceValidator<String> sequenceValidator;
    private String label;
    
    public ChunkAnalysisAndPOSBasedWordME(String label) {
    	this.label = label;
    }
	
    /**
     * 构造方法
     * @param model			组块分析模型
     * @param contextGen	上下文生成器
     */
	public ChunkAnalysisAndPOSBasedWordME(ChunkAnalysisAndPOSBasedWordModel model, String label, ChunkAnalysisBasedWordContextGenerator contextGen) {
		this.label = label;
		init(model , contextGen);
	}
	
    /**
     * 初始化工作
     * @param model 		组块分析模型
     * @param contextGen	上下文生成器
     */
	private void init(ChunkAnalysisAndPOSBasedWordModel model, ChunkAnalysisBasedWordContextGenerator contextGen) {
		int beamSize = ChunkAnalysisAndPOSBasedWordME.DEFAULT_BEAM_SIZE;
        String beamSizeString = model.getManifestProperty(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
            beamSize = Integer.parseInt(beamSizeString);

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new ChunkAnalysisAndPOSSequenceValidator(label);
        
        if (model.getChunkAnalysisSequenceModel() != null)
            this.model = model.getChunkAnalysisSequenceModel();
        else
            this.model = new BeamSearch<String>(beamSize, model.getChunkAnalysisModel(), 0);
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
	public ChunkAnalysisAndPOSBasedWordModel train(File file, TrainingParameters params, ChunkAnalysisBasedWordContextGenerator contextGen,
			String encoding){
		ChunkAnalysisAndPOSBasedWordModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisAndPOSBasedWordSampleStream(lineStream, label);
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
	public ChunkAnalysisAndPOSBasedWordModel train(String languageCode, ObjectStream<AbstractChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisBasedWordContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkAnalysisAndPOSBasedWordME.DEFAULT_BEAM_SIZE;
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
            ObjectStream<Event> es = new ChunkAnalysisSampleEventBasedWord(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            maxentModel = trainer.train(es);                       
        }

        if (maxentModel != null) {
            return new ChunkAnalysisAndPOSBasedWordModel(languageCode, maxentModel, beamSize, manifestInfoEntries);
        } else {
            return new ChunkAnalysisAndPOSBasedWordModel(languageCode, chunkClassificationModel, manifestInfoEntries);
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
	public ChunkAnalysisAndPOSBasedWordModel train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			ChunkAnalysisBasedWordContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		ChunkAnalysisAndPOSBasedWordModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisAndPOSBasedWordSampleStream(lineStream, label);
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
	public static ChunkAnalysisAndPOSBasedWordModel readModel(File modelFile, TrainingParameters params, ChunkAnalysisBasedWordContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		ChunkAnalysisAndPOSBasedWordModel model = null;
		String beamSizeString = params.getSettings().get(ChunkAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = ChunkAnalysisAndPOSBasedWordME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) 
            beamSize = Integer.parseInt(beamSizeString);

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new ChunkAnalysisAndPOSBasedWordModel(encoding, abModel, beamSize,manifestInfoEntries);
	
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
	 * @return 分词加词性标注的序列
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
	
//	/**
//	 * 返回词组组块标注后的结果
//	 * @param words	词组
//	 * @return		组块标注后的结果
//	 */
//	public String analysis(String[] words){
//		return analysis(words, null);
//	}
//	
//	/**
//	 * 返回词组组块标注后的结果
//	 * @param words				词组
//	 * @param additionaContext	其他上下文信息
//	 * @return					组块标注后的结果
//	 */
//	public String analysis(String[] words, Object[] additionaContext){
//		bestSequence = model.bestSequence(words, additionaContext, contextGenerator, sequenceValidator);
//		List<String> posChunks = bestSequence.getOutcomes();
//		String chunksResult = null;
//		
//		for(int i = 0; i < posChunks.size() - 1; i++) {
//			String chunk = posChunks.get(i).split("-")[1];
//			
//			if(chunk.equals("O"))
//				chunksResult += words[i] + "  ";
//			else if(chunk.split("_")[1].equals("I")) 
//				chunksResult += words[i] + "  ";	
//			else {
//				if(i > 0) {
//					String lastChunk = posChunks.get(i - 1);
//					if(lastChunk.equals("O"))
//						chunksResult += "[" + words[i] + "  ";
//					else 
//						chunksResult = chunksResult.trim() + "]" + lastChunk.split("_")[0] + "[" + words[i] + "  ";
//				}else				
//					chunksResult += "[" + words[i] + "  ";
//			}
//		}
//		
//		String finalChunk = posChunks.get(posChunks.size() - 1);
//		if(finalChunk.equals("O"))
//			chunksResult += words[posChunks.size() - 1];
//		else
//			chunksResult += words[posChunks.size() - 1] + "]" + finalChunk.split("_")[0];
//		
//		return chunksResult;
//	}

	/**
	 * 返回词组的组块标注结果
	 * @param words	词组
	 * @return		词组的组块标注结果
	 */
	public String[] tag(String[] words){
		return tag(words, null);
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
    	List<Chunk> chunks = new ArrayList<>();
		String[] words = sentence.split("//s+");
		
		String[] posChunks = tag(words);
		
		int start = 0;
		int end = 0;
		boolean isChunk = false;
		String type = null;
		
		for(int i = 0; i < posChunks.length; i++) {
			String chunkTag = posChunks[i].split("-")[1];
			
			if(chunkTag.equals("O")) {
				if(isChunk) {
					end = i - 1;
					chunks.add(new Chunk(type, join(words, start, end), start, end));
				}
				isChunk = false;
				
				chunks.add(new Chunk(chunkTag, new String[]{words[i]}, i, i));
			}else {
				if(chunkTag.split("_").equals("B")) {
					if(isChunk) {
						end = i - 1;
						chunks.add(new Chunk(type, join(words, start, end), start, end));
					}
					isChunk = false;
					
					start = i;
					isChunk = true;
					type = chunkTag.split("_")[0];
				}
			}
		}
		
		if(isChunk) {
			end = posChunks.length - 1;
			chunks.add(new Chunk(type, join(words, start, end), start, end));
		}
		
		return chunks.toArray(new Chunk[chunks.size()]);
	}

	@Override
	public Chunk[][] parse(String sentence, int k) {
		List<Chunk[]> chunks = new ArrayList<>();
		String[] words = sentence.split("//s+");

		String[][] posChunks = tag(k, words);
		
		int start = 0;
		int end = 0;
		boolean isChunk = false;
		String type = null;
		
		List<Chunk> temp = new ArrayList<>();
		for(int i = 0; i < posChunks.length; i++) {
			for(int j = 0; j < posChunks[i].length; j++) {	
				String chunkTag = posChunks[i][j].split("-")[1];
				
				if(chunkTag.equals("O")) {
					if(isChunk) {
						end = i - 1;
						temp.add(new Chunk(type, join(words, start, end), start, end));
					}
					isChunk = false;
					
					temp.add(new Chunk(chunkTag, new String[]{words[j]}, j, j));
				}else {
					if(chunkTag.split("_").equals("B")) {
						if(isChunk) {
							end = j - 1;
							temp.add(new Chunk(type, join(words, start, end), start, end));
						}
						isChunk = false;
						
						start = j;
						isChunk = true;
						type = chunkTag.split("_")[0];
					}
				}
			}
			
			if(isChunk) {
				end = posChunks[i].length - 1;
				temp.add(new Chunk(type, join(words, start, end), start, end));
			}
			
			chunks.add(temp.toArray(new Chunk[temp.size()]));
		}
		
		Chunk[][] result = new Chunk[chunks.size()][];
		for(int i = 0; i < result.length; i++)
			result[i] = chunks.get(i);
		
		return result;
	}
	
	/**
	 * 拼接字符串word/pos  word/pos  ...
	 * @param words	带拼接的词组
	 * @param poses	词组对应的词性
	 * @param start	拼接的开始位置
	 * @param end	拼接的结束位置
	 * @return		拼接后的字符串
	 */
	private List<String> join(String[] words, int start, int end) {
		List<String> string = new ArrayList<>();
		for(int i = start; i <= end; i++) 
			string.add(words[i]);
		
		return string;
	}
}

