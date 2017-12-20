package hust.tools.ca.evaluate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import hust.tools.ca.utils.Dictionary;

/**
 *<ul>
 *<li>Description: 组块分析评价类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月20日
 *</ul>
 */
public abstract class AbstractChunkAnalysisMeasure {

	/**
	 * 词典
	 */
	protected Dictionary wordDict;
	
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
	
	public AbstractChunkAnalysisMeasure(Dictionary wordDict) {
		this.wordDict = wordDict;
		referenceChunkTagMap = new HashMap<>();
		predictChunkTagMap = new HashMap<>();
		correctTaggedChunkTagMap = new HashMap<>();
	}
	
	/**
	 * 动态统计预测样本与标准样本
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public void add(ChunkAnalysisBasedWordSample reference, ChunkAnalysisBasedWordSample prediction) {
		String[] words = reference.getWords();				//每个测试样本中的词组
		String[] refChunkTags = reference.getChunkTags();	//参考样本中每个词的组块标记
		String[] preChunkTags = prediction.getChunkTags();	//预测样本中每个词的组块标记
		update(words, refChunkTags, preChunkTags);
	}

	/**
	 * 动态统计预测样本与标准样本
	 * @param words		样本中的词组
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public abstract void update(String[] words, String[] refChunkTags, String[] preChunkTags);
	
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
		return "Accuracy = " + getAccuracy() + "\t" + 
						  "Precision = " + getPrecision() + "\t" + 
						  "Rcall = " + getRecall() + "\t" + 
						  "F = " + getF() + "\n" + 
						  "BNP_P = " + getPrecision("BNP") + "\t" + 
						  "BNP_R = " + getRecall("BNP") + "\t" + 
						  "BNP_F = " + getF("BNP") + "\n" + 
						  "BAP_P = " + getPrecision("BAP") + "\t" + 
						  "BAP_R = " + getRecall("BAP") + "\t" + 
						  "BAP_F = " + getF("BAP") + "\n" + 
						  "BVP_P = " + getPrecision("BVP") + "\t" + 
						  "BVP_R = " + getRecall("BVP") + "\t" + 
						  "BVP_F = " + getF("BVP") + "\n" + 
						  "BDP_P = " + getPrecision("BDP") + "\t" + 
						  "BDP_R = " + getRecall("BDP") + "\t" + 
						  "BDP_F = " + getF("BDP") + "\n" + 
						  "BQP_P = " + getPrecision("BQP") + "\t" + 
						  "BQP_R = " + getRecall("BQP") + "\t" + 
						  "BQP_F = " + getF("BQP") + "\n" + 
						  "BTP_P = " + getPrecision("BTP") + "\t" + 
						  "BTP_R = " + getRecall("BTP") + "\t" + 
						  "BTP_F = " + getF("BTP") + "\n" + 
						  "BFP_P = " + getPrecision("BFP") + "\t" + 
						  "BFP_R = " + getRecall("BFP") + "\t" + 
						  "BFP_F = " + getF("BFP") + "\n" +
						  "BNT_P = " + getPrecision("BNT") + "\t" + 
						  "BNT_R = " + getRecall("BNT") + "\t" + 
						  "BNT_F = " + getF("BNT") + "\n" +
						  "BNS_P = " + getPrecision("BNS") + "\t" + 
						  "BNS_R = " + getRecall("BNS") + "\t" + 
						  "BNS_F = " + getF("BNS") + "\n" +
						  "BNZ_P = " + getPrecision("BNZ") + "\t" + 
						  "BNZ_R = " + getRecall("BNZ") + "\t" + 
						  "BNZ_F = " + getF("BNZ") + "\n" +
						  "BSV_P = " + getPrecision("BSV") + "\t" + 
						  "BSV_R = " + getRecall("BSV") + "\t" + 
						  "BSV_F = " + getF("BSV") + "\n"+
						  "Ref_O=" + referenceChunkTagMap.get("O")+"\t"+"Pre_O="+predictChunkTagMap.get("O")+"\tcorrect="+correctTaggedChunkTagMap.get("O")+"\n"+
						  "Ref_BNP=" + referenceChunkTagMap.get("BNP")+"\tPre_BNP="+predictChunkTagMap.get("BNP")+"\tcorrect="+correctTaggedChunkTagMap.get("BNP")+"\n"+
						  "Ref_BAP=" + referenceChunkTagMap.get("BAP")+"\tPre_BAP="+predictChunkTagMap.get("BAP")+"\tcorrect="+correctTaggedChunkTagMap.get("BAP")+"\n"+
						  "Ref_BVP=" + referenceChunkTagMap.get("BVP")+"\tPre_BVP="+predictChunkTagMap.get("BVP")+"\tcorrect="+correctTaggedChunkTagMap.get("BVP")+"\n"+
						  "Ref_BDP=" + referenceChunkTagMap.get("BDP")+"\tPre_BDP="+predictChunkTagMap.get("BDP")+"\tcorrect="+correctTaggedChunkTagMap.get("BDP")+"\n"+
						  "Ref_BQP=" + referenceChunkTagMap.get("BQP")+"\tPre_BQP="+predictChunkTagMap.get("BQP")+"\tcorrect="+correctTaggedChunkTagMap.get("BQP")+"\n"+
						  "Ref_BTP=" + referenceChunkTagMap.get("BTP")+"\tPre_BTP="+predictChunkTagMap.get("BTP")+"\tcorrect="+correctTaggedChunkTagMap.get("BTP")+"\n"+
						  "Ref_BFP=" + referenceChunkTagMap.get("BFP")+"\tPre_BFP="+predictChunkTagMap.get("BFP")+"\tcorrect="+correctTaggedChunkTagMap.get("BFP")+"\n"+
						  "Ref_BNT=" + referenceChunkTagMap.get("BNT")+"\tPre_BNT="+predictChunkTagMap.get("BNT")+"\tcorrect="+correctTaggedChunkTagMap.get("BNT")+"\n"+
						  "Ref_BNS=" + referenceChunkTagMap.get("BNS")+"\tPre_BNS="+predictChunkTagMap.get("BNS")+"\tcorrect="+correctTaggedChunkTagMap.get("BNS")+"\n"+
						  "Ref_BNZ=" + referenceChunkTagMap.get("BNZ")+"\tPre_BNZ="+predictChunkTagMap.get("BNZ")+"\tcorrect="+correctTaggedChunkTagMap.get("BNZ")+"\n"+
						  "Ref_BSV=" + referenceChunkTagMap.get("BSV")+"\tPre_BSV="+predictChunkTagMap.get("BSV")+"\tcorrect="+correctTaggedChunkTagMap.get("BSV");
	}
}
