package hust.tools.ca.run;

import java.io.File;
import java.io.IOException;

import hust.tools.ca.cv.ChunkAnalysisAndPOSBasedWordCrossValidation;
import hust.tools.ca.cv.ChunkAnalysisBasedWordAndPOSCrossValidation;
import hust.tools.ca.cv.ChunkAnalysisBasedWordCrossValidation;
import hust.tools.ca.feature.ChunkAnalysisAndPOSBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import hust.tools.ca.stream.ChunkAnalysisAndPOSBasedWordSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
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
        System.out.println(ChunkAnalysisCrossValidatorTool.class.getName() + " -data <corpusFile> -method <method> -label <label> -encoding <encoding> [-folds <nFolds>] [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        if(args.length < 1) {
            usage();
            return;
        }

        int cutoff = 3;
        int iters = 100;
        int folds = 10;
        
        String method = "wp";
        String label = "BIEO";
        File corpusFile = null;
        String encoding = "UTF-8";
        for(int i = 0; i < args.length; i++) {
            if (args[i].equals("-data")) {
                corpusFile = new File(args[i + 1]);
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
        
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        
        if(method.equals("w")) {
        	ChunkAnalysisBasedWordCrossValidation crossValidator = new ChunkAnalysisBasedWordCrossValidation(params);
        	ObjectStream<ChunkAnalysisBasedWordSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, label);
        	ChunkAnalysisBasedWordContextGenerator contextGen = new  ChunkAnalysisBasedWordContextGeneratorConf();

        	crossValidator.evaluate(sampleStream, folds, contextGen, label);
        }else if(method.equals("wp")) {
        	ChunkAnalysisBasedWordAndPOSCrossValidation crossValidator = new ChunkAnalysisBasedWordAndPOSCrossValidation(params);
        	ObjectStream<ChunkAnalysisBasedWordSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, label);
        	ChunkAnalysisBasedWordAndPOSContextGenerator contextGen = new  ChunkAnalysisBasedWordAndPOSContextGeneratorConf();

        	crossValidator.evaluate(sampleStream, folds, contextGen, label);
        }else if(method.equals("cp")) {
        	ChunkAnalysisAndPOSBasedWordCrossValidation crossValidator = new ChunkAnalysisAndPOSBasedWordCrossValidation(params);
        	ObjectStream<ChunkAnalysisBasedWordSample> sampleStream = new ChunkAnalysisAndPOSBasedWordSampleStream(lineStream, label);
        	ChunkAnalysisBasedWordContextGenerator contextGen = new  ChunkAnalysisAndPOSBasedWordContextGeneratorConf();

        	crossValidator.evaluate(sampleStream, folds, contextGen, label);
        }else{
        	System.err.println("错误的模型方法：" + method);
        }
    }
}
