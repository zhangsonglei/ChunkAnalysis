package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
	protected String[][] additionalContext;

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
	public AbstractChunkAnalysisSample(String[] tokens, String[] tags, String[][] additionalContext){
		this(Arrays.asList(tokens), Arrays.asList(tags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词或字序列
	 * @param chunkTags			组块标记序列
	 * @param additionalContext	其他上下文信息
	 */
    public AbstractChunkAnalysisSample(List<String> tokens, List<String> tags, String[][] additionalContext){
    	this.tags = Collections.unmodifiableList(tags);
        this.tokens = Collections.unmodifiableList(tokens);

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
    public String[][] getAditionalContext(){
    	return additionalContext;
    }
	
//	@Override
//	public abstract String toString();
}
