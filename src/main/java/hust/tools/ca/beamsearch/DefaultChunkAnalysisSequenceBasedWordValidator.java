package hust.tools.ca.beamsearch;

import opennlp.tools.util.SequenceValidator;

/**
 *<ul>
 *<li>Description: 序列验证类,验证当前组块标记是否合法
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月6日
 *</ul>
 */
public class DefaultChunkAnalysisSequenceBasedWordValidator implements SequenceValidator<String> {
	
	private boolean isBIEO;
	
	public DefaultChunkAnalysisSequenceBasedWordValidator(boolean isBIEO) {
		this.isBIEO = isBIEO;
	}

	@Override
	public boolean validSequence(int index, String[] words, String[] chunkTags, String out) {
		if(index == 0) {//当前词为句子开始位置,只能为O||*_B				
			if(out.equals("O") || out.split("_")[1].equals("B")) 
				return true;
		}else {//当前词不是句子开始位置
			if(isBIEO)
				return isBIEO(chunkTags, index, out);
			else
				return isBIO(chunkTags, index, out);
		}
		
		return false;
	}
	
	private boolean isBIO(String[] chunkTags, int index, String out) {
		if(index == chunkTags.length - 1) {//当前词是句子结束
			String chunkTag = chunkTags[index - 1];
			if(out.equals("O")) {
				if(chunkTag.equals("O") || chunkTag.split("_")[1].equals("I"))
					return true;
			}else if(out.split("_")[1].equals("I")) {
				if(chunkTag.equals("O"))
					return false;
				else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
						out.split("_")[0].equals(chunkTag.split("_")[0]))
					return true;
			}
		}else {
			String chunkTag = chunkTags[index - 1];
			if(out.equals("O") || out.split("_")[1].equals("B")) {
				if(chunkTag.equals("O") ||  chunkTag.split("_")[1].equals("I"))
					return true;
			}else{
				if(chunkTag.equals("O"))
					return false;
				else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
						out.split("_")[0].equals(chunkTag.split("_")[0]))
					return true;
			}
		}
		
		return false;
	}
	
	private boolean isBIEO(String[] chunkTags, int index, String out) {
		if(index == chunkTags.length - 1) {//当前词是句子结束
			String chunkTag = chunkTags[index - 1];
			if(out.equals("O")) {
				if(chunkTag.equals("O") || chunkTag.split("_")[1].equals("E"))
					return true;
			}else if(out.split("_")[1].equals("E")) {
				if(chunkTag.equals("O"))
					return false;
				else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
						out.split("_")[0].equals(chunkTag.split("_")[0]))
					return true;
			}
		}else {
			String chunkTag = chunkTags[index - 1];
			if(out.equals("O") || out.split("_")[1].equals("B")) {
				if(chunkTag.equals("O") ||  chunkTag.split("_")[1].equals("E"))
					return true;
			}else{
				if(chunkTag.equals("O"))
					return false;
				else if((chunkTag.split("_")[1].equals("B") || chunkTag.split("_")[1].equals("I")) &&
						out.split("_")[0].equals(chunkTag.split("_")[0]))
					return true;
			}
		}
		
		return false;
	}
}