package hust.tools.ca.evaluate;

import java.io.OutputStream;
import java.io.PrintStream;

import hust.tools.ca.stream.ChunkAnalysisSample;

/**
 *<ul>
 *<li>Description: 错误样本输出 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月7日
 *</ul>
 */
public class ChunkAnalysisErrorPrinter extends ChunkAnalysisEvaluateMonitor {
	
	private PrintStream errOut;
	
	/**
	 * 构造方法
	 * @param out	输出流
	 */
	public ChunkAnalysisErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	
	@Override
	public void missclassified(ChunkAnalysisSample reference, ChunkAnalysisSample prediction) {
		 errOut.println("样本的结果：" + reference);
		 errOut.println("预测的结果：" + prediction);
		 
		 String[] actualChunks = reference.getChunkTags();
		 String[] predictChunks = prediction.getChunkTags();
		 String errorInfo = "";
		 
		 if(actualChunks.length != predictChunks.length) {
			 errorInfo += "组块标记长度不同";
		 }else{
			 for(int i = 0; i < actualChunks.length; i++) {
				 if(!actualChunks[i].equals(predictChunks[i]))
					 errorInfo += i + "-" + actualChunks[i] + "/" + predictChunks[i] + ", ";
			 }
			 
			 if(!errorInfo.trim().equals(""))
				 errorInfo = errorInfo.trim().substring(0, errorInfo.length() - 1);
		 }
		 errOut.println(errorInfo+"\n");
	}
}
