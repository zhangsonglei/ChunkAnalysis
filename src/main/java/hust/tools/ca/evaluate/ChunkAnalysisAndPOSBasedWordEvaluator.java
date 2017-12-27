package hust.tools.ca.evaluate;

import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordME;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSample;
import opennlp.tools.util.eval.Evaluator;

/**
 *<ul>
 *<li>Description: 基于词的词性标注和组块分析评价器 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisAndPOSBasedWordEvaluator extends Evaluator<AbstractChunkAnalysisSample>{
	
	/**
	 * 组块分析模型
	 */
	private ChunkAnalysisAndPOSBasedWordME chunkTagger;
	
	/**
	 * 组块分析评估
	 */
	private AbstractChunkAnalysisMeasure measure;
	
	/**
	 * 词性标注评估
	 */
	private POSBasedWordMeasure posMeasure;
	
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
	public ChunkAnalysisAndPOSBasedWordEvaluator(ChunkAnalysisAndPOSBasedWordME chunkTagger, AbstractChunkAnalysisMeasure measure,
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
	
	public void setMeasure(POSBasedWordMeasure posMeasure){
		this.posMeasure = posMeasure;
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
		String[] posChunksRef = sample.getTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] posChunksPre = chunkTagger.tag(wordsRef, acRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisAndPOSBasedWordSample prediction = new ChunkAnalysisAndPOSBasedWordSample(wordsRef, posChunksPre);
		prediction.setLabel(sample.getLabel());
		String[] chunksPre = new String[posChunksPre.length];
		String[] chunksRef = new String[posChunksPre.length];
		String[] posRef = new String[posChunksRef.length];
		String[] posPre = new String[posChunksPre.length];
		for(int i = 0; i < chunksPre.length; i++) {
			posRef[i] = posChunksRef[i].split("-")[0];
			posPre[i] = posChunksPre[i].split("-")[0];
			chunksPre[i] = posChunksPre[i].split("-")[1];
			chunksRef[i] = posChunksRef[i].split("-")[1];
		}
		
		measure.update(wordsRef, chunksRef, chunksPre);
		posMeasure.updateScores(wordsRef, posRef, posPre);
//		measure.add(sample, prediction);
		return prediction;
	}
}
