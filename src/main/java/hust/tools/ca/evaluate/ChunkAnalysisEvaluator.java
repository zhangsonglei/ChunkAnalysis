package hust.tools.ca.evaluate;

import hust.tools.ca.model.ChunkAnalysisME;
import hust.tools.ca.stream.ChunkAnalysisSample;
import opennlp.tools.util.eval.Evaluator;

/**
 * 评估类
 * @author 王馨苇
 *
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
	 * 构造
	 * @param tagger 训练得到的模型
	 */
	public ChunkAnalysisEvaluator(ChunkAnalysisME chunkTagger) {
		this.chunkTagger = chunkTagger;
	}
	
	/**
	 * 构造
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
		String[] chunkTagsRef = sample.getTags();
		String[][] acRef = sample.getAditionalContext();
		
		String[] chunktagsPre = chunkTagger.tag(wordsRef, posesRef, acRef);
		
		//将结果进行解析，用于评估
		ChunkAnalysisSample prediction = new ChunkAnalysisSample(wordsRef, posesRef, chunktagsPre);
//		measure.update(wordsRef, tagsRef, wordsPre, tagsPre);

		return prediction;
	}
}
