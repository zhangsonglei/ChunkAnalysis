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
public class ChunkAnalysisBasedWordSample  extends AbstractChunkAnalysisSample {
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param chunkTags	词语组块标记数组
	 */
	public ChunkAnalysisBasedWordSample(String[] words, String[] chunkTags){
		this(words, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语列表
	 * @param chunkTags	词语组块标记列表
	 */
	public ChunkAnalysisBasedWordSample(List<String> words, List<String> chunkTags){
		this(words, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param chunkTags			词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisBasedWordSample(String[] words, String[] chunkTags, String[][] additionalContext){
		this(Arrays.asList(words), Arrays.asList(chunkTags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语列表
	 * @param chunkTags			词语组块标记列表
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisBasedWordSample(List<String> words, List<String> chunkTags, String[][] additionalContext){
    	super(words, new ArrayList<>(), chunkTags, additionalContext);
	}
    
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj) {
//            return true;
//        } else if (obj instanceof ChunkAnalysisBasedWordSample) {
//        	ChunkAnalysisBasedWordSample a = (ChunkAnalysisBasedWordSample) obj;
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
		List<String> words = new ArrayList<>();
		String chunk = null;
		
		for(int i = 0; i < words.size(); i++) {
			if(chunkTags.get(i).equals("O")) {
				if(words.size() != 0) {
					res += "[";
					for(String word : words)
						res +=  word + "  ";
					
					res += res.trim() + "]" + chunk + "  ";
					
					words = new ArrayList<>();
					chunk = null;
				}
				
				res += words.get(i) + "  ";
			}else {
				if(chunkTags.get(i).split("_")[1].equals("B")) {
					if(words.size() != 0) {
						res += "[";
						for(String word : words)
							res += word + "  ";
						
						res += res.trim() + "]" + chunk + "  ";
						
						words = new ArrayList<>();
						chunk = null;
					}
					
					words.add(words.get(i));
					chunk =  chunkTags.get(i).split("_")[0];
				}else
					words.add(words.get(i));				
			}
		}
		
		if(words.size() != 0) {
			res += "[";
			for(String word : words)
				res +=  word + "  ";
			
			res += res.trim() + "]" + chunk + "  ";
		}
		
		return res.trim();
	}
}

