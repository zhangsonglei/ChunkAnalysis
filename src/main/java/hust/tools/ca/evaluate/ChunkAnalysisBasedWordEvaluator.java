package hust.tools.ca.evaluate;

import org.apache.log4j.Logger;

import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 评估类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisBasedWordEvaluator extends Evaluator<AbstractChunkAnalysisSample>{

	Logger logger = Logger.getLogger(ChunkAnalysisBasedWordEvaluator.class);
	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisBasedWordME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private ChunkAnalysisMeasure measure;
	
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
	public ChunkAnalysisBasedWordEvaluator(ChunkAnalysisBasedWordME chunkTagger, boolean isBIEO ,ChunkAnalysisEvaluateMonitor... evaluateMonitors) {
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
	protected AbstractChunkAnalysisSample processSample(AbstractChunkAnalysisSample sample) {
		String[] wordsRef = sample.getWords();
		String[] chunkTagsRef = sample.getChunkTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] chunkTagsPre = chunkTagger.tag(wordsRef, acRef);
		
		//将结果进行解析，用于评估
		AbstractChunkAnalysisSample prediction = new ChunkAnalysisBasedWordSample(wordsRef, chunkTagsPre);
		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
//		measure.add(sample, prediction);
//		logger.info(sample+"\n"+prediction);
		return prediction;
	}
}
