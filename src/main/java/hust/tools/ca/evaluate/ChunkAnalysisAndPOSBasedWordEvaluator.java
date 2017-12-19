package hust.tools.ca.evaluate;

import org.apache.log4j.Logger;

import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordME;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 评估类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisAndPOSBasedWordEvaluator extends Evaluator<AbstractChunkAnalysisSample>{

	Logger logger = Logger.getLogger(ChunkAnalysisAndPOSBasedWordEvaluator.class);
	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisAndPOSBasedWordME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 */
	public ChunkAnalysisAndPOSBasedWordEvaluator(ChunkAnalysisAndPOSBasedWordME chunkTagger) {
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 构造方法
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public ChunkAnalysisAndPOSBasedWordEvaluator(ChunkAnalysisAndPOSBasedWordME chunkTagger, String label,
			ChunkAnalysisEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.chunkTagger = chunkTagger;
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
		String[] wordsRef = sample.getWords();
		String[] posChunksRef = sample.getChunkTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] posChunksPre = chunkTagger.tag(wordsRef, acRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisAndPOSBasedWordSample prediction = new ChunkAnalysisAndPOSBasedWordSample(wordsRef, posChunksPre);

		String[] chunksPre = new String[posChunksPre.length];
		String[] chunksRef = new String[posChunksPre.length];
		for(int i = 0; i < chunksPre.length; i++) {
			chunksPre[i] = posChunksPre[i].split("-")[1];
			chunksRef[i] = posChunksRef[i].split("-")[1];
		}
		measure.update(wordsRef, chunksRef, chunksPre);
//		measure.add(sample, prediction);
//		logger.info(sample+"\n"+prediction);
		return prediction;
	}
}
