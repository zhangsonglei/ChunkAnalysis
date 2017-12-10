package hust.tools.ca.evaluate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import hust.tools.ca.stream.ChunkAnalysisSample;
import hust.tools.ca.utils.Dictionary;

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
	private Dictionary wordDict;
	
	/**
	 * 预测结果中，每个组块标记的数量
	 */
	private HashMap<String, Long> predictChunkTagMap;
	
	/**
	 * 每个组块标记的标准数量
	 */
	private HashMap<String, Long> referenceChunkTagMap; 
	
	/**
	 * 每个组块被标注正确的数量
	 */
	private HashMap<String, Long> correctTaggedChunkTagMap;

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
		this(new Dictionary());
	}
	
	public ChunkAnalysisMeasure(Dictionary wordDict) {
		this.wordDict = wordDict;
		referenceChunkTagMap = new HashMap<>();
		predictChunkTagMap = new HashMap<>();
		correctTaggedChunkTagMap = new HashMap<>();
	}
	
	/**
	 * 构造方法
	 * @param wordDict	词典，用于辨别未登录词
	 * @param reference	标准样本集
	 * @param predict	预测样本集
	 */
	public ChunkAnalysisMeasure(Dictionary wordDict, List<ChunkAnalysisSample> reference, List<ChunkAnalysisSample> predict) {
		this.wordDict = wordDict;
		referenceChunkTagMap = new HashMap<>();
		predictChunkTagMap = new HashMap<>();
		correctTaggedChunkTagMap = new HashMap<>();
		statistics(reference, predict);
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

	/**
	 * 统计评价指标所需的数据
	 * @param references	标准样本
	 * @param predictions	预测结果
	 */
	public void statistics(List<ChunkAnalysisSample> references, List<ChunkAnalysisSample> predictions) {
		for(int i = 0; i < references.size(); i++)//遍历每个样本
			add(references.get(i), predictions.get(i));
	}

	/**
	 * 动态统计预测样本与标准样本
	 * @param words		样本中的词组
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public void update(String[] words, String[] refChunkTags, String[] preChunkTags) {
		String refChunkTag;									//参考样本中当前词的组块标记
		String preChunkTag;									//预测样本中当前词的组块标记
		
		List<String> tempRefChunk = new ArrayList<>();		//临时存放参考样本中的组块
		List<String> tempPreChunk = new ArrayList<>();		//临时存放预测样本中的组块
		List<String> correctPreChunk = new ArrayList<>();	//临时存放预测正确的组块
		List<String> wordsInChunk = new ArrayList<>();		//临时存放组块中的词组
		
		for(int i = 0; i < words.length; i++) {//便利样本中的每个词,统计样本中每类组块标记标准数量与预测数量
			totalWordCounts++;
			
			if(!wordDict.contains(words[i])) 
				OOVs++;
			
			refChunkTag = refChunkTags[i];
			preChunkTag = preChunkTags[i];
			if(refChunkTag.equals("O") || refChunkTag.split("_")[1].equals("B")) {//统计参考样本中各个组块数量，及预测正确的数量
				if(tempRefChunk.size() != 0) {//存在未处理的参考组块, 进行统计
					processChunk(tempRefChunk, correctPreChunk, wordsInChunk);
					tempRefChunk = new ArrayList<>();
					correctPreChunk = new ArrayList<>();
					wordsInChunk = new ArrayList<>();
				}
				
				if(refChunkTag.equals("O")) {//当前词的组块标记为O
					if(referenceChunkTagMap.containsKey(refChunkTag))
						referenceChunkTagMap.put(refChunkTag, referenceChunkTagMap.get(refChunkTag) + 1);
					else
						referenceChunkTagMap.put(refChunkTag, 1L);
					
					if(preChunkTag.equals(refChunkTag)) {//非组块被预测正确，进行统计
						if(correctTaggedChunkTagMap.containsKey(refChunkTag))
							correctTaggedChunkTagMap.put(refChunkTag, correctTaggedChunkTagMap.get(refChunkTag) + 1);
						else
							correctTaggedChunkTagMap.put(refChunkTag, 1L);
						
						if(!wordDict.contains(words[i]))//被预测正确的非组块为未登录词
							correctTaggedOOVs++;
						
						correctTaggedWordCounts++;
					}
				}else {//当前词的组块标记为*_B
					tempRefChunk.add(refChunkTag);
					correctPreChunk.add(preChunkTag);
					wordsInChunk.add(words[i]);
				}
			}else{//当前词的组块标记为*_I || *_E
				tempRefChunk.add(refChunkTag);
				correctPreChunk.add(preChunkTag);
				wordsInChunk.add(words[i]);
			}
			
			//统计预测结果中各个组块数量
			if(preChunkTag.equals("O") || preChunkTag.split("_")[1].equals("B")) {
				if(tempPreChunk.size() != 0) {//存在未处理的预测组块, 进行统计
					String chunk = tempPreChunk.get(0).split("_")[0];
					if(predictChunkTagMap.containsKey(chunk))
						predictChunkTagMap.put(chunk, predictChunkTagMap.get(chunk) + 1);
					else
						predictChunkTagMap.put(chunk, 1L);
					
					tempPreChunk = new ArrayList<>();
				}
				
				if(preChunkTag.equals("O")) {//当前词的组块预测标记为O
					if(predictChunkTagMap.containsKey(preChunkTag))
						predictChunkTagMap.put(preChunkTag, predictChunkTagMap.get(preChunkTag) + 1);
					else
						predictChunkTagMap.put(preChunkTag, 1L);
				}else//当前词的组块预测标记为*_B
					tempPreChunk.add(preChunkTag);
			}else//当前词的组块预测标记为*_I || *_E
				tempPreChunk.add(preChunkTag);
		}
		
		if(tempRefChunk.size() != 0) //存在未处理的参考组块, 进行统计
			processChunk(tempRefChunk, correctPreChunk, wordsInChunk);
	
		if(tempPreChunk.size() != 0) {//存在未处理的预测组块, 进行统计
			String chunk = tempPreChunk.get(0).split("_")[0];
			if(predictChunkTagMap.containsKey(chunk))
				predictChunkTagMap.put(chunk, predictChunkTagMap.get(chunk) + 1);
			else
				predictChunkTagMap.put(chunk, 1L);
		}
	
	}
	
	/**
	 * 动态统计预测样本与标准样本
	 * @param reference	标准样本
	 * @param prediction预测样本
	 */
	public void add(ChunkAnalysisSample reference, ChunkAnalysisSample prediction) {
		String[] words = reference.getWords();				//每个样本中的词组
		String[] refChunkTags = reference.getChunkTags();	//参考样本中每个词的组块标记
		String[] preChunkTags = prediction.getChunkTags();	//预测样本中每个词的组块标记
		update(words, refChunkTags, preChunkTags);
	}
	
	/**
	 * 统计未处理的参考组块
	 * @param tempRefChunk		参考组块
	 * @param correctPreChunk	对应位置的预测结果
	 * @param wordsInChunk		组块对应的词
	 */
	private void processChunk(List<String> tempRefChunk, List<String> correctPreChunk, List<String> wordsInChunk) {
		String chunk = tempRefChunk.get(0).split("_")[0];
		if(referenceChunkTagMap.containsKey(chunk))
			referenceChunkTagMap.put(chunk, referenceChunkTagMap.get(chunk) + 1);
		else
			referenceChunkTagMap.put(chunk, 1L);
		
		if(tempRefChunk.equals(correctPreChunk)) {//未处理的组块被预测正确，进行统计
			if(correctTaggedChunkTagMap.containsKey(chunk))
				correctTaggedChunkTagMap.put(chunk, correctTaggedChunkTagMap.get(chunk) + 1);
			else
				correctTaggedChunkTagMap.put(chunk, 1L);
		
			for(String word : wordsInChunk) {//遍历被正确预测的组块的所有词，统计未登录词
				if(!wordDict.contains(word))
					correctTaggedOOVs++;
			}
			
			correctTaggedWordCounts += wordsInChunk.size();
		}
	}
	
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
						  "RefO=" + referenceChunkTagMap.get("O")+"\t"+"PreO="+predictChunkTagMap.get("O")+"\tcorrect="+correctTaggedChunkTagMap.get("O")+"\n"+
						  "RefBNP=" + referenceChunkTagMap.get("BNP")+"\tPreBNP="+predictChunkTagMap.get("BNP")+"\tcorrect="+correctTaggedChunkTagMap.get("BNP")+"\n"+
						  "RefBAP=" + referenceChunkTagMap.get("BAP")+"\tPreBAP="+predictChunkTagMap.get("BAP")+"\tcorrect="+correctTaggedChunkTagMap.get("BAP")+"\n"+
						  "RefBVP=" + referenceChunkTagMap.get("BVP")+"\tPreBVP="+predictChunkTagMap.get("BVP")+"\tcorrect="+correctTaggedChunkTagMap.get("BVP")+"\n"+
						  "RefBDP=" + referenceChunkTagMap.get("BDP")+"\tPreBDP="+predictChunkTagMap.get("BDP")+"\tcorrect="+correctTaggedChunkTagMap.get("BDP")+"\n"+
						  "RefBQP=" + referenceChunkTagMap.get("BQP")+"\tPreBQP="+predictChunkTagMap.get("BQP")+"\tcorrect="+correctTaggedChunkTagMap.get("BQP")+"\n"+
						  "RefBTP=" + referenceChunkTagMap.get("BTP")+"\tPreBTP="+predictChunkTagMap.get("BTP")+"\tcorrect="+correctTaggedChunkTagMap.get("BTP")+"\n"+
						  "RefBFP=" + referenceChunkTagMap.get("BFP")+"\tPreBFP="+predictChunkTagMap.get("BFP")+"\tcorrect="+correctTaggedChunkTagMap.get("BFP")+"\n"+
						  "RefBNT=" + referenceChunkTagMap.get("BNT")+"\tPreBNT="+predictChunkTagMap.get("BNT")+"\tcorrect="+correctTaggedChunkTagMap.get("BNT")+"\n"+
						  "RefBNS=" + referenceChunkTagMap.get("BNS")+"\tPreBNS="+predictChunkTagMap.get("BNS")+"\tcorrect="+correctTaggedChunkTagMap.get("BNS")+"\n"+
						  "RefBNZ=" + referenceChunkTagMap.get("BNZ")+"\tPreBNZ="+predictChunkTagMap.get("BNZ")+"\tcorrect="+correctTaggedChunkTagMap.get("BNZ")+"\n"+
						  "RefBSV=" + referenceChunkTagMap.get("BSV")+"\tPreBSV="+predictChunkTagMap.get("BSV")+"\tcorrect="+correctTaggedChunkTagMap.get("BSV");
	}
}
