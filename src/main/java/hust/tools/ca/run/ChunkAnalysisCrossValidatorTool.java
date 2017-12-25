package hust.tools.ca.run;

import java.io.File;
import java.io.IOException;

import hust.tools.ca.beamsearch.ChunkAnalysisAndPOSSequenceValidatorWithBIEO;
import hust.tools.ca.beamsearch.ChunkAnalysisAndPOSSequenceValidatorWithBIEOS;
import hust.tools.ca.beamsearch.ChunkAnalysisAndPOSSequenceValidatorWithBIO;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceValidatorWithBIEO;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceValidatorWithBIEOS;
import hust.tools.ca.beamsearch.ChunkAnalysisSequenceValidatorWithBIO;
import hust.tools.ca.cv.ChunkAnalysisAndPOSBasedWordCrossValidation;
import hust.tools.ca.cv.ChunkAnalysisBasedWordAndPOSCrossValidation;
import hust.tools.ca.cv.ChunkAnalysisBasedWordCrossValidation;
import hust.tools.ca.evaluate.AbstractChunkAnalysisMeasure;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIEO;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIEOS;
import hust.tools.ca.evaluate.ChunkAnalysisMeasureWithBIO;
import hust.tools.ca.feature.ChunkAnalysisAndPOSBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
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
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 交叉验证工具类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月18日
 *</ul>
 */
public class ChunkAnalysisCrossValidatorTool {
	    
    private static void usage() {
        System.out.println(ChunkAnalysisCrossValidatorTool.class.getName() + " -data <corpusFile> -type <type> -method <method> -label <label> -encoding <encoding> [-folds <nFolds>] [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        if(args.length < 1) {
            usage();
            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        //Maxent, Perceptron, MaxentQn, NaiveBayes
        String type = "Maxent";
        String method = "wp";
        String label = "BIEO";
        File corpusFile = null;
        String encoding = "UTF-8";
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-data")) {
                corpusFile = new File(args[i + 1]);
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
            }else if(args[i].equals("-encoding")) {
                encoding = args[i + 1];
                i++;
            }else if (args[i].equals("-cutoff")) {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }else if (args[i].equals("-iters")) {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }else if (args[i].equals("-folds")) {
                folds = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        AbstractChunkAnalysisParse parse = null;
        AbstractChunkAnalysisMeasure measure = null;
        SequenceValidator<String> sequenceValidator = null;
        
        if(method.equals("w")) {        	
        	if(label.equals("BIEOS")) {
        		parse = new ChunkAnalysisBasedWordParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEOS();
        	}else if(label.equals("BIEO")) {
        		parse = new ChunkAnalysisBasedWordParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEO();
        	}else {
        		parse = new ChunkAnalysisBasedWordParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIO();	
        	}
        	
        	ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, parse);
        	ChunkAnalysisBasedWordContextGenerator contextGen = new  ChunkAnalysisBasedWordContextGeneratorConf();
        	ChunkAnalysisBasedWordCrossValidation crossValidator = new ChunkAnalysisBasedWordCrossValidation(params);
        	
        	crossValidator.evaluate(sampleStream, folds, contextGen, measure, sequenceValidator);
        }else if(method.equals("wp")) {        	
        	if(label.equals("BIEOS")) {
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEOS();
        	}else if(label.equals("BIEO")) {
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIEO();
        	}else {
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        		sequenceValidator = new ChunkAnalysisSequenceValidatorWithBIO();
        	}
        	
        	ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, parse);
        	ChunkAnalysisBasedWordAndPOSContextGenerator contextGen = new  ChunkAnalysisBasedWordAndPOSContextGeneratorConf();
        	ChunkAnalysisBasedWordAndPOSCrossValidation crossValidator = new ChunkAnalysisBasedWordAndPOSCrossValidation(params);
        	
        	crossValidator.evaluate(sampleStream, folds, contextGen, measure, sequenceValidator);
        }else if(method.equals("cp")) {        	
        	if(label.equals("BIEOS")) {
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEOS();
        		measure = new ChunkAnalysisMeasureWithBIEOS();
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIEOS();
        	}else if(label.equals("BIEO")) {
        		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEO();
        		measure = new ChunkAnalysisMeasureWithBIEO();
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIEO();
        	}else {
        		parse = new ChunkAnalysisAndPOSBasedWordParseWithBIO();
        		measure = new ChunkAnalysisMeasureWithBIO();
        		sequenceValidator = new ChunkAnalysisAndPOSSequenceValidatorWithBIO();
        	}
        	
        	ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisAndPOSBasedWordSampleStream(lineStream, parse);
        	ChunkAnalysisBasedWordContextGenerator contextGen = new  ChunkAnalysisAndPOSBasedWordContextGeneratorConf();
        	ChunkAnalysisAndPOSBasedWordCrossValidation crossValidator = new ChunkAnalysisAndPOSBasedWordCrossValidation(params);

        	crossValidator.evaluate(sampleStream, folds, contextGen, measure, sequenceValidator);
        }else{
        	System.err.println("错误的模型方法：" + method);
        }
    }
}
