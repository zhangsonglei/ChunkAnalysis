package hust.tools.ca.feature;

/**
 *<ul>
 *<li>Description: 生成特征的接口
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public interface ChunkAnalysisContextGenerator extends BeamSearchChunkAnalysisContextGenerator<String>{

	@Override
	String[] getContext(int index, String[] words, String[] poses, String[] chunkTags, Object[] additionalContext);
}
