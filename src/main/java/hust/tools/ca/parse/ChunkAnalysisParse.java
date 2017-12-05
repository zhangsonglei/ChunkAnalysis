package hust.tools.ca.parse;

import java.util.ArrayList;
import java.util.List;

import hust.tools.ca.stream.ChunkAnalysisSample;

/**
 *<ul>
 *<li>Description:  对样本进行解析
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisParse {

	private String sentence;
	private final String ChunkBegin = "_B";
	private final String ChunkEnd = "_E";
	private final String InChunk = "_I";
	private final String OutChunk = "O";
	
	public ChunkAnalysisParse(String sentence){
		this.sentence = sentence;
	}
	
	/**
	 * 返回由字符串句子解析而成的样本
	 * @return	样本
	 */
	public ChunkAnalysisSample parse(){
		List<String> chunkTags = new ArrayList<>();
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();
		
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
				}else {
					wordTagsInChunk.add(string);
				}
			}else {//当前词不在组块中
				if(wordTagsInChunk != null && chunk != null) {//上一个组块中的词未处理，先处理上一个组块中的词	
					wordTag = wordTagsInChunk.get(0).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + ChunkBegin);
					
					if(wordTagsInChunk.size() > 2) {
						for(int i = 1; i < wordTagsInChunk.size() - 1; i++) {
							wordTag = wordTagsInChunk.get(i).split("/");
							words.add(wordTag[0]);
							poses.add(wordTag[1]);
							chunkTags.add(chunk + InChunk);
						}
					}
					wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + ChunkEnd);
					
					wordTagsInChunk = new ArrayList<>();
					chunk = null;
					
					if(string.startsWith("[")) {
						wordTagsInChunk.add(string.replace("[", ""));
						isInChunk = true;
					}else {
						wordTag = string.split("/");
						words.add(wordTag[0]);
						poses.add(wordTag[1]);
						chunkTags.add(OutChunk);
					}
					
				}else {
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
		}
		
		//句子结尾是组块，进行解析
		if(wordTagsInChunk != null && chunk != null) {
			wordTag = wordTagsInChunk.get(0).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + ChunkBegin);
			
			if(wordTagsInChunk.size() > 2) {
				for(int i = 1; i < wordTagsInChunk.size() - 1; i++) {
					wordTag = wordTagsInChunk.get(i).split("/");
					words.add(wordTag[0]);
					poses.add(wordTag[1]);
					chunkTags.add(chunk + InChunk);
				}
			}
			wordTag = wordTagsInChunk.get(wordTagsInChunk.size() - 1).split("/");
			words.add(wordTag[0]);
			poses.add(wordTag[1]);
			chunkTags.add(chunk + ChunkEnd);
		}
		
		return new ChunkAnalysisSample(words, poses, chunkTags);
	}
}
