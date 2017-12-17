package hust.tools.ca.evaluate;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
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
public class ChunkAnalysisBasedWordEvaluateMonitor implements EvaluationMonitor<AbstractChunkAnalysisSample>{

	/**
	 * 预测正确
	 * @param reference 	参考的结果
	 * @param prediction	预测的结果
	 */
	@Override
	public void correctlyClassified(AbstractChunkAnalysisSample reference, AbstractChunkAnalysisSample prediction) {
		
	}

	/**
	 * 预测出错，打印错误信息
	 * @param reference 	参考的结果
	 * @param prediction	预测的结果
	 */
	@Override
	public void missclassified(AbstractChunkAnalysisSample reference, AbstractChunkAnalysisSample prediction) {
		
	}
}
