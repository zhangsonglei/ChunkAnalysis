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
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIEOS;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIO;
import hust.tools.ca.feature.ChunkAnalysisAndPOSBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordME;
import hust.tools.ca.model.ChunkAnalysisAndPOSBasedWordModel;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSModel;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisAndPOSBasedWordParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisAndPOSBasedWordParseWithBIEOS;
import hust.tools.ca.parse.ChunkAnalysisAndPOSBasedWordParseWithBIO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIEOS;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIEOS;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIO;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import hust.tools.ca.sv.ChunkAnalysisAndPOSSequenceValidatorWithBIEO;
import hust.tools.ca.sv.ChunkAnalysisAndPOSSequenceValidatorWithBIEOS;
import hust.tools.ca.sv.ChunkAnalysisAndPOSSequenceValidatorWithBIO;
import hust.tools.ca.sv.ChunkAnalysisSequenceValidatorWithBIEO;
import hust.tools.ca.sv.ChunkAnalysisSequenceValidatorWithBIEOS;
import hust.tools.ca.sv.ChunkAnalysisSequenceValidatorWithBIO;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.SequenceValidator;
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
	
	private static AbstractChunkAnalysisMeasure measure;
	
	private static AbstractChunkAnalysisParse parse;
	
	private static SequenceValidator<String> sequenceValidator;
	
	private static String label;

	/**
	 * 依据黄金标准评价基于词和词性的标注效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param modelFile		模型文件
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkBasedWordAndPOS(File modelFile, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("读取模型...");  
        long start = System.currentTimeMillis();
        ChunkAnalysisBasedWordAndPOSModel model = new ChunkAnalysisBasedWordAndPOSModel(modelFile);
        System.out.println("读取模型时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisBasedWordAndPOSContextGeneratorConf();
        ChunkAnalysisBasedWordAndPOSME tagger = new ChunkAnalysisBasedWordAndPOSME(model, sequenceValidator, contextGen, label);
        ChunkAnalysisBasedWordAndPOSEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisBasedWordAndPOSEvaluator(tagger, measure, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisBasedWordAndPOSEvaluator(tagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisBasedWordAndPOSSampleStream(goldStream, parse, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    /**
	 * 依据黄金标准评价基于词的标注效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param modelFile		模型文件
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkBasedWord(File modelFile, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("读取模型...");          
        long start = System.currentTimeMillis();
        ChunkAnalysisBasedWordModel model = new ChunkAnalysisBasedWordModel(modelFile);
        System.out.println("读取模型时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisBasedWordContextGeneratorConf();
        ChunkAnalysisBasedWordME tagger = new ChunkAnalysisBasedWordME(model, sequenceValidator, contextGen, label);
        ChunkAnalysisBasedWordEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisBasedWordEvaluator(tagger, measure, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisBasedWordEvaluator(tagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisBasedWordSampleStream(goldStream, parse, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    /**
	 * 依据黄金标准评价基于词的词性标注和组块分析效果, 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
	 * @param modelFile		模型文件
	 * @param goldFile		黄标准文件
	 * @param encoding		黄金标准文件编码
	 * @param errorFile		错误输出文件
	 * @throws IOException
	 */
    public static void evalChunkAndPOSBasedWord(File modelFile, File goldFile, String encoding, File errorFile) throws IOException {
        System.out.println("读取模型...");  
        long start = System.currentTimeMillis();
        ChunkAnalysisAndPOSBasedWordModel model = new ChunkAnalysisAndPOSBasedWordModel(modelFile);
        System.out.println("读取模型练间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        ChunkAnalysisContextGenerator contextGen = new ChunkAnalysisAndPOSBasedWordContextGeneratorConf();
        ChunkAnalysisAndPOSBasedWordME tagger = new ChunkAnalysisAndPOSBasedWordME(model, sequenceValidator, contextGen, label);
        ChunkAnalysisAndPOSBasedWordEvaluator evaluator = null;       
        
        if (errorFile != null) {
        	ChunkAnalysisEvaluateMonitor errorMonitor = new ChunkAnalysisErrorPrinter(new FileOutputStream(errorFile));
            evaluator = new ChunkAnalysisAndPOSBasedWordEvaluator(tagger, measure, errorMonitor);
        }
        else
            evaluator = new ChunkAnalysisAndPOSBasedWordEvaluator(tagger);
        
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<AbstractChunkAnalysisSample> testStream = new ChunkAnalysisAndPOSBasedWordSampleStream(goldStream, parse, label);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
    }
    
    private static void usage() {
        System.out.println(ChunkAnalysisEvalTool.class.getName() + " -model <modelFile> -type <type> -method <method> -label <label> -gold <goldFile> -encoding <encoding> [-error <errorFile>]");
    }

    public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException  {
        if (args.length < 1) {
            usage();
            return;
        }
        
        //Maxent, Perceptron, NaiveBayes
        String type = "Maxent";
        label = "BIEO";	
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
            }else if(args[i].equals("-type")) {
            	type = args[i + 1];
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
        
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());

        if(method.equals("w")) {        	
        	if(label.equals("BIEOS")) {
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEOS();
        		parse = new ChunkAnalysisBasedWordParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        	}else if(label.equals("BIEO")){
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEO();
        		parse = new ChunkAnalysisBasedWordParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        	}else {
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIO();
        		parse = new ChunkAnalysisBasedWordParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        	}
        	
        	if (errorFile != null)
        		evalChunkBasedWord(new File(modelFile), new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkBasedWord(new File(modelFile), new File(goldFile), encoding, null);
        }else if(method.equals("wp")){        	
        	if(label.equals("BIEOS")) {
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEOS();
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        	}else if(label.equals("BIEO")){
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEO();
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        	}else {
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIO();
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        	}
        	
        	if (errorFile != null)
        		evalChunkBasedWordAndPOS(new File(modelFile), new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkBasedWordAndPOS(new File(modelFile), new File(goldFile), encoding, null);
        }else if(method.equals("cp")) {
        	if(label.equals("BIEOS")) {
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIEOS();
        		parse = new ChunkAnalysisAndPOSBasedWordParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        	}else if(label.equals("BIEO")){
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIEO();
        		parse = new ChunkAnalysisAndPOSBasedWordParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        	}else {
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIO();
        		parse = new ChunkAnalysisAndPOSBasedWordParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        	}
        	
        	if (errorFile != null)
        		evalChunkAndPOSBasedWord(new File(modelFile), new File(goldFile), encoding, new File(errorFile));
        	else
        		evalChunkAndPOSBasedWord(new File(modelFile), new File(goldFile), encoding, null);
        }else{
        	System.err.println("错误的模型方法：" + method);
        }
    }
}