package hust.tools.ca.pos.word;

import java.io.OutputStream;
import java.io.PrintStream;

import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;

/**
 * 词性标注评价中输出错误的结果，帮助进行结果分析
 * 
 * @author 刘小峰
 */
public class WordPOSErrorPrinter implements POSTaggerEvaluationMonitor
{

    private PrintStream errOut;

    public WordPOSErrorPrinter(OutputStream out)
    {
        errOut = new PrintStream(out);
    }

    @Override
    public void missclassified(POSSample reference, POSSample prediction)
    {
        errOut.println(reference.toString());
        errOut.println("[*]" + prediction.toString());
    }

    @Override
    public void correctlyClassified(POSSample reference, POSSample prediction)
    {     
    }
}
