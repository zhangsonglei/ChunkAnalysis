package hust.tools.ca.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hust.tools.ca.model.Chunk;

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
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags) {
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words		词语序列
	 * @param poses		词语对应的词性序列
	 * @param chunkTags	词语组块标记序列
	 */
	public ChunkAnalysisBasedWordAndPOSSample(List<String> words,List<String> poses,List<String> chunkTags) {
		this(words, poses, chunkTags, null);
	}

	/**
	 * 构造方法
	 * @param words				词语数组
	 * @param poses				词语对应的词性数组
	 * @param chunkTags			词语组块标记数组
	 * @param additionalContext	其他上下文信息
	 */
	public ChunkAnalysisBasedWordAndPOSSample(String[] words,String[] poses, String[] chunkTags, String[][] additionalContext) {
		this(Arrays.asList(words), Arrays.asList(poses), Arrays.asList(chunkTags), additionalContext);
	}

	/**
	 * 构造方法
	 * @param words				词语序列
	 * @param poses				词语对应的词性序列
	 * @param chunkTags			词语组块标记序列
	 * @param additionalContext	其他上下文信息
	 */
    public ChunkAnalysisBasedWordAndPOSSample(List<String> words, List<String> poses, List<String> chunkTags, String[][] additionalContext) {
    	super(words, chunkTags, additionalContext);
    	this.poses = poses;
	}
    
    /**
     * 返回样本词性数组
     * @return 样本词性数组
     */
    public String[] getPoses() {
    	if(poses.size() != 0)
    		return this.poses.toArray(new String[poses.size()]);
    	
    	return null;
    }
	
	public Chunk[] toChunk() { 
    	if(label.equals("BIEOS"))
    		return toChunkFromBIEOS();
    	else if(label.equals("BIEO"))
    		return toChunkFromBIEO();
    	else
    		return toChunkFromBIO();
	}

	private Chunk[] toChunkFromBIEOS() {
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;
		
		for(int i = 0; i < tags.size(); i++) {
			if(tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B")) {
				if(isChunk) 
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));
				
				isChunk = false;
				if(!tags.get(i).equals("O")) {
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			}else if(tags.get(i).split("_")[1].equals("S")) {
				if(isChunk) 
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));
				
				type = tags.get(i).split("_")[0];
				chunks.add(new Chunk(type, tokens.get(i) + "/" + poses.get(i), i, i - 1));
				isChunk = false;
			} 
		}
		
		if(isChunk)
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));
		
		return chunks.toArray(new Chunk[chunks.size()]);
	}
	
	private Chunk[] toChunkFromBIEO() {
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;
		
		for(int i = 0; i < tags.size(); i++) {
			if(tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B")) {
				if(isChunk) 
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));
				
				isChunk = false;
				if(!tags.get(i).equals("O")) {
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			} 
		}
		
		if(isChunk) 
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));
		
		return chunks.toArray(new Chunk[chunks.size()]);
	}
	
	private Chunk[] toChunkFromBIO() {
		List<Chunk> chunks = new ArrayList<>();
		int start = 0;
		boolean isChunk = false;
		String type = null;
		
		for(int i = 0; i < tags.size(); i++) {
			if(tags.get(i).equals("O") || tags.get(i).split("_")[1].equals("B")) {
				if(isChunk) 
					chunks.add(new Chunk(type, join(tokens, poses, start, i), start, i - 1));
				
				isChunk = false;
				if(!tags.get(i).equals("O")) {
					start = i;
					isChunk = true;
					type = tags.get(i).split("_")[0];
				}
			} 
		}
		
		if(isChunk) 
			chunks.add(new Chunk(type, join(tokens, poses, start, tags.size()), start, tags.size() - 1));
		
		return chunks.toArray(new Chunk[chunks.size()]);
	}
	
	/**
	 * 拼接字符串word/pos  word/pos  ...
	 * @param words	带拼接的词组
	 * @param poses	词组对应的词性
	 * @param start	拼接的开始位置
	 * @param end	拼接的结束位置
	 * @return		拼接后的字符串
	 */
	private List<String> join(List<String> words, List<String> poses, int start, int end) {
		List<String> string = new ArrayList<>();
		for(int i = start; i < end; i++) 
			string.add(words.get(i) + "/" + poses.get(i));
		
		return string;
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
	
	@Override
	public String toString() {
		String string = "";
		
		Chunk[] chunks = toChunk();
		int j = 0; 
		for(int i = 0; i < tokens.size(); i++) {
			if(j < chunks.length && i >= chunks[j].getStart() && i <= chunks[j].getEnd()) {
				if(i == chunks[j].getEnd()) 
					string += chunks[j++] + "  ";
			}else 
				string += tokens.get(i) + "/" + poses.get(i) + "  ";			
		}

		return string.trim();
	}
}

