package hust.tools.ca.evaluate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import hust.tools.ca.stream.ChunkAnalysisSample;

/**
 *<ul>
 *<li>Description: 组块分析模型评价 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月2日
 *</ul>
 */
public class ChunkAnalysisMeasure {
	
	/**
	 * 词典
	 */
	private HashSet<String> wordDict;
	
	/**
	 * 预测结果中，每个组块标记的数量
	 */
	private HashMap<String, Long> predictChunkTagCounts;
	
	/**
	 * 每个组块标记的标准数量
	 */
	private HashMap<String, Long> referenceChunkTagCounts; 
	
	/**
	 * 每个组块被标注正确的数量
	 */
	private HashMap<String, Long> correctTaggedChunkTagCounts;

	/**
	 * 所有词的数量
	 */
	private long totalWordCounts;
	
	/**
	 * 标注正确的词的数量
	 */
	private long correctTaggedWordCounts;
	
	/**
	 * 未登录词总数量
	 */
	private long OOVs;
	
	/**
	 * 正确标记的未登录词数量
	 */
	private long correctTaggedOOVs;
	
	/**
	 * 默认构造方法
	 */
	public ChunkAnalysisMeasure() {
		super();
	}
	
	/**
	 * 构造方法
	 * @param wordDict	词典，用于辨别未登录词
	 * @param reference	标准样本集
	 * @param predict	预测样本集
	 */
	public ChunkAnalysisMeasure(HashSet<String> wordDict, List<ChunkAnalysisSample> reference, List<ChunkAnalysisSample> predict) {
		super();
		this.wordDict = wordDict;
		statistics(reference, predict);
	}

	/**
	 * 统计评价指标所需的数据
	 * @param references	标准样本
	 * @param predictions	预测结果
	 */
	private void statistics(List<ChunkAnalysisSample> references, List<ChunkAnalysisSample> predictions) {
		for(int i = 0; i < references.size(); i++)//遍历每个样本
			add(references.get(i), predictions.get(i));
	}
	
	/**
	 * 动态统计预测样本与标准样本
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public void add(ChunkAnalysisSample reference, ChunkAnalysisSample prediction) {
		String[] words = reference.getWords();				//每个样本中的词组
		String[] refChunkTags = reference.getChunkTags();	//样本中每个词的正确组块标记
		String[] preChunkTags = prediction.getChunkTags();		//样本中每个词的预测组块标记
		String refChunkTag;
		String preChunkTag;
		
		for(int i = 0; i < words.length; i++) {//便利样本中的每个词
			totalWordCounts++;
			
			if(!wordDict.contains(words[i])) 
				OOVs++;
			
			//统计样本中每类组块标记标准数量与预测数量
			refChunkTag = refChunkTags[i];
			if(refChunkTag.equals("O")){
				if(referenceChunkTagCounts.containsKey(refChunkTag))
					referenceChunkTagCounts.put(refChunkTag, referenceChunkTagCounts.get(refChunkTag) + 1);
				else
					referenceChunkTagCounts.put(refChunkTag, 1L);
			}else {
				String ref = refChunkTag.split("_")[0];
				if(referenceChunkTagCounts.containsKey(ref))
					referenceChunkTagCounts.put(ref, referenceChunkTagCounts.get(ref) + 1);
				else
					referenceChunkTagCounts.put(ref, 1L);
			}
			preChunkTag = preChunkTags[i];
			if(preChunkTag.equals("O")){
				if(predictChunkTagCounts.containsKey(preChunkTag))
					predictChunkTagCounts.put(preChunkTag, predictChunkTagCounts.get(preChunkTag) + 1);
				else
					predictChunkTagCounts.put(preChunkTag, 1L);
			}else {
				String pre = preChunkTag.split("_")[0];
				if(predictChunkTagCounts.containsKey(pre))
					predictChunkTagCounts.put(pre, predictChunkTagCounts.get(pre) + 1);
				else
					predictChunkTagCounts.put(pre, 1L);
			}
			
			//统计每类组块标记被正确预测的数量
			if(preChunkTag.equals(refChunkTag)) {
				correctTaggedWordCounts++;
				
				if(!wordDict.contains(words[i]))
					correctTaggedOOVs++;
				
				if(preChunkTag.equals("O")) {
					if(correctTaggedChunkTagCounts.containsKey(preChunkTag))
						correctTaggedChunkTagCounts.put(preChunkTag, correctTaggedChunkTagCounts.get(preChunkTag) + 1);
					else
						correctTaggedChunkTagCounts.put(preChunkTag, 1L);
				}else {
					String chunkTag = preChunkTag.split("_")[0];
					if(correctTaggedChunkTagCounts.containsKey(chunkTag))
						correctTaggedChunkTagCounts.put(chunkTag, correctTaggedChunkTagCounts.get(chunkTag) + 1);
					else
						correctTaggedChunkTagCounts.put(chunkTag, 1L);
				}
			}
		}//end for(j)
	}
	
	/**
	 * 返回样本中所有组块标记的迭代器
	 * @return	样本中所有组块标记的迭代器
	 */
	private Iterator<String> chunkTagIterator() {
		return referenceChunkTagCounts.keySet().iterator();
	}
	
	/**
	 * 返回模型的准确率(各个组块标记准确率的平均值)
	 * @return	模型的准确率
	 */
	public double getAccuracy() {
		return 1.0 * correctTaggedWordCounts / totalWordCounts;
	}
	
	/**
	 * 返回未登录词组块标记的准确率
	 * @return	登录词组块标记的准确率
	 */
	public double getOOVAccuracy() {
		return 1.0 * correctTaggedOOVs / OOVs;
	}
	
	/**
	 * 返回给定组块标记的准确率
	 * @param chunkTag	待求准确率的组块标记
	 * @return			给定组块标记的准确率
	 */
	public double getAccuracy(String chunkTag) {
		return 1.0 * correctTaggedChunkTagCounts.get(chunkTag) / referenceChunkTagCounts.get(chunkTag);
	}
	
	/**
	 * 返回模型的召回率(各个组块标记召回率的平均值)
	 * @return	模型的召回率
	 */
	public double getRecall() {
		double recall = 0.0;
		
		Iterator<String> iterator = chunkTagIterator();
		while(iterator.hasNext()) {
			recall += getRecall(iterator.next());
		}
		
		return recall;
	}
	
	/**
	 * 返回给定组块标记的召回率
	 * @param chunkTag	待求召回率的组块标记
	 * @return			给定组块标记的召回率
	 */
	public double getRecall(String chunkTag) {
		return 1.0 * correctTaggedChunkTagCounts.get(chunkTag) / referenceChunkTagCounts.get(chunkTag);
	}
	
	/**
	 * 返回模型的精确率(各个组块标记精确率的平均值)
	 * @return	模型的精确率
	 */
	public double getPrecision() {
		double precision = 0.0;
		
		Iterator<String> iterator = chunkTagIterator();
		while(iterator.hasNext()) {
			precision += getPrecision(iterator.next());
		}
		
		return precision;
	}
	
	/**
	 * 返回给定组块标记的精确率
	 * @param chunkTag	待求精确率的组块标记
	 * @return			给定组块标记的精确率
	 */
	public double getPrecision(String chunkTag) {		
		return 1.0 * correctTaggedChunkTagCounts.get(chunkTag) / predictChunkTagCounts.get(chunkTag);
	}
	
	/**
	 * 返回模型的F值
	 * @return	模型的F值
	 */
	public double getF() {
		if(getPrecision() == 0 || getRecall() == 0)
			return 0;
		else
			return 2 * getPrecision() * getRecall() / (getPrecision() + getRecall());
	}
	
	/**
	 * 返回给定组块标记的F值
	 * @param chunkTag	待求F值的组块标记
	 * @return			给定组块标记的F值
	 */
	public double getF(String chunkTag) {
		double precision = getPrecision(chunkTag);
		double recall = getRecall(chunkTag);
		
		if(precision == 0 || recall == 0)
			return 0;
		else
			return 2 * precision * recall / (precision + recall);
	}
}
