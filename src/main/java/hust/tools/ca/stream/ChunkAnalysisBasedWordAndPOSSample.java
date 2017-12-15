package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *<ul>
 *<li>Description: 样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSample {
	
	public List<String> chunkTags;			//组块标记列表
	public List<String> words;				//词语列表
	public List<String> poses;				//词性列表
	private String[][] additionalContext;	//其他上下文信息
	
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
    	this.chunkTags = Collections.unmodifiableList(chunkTags);
        this.words = Collections.unmodifiableList(words);
        this.poses = Collections.unmodifiableList(poses);
        

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
     * 返回样本词性数组
     * @return 样本词性数组
     */
    public String[] getPoses(){
    	
    	return this.poses.toArray(new String[poses.size()]);
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
    	return this.additionalContext;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        } else if (obj instanceof ChunkAnalysisBasedWordAndPOSSample) {
        	ChunkAnalysisBasedWordAndPOSSample a = (ChunkAnalysisBasedWordAndPOSSample) obj;

            return Arrays.equals(getWords(), a.getWords())
                    && Arrays.equals(getChunkTags(), a.getChunkTags());
        } else {
            return false;
        }
	}
	
	@Override
	public String toString() {
		String res = "";
		
		for(int i = 0; i < words.size(); i++) {
			res += "[" + words.get(i)+ "/" + poses.get(i) + "]" + chunkTags.get(i) + " ";
		}
		
		return res.trim();
	}
}

