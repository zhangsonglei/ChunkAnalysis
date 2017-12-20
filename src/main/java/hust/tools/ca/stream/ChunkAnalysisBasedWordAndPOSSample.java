package hust.tools.ca.stream;

import java.util.ArrayList;
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
public class ChunkAnalysisBasedWordAndPOSSample extends ChunkAnalysisBasedWordSample {
	
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
	 * @param words		词语列表
	 * @param poses		词语对应的词性列表
	 * @param chunkTags	词语组块标记列表
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
	 * @param words				词语列表
	 * @param poses				词语对应的词性列表
	 * @param chunkTags			词语组块标记列表
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
		result = prime * result + ((chunkTags == null) ? 0 : chunkTags.hashCode());
		result = prime * result + ((words == null) ? 0 : words.hashCode());
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
		if (chunkTags == null) {
			if (other.chunkTags != null)
				return false;
		} else if (!chunkTags.equals(other.chunkTags))
			return false;
		if (words == null) {
			if (other.words != null)
				return false;
		} else if (!words.equals(other.words))
			return false;
		if (poses == null) {
			if (other.poses != null)
				return false;
		} else if (!poses.equals(other.poses))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String res = "";
		List<String> wordTags = new ArrayList<>();
		String chunk = null;
		
		for(int i = 0; i < words.size(); i++) {
			if(chunkTags.get(i).equals("O")) {
				if(wordTags.size() != 0) {
					res += "[";
					for(String wordTag : wordTags)
						res +=  wordTag + "  ";
					
					res += res.trim() + "]" + chunk + "  ";
					
					wordTags = new ArrayList<>();
					chunk = null;
				}
				
				res += words.get(i)+ "/" + poses.get(i) + "  ";
			}else {
				if(chunkTags.get(i).split("_")[1].equals("B")) {
					if(wordTags.size() != 0) {
						res += "[";
						for(String wordTag : wordTags)
							res += wordTag + "  ";
						
						res += res.trim() + "]" + chunk + "  ";
						
						wordTags = new ArrayList<>();
						chunk = null;
					}
					
					wordTags.add(words.get(i) + "/" + poses.get(i));
					chunk =  chunkTags.get(i).split("_")[0];
				}else
					wordTags.add(words.get(i) + "/" + poses.get(i));				
			}
		}
		
		if(wordTags.size() != 0) {
			res += "[";
			for(String wordTag : wordTags)
				res +=  wordTag + "  ";
			
			res += res.trim() + "]" + chunk + "  ";
		}
		
		return res.trim();
	}
}

