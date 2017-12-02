package hust.tools.cp.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 样本类
 * @author 王馨苇
 *
 */
public class ChunkAnalysisSample {
	public List<String> chunkTags;			//组块标记列表
	public List<String> words;				//词语列表
	public List<String> poses;				//词性列表
	private String[][] additionalContext;	//其他上下文信息
	
	/**
	 * 构造
	 * @param words 词语
	 * @param poses 词性
	 * @param tags 实体标记
	 */
	public ChunkAnalysisSample(String[] words,String[] poses, String[] tags){
		this(words,poses,tags,null);
	}
	
	/**
	 * 构造
	 * @param words 词语
	 * @param poses 词性
	 * @param tags 实体标记
	 */
	public ChunkAnalysisSample(List<String> words,List<String> poses,List<String> tags){
		this(words,poses,tags,null);
	}
	
	/**
	 * 构造
	 * @param words 词语
	 * @param poses 词性
	 * @param tags 实体标记
	 * @param additionalContext 额外的信息
	 */
	public ChunkAnalysisSample(String[] words,String[] poses, String[] tags,String[][] additionalContext){
		this(Arrays.asList(words),Arrays.asList(poses),Arrays.asList(tags),additionalContext);
	}
	
	/**
	 * 构造
	 * @param words 词语
	 * @param poses 词性
	 * @param tags 实体标记
	 * @param additionalContext 额外的信息
	 */
    public ChunkAnalysisSample(List<String> words,List<String> poses,List<String> chunkTags,String[][] additionalContext){
    	
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
     * 获得词语
     * @return 
     */
    public String[] getWords(){
    	return this.words.toArray(new String[words.size()]);
    }
   
    /**
     * 获得词性
     * @return
     */
    public String[] getPoses(){
    	
    	return this.poses.toArray(new String[poses.size()]);
    }
    
    /**
     * 获得实体标记
     * @return
     */
    public String[] getTags(){
    	return chunkTags.toArray(new String[chunkTags.size()]);
    }
    /**
     * 获得额外的信息
     * @return
     */
    public String[][] getAditionalContext(){
    	return this.additionalContext;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
            return true;
        } else if (obj instanceof ChunkAnalysisSample) {
        	ChunkAnalysisSample a = (ChunkAnalysisSample) obj;

            return Arrays.equals(getWords(), a.getWords())
                    && Arrays.equals(getTags(), a.getTags());
        } else {
            return false;
        }
	}
}

