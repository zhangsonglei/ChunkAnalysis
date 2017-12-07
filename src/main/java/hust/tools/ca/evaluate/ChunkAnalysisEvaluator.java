package hust.tools.ca.evaluate;

import hust.tools.ca.model.ChunkAnalysisME;
import hust.tools.ca.stream.ChunkAnalysisSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 评估类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisEvaluator extends Evaluator<ChunkAnalysisSample>{

	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private ChunkAnalysisMeasure measure;
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 */
	public ChunkAnalysisEvaluator(ChunkAnalysisME chunkTagger) {
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public ChunkAnalysisEvaluator(ChunkAnalysisME chunkTagger,ChunkAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(ChunkAnalysisMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public ChunkAnalysisMeasure getMeasure(){
		return measure;
	}
	
	
	@Override
	protected ChunkAnalysisSample processSample(ChunkAnalysisSample sample) {
		String[] wordsRef = sample.getWords();
		String[] posesRef = sample.getPoses();
//		String[] chunkTagsRef = sample.getChunkTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] chunkTagsPre = chunkTagger.tag(wordsRef, posesRef, acRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisSample prediction = new ChunkAnalysisSample(wordsRef, posesRef, chunkTagsPre);
//		measure.update(wordsRef, tagsRef, wordsPre, tagsPre);

		return prediction;
	}
}
