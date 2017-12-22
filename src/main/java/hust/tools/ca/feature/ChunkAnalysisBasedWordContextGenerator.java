package hust.tools.ca.feature;

import opennlp.tools.util.BeamSearchContextGenerator;

/**
 *<ul>
 *<li>Description: 基于词的组块分析模型特征生成接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisBasedWordContextGenerator extends BeamSearchContextGenerator<String>{

	@Override
	String[] getContext(int index, String[] words, String[] chunkTags, Object[] additionalContext);
}
