package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import hust.tools.ca.model.Chunk;

/**
 *<ul>
 *<li>Description: 组块标注样本抽象类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月20日
 *</ul>
 */
public abstract class AbstractChunkAnalysisSample {
	
	/**
	 * 组块标记的位置标签类型，BIEOS/BIEO/BIO
	 */
	protected String label;
	
	/**
	 * 词或字序列
	 */
	protected List<String> tokens;

	/**
	 * 组块标记序列，与tokens对应
	 */
	protected List<String> tags;
	
	/**
	 * 其他上下文信息
	 */
	protected Object[] additionalContext;

	/**
	 * 构造方法
	 * @param tokens	词或字数组
	 * @param tags		组块标记数组
	 */
	public AbstractChunkAnalysisSample(String[] tokens, String[] tags){
		this(tokens, tags, null);
	}

	/**
	 * 构造方法
	 * @param tokens	词或字序列
	 * @param tags		组块标记序列
	 */
	public AbstractChunkAnalysisSample(List<String> tokens, List<String> tags){
		this(tokens, tags, null);
	}

	/**
	 * 构造方法
	 * @param tokens			词或字数组
	 * @param tags				组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public AbstractChunkAnalysisSample(String[] tokens, String[] tags, Object[] additionalContext){
		this(Arrays.asList(tokens), Arrays.asList(tags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词或字序列
	 * @param chunkTags			组块标记序列
	 * @param additionalContext	其他上下文信息
	 */
    public AbstractChunkAnalysisSample(List<String> tokens, List<String> tags, Object[] additionalContext){
    	this.tags = Collections.unmodifiableList(tags);
        this.tokens = Collections.unmodifiableList(tokens);

        Object[] ac;
        if (additionalContext != null) {
            ac = new Object[additionalContext.length];

            for (int i = 0; i < additionalContext.length; i++) {
            	ac[i] = additionalContext[i];
            }
        } else {
            ac = null;
        }
        this.additionalContext = ac;
	}
	
    public void setLabel(String label) {
		this.label = label;
	}
    
    public String getLabel() {
		return label;
	}
    
	/**
     * 返回样本词语数组
     * @return 样本词语数组
     */
    public String[] getTokens(){
    	return this.tokens.toArray(new String[tokens.size()]);
    }
    
    /**
     * 返回样本组块标记数组
     * @return 样本组块标记数组
     */
    public String[] getTags(){
    	return tags.toArray(new String[tags.size()]);
    }
    
    /**
     * 返回样本其他上下文信息
     * @return 样本其他上下文信息
     */
    public Object[] getAditionalContext(){
    	return additionalContext;
    }
    
    /**
     * 返回语句中的所有组块
     * @return	组块
     */
	public abstract Chunk[] toChunk();
	
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(additionalContext);
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
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
		if (!Arrays.equals(additionalContext, other.additionalContext))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		return true;
	}

	@Override
	public abstract String toString();
}
