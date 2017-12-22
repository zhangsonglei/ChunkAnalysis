package hust.tools.ca.beamsearch;

import hust.tools.ca.feature.BeamSearchChunkAnalysisBasedWordAndPOSContextGenerator;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;

/**
 *<ul>
 *<li>Description: 得到组块标注序列的接口 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisSequenceClassificationModel<T> {
	
	/**
	 * 得到最好的序列
	 * @param tokens 			词或字组
	 * @param poses 			词性数组
	 * @param additionalContext	其他上下文信息
	 * @param contextGenerator	上下文生成器
	 * @param validator			序列的验证器
	 * @return 					最好的序列
	 */
	Sequence bestSequence(T[] tokens, T[] poses, Object[] additionalContext, 
			BeamSearchChunkAnalysisBasedWordAndPOSContextGenerator<T> contextGenerator, SequenceValidator<T> validator);

	/**
	 * 返回最优的numSequences个组块标注序列
	 * @param numSequences		返回的序列数量
	 * @param tokens			词或字组
	 * @param poses				词组对应的词性数组
	 * @param additionalContext 其他上下文信息
	 * @param minSequenceScore 	标记所得最小的分数的限制
	 * @param contextGenerator 	上下文生成器
	 * @param validator 		序列的验证器
	 * @return					最优的numSequences个组块标注序列
	 */
	public Sequence[] bestSequences(int numSequences, T[] tokens, T[] poses, Object[] additionalContext, double minSequenceScore,
			BeamSearchChunkAnalysisBasedWordAndPOSContextGenerator<T> contextGenerator, SequenceValidator<T> validator);

	/**
	 * 返回最优的numSequences个组块标注序列
	 * @param numSequences 		返回的序列数量
	 * @param tokens 			词或字组
	 * @param poses 			词组对应的词性数组
	 * @param additionalContext	其他上下文信息
	 * @param contextGenerator 	上下文生成器
	 * @param validator			序列的验证器
	 * @return					最优的numSequences个组块标注序列
	 */
	public Sequence[] bestSequences(int numSequences, T[] tokens, T[] poses, Object[] additionalContext, 
			BeamSearchChunkAnalysisBasedWordAndPOSContextGenerator<T> contextGenerator, SequenceValidator<T> validator);
	
	/**
	 * 得到最好的组块标注结果
	 * @return	最好的组块标注结果
	 */
	String[] getOutcomes();
}

