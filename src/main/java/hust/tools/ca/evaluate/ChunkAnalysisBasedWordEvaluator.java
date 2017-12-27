package hust.tools.ca.evaluate;

import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 基于词的组块分析评价器
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisBasedWordEvaluator extends Evaluator<AbstractChunkAnalysisSample>{
	
	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisBasedWordME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 */
	public ChunkAnalysisBasedWordEvaluator(ChunkAnalysisBasedWordME chunkTagger) {
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public ChunkAnalysisBasedWordEvaluator(ChunkAnalysisBasedWordME chunkTagger, AbstractChunkAnalysisMeasure measure, ChunkAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.chunkTagger = chunkTagger;
		this.measure = measure;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(AbstractChunkAnalysisMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public AbstractChunkAnalysisMeasure getMeasure(){
		return measure;
	}
	
	@Override
	protected AbstractChunkAnalysisSample processSample(AbstractChunkAnalysisSample sample) {
		String[] wordsRef = sample.getTokens();
		String[] chunkTagsRef = sample.getTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] chunkTagsPre = chunkTagger.tag(wordsRef, acRef);
		
		//将结果进行解析，用于评估
		AbstractChunkAnalysisSample prediction = new ChunkAnalysisBasedWordSample(wordsRef, chunkTagsPre);
		prediction.setLabel(sample.getLabel());
		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
//		measure.add(sample, prediction);
		return prediction;
	}
}
