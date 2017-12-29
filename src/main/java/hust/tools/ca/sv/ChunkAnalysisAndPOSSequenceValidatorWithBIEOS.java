package hust.tools.ca.sv;

import opennlp.tools.util.SequenceValidator;

/**
 *<ul>
 *<li>Description: BIEOS序列验证类, 验证当前组块标记是否合法，（组块最小长度为1）
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月22日
 *</ul>
 */
public class ChunkAnalysisAndPOSSequenceValidatorWithBIEOS implements SequenceValidator<String> {
	
	public ChunkAnalysisAndPOSSequenceValidatorWithBIEOS() {
		
	}

	@Override
	public boolean validSequence(int index, String[] words, String[] posChunkTags, String chunk) {
		String[] chunkTags = new String[posChunkTags.length];
		String out = chunk.split("-")[1];
		for(int i = 0; i < chunkTags.length; i++)
			chunkTags[i] = posChunkTags[i].split("-")[1];		
		
		if(index == 0) {//当前词为句子开始位置,只能为O||*_B||*_S			
			if(out.equals("O") || out.split("_")[1].equals("B") || out.split("_")[1].equals("S")) 
				return true;
		}else {//当前词不是句子开始位置
			if(index == chunkTags.length - 1) {//当前词是句子结束
				String chunkTag = chunkTags[index - 1];
				if(out.equals("O")) {
					if(chunkTag.equals("O") || chunkTag.split("_")[1].equals("E") || chunkTag.split("_")[1].equals("S"))
						return true;
				}else if(out.split("_")[1].equals("E")) {
					if(chunkTag.equals("O"))
						return false;
					else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
							out.split("_")[0].equals(chunkTag.split("_")[0]))
						return true;
				}else if(out.split("_")[1].equals("E")) {
					if(chunkTag.equals("O") || chunkTag.split("_")[1].equals("E") || chunkTag.split("_")[1].equals("S"))
						return true;
				}
			}else {
				String chunkTag = chunkTags[index - 1];
				if(out.equals("O") || out.split("_")[1].equals("B") || out.split("_")[1].equals("S")) {
					if(chunkTag.equals("O") ||  chunkTag.split("_")[1].equals("E") ||  chunkTag.split("_")[1].equals("S"))
						return true;
				}else{
					if(chunkTag.equals("O"))
						return false;
					else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
							out.split("_")[0].equals(chunkTag.split("_")[0]))
						return true;
				}
			}
		}

		return false;
	}
}
