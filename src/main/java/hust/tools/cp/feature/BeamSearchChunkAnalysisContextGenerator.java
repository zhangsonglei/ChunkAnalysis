package hust.tools.cp.feature;

/**
 * 生成特征的接口
 * @author 王馨苇
 *
 * @param <T>
 */
public interface BeamSearchChunkAnalysisContextGenerator<T> {

	/**
	 * 特征生成方法
	 * @param index				当前位置
	 * @param words 			词语序列
 	 * @param poses 			词性序列
	 * @param chunkTags			组块标记序列
	 * @param additionalContext	其他上下文信息
	 * @return
	 */
	String[] getContext(int index, T[] words, T[] poses, String[] chunkTags, Object[] additionalContext);
}