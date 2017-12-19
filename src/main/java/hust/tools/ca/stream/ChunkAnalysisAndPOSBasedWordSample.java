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
public class ChunkAnalysisAndPOSBasedWordSample  extends AbstractChunkAnalysisSample {
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param tags		词语组块标记数组
	 */
	public ChunkAnalysisAndPOSBasedWordSample(String[] words, String[] tags){
		this(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语列表
	 * @param tags		词语组块标记列表
	 */
	public ChunkAnalysisAndPOSBasedWordSample(List<String> words, List<String> tags){
		this(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param tags				词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisAndPOSBasedWordSample(String[] words, String[] tags, String[][] additionalContext){
		this(Arrays.asList(words), Arrays.asList(tags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语列表
	 * @param tags				词语组块标记列表
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisAndPOSBasedWordSample(List<String> words, List<String> tags, String[][] additionalContext){
    	super(words, new ArrayList<>(), tags, additionalContext);
	}
	
	@Override
	public String toString() {
		String res = "";
		List<String> wordTags = new ArrayList<>();
		String chunk = null;
		for(int i = 0; i < words.size(); i++) {
			String pos = chunkTags.get(i).split("-")[0];
			String chunkTag = chunkTags.get(i).split("-")[1];
					
			if(chunkTag.equals("O")) {
				if(wordTags.size() != 0) {
					res += "[";
					for(String wordTag : wordTags)
						res +=  wordTag + "  ";
					
					res += res.trim() + "]" + chunk + "  ";
					
					words = new ArrayList<>();
					chunk = null;
				}
				
				res += words.get(i) + "  ";
			}else {
				if(chunkTag.split("_")[1].equals("B")) {
					if(wordTags.size() != 0) {
						res += "[";
						for(String wordTag : wordTags)
							res += wordTag + "  ";
						
						res += res.trim() + "]" + chunk + "  ";
						
						words = new ArrayList<>();
						chunk = null;
					}
					
					words.add(words.get(i));
					chunk =  chunkTag.split("_")[0];
				}else
					words.add(words.get(i) + "/" + pos);				
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

