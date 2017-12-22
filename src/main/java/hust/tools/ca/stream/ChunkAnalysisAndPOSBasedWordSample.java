package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.List;

/**
 *<ul>
 *<li>Description: 基于词的词性标注和组块分析样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisAndPOSBasedWordSample extends AbstractChunkAnalysisSample {
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param tags		词语组块标记数组
	 */
	public ChunkAnalysisAndPOSBasedWordSample(String[] words, String[] tags){
		super(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语列表
	 * @param tags		词语组块标记列表
	 */
	public ChunkAnalysisAndPOSBasedWordSample(List<String> words, List<String> tags){
		super(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param tags				词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisAndPOSBasedWordSample(String[] words, String[] tags, String[][] additionalContext){
		super(Arrays.asList(words), Arrays.asList(tags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语列表
	 * @param tags				词语组块标记列表
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisAndPOSBasedWordSample(List<String> words, List<String> tags, String[][] additionalContext){
    	super(words, tags, additionalContext);
	}
	
//	@Override
//	public String toString() {
//		String res = "";
//		List<String> words = new ArrayList<>();
//		String chunk = null;
//		for(int i = 0; i < tokens.size(); i++) {
//			String pos = tags.get(i).split("-")[0];
//			String chunkTag = tags.get(i).split("-")[1];
//					
//			if(chunkTag.equals("O")) {
//				if(words.size() != 0) {
//					res += "[";
//					for(String word : words)
//						res +=  word + "  ";
//					
//					res += res.trim() + "]" + chunk + "  ";
//					
//					words = new ArrayList<>();
//					chunk = null;
//				}
//				
//				res += tokens.get(i) + "  ";
//			}else {
//				if(chunkTag.split("_")[1].equals("B")) {
//					if(words.size() != 0) {
//						res += "[";
//						for(String wordTag : words)
//							res += wordTag + "  ";
//						
//						res += res.trim() + "]" + chunk + "  ";
//						
//						words = new ArrayList<>();
//						chunk = null;
//					}
//					
//					words.add(tokens.get(i));
//					chunk =  chunkTag.split("_")[0];
//				}else
//					words.add(tokens.get(i) + "/" + pos);				
//			}
//		}
//		
//		if(words.size() != 0) {
//			res += "[";
//			for(String word : words)
//				res +=  word + "  ";
//			
//			res += res.trim() + "]" + chunk + "  ";
//		}
//		
//		return res.trim();
//	}
}

