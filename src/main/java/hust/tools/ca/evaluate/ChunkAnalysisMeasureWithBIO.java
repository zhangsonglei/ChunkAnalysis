package hust.tools.ca.evaluate;

import java.util.ArrayList;
import java.util.List;
import hust.tools.ca.utils.Dictionary;

/**
 *<ul>
 *<li>Description: 组块分析模型评价 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月2日
 *</ul>
 */
public class ChunkAnalysisMeasureWithBIO extends AbstractChunkAnalysisMeasure {

	public ChunkAnalysisMeasureWithBIO() {
		this(new Dictionary());
	}
	
	public ChunkAnalysisMeasureWithBIO(Dictionary wordDict) {
		super(wordDict);
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
		
		for(int i = 0; i < words.length; i++) {//遍历样本中的每个词,统计样本中每类组块标记标准数量与预测数量
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
	
	
}
