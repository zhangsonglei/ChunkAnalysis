package hust.tools.ca.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hust.tools.ca.model.Chunk;

/**
 *<ul>
 *<li>Description: 基于词的组块分析样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordSample extends AbstractChunkAnalysisSample {
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param tags		词语组块标记数组
	 */
	public ChunkAnalysisBasedWordSample(String[] words, String[] tags){
		super(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语序列
	 * @param tags		词语组块标记序列
	 */
	public ChunkAnalysisBasedWordSample(List<String> words, List<String> tags){
		super(words, tags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param tags				词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisBasedWordSample(String[] words, String[] tags, String[][] additionalContext){
		super(Arrays.asList(words), Arrays.asList(tags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语序列
	 * @param tags				词语组块标记序列
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisBasedWordSample(List<String> words, List<String> tags, String[][] additionalContext){
    	super(words, tags, additionalContext);
	}
	
    @Override
	public Chunk[] toChunk() {

		return null;
	}
    
    @Override
	public String toString() {
		String res = "";
		List<String> words = new ArrayList<>();
		String chunk = null;
		
		for(int i = 0; i < tokens.size(); i++) {
			if(tags.get(i).equals("O")) {
				if(words.size() != 0) {
					res += "[";
					for(String word : words)
						res +=  word + "  ";
					
					res += res.trim() + "]" + chunk + "  ";
					
					words = new ArrayList<>();
					chunk = null;
				}
				
				res += tokens.get(i) + "  ";
			}else {
				if(tags.get(i).split("_")[1].equals("B")) {
					if(words.size() != 0) {
						res += "[";
						for(String word : words)
							res += word + "  ";
						
						res += res.trim() + "]" + chunk + "  ";
						
						words = new ArrayList<>();
						chunk = null;
					}
					
					words.add(tokens.get(i));
					chunk =  tags.get(i).split("_")[0];
				}else
					words.add(tokens.get(i));				
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

