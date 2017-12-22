package hust.tools.ca.parse;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;

/**
 *<ul>
 *<li>Description: 样本解析抽象类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月22日
 *</ul>
 */
public abstract class AbstractChunkAnalysisParse {

	/**
	 * 返回由字符串句子解析而成的样本
	 * @return	样本
	 */
	public abstract AbstractChunkAnalysisSample parse(String sentence);
}
