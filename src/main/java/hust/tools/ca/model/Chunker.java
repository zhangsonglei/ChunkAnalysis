package hust.tools.ca.model;

/**
 *<ul>
 *<li>Description: 组块分析器
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月17日
 *</ul>
 */
public interface Chunker {
	
	/**
	 * 返回给定句子的组块信息
	 * @param sentence	待返回组块信息的句子
	 * @return			句子的组块信息
	 */
	public Chunk[] parse(String sentence);
	
	/**
	 * 返回给定句子的最优的k个组块信息
	 * @param sentence	待返回组块信息的句子
	 * @param k			返回组块信息的候选个数
	 * @return			最优的k个组块信息
	 */
	public Chunk[][] parse(String sentence, int k);
}
