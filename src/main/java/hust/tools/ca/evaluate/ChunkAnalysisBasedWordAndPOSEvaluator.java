package hust.tools.ca.evaluate;

import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析评价器
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSEvaluator extends Evaluator<AbstractChunkAnalysisSample>{
	
	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisBasedWordAndPOSME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 */
	public ChunkAnalysisBasedWordAndPOSEvaluator(ChunkAnalysisBasedWordAndPOSME chunkTagger) {
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public ChunkAnalysisBasedWordAndPOSEvaluator(ChunkAnalysisBasedWordAndPOSME chunkTagger, AbstractChunkAnalysisMeasure measure,
			ChunkAnalysisEvaluateMonitor... evaluateMonitors) {
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
		ChunkAnalysisBasedWordAndPOSSample wordAndPOSSample = (ChunkAnalysisBasedWordAndPOSSample) sample;
		
		String[] wordsRef = wordAndPOSSample.getTokens();
		String[] chunkTagsRef = wordAndPOSSample.getTags();
		
		Object[] objectPosesRef = wordAndPOSSample.getAditionalContext();
		String[] posesRef = new String[objectPosesRef.length];
		for(int i = 0; i < posesRef.length; i++)
			posesRef[i] = (String) objectPosesRef[i];

		String[] chunkTagsPre = chunkTagger.tag(wordsRef, posesRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisBasedWordAndPOSSample prediction = new ChunkAnalysisBasedWordAndPOSSample(wordsRef, posesRef, chunkTagsPre);
		prediction.setLabel(sample.getLabel());
		
		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
//		measure.add(wordAndPOSSample, prediction);
		return prediction;
	}
}
