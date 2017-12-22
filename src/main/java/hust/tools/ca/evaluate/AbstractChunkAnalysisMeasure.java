package hust.tools.ca.evaluate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.utils.Dictionary;

/**
 *<ul>
 *<li>Description: 组块分析评价抽象类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月20日
 *</ul>
 */
public abstract class AbstractChunkAnalysisMeasure {

	/**
	 * 词典
	 */
	protected Dictionary dict;
	
	/**
	 * 预测结果中，每个组块标记的数量
	 */
	protected HashMap<String, Long> predictChunkTagMap;
	
	/**
	 * 每个组块标记的标准数量
	 */
	protected HashMap<String, Long> referenceChunkTagMap; 
	
	/**
	 * 每个组块被标注正确的数量
	 */
	protected HashMap<String, Long> correctTaggedChunkTagMap;

	/**
	 * 所有词的数量
	 */
	protected long totalWordCounts;
	
	/**
	 * 标注正确的词的数量
	 */
	protected long correctTaggedWordCounts;
	
	/**
	 * 未登录词总数量
	 */
	protected long OOVs;
	
	/**
	 * 正确标记的未登录词数量
	 */
	protected long correctTaggedOOVs;
	
	public AbstractChunkAnalysisMeasure() {
		this(new Dictionary());
	}
	
	public AbstractChunkAnalysisMeasure(Dictionary dict) {
		this.dict = dict;
		referenceChunkTagMap = new HashMap<>();
		predictChunkTagMap = new HashMap<>();
		correctTaggedChunkTagMap = new HashMap<>();
	}
	
	/**
	 * 动态统计预测样本与标准样本
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public void add(AbstractChunkAnalysisSample reference, AbstractChunkAnalysisSample prediction) {
		String[] tokens = reference.getTokens();				//每个测试样本中的词组
		String[] refChunkTags = reference.getTags();	//参考样本中每个词的组块标记
		String[] preChunkTags = prediction.getTags();	//预测样本中每个词的组块标记
		update(tokens, refChunkTags, preChunkTags);
	}

	/**
	 * 动态统计预测样本与标准样本
	 * @param tokens	样本中的词或字组
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public abstract void update(String[] tokens, String[] refChunkTags, String[] preChunkTags);
	
	/**
	 * 返回样本中所有组块标记的迭代器
	 * @return	样本中所有组块标记的迭代器
	 */
	public Iterator<String> chunkTagIterator() {
		return referenceChunkTagMap.keySet().iterator();
	}
	
	/**
	 * 返回模型的准确率(各个组块标记准确率的平均值)
	 * @return	模型的准确率
	 */
	public double getAccuracy() {
		if(totalWordCounts == 0)
			return 0;
		
		return 1.0 * correctTaggedWordCounts / totalWordCounts;
	}
	
	/**
	 * 返回未登录词组块标记的准确率
	 * @return	登录词组块标记的准确率
	 */
	public double getOOVAccuracy() {
		if(OOVs == 0)
			return 0;
		
		return 1.0 * correctTaggedOOVs / OOVs;
	}
	
	/**
	 * 返回模型的召回率(各个组块标记召回率的平均值)
	 * @return	模型的召回率
	 */
	public double getRecall() {
		long correct = 0L;
		long total = 0L;
		
		for(Entry<String, Long> entry : correctTaggedChunkTagMap.entrySet())
			if(!entry.getKey().equals("O"))
				correct += entry.getValue();
		for(Entry<String, Long> entry : referenceChunkTagMap.entrySet())
			if(!entry.getKey().equals("O"))
				total += entry.getValue();
		
		return 1.0 * correct / total;
	}
	
	/**
	 * 返回给定组块标记的召回率
	 * @param chunkTag	待求召回率的组块标记
	 * @return			给定组块标记的召回率
	 */
	public double getRecall(String chunkTag) {
		if(!referenceChunkTagMap.containsKey(chunkTag) || !correctTaggedChunkTagMap.containsKey(chunkTag))
			return 0;
		
		return 1.0 * correctTaggedChunkTagMap.get(chunkTag) / referenceChunkTagMap.get(chunkTag);
	}
	
	/**
	 * 返回模型的精确率(各个组块标记精确率的平均值)
	 * @return	模型的精确率
	 */
	public double getPrecision() {
		long correct = 0L;
		long total = 0L;
		
		for(Entry<String, Long> entry : correctTaggedChunkTagMap.entrySet())
			if(!entry.getKey().equals("O"))
				correct += entry.getValue();
		for(Entry<String, Long> entry : predictChunkTagMap.entrySet())
			if(!entry.getKey().equals("O"))
				total += entry.getValue();
		
		return 1.0 * correct / total;
	}
	
	/**
	 * 返回给定组块标记的精确率
	 * @param chunkTag	待求精确率的组块标记
	 * @return			给定组块标记的精确率
	 */
	public double getPrecision(String chunkTag) {	
		if(!predictChunkTagMap.containsKey(chunkTag) || !correctTaggedChunkTagMap.containsKey(chunkTag))
			return 0;
		
		return 1.0 * correctTaggedChunkTagMap.get(chunkTag) / predictChunkTagMap.get(chunkTag);
	}
	
	/**
	 * 返回模型的F值
	 * @return	模型的F值
	 */
	public double getF() {
		double precision = getPrecision();
		double recall = getRecall();
		
		if(precision == 0 || recall == 0)
			return 0;
		else
			return 2 * precision * recall / (precision + recall);
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
	

	public long getTotalWordCounts() {
		return totalWordCounts;
	}

	public long getCorrectTaggedWordCounts() {
		return correctTaggedWordCounts;
	}

	public long getOOVs() {
		return OOVs;
	}

	public long getCorrectTaggedOOVs() {
		return correctTaggedOOVs;
	}
	
	public String toString() {
		String result = "";
		result += "A = " + getAccuracy() + "\tP = " + getPrecision() + "\tR = " + getRecall() + "\tF = " + getF() + "\n";
		for(Entry<String, Long> entry : referenceChunkTagMap.entrySet()) {
			String chunk = entry.getKey();
			result += chunk + "\tP = " + getPrecision(chunk) + "\tR = " + getRecall(chunk) + "\tF = " + getF(chunk) + 
					"\tRef = " + referenceChunkTagMap.get(chunk) + "\tPre = " + predictChunkTagMap.get(chunk) + "\tcorrect = " + correctTaggedChunkTagMap.get(chunk) + "\n";
		}
		return result;
	}
}
