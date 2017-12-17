package hust.tools.ca.parse;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;

public abstract class AbstractChunkAnalysisParse {

	/**
	 * 返回由字符串句子解析而成的样本
	 * @return	样本
	 */
	public abstract AbstractChunkAnalysisSample parse(String sentence);
}