package hust.tools.ca.feature;

/**
 *<ul>
 *<li>Description: 生成特征的接口 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface BeamSearchChunkAnalysisContextGenerator<T> {

	/**
	 * 特征生成方法
	 * @param index				当前位置
	 * @param words 			词语数组
 	 * @param poses 			词性数组
	 * @param chunkTags			组块标记数组
	 * @param additionalContext	其他上下文信息
	 * @return
	 */
	String[] getContext(int index, T[] words, T[] poses, String[] chunkTags, Object[] additionalContext);
}
