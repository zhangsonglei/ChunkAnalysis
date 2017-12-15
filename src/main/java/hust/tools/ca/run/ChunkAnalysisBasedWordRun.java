package hust.tools.ca.run;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import hust.tools.ca.cv.ChunkAnalysisBasedWordCrossValidation;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordErrorPrinter;
import hust.tools.ca.evaluate.ChunkAnalysisBasedWordEvaluator;
import hust.tools.ca.evaluate.ChunkAnalysisMeasure;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenerator;
import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGenratorConf;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSampleStream;
import hust.tools.ca.stream.FileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 组块分析应用程序 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordRun {

	private static String flag = "train";
	
	/**
	 * 组块位置标签是否为BIEO，不是的话使用默认的BIO
	 */
	private static boolean isBIEO = false;
	
	private static InputStream configStream;

	public static class Corpus{
		public String name;
		public String encoding;
		public String trainFile;
		public String testFile;
		public String modelbinaryFile;
		public String modeltxtFile;
		public String errorFile;
	}
	
	private static String[] corpusName = {"chunk"};
	
	private static Corpus[] getCorporaFromConf(Properties config) {
		Corpus[] corpuses = new Corpus[corpusName.length];
		for (int i = 0; i < corpuses.length; i++) {
			String name = corpusName[i];
			String encoding = config.getProperty(name + "." + "corpus.encoding");
			String trainFile = config.getProperty(name + "." + "corpus.train.file");
			String testFile = config.getProperty(name+"."+"corpus.test.file");
			String modelbinaryFile = config.getProperty(name + "." + "corpus.modelbinary.file");
			String modeltxtFile = config.getProperty(name + "." + "corpus.modeltxt.file");
			String errorFile = config.getProperty(name + "." + "corpus.error.file");
			Corpus corpus = new Corpus();
			corpus.name = name;
			corpus.encoding = encoding;
			corpus.trainFile = trainFile;
			corpus.testFile = testFile;
			corpus.modeltxtFile = modeltxtFile;
			corpus.modelbinaryFile = modelbinaryFile;
			corpus.errorFile = errorFile;
			corpuses[i] = corpus;			
		}
		return corpuses;
	}
	
	public static void main(String[] args) throws IOException {
		String cmd = args[0];
		isBIEO = Boolean.parseBoolean(args[1]);
		
		configStream = ChunkAnalysisBasedWordRun.class.getClassLoader().getResourceAsStream("properties/corpus.properties");
		
		if(cmd.equals("-train")){
			flag = "train";
			runFeature(isBIEO);
		}else if(cmd.equals("-model")){
			flag = "model";
			runFeature(isBIEO);
		}else if(cmd.equals("-evaluate")){
			flag = "evaluate";
			runFeature(isBIEO);
		}else if(cmd.equals("-cross")){
			String corpus = args[2];
			crossValidation(corpus, isBIEO);
		}
	}
	
	/**
	 * 交叉验证
	 * @param corpus 语料的名称
	 * @throws IOException 
	 */
	private static void crossValidation(String corpusName, boolean isBIEO) throws IOException {
		Properties config = new Properties();
		config.load(configStream);
		Corpus[] corpora = getCorporaFromConf(config);
        //定位到某一语料
        Corpus corpus = getCorpus(corpora, corpusName);       
        ObjectStream<String> lineStream = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.trainFile)), corpus.encoding);

        //默认参数
        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(2));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(40));
    
        
        //把刚才属性信息封装

        ChunkAnalysisBasedWordCrossValidation crossValidator = new ChunkAnalysisBasedWordCrossValidation("zh", params);
        ObjectStream<ChunkAnalysisBasedWordSample> sampleStream = new ChunkAnalysisBasedWordSampleStream(lineStream, isBIEO);
        ChunkAnalysisBasedWordContextGenerator contextGen = getWordContextGenerator(config);
        System.out.println(contextGen);
        crossValidator.evaluate(sampleStream, 10, contextGen, isBIEO);
	}

	/**
	 * 根据语料名称获取某个语料
	 * @param corpora 语料内部类数组，包含了所有语料的信息
	 * @param corpusName 语料的名称
	 * @return
	 */
	private static Corpus getCorpus(Corpus[] corpora, String corpusName) {
		for (Corpus c : corpora) {
            if (c.name.equalsIgnoreCase(corpusName)) {
                return c;
            }
        }
        return null;
	}
	
	/**
	 * 根据配置文件配置的信息获取特征
	 * @throws IOException IO异常
	 */
	private static void runFeature(boolean isBIEO) throws IOException {
		//配置参数
		TrainingParameters params = TrainingParameters.defaultParams();
		params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(1));
	
		//加载语料文件
        Properties config = new Properties();
        config.load(configStream);
        Corpus[] corpora = getCorporaFromConf(config);//获取语料

        ChunkAnalysisBasedWordContextGenerator contextGen = getWordContextGenerator(config);
        runFeatureOnCorporaByFlag(contextGen, corpora, params, isBIEO);
	}

	/**
	 * 根据输入的命令执行操作
	 * @param contextGen 上下文生成器
	 * @param corpora 语料
	 * @param params 训练参数
	 * @throws IOException 
	 */
	private static void runFeatureOnCorporaByFlag(ChunkAnalysisBasedWordContextGenerator contextGen, Corpus[] corpora,
			TrainingParameters params, boolean isBIEO) throws IOException {
		if(flag == "train" || flag.equals("train")){
			for (int i = 0; i < corpora.length; i++) {
				trainOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "model" || flag.equals("model")){
			for (int i = 0; i < corpora.length; i++) {
				modelOutOnCorpus(contextGen,corpora[i],params);
			}
		}else if(flag == "evaluate" || flag.equals("evaluate")){
			for (int i = 0; i < corpora.length; i++) {
				evaluateOnCorpus(contextGen,corpora[i],params, isBIEO);
			}
		}
	}

	/**
	 * 得到用于运行的特征类
	 * @param config
	 * @return
	 */
	private static ChunkAnalysisBasedWordContextGenerator getWordContextGenerator(Properties config) {
		String featureClass = config.getProperty("feature.class");
		if(featureClass.equals("hsut.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenratorConf") ||
				featureClass.equals("hsut.tools.ca.feature.ChunkAnalysisBasedWordContextGenratorConf")){
			//初始化需要哪些特征
        	return  new ChunkAnalysisBasedWordContextGenratorConf(config);
		}else{
			return null;
		} 
	}
	
	/**
	 * 读取模型，评估模型
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws UnsupportedOperationException 
	 * @throws IOException 
	 */	
	private static void evaluateOnCorpus(ChunkAnalysisBasedWordContextGenerator contextGen, Corpus corpus,
			TrainingParameters params, boolean isBIEO) throws IOException {
		System.out.println("ContextGenerator: " + contextGen);

        System.out.println("Reading on " + corpus.name + "...");
        ChunkAnalysisBasedWordModel model = ChunkAnalysisBasedWordME.readModel(new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);     
        
        ChunkAnalysisBasedWordME tagger = new ChunkAnalysisBasedWordME(model,isBIEO, contextGen);
       
        ChunkAnalysisMeasure measure = new ChunkAnalysisMeasure();
        ChunkAnalysisBasedWordEvaluator evaluator = null;
        ChunkAnalysisBasedWordErrorPrinter printer = null;
        if(corpus.errorFile != null){
        	System.out.println("Print error to file " + corpus.errorFile);
        	printer = new ChunkAnalysisBasedWordErrorPrinter(new FileOutputStream(corpus.errorFile));    	
        	evaluator = new ChunkAnalysisBasedWordEvaluator(tagger, isBIEO, printer);
        }else{
        	evaluator = new ChunkAnalysisBasedWordEvaluator(tagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStreamNoNull = new PlainTextByLineStream(new FileInputStreamFactory(new File(corpus.testFile)), corpus.encoding);
        ObjectStream<ChunkAnalysisBasedWordSample> sampleStreamNoNull = new ChunkAnalysisBasedWordSampleStream(linesStreamNoNull, isBIEO);
        evaluator.evaluate(sampleStreamNoNull);
        ChunkAnalysisMeasure measureRes = evaluator.getMeasure();
        System.out.println("--------结果--------");
        System.out.println(measureRes);
	}

	/**
	 * 训练模型，输出模型文件
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws UnsupportedOperationException 
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 */	
	private static void modelOutOnCorpus(ChunkAnalysisBasedWordContextGenerator contextGen, Corpus corpus,
			TrainingParameters params) {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        ChunkAnalysisBasedWordME me = new ChunkAnalysisBasedWordME(isBIEO);
        //训练模型
        me.train(new File(corpus.trainFile), new File(corpus.modelbinaryFile), new File(corpus.modeltxtFile), params, contextGen, corpus.encoding);
		
	}

	/**
	 * 训练模型
	 * @param contextGen 上下文特征生成器
	 * @param corpus 语料对象
	 * @param params 训练模型的参数
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */	
	private static void trainOnCorpus(ChunkAnalysisBasedWordContextGenerator contextGen, Corpus corpus, TrainingParameters params) throws IOException {
		System.out.println("ContextGenerator: " + contextGen);
        System.out.println("Training on " + corpus.name + "...");
        ChunkAnalysisBasedWordME me = new ChunkAnalysisBasedWordME(isBIEO);
        //训练模型
        me.train(new File(corpus.trainFile), params, contextGen, corpus.encoding);
	}
}