package hust.tools.ca.run;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSModel;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSampleStream;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 模型训练工具类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月18日
 *</ul>
 */
public class ChunkAnalysisTrainerTool {
	 	
    private static void usage() {
        System.out.println(ChunkAnalysisTrainerTool.class.getName() + " -data <corpusFile> -method <method> -label <label> -model <modelFile> -encoding <encoding> "
                + " [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException {
        if (args.length < 1) {
            usage();
            return;
        }

        int cutoff = 3;
        int iters = 100;
        
        String method = "wp";
        String label = "BIEO";
        File corpusFile = null;
        File modelFile = null;
        String encoding = "UTF-8";
        
        for (int i = 0; i < args.length; i++) {
            if(args[i].equals("-data")) {
                corpusFile = new File(args[i + 1]);
                i++;
            }else if(args[i].equals("-method")) {
            	method = args[i + 1];
                i++;
            }else if(args[i].equals("-label")) {
            	label = args[i + 1];
                i++;
            }else if(args[i].equals("-model")) {
                modelFile = new File(args[i + 1]);
                i++;
            }else if(args[i].equals("-encoding")) {
                encoding = args[i + 1];
                i++;
            }else if(args[i].equals("-cutoff")) {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }else if(args[i].equals("-iters")) {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }
        
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));

        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));

        if(method.equals("w")){
        	ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, label);
            ChunkAnalysisBasedWordContextGenerator contextGen = new ChunkAnalysisBasedWordContextGeneratorConf();
            ChunkAnalysisBasedWordME me = new ChunkAnalysisBasedWordME(label);
            ChunkAnalysisBasedWordModel model = me.train("zh", sampleStream, params, contextGen);
            model.serialize(modelOut);
        }else if(method.equals("wp")) {
        	ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, label);
            ChunkAnalysisBasedWordAndPOSContextGenerator contextGen = new ChunkAnalysisBasedWordAndPOSContextGeneratorConf();
            ChunkAnalysisBasedWordAndPOSME me = new ChunkAnalysisBasedWordAndPOSME(label);
            ChunkAnalysisBasedWordAndPOSModel model = me.train("zh", sampleStream, params, contextGen);
            model.serialize(modelOut);
        }else{
        	System.err.println("错误的模型方法：" + method);
        }
    }
}
