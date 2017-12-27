package hust.tools.ca.parse;

import java.util.ArrayList;
import java.util.List;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;

/**
 *<ul>
 *<li>Description: 基于词和词性组块分析的BIEO样本解析（组块最小长度为2）
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSParseWithBIEO extends AbstractChunkAnalysisParse {
	
	private final String ChunkBegin = "_B";
	private final String InChunk = "_I";
	private final String OutChunk = "O";
	private final String ChunkEnd = "_E";
	
	private List<String> chunkTags;
	private List<String> words;
	private List<String> poses;
	
	/**
	 * 构造方法
	 */
	public ChunkAnalysisBasedWordAndPOSParseWithBIEO() {
		super();
	}
	
	@Override
	protected void setLabel() {
		this.label = "BIEO";
	}
	
	public AbstractChunkAnalysisSample parse(String sentence){
		chunkTags = new ArrayList<>();
		words = new ArrayList<>();
		poses = new ArrayList<>();
		
		boolean isInChunk = false;							//当前词是否在组块中
		List<String> wordTagsInChunk = new ArrayList<>();	//临时存储在组块中的词与词性
		String[] wordTag = null;							//词与词性标注
		String chunk = null;								//组块的标签
		String[] content = sentence.split("\\s+");
		
		for(String string : content) {
			if(isInChunk) {	//当前词在组块中
				if(string.contains("]")) {//当前词是组块的结束
					String[] strings = string.split("]");
					wordTagsInChunk.add(strings[0]);
					chunk = strings[1];
					isInChunk = false;
				}else 
					wordTagsInChunk.add(string);
			}else {//当前词不在组块中
				if(wordTagsInChunk.size() != 0 && chunk != null) {//上一个组块中的词未处理，先处理上一个组块中的词
					processChunk(wordTagsInChunk, chunk);
					
					wordTagsInChunk = new ArrayList<>();
					chunk = null;
				}	
				
				if(string.startsWith("[")) {
					wordTagsInChunk.add(string.replace("[", ""));
					isInChunk = true;
				}else {
					wordTag = string.split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(OutChunk);
				}
			}
		}
		
		//句子结尾是组块，进行解析
		if(wordTagsInChunk.size() != 0 && chunk != null) 
			processChunk(wordTagsInChunk, chunk);
		
		ChunkAnalysisBasedWordAndPOSSample sample = new ChunkAnalysisBasedWordAndPOSSample(words, poses, chunkTags);
		sample.setLabel(label);
		
		return sample;
	}
	
	/**
	 * 处理组块，为组块中的词赋予标签
	 * @param wordTagsInChunk	待处理的组块	
	 * @param chunk				组块的类型
	 * @param contain_End		是否含有结束标签(BIO/BIEO)
	 */
	private void processChunk(List<String> wordTagsInChunk, String chunk) {
		for(int i = 0; i < wordTagsInChunk.size(); i++) {
			String[] wordTag = wordTagsInChunk.get(i).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			
			if(i == 0)
				chunkTags.add(chunk + ChunkBegin);
			else if(i == wordTagsInChunk.size() - 1)
				chunkTags.add(chunk + ChunkEnd);
			else
				chunkTags.add(chunk + InChunk);
		}
	}
}
