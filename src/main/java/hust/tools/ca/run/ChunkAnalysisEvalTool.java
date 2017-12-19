package hust.tools.ca.run;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import hust.tools.ca.evaluate.AbstractChunkAnalysisMeasure;
import hust.tools.ca.evaluate.ChunkAnalysisAndPOSBasedWordEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordAndPOSEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisErrorPrinter;
import hust.tools.ca.evaluate.ChunkAnalysisEvaluateMonitor;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIEO;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIO;
import hust.tools.ca.feature.ChunkAnalysisAndPOSBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordME;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordModel;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSModel;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 模型评估工具类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月18日
 *</ul>
 */
public class ChunkAnalysisEvalTool {
	
	/**
	 * 样本中组块的标注方法BIO/BIEO
	 */
	private static String label = "BIEO";
	
	private static AbstractChunkAnalysisMeasure measure;

	/**
	 * 依据黄金标准评价基于词和词性的标注效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param trainFile		系模型文件
	 * @param params		模型参数集
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkBasedWordAndPOS(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("训练模型...");  
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, label);
        
        ChunkAnalysisBasedWordAndPOSContextGenerator contextGen = new ChunkAnalysisBasedWordAndPOSContextGeneratorConf();
        long start = System.currentTimeMillis();
        ChunkAnalysisBasedWordAndPOSME me = new ChunkAnalysisBasedWordAndPOSME(label);
        ChunkAnalysisBasedWordAndPOSModel model = me.train("zh", sampleStream, params, contextGen);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisBasedWordAndPOSME chunkTagger = new ChunkAnalysisBasedWordAndPOSME(model, label, contextGen);
        ChunkAnalysisBasedWordAndPOSEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisBasedWordAndPOSEvaluator(chunkTagger, label, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisBasedWordAndPOSEvaluator(chunkTagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisBasedWordAndPOSSampleStream(goldStream, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    /**
	 * 依据黄金标准评价基于词的标注效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param trainFile		系模型文件
	 * @param params		模型参数集
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkBasedWord(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("训练模型...");  
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, label);
        
        ChunkAnalysisBasedWordContextGenerator contextGen = new ChunkAnalysisBasedWordContextGeneratorConf();
        long start = System.currentTimeMillis();
        ChunkAnalysisBasedWordME me = new ChunkAnalysisBasedWordME(label);
        ChunkAnalysisBasedWordModel model = me.train("zh", sampleStream, params, contextGen);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisBasedWordME chunkTagger = new ChunkAnalysisBasedWordME(model, label, contextGen);
        ChunkAnalysisBasedWordEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisBasedWordEvaluator(chunkTagger, label, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisBasedWordEvaluator(chunkTagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisBasedWordAndPOSSampleStream(goldStream, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    /**
	 * 依据黄金标准评价基于词的词性标注和组块分析效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param trainFile		系模型文件
	 * @param params		模型参数集
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkAndPOSBasedWord(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("训练模型...");  
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisAndPOSBasedWordSampleStream(lineStream, label);
        
        ChunkAnalysisBasedWordContextGenerator contextGen = new ChunkAnalysisAndPOSBasedWordContextGeneratorConf();
        long start = System.currentTimeMillis();
        ChunkAnalysisAndPOSBasedWordME me = new ChunkAnalysisAndPOSBasedWordME(label);
        ChunkAnalysisAndPOSBasedWordModel model = me.train("zh", sampleStream, params, contextGen);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisAndPOSBasedWordME chunkTagger = new ChunkAnalysisAndPOSBasedWordME(model, label, contextGen);
        ChunkAnalysisAndPOSBasedWordEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisAndPOSBasedWordEvaluator(chunkTagger, label, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisAndPOSBasedWordEvaluator(chunkTagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisAndPOSBasedWordSampleStream(goldStream, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    private static void usage() {
        System.out.println(ChunkAnalysisEvalTool.class.getName() + " -model <modelFile> -method <method> -label <label> -gold <goldFile> -encoding <encoding> [-error <errorFile>]");
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException  {
        if (args.length < 1) {
            usage();
            return;
        }
        
        String method = "wp";
        String modelFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
       
        int cutoff = 3;
        int iters = 100;
        for(int i = 0; i < args.length; i++) {
            if(args[i].equals("-model")) {
                modelFile = args[i + 1];
                i++;
            }else if(args[i].equals("-method")) {
            	method = args[i + 1];
                i++;
            }else if(args[i].equals("-label")) {
            	label = args[i + 1];
                i++;
            }else if(args[i].equals("-gold")) {
                goldFile = args[i + 1];
                i++;
            }else if(args[i].equals("-error")) {
                errorFile = args[i + 1];
                i++;
            }else if (args[i].equals("-encoding")) {
                encoding = args[i + 1];
                i++;
            }else if (args[i].equals("-cutoff")) {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters")) {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        switch(label) {
		case "BIEO":
			measure = new ChunkAnalysisMeasureWithBIEO();
			break;
		case "BIO":
			measure = new ChunkAnalysisMeasureWithBIO();
			break;
		default:
			System.err.println("错误的标签类型，已默认为BIEO");
			measure = new ChunkAnalysisMeasureWithBIEO();
			break;
        }
        
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));

        if(method.equals("w")) {
        	if (errorFile != null)
        		evalChunkBasedWord(new File(modelFile), params, new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkBasedWord(new File(modelFile), params, new File(goldFile), encoding, null);
        }else if(method.equals("wp")){
        	if (errorFile != null)
        		evalChunkBasedWordAndPOS(new File(modelFile), params, new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkBasedWordAndPOS(new File(modelFile), params, new File(goldFile), encoding, null);
        }else if(method.equals("cp")) {
        	if (errorFile != null)
        		evalChunkAndPOSBasedWord(new File(modelFile), params, new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkAndPOSBasedWord(new File(modelFile), params, new File(goldFile), encoding, null);
        }else{
        	System.err.println("错误的模型方法：" + method);
        }
    }
}