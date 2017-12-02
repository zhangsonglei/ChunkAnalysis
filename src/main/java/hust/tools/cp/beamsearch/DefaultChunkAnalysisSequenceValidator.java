package hust.tools.cp.beamsearch;

/**
 * 验证实现类
 * @author 王馨苇
 *
 */
public class DefaultChunkAnalysisSequenceValidator implements ChunkAnalysisSequenceValidator<String> {

	@Override
	public boolean validSequence(int index, String[] words, String[] poses, String[] chunkTags, String out) {
		return false;
	}
}
