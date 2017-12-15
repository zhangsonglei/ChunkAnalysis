package hust.tools.ca.evaluate;

import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * <ul>
 *<li>Description: 检测类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 * @param <T>
 */
public class ChunkAnalysisBasedWordAndPOSEvaluateMonitor implements EvaluationMonitor<ChunkAnalysisBasedWordAndPOSSample>{

	/**
	 * 预测正确
	 * @param reference 	参考的结果
	 * @param prediction	预测的结果
	 */
	@Override
	public void correctlyClassified(ChunkAnalysisBasedWordAndPOSSample reference, ChunkAnalysisBasedWordAndPOSSample prediction) {
		
	}

	/**
	 * 预测出错，打印错误信息
	 * @param reference 	参考的结果
	 * @param prediction	预测的结果
	 */
	@Override
	public void missclassified(ChunkAnalysisBasedWordAndPOSSample reference, ChunkAnalysisBasedWordAndPOSSample prediction) {
		
	}
}
