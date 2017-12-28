package hust.tools.ca.parse;

import java.util.ArrayList;
import java.util.List;

import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSample;

/**
 *<ul>
 *<li>Description: 基于词的词性标注和组块分析的BIEOS样本解析（组块最小长度为1）
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisAndPOSBasedWordParseWithBIEOS extends AbstractChunkAnalysisParse {
	
	private final String ChunkBegin = "_B";
	private final String InChunk = "_I";
	private final String OutChunk = "O";
	private final String ChunkEnd = "_E";
	private final String SingleChunk = "_S";
	
	private List<String> posChunkTags;
	private List<String> words;
	
	/**
	 * 构造方法
	 */
	public ChunkAnalysisAndPOSBasedWordParseWithBIEOS() {

	}
	
	@Override
	protected void setLabel() {
		this.label = "BIEOS";
	}
	
	public AbstractChunkAnalysisSample parse(String sentence){
		posChunkTags = new ArrayList<>();
		words = new ArrayList<>();
		
		boolean isInChunk = false;							//当前词是否在组块中
		List<String> wordsInChunk = new ArrayList<>();		//临时存储在组块中的词
		List<String> posesInChunk = new ArrayList<>();		//临时存储在组块中的词性
		String[] wordTag = null;							//词与词性标注
		String chunk = null;								//组块的标签
		String[] content = sentence.split("\\s+");
		
		for(String string : content) {
			if(isInChunk) {	//当前词在组块中
				if(string.contains("]")) {//当前词是组块的结束
					String[] strings = string.split("]");
					wordsInChunk.add(strings[0].split("/")[0]);
					posesInChunk.add(strings[0].split("/")[1]);
					chunk = strings[1];
					isInChunk = false;
				}else {
					wordsInChunk.add(string.split("/")[0]);
					posesInChunk.add(string.split("/")[1]);
				}
			}else {//当前词不在组块中
				if(wordsInChunk.size() != 0 && chunk != null) {//上一个组块中的词未处理，先处理上一个组块中的词
					processChunk(wordsInChunk, posesInChunk, chunk);
					
					wordsInChunk = new ArrayList<>();
					posesInChunk = new ArrayList<>();
					chunk = null;
				}
				
				if(string.startsWith("[")) {
					string = string.replace("[", "");
					
					if(string.contains("]")) {//只有一个词的组块
						words.add(string.split("]")[0].split("/")[0]);
						posChunkTags.add(string.split("]")[0].split("/")[1] + "-" + string.split("]")[1] + SingleChunk);
					}else {
						wordsInChunk.add(string.split("/")[0]);
						posesInChunk.add(string.split("/")[1]);
						isInChunk = true;
					}
				}else {
					wordTag = string.split("/");
					words.add(wordTag[0]);
					posChunkTags.add(wordTag[1] + "-" + OutChunk);
				}
			}
		}
		
		//句子结尾是组块，进行解析
		if(wordsInChunk.size() != 0 && chunk != null) 
			processChunk(wordsInChunk, posesInChunk, chunk);
		
		ChunkAnalysisAndPOSBasedWordSample sample = new ChunkAnalysisAndPOSBasedWordSample(words, posChunkTags);
		sample.setLabel(label);
		
		return sample;
	}
	
	/**
	 * 处理组块，为组块中的词赋予标签
	 * @param wordTagsInChunk	待处理的组块	
	 * @param chunk				组块的类型
	 * @param contain_End		是否含有结束标签(BIO/BIEO)
	 */
	private void processChunk(List<String> wordsInChunk, List<String> posesInChunk, String chunk) {
		for(int i = 0; i < wordsInChunk.size(); i++) {
			words.add(wordsInChunk.get(i));
			
			if(i == 0) 
				posChunkTags.add(posesInChunk.get(i) + "-" + chunk + ChunkBegin);
			else if(i == wordsInChunk.size() - 1) 
				posChunkTags.add(posesInChunk.get(i) + "-" + chunk + ChunkEnd);
			else
				posChunkTags.add(posesInChunk.get(i) + "-" + chunk + InChunk);
		}
	}
}
