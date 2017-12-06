package hust.tools.ca.beamsearch;

/**
 *<ul>
 *<li>Description: 序列验证类,验证当前组块标记是否合法
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月6日
 *</ul>
 */
public class DefaultChunkAnalysisSequenceValidator implements ChunkAnalysisSequenceValidator<String> {

	@Override
	public boolean validSequence(int index, String[] words, String[] poses, String[] chunkTags, String out) {
		if(index == 0) {//当前词为句子开始位置
			if(chunkTags.length > index + 1) {//当前词不是句子结束，当前词和下一个词的组块标记为OO||OB||BI||BE
				String chunkTag = chunkTags[index + 1];
				
				if(out.equals("O")) {
					if(chunkTag.equals("O"))
						return true;
					else if(chunkTag.split("_")[1].equals("B"))
						return true;
				}else if(out.split("_")[1].equals("B") && !chunkTag.equals("O")) {
					if(out.split("_")[0].equals(chunkTag.split("_")[0]) &&	(chunkTag.split("_")[1].equals("I") || chunkTag.split("_")[1].equals("E")))
						return true;
				}
			}else if(out.equals("O")) {//句子只有一个词，无法组成词组，组块标记为O
				return true;
			}
		}else{//当前词不是句子开始位置
			if(chunkTags.length > index + 1) {//当前词不是句子结束, 当前词和下一个词的组块标记为OO||OB||BI||BE
				String chunkTag = chunkTags[index + 1];
				
				if(out.equals("O")) {
					if(chunkTag.equals("O")) 
						return true; 
					else if(chunkTag.split("_")[1].equals("B"))
						return true;
				}else if((out.split("_")[1].equals("B") || out.split("_")[1].equals("I"))){
					if(!chunkTag.equals("O")) {
						if(out.split("_")[0].equals(chunkTag.split("_")[0]) && (chunkTag.split("_")[1].equals("I") || chunkTag.split("_")[1].equals("E")))
							return true;
					}
				}else if(out.split("_")[1].equals("E")) { 
					if(chunkTag.equals("O"))
						return true;
					else if(chunkTag.split("_")[1].equals("B")) 
						return true; 
				}
			}else {
				if(out.equals("O")) 
					return true;
				else if(out.split("_")[1].equals("E"))//当前词是句子结束,当前词的组块标记只能为O||E
					return true;
			}
		}
		
		return false;
		
//		if(index == 0) {//当前词为句子开始位置
//			if(out.equals("O")) {//当前词不是组块内容,下一个词必须是O或B
//				if(chunkTags.length > index + 1) {
//					String chunkTag = chunkTags[index + 1];
//					
//					if(chunkTag.equals("O"))
//						return true;
//					else if(chunkTag.split("_")[1].equals("B")) 
//						return true;
//				}else 
//					return true;
//			}else {//当前词的组块标记位置必须为B
//				if(out.split("_")[1].equals("B")) {
//					if(chunkTags.length > index + 1) {
//						String chunkTag = chunkTags[index + 1];
//						if(chunkTag.split("_")[0].equals(out.split("_")[0]) && (chunkTag.split("_")[1].equals("I"))||(chunkTag.split("_")[1].equals("E")))
//							return true;	//组块标注相同，前后词的位置合法
//					}
//				}
//			}
//		}else{//当前词不是句子开始位置
//			if(out.equals("O")) {
//				if(chunkTags.length > index + 1) {
//					String chunkTag = chunkTags[index + 1];
//					
//					if(chunkTag.equals("O"))
//						return true;
//					else if(chunkTag.split("_")[1].equals("B")) 
//						return true;
//				}else 
//					return true;
//			}else if(out.split("_")[1].equals("B") || out.split("_")[1].equals("I")) {
//				if(chunkTags.length > index + 1) {
//					String chunkTag = chunkTags[index + 1];
//					if(chunkTag.split("_")[0].equals(out.split("_")[0]) && (chunkTag.split("_")[1].equals("I"))||(chunkTag.split("_")[1].equals("E")))
//						return true;	//组块标注相同，前后词的位置合法
//				}
//			}else if(out.split("_")[1].equals("E")) {
//				if(chunkTags.length > index + 1) {
//					String chunkTag = chunkTags[index + 1];
//					if(chunkTag.split("_")[1].equals("B") || chunkTag.equals("O")) 
//						return true;
//				}else
//					return true;
//			}
//		}
//		
//		return false;
	}
}
