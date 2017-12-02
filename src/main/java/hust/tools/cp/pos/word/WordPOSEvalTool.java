package hust.tools.cp.pos.word;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;

import hust.tools.cp.pos.CorpusStat;
import hust.tools.cp.pos.WordPOSMeasure;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.Evaluator;

/**
 * 词性标注评价
 * 
 * @author 刘小峰
 * 
 */
public class WordPOSEvalTool extends Evaluator<POSSample>
{

    private POSTaggerWordME tagger;

    private WordPOSMeasure measure;

    public WordPOSEvalTool(POSTaggerWordME tagger, POSTaggerEvaluationMonitor... listeners)
    {
        super(listeners);
        this.tagger = tagger;
    }

    public WordPOSMeasure getMeasure()
    {
        return measure;
    }

    public void setMeasure(WordPOSMeasure m)
    {
        this.measure = m;
    }

    /**
     * 根据参考词性标注样本进行评价
     * 
     * 
     * @param reference
     *            参考标注
     * 
     * @return 系统标注
     */
    @Override
    protected POSSample processSample(POSSample reference)
    {
        String predictedTags[] = tagger.tag(reference.getSentence());

        POSSample predictions = new POSSample(reference.getSentence(), predictedTags);

        String referenceTags[] = reference.getTags();
        measure.updateScores(reference.getSentence(), referenceTags, predictedTags);

        return predictions;
    }

    public static HashSet<String> buildDict(ObjectStream<POSSample> samples) throws IOException
    {
        HashSet<String> dict = new HashSet<String>();

        POSSample sample;
        while ((sample = samples.read()) != null)
        {
            String[] words = sample.getSentence();

            for (String w : words)
                dict.add(w);
        }

        return dict;
    }

    /**
     * 依据黄金标准评价标注效果
     * 
     * 各种评价指标结果会输出到控制台，错误的结果会输出到指定文件
     * 
     * @param modelFile
     *            系模型文件
     * @param goldFile
     *            黄标准文件
     * @param errorFile
     *            错误输出文件
     * @param encoding
     *            黄金标准文件编码
     * @throws IOException
     */
    public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException
    {
        System.out.println("构建词典...");
        HashSet<String> dict = CorpusStat.buildDict(trainFile.toString(), encoding);

        System.out.println("训练模型...");
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(trainFile), encoding);
        ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);
        POSTaggerFactory posFactory = new WordPOSTaggerFactory();
        System.out.println(posFactory.getPOSContextGenerator());

        long start = System.currentTimeMillis();
        POSModel model = POSTaggerME.train("zh", sampleStream, params, posFactory);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));

        System.out.println("评价模型...");
        POSTaggerWordME tagger = new POSTaggerWordME(model, posFactory);
        WordPOSEvalTool evaluator;
        WordPOSConfusionMatrixBuilder matrixBuilder = null;
        if (errorFile != null)
        {
            WordPOSErrorPrinter errorMonitor = new WordPOSErrorPrinter(new FileOutputStream(errorFile));
            matrixBuilder = new WordPOSConfusionMatrixBuilder();
            evaluator = new WordPOSEvalTool(tagger, errorMonitor, matrixBuilder);
        }
        else
            evaluator = new WordPOSEvalTool(tagger);
        WordPOSMeasure measure = new WordPOSMeasure(dict);
        evaluator.setMeasure(measure);

        ObjectStream<String> goldStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
        ObjectStream<POSSample> testStream = new WordTagSampleStream(goldStream);

        start = System.currentTimeMillis();
        evaluator.evaluate(testStream);
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));

        System.out.println(evaluator.getMeasure());
        
        if(matrixBuilder!=null)
            System.out.println(matrixBuilder.getMatrix());
    }


    private static void usage()
    {
        System.out.println(WordPOSEvalTool.class.getName() + " -data <trainFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>]" + " [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));

        if (errorFile != null)
        {
            eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(new File(trainFile), params, new File(goldFile), encoding, null);
    }
}
