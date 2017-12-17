package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class AbstractChunkAnalysisSample {

	protected List<String> chunkTags;			//组块标记列表
	protected List<String> words;				//词语列表
	protected List<String> poses;				//词性列表
	protected String[][] additionalContext;		//其他上下文信息

	/**
	 * 构造方法
	 * @param words				词语列表
	 * @param poses				词语对应的词性列表
	 * @param chunkTags			词语组块标记列表
	 * @param additionalContext	其他上下文信息
	 */
    public AbstractChunkAnalysisSample(List<String> words, List<String> poses, List<String> chunkTags, String[][] additionalContext){
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
    	if(poses.size() != 0)
    		return this.poses.toArray(new String[poses.size()]);
    	
    	return null;
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
		result = prime * result + ((poses == null) ? 0 : poses.hashCode());
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
		AbstractChunkAnalysisSample other = (AbstractChunkAnalysisSample) obj;
		if (!Arrays.deepEquals(additionalContext, other.additionalContext))
			return false;
		if (chunkTags == null) {
			if (other.chunkTags != null)
				return false;
		} else if (!chunkTags.equals(other.chunkTags))
			return false;
		if (poses == null) {
			if (other.poses != null)
				return false;
		} else if (!poses.equals(other.poses))
			return false;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		return true;
	}
}
