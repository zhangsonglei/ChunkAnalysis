package hust.tools.ca.model;

import opennlp.tools.util.Sequence;

/**
 *<ul>
 *<li>Description: 组块分析模型接口 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月6日
 *</ul>
 */
public interface ChunkAnalysis {
	
	/**
	 * 返回词组的词性和组块标注结果
	 * @param words	词组
	 * @return		词组的词性和组块标注结果
	 */
	public String analysis(String[] words);
	
	/**
	 * 返回词组的组块标注结果
	 * @param words	词组
	 * @param poses	词组对应词性数组
	 * @return		词组的组块标注结果
	 */
	public String analysis(String[] words,String[] pos);
	
	/**
	 * 返回词组的组块标注结果
	 * @param words				词组
	 * @param pos				词组对应词性数组
	 * @param additionaContext	其他上下文信息
	 * @return					词组的组块标注结果
	 */
	public String analysis(String[] words,String[] pos, Object[] additionaContext);

	/**
	 * 根据给定词组，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @return		最优的K个标注序列
	 */
	public Sequence[] getTopKSequences(String[] words);
	
	/**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param poses	与词组对应的词性数组
	 * @return		最优的K个标注序列
	 */
	public Sequence[] getTopKSequences(String[] words,String[] poses);
	
	/**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param poses	与词组对应的词性数组
	 * @param additionaContext
	 * @return		最优的K个标注序列
	 */
	public Sequence[] getTopKSequences(String[] characters, String[] pos, Object[] additionaContext);
}
