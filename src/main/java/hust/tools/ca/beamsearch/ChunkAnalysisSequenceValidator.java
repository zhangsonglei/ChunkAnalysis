package hust.tools.ca.beamsearch;

/**
 *<ul>
 *<li>Description: 验证输出序列是否合法的接口 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisSequenceValidator<T> {

	/**
	 * 验证序列是否正确
	 * @param index		当前词语下标
	 * @param words		词语数组
	 * @param poses 	词组对应的词性数组
	 * @param chunkTags	组块标记数组
	 * @param out 		当前词的组块标记
	 */
	boolean validSequence(int index, T[] words, T[] poses, String[] chunkTags, String out);
}
