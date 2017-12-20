package hust.tools.ca.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *<ul>
 *<li>Description: 基于词的组块分析样本 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月20日
 *</ul>
 */
public class ChunkAnalysisBasedWordSample {

	/**
	 * 组块标记列表
	 */
	protected List<String> chunkTags;
	
	/**
	 * 词语列表
	 */
	protected List<String> words;
	
	/**
	 * 其他上下文信息
	 */
	protected String[][] additionalContext;

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
    	this.chunkTags = Collections.unmodifiableList(chunkTags);
        this.words = Collections.unmodifiableList(words);

        String[][] ac;
        if (additionalContext != null) {
            ac = new String[additionalContext.length][];

            for (int i = 0; i < additionalContext.length; i++) {
                ac[i] = new String[additionalContext[i].length];
                System.arraycopy(additionalContext[i], 0, ac[i], 0,
                        additionalContext[i].length);
            }
        } else {
            ac = null;
        }
        this.additionalContext = ac;
	}
	
	/**
     * 返回样本词语数组
     * @return 样本词语数组
     */
    public String[] getWords(){
    	return this.words.toArray(new String[words.size()]);
    }
    
    /**
     * 返回样本组块标记数组
     * @return 样本组块标记数组
     */
    public String[] getChunkTags(){
    	return chunkTags.toArray(new String[chunkTags.size()]);
    }
    
    /**
     * 返回样本其他上下文信息
     * @return 样本其他上下文信息
     */
    public String[][] getAditionalContext(){
    	return additionalContext;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(additionalContext);
		result = prime * result + ((chunkTags == null) ? 0 : chunkTags.hashCode());
		result = prime * result + ((words == null) ? 0 : words.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkAnalysisBasedWordSample other = (ChunkAnalysisBasedWordSample) obj;
		if (!Arrays.deepEquals(additionalContext, other.additionalContext))
			return false;
		if (chunkTags == null) {
			if (other.chunkTags != null)
				return false;
		} else if (!chunkTags.equals(other.chunkTags))
			return false;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}
	
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
