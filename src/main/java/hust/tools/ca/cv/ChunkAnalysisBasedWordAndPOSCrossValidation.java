package hust.tools.ca.cv;

import java.io.IOException;

import hust.tools.ca.evaluate.AbstractChunkAnalysisMeasure;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordAndPOSEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIEO;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIO;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSModel;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import opennlp.tools.util.ObjectStream;
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
public class ChunkAnalysisBasedWordAndPOSCrossValidation {
	
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
	public ChunkAnalysisBasedWordAndPOSCrossValidation(TrainingParameters params){
		this.params = params;
	}
	
	/**
	 * n折交叉验证评估
	 * @param sampleStream		样本流
	 * @param nFolds			折数
	 * @param contextGenerator	上下文
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<AbstractChunkAnalysisSample> sampleStream, int nFolds,
			ChunkAnalysisBasedWordAndPOSContextGenerator contextGenerator, String label) throws IOException{
		CrossValidationPartitioner<AbstractChunkAnalysisSample> partitioner = new CrossValidationPartitioner<AbstractChunkAnalysisSample>(sampleStream, nFolds);
		
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			
			CrossValidationPartitioner.TrainingSampleStream<AbstractChunkAnalysisSample> trainingSampleStream = partitioner.next();
			ChunkAnalysisBasedWordAndPOSME me = new ChunkAnalysisBasedWordAndPOSME(label);
			ChunkAnalysisBasedWordAndPOSModel model = me.train("zh", trainingSampleStream, params, contextGenerator);
			ChunkAnalysisBasedWordAndPOSEvaluator evaluator = new ChunkAnalysisBasedWordAndPOSEvaluator(new ChunkAnalysisBasedWordAndPOSME(model, label, contextGenerator), label);
			
			AbstractChunkAnalysisMeasure measure = null;
			switch (label) {
			case "BIO":
				measure = new ChunkAnalysisMeasureWithBIO();
				break;
			case "BIEO":
				measure = new ChunkAnalysisMeasureWithBIEO();
				break;
			default:
				System.err.println("错误的标签类型，已默认为BIEO");
				measure = new ChunkAnalysisMeasureWithBIEO();
				break;
			}
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(measure);
	}
}
