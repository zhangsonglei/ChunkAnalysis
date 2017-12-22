package hust.tools.ca.stream;

import java.util.Arrays;
import java.util.List;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析样本类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSample extends AbstractChunkAnalysisSample {
	
	/**
	 * 词组对应的词性
	 */
	private List<String> poses;
	
	/**
	 * 构造方法
	 * @param words		词语数组
	 * @param poses		词语对应的词性数组
	 * @param chunkTags	词语组块标记数组
	 */
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags){
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语序列
	 * @param poses		词语对应的词性序列
	 * @param chunkTags	词语组块标记序列
	 */
	public ChunkAnalysisBasedWordAndPOSSample(List<String> words,List<String> poses,List<String> chunkTags){
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param poses				词语对应的词性数组
	 * @param chunkTags			词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags, String[][] additionalContext){
		this(Arrays.asList(words), Arrays.asList(poses), Arrays.asList(chunkTags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语序列
	 * @param poses				词语对应的词性序列
	 * @param chunkTags			词语组块标记序列
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisBasedWordAndPOSSample(List<String> words, List<String> poses, List<String> chunkTags, String[][] additionalContext){
    	super(words, chunkTags, additionalContext);
    	this.poses = poses;
	}
    
    /**
     * 返回样本词性数组
     * @return 样本词性数组
     */
    public String[] getPoses(){
    	if(poses.size() != 0)
    		return this.poses.toArray(new String[poses.size()]);
    	
    	return null;
    }
	
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(additionalContext);
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((tokens == null) ? 0 : tokens.hashCode());
		result = prime * result + ((poses == null) ? 0 : poses.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChunkAnalysisBasedWordAndPOSSample other = (ChunkAnalysisBasedWordAndPOSSample) obj;
		if (!Arrays.deepEquals(additionalContext, other.additionalContext))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (tokens == null) {
			if (other.tokens != null)
				return false;
		} else if (!tokens.equals(other.tokens))
			return false;
		if (poses == null) {
			if (other.poses != null)
				return false;
		} else if (!poses.equals(other.poses))
			return false;
		return true;
	}

//	@Override
//	public String toString() {
//		String res = "";
//		List<String> wordTags = new ArrayList<>();
//		String chunk = null;
//		
//		for(int i = 0; i < tokens.size(); i++) {
//			if(tags.get(i).equals("O")) {
//				if(wordTags.size() != 0) {
//					res += "[";
//					for(String wordTag : wordTags)
//						res +=  wordTag + "  ";
//					
//					res += res.trim() + "]" + chunk + "  ";
//					
//					wordTags = new ArrayList<>();
//					chunk = null;
//				}
//				
//				res += tokens.get(i)+ "/" + poses.get(i) + "  ";
//			}else {
//				if(tags.get(i).split("_")[1].equals("B")) {
//					if(wordTags.size() != 0) {
//						res += "[";
//						for(String wordTag : wordTags)
//							res += wordTag + "  ";
//						
//						res += res.trim() + "]" + chunk + "  ";
//						
//						wordTags = new ArrayList<>();
//						chunk = null;
//					}
//					
//					wordTags.add(tokens.get(i) + "/" + poses.get(i));
//					chunk =  tags.get(i).split("_")[0];
//				}else
//					wordTags.add(tokens.get(i) + "/" + poses.get(i));				
//			}
//		}
//		
//		if(wordTags.size() != 0) {
//			res += "[";
//			for(String wordTag : wordTags)
//				res +=  wordTag + "  ";
//			
//			res += res.trim() + "]" + chunk + "  ";
//		}
//		
//		return res.trim();
//	}
}

