package hust.tools.ca.cv;

import java.io.IOException;

import hust.tools.ca.evaluate.ChunkAnalysisEvaluateMonitor;
import hust.tools.ca.evaluate.ChunkAnalysisEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisMeasure;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.model.ChunkAnalysisME;
import hust.tools.ca.model.ChunkAnalysisModel;
import hust.tools.ca.stream.ChunkAnalysisSample;
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
public class ChunkAnalysisCrossValidation {

	/**
	 * 语料文件字符编码
	 */
	private final String encoding;
	
	/**
	 * 训练的参数集
	 */
	private final TrainingParameters params;
	
	/**
	 * 组块分析评估监视器
	 */
	private ChunkAnalysisEvaluateMonitor[] monitors;
	
	
	/**
	 * 构造方法
	 * @param encoding	编码格式
	 * @param params	训练的参数
	 * @param monitor 	监听器
	 */
	public ChunkAnalysisCrossValidation(String encoding,TrainingParameters params,ChunkAnalysisEvaluateMonitor... monitors){
		this.encoding = encoding;
		this.params = params;
		this.monitors = monitors;
	}
	
	/**
	 * n折交叉验证评估
	 * @param sampleStream		样本流
	 * @param nFolds			折数
	 * @param contextGenerator	上下文
	 * @throws IOException
	 */
	public void evaluate(ObjectStream<ChunkAnalysisSample> sampleStream, int nFolds,
			ChunkAnalysisContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<ChunkAnalysisSample> partitioner = new CrossValidationPartitioner<ChunkAnalysisSample>(sampleStream, nFolds);
		
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			
			CrossValidationPartitioner.TrainingSampleStream<ChunkAnalysisSample> trainingSampleStream = partitioner.next();
			ChunkAnalysisModel model = ChunkAnalysisME.train(encoding, trainingSampleStream, params, contextGenerator);
			ChunkAnalysisEvaluator evaluator = new ChunkAnalysisEvaluator(new ChunkAnalysisME(model, contextGenerator), monitors);
			ChunkAnalysisMeasure measure = new ChunkAnalysisMeasure();
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(measure);
	}
}
