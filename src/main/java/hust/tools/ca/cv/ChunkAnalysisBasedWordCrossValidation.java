package hust.tools.ca.cv;

import java.io.IOException;
import java.util.HashSet;

import hust.tools.ca.evaluate.AbstractChunkAnalysisMeasure;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordEvaluator;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 *<ul>
 *<li>Description: 基于词的组块分析交叉验证 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordCrossValidation {

	/**
	 * 训练的参数集
	 */
	private final TrainingParameters params;
	
	/**
	 * 构造方法
	 * @param encoding	编码格式
	 * @param params	训练的参数
	 * @param monitor 	监听器
	 */
	public ChunkAnalysisBasedWordCrossValidation(TrainingParameters params){
		this.params = params;
	}
	
	/**
	 * n折交叉验证评估
	 * @param sampleStream		样本流
	 * @param nFolds			折数
	 * @param contextGenerator	上下文
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<AbstractChunkAnalysisSample> sampleStream, int nFolds, ChunkAnalysisContextGenerator contextGenerator, 
			AbstractChunkAnalysisMeasure measure, SequenceValidator<String> sequenceValidator) throws IOException{
		
		String label = ((ChunkAnalysisBasedWordSampleStream) sampleStream).getLabel();
		CrossValidationPartitioner<AbstractChunkAnalysisSample> partitioner = new CrossValidationPartitioner<AbstractChunkAnalysisSample>(sampleStream, nFolds);
		
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			
			CrossValidationPartitioner.TrainingSampleStream<AbstractChunkAnalysisSample> trainingSampleStream = partitioner.next();
			HashSet<String> dict = getDict(trainingSampleStream);
			trainingSampleStream.reset();
			measure.setDictionary(dict);
			
			ChunkAnalysisBasedWordME me = new ChunkAnalysisBasedWordME();
			ChunkAnalysisBasedWordModel model = me.train("zh", trainingSampleStream, params, contextGenerator);
			ChunkAnalysisBasedWordEvaluator evaluator = new ChunkAnalysisBasedWordEvaluator(new ChunkAnalysisBasedWordME(model, sequenceValidator, contextGenerator, label), measure);
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
		
		
	}
	
	/**
     * 获取词典
     * @param sampleStream	样本流
     * @return				词典
     * @throws IOException
     */
	private HashSet<String> getDict(ObjectStream<AbstractChunkAnalysisSample> sampleStream) throws IOException {
    	HashSet<String> dictionary = new HashSet<String>();
        AbstractChunkAnalysisSample sample = null;
        while ((sample = sampleStream.read()) != null) {
        	String[] words = sample.getTokens();
        	
        	for(String word : words)
        		dictionary.add(word);
		}
        
        return dictionary;
    }
}
