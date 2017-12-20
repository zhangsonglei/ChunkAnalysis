package hust.tools.ca.cv;

import java.io.IOException;

import hust.tools.ca.evaluate.AbstractChunkAnalysisMeasure;
import hust.tools.ca.evaluate.ChunkAnalysisAndPOSBasedWordEvaluator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordME;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordModel;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 *<ul>
 *<li>Description: 交叉验证 
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
	public void evaluate(ObjectStream<ChunkAnalysisBasedWordSample> sampleStream, int nFolds,
			ChunkAnalysisBasedWordContextGenerator contextGenerator, AbstractChunkAnalysisMeasure measure, SequenceValidator<String> sequenceValidator) throws IOException{
		CrossValidationPartitioner<ChunkAnalysisBasedWordSample> partitioner = new CrossValidationPartitioner<ChunkAnalysisBasedWordSample>(sampleStream, nFolds);
		
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			
			CrossValidationPartitioner.TrainingSampleStream<ChunkAnalysisBasedWordSample> trainingSampleStream = partitioner.next();
			ChunkAnalysisAndPOSBasedWordME me = new ChunkAnalysisAndPOSBasedWordME(); 
			ChunkAnalysisAndPOSBasedWordModel model = me.train("zh", trainingSampleStream, params, contextGenerator);
			ChunkAnalysisAndPOSBasedWordEvaluator evaluator = new ChunkAnalysisAndPOSBasedWordEvaluator(new ChunkAnalysisAndPOSBasedWordME(model, sequenceValidator, contextGenerator), measure);
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
	}
}
