package hust.tools.ca.evaluate;

import java.io.OutputStream;
import java.io.PrintStream;

import hust.tools.ca.stream.ChunkAnalysisSample;

/**
 * 错误样本输出
 * @author 王馨苇
 *
 */
public class ChunkAnalysisErrorPrinter extends ChunkAnalysisEvaluateMonitor {
	
	private PrintStream errOut;
	
	public ChunkAnalysisErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	
	@Override
	public void missclassified(ChunkAnalysisSample reference, ChunkAnalysisSample prediction) {
		 errOut.println("样本的结果：");
		 
		 errOut.println();
		 errOut.println("预测的结果：");
		
		 errOut.println();
	}
}
