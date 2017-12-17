package hust.tools.ca.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *<ul>
 *<li>Description: 样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSample extends AbstractChunkAnalysisSample {
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param poses		词语对应的词性数组
	 * @param chunkTags	词语组块标记数组
	 */
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags){
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语列表
	 * @param poses		词语对应的词性列表
	 * @param chunkTags	词语组块标记列表
	 */
	public ChunkAnalysisBasedWordAndPOSSample(List<String> words,List<String> poses,List<String> chunkTags){
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param poses				词语对应的词性数组
	 * @param chunkTags			词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags, String[][] additionalContext){
		this(Arrays.asList(words), Arrays.asList(poses), Arrays.asList(chunkTags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语列表
	 * @param poses				词语对应的词性列表
	 * @param chunkTags			词语组块标记列表
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisBasedWordAndPOSSample(List<String> words, List<String> poses, List<String> chunkTags, String[][] additionalContext){
    	super(words, poses, chunkTags, additionalContext);
	}

//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//            return true;
//        } else if (obj instanceof ChunkAnalysisBasedWordAndPOSSample) {
//        	ChunkAnalysisBasedWordAndPOSSample a = (ChunkAnalysisBasedWordAndPOSSample) obj;
//
//            return Arrays.equals(getWords(), a.getWords())
//                    && Arrays.equals(getChunkTags(), a.getChunkTags());
//        } else {
//            return false;
//        }
//	}
	
	@Override
	public String toString() {
		String res = "";
		List<String> wordTags = new ArrayList<>();
		String chunk = null;
		
		for(int i = 0; i < words.size(); i++) {
			if(chunkTags.get(i).equals("O")) {
				if(wordTags.size() != 0) {
					res += "[";
					for(String wordTag : wordTags)
						res +=  wordTag + "  ";
					
					res += res.trim() + "]" + chunk + "  ";
					
					wordTags = new ArrayList<>();
					chunk = null;
				}
				
				res += words.get(i)+ "/" + poses.get(i) + "  ";
			}else {
				if(chunkTags.get(i).split("_")[1].equals("B")) {
					if(wordTags.size() != 0) {
						res += "[";
						for(String wordTag : wordTags)
							res += wordTag + "  ";
						
						res += res.trim() + "]" + chunk + "  ";
						
						wordTags = new ArrayList<>();
						chunk = null;
					}
					
					wordTags.add(words.get(i) + "/" + poses.get(i));
					chunk =  chunkTags.get(i).split("_")[0];
				}else
					wordTags.add(words.get(i) + "/" + poses.get(i));				
			}
		}
		
		if(wordTags.size() != 0) {
			res += "[";
			for(String wordTag : wordTags)
				res +=  wordTag + "  ";
			
			res += res.trim() + "]" + chunk + "  ";
		}
		
		return res.trim();
	}
}

