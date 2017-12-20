package hust.tools.ca.beamsearch;

import opennlp.tools.util.SequenceValidator;

/**
 *<ul>
 *<li>Description: BIO序列验证类,验证当前组块标记是否合法
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月6日
 *</ul>
 */
public class ChunkAnalysisAndPOSSequenceValidatorWithBIEO implements SequenceValidator<String> {
	
	public ChunkAnalysisAndPOSSequenceValidatorWithBIEO() {
		
	}

	@Override
	public boolean validSequence(int index, String[] words, String[] posChunkTags, String out) {
		String[] chunkTags = new String[posChunkTags.length];
		String chunk = out.split("-")[1];
		for(int i = 0; i < chunkTags.length; i++)
			chunkTags[i] = posChunkTags[i].split("-")[1];		
		
		if(index == 0) {//当前词为句子开始位置,只能为O||*_B				
			if(chunk.equals("O") || chunk.split("_")[1].equals("B")) 
				return true;
		}else {//当前词不是句子开始位置
			if(index == chunkTags.length - 1) {//当前词是句子结束
				String chunkTag = chunkTags[index - 1];
				if(chunk.equals("O")) {
					if(chunkTag.equals("O") || chunkTag.split("_")[1].equals("E"))
						return true;
				}else if(chunk.split("_")[1].equals("E")) {
					if(chunkTag.equals("O"))
						return false;
					else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
							chunk.split("_")[0].equals(chunkTag.split("_")[0]))
						return true;
				}
			}else {
				String chunkTag = chunkTags[index - 1];
				if(chunk.equals("O") || chunk.split("_")[1].equals("B")) {
					if(chunkTag.equals("O") ||  chunkTag.split("_")[1].equals("E"))
						return true;
				}else{
					if(chunkTag.equals("O"))
						return false;
					else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
							chunk.split("_")[0].equals(chunkTag.split("_")[0]))
						return true;
				}
			}
		}

		return false;
	}
}
