package hust.tools.ca.evaluate;

import org.apache.log4j.Logger;

import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 评估类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSEvaluator extends Evaluator<ChunkAnalysisBasedWordSample>{

	Logger logger = Logger.getLogger(ChunkAnalysisBasedWordAndPOSEvaluator.class);
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
	protected ChunkAnalysisBasedWordSample processSample(ChunkAnalysisBasedWordSample sample) {
		ChunkAnalysisBasedWordAndPOSSample wordAndPOSSample = (ChunkAnalysisBasedWordAndPOSSample) sample;
		
		String[] wordsRef = wordAndPOSSample.getWords();
		String[] posesRef = wordAndPOSSample.getPoses();
		String[] chunkTagsRef = wordAndPOSSample.getChunkTags();
		String[][] acRef = wordAndPOSSample.getAditionalContext();
		
		String[] chunkTagsPre = chunkTagger.tag(wordsRef, posesRef, acRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisBasedWordAndPOSSample prediction = new ChunkAnalysisBasedWordAndPOSSample(wordsRef, posesRef, chunkTagsPre);
		measure.update(wordsRef, chunkTagsRef, chunkTagsPre);
//		measure.add(wordAndPOSSample, prediction);
//		logger.info(wordAndPOSSample+"\n"+prediction);
		return prediction;
	}
}
