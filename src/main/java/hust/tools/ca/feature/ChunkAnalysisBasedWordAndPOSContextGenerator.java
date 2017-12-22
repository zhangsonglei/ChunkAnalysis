package hust.tools.ca.feature;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析模型生成特征的接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisBasedWordAndPOSContextGenerator extends BeamSearchChunkAnalysisBasedWordAndPOSContextGenerator<String>{

	@Override
	String[] getContext(int index, String[] words, String[] poses, String[] chunkTags, Object[] additionalContext);
}
