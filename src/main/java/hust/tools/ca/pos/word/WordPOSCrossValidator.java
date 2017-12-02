package hust.tools.ca.pos.word;

import java.io.IOException;
import java.util.HashSet;

import hust.tools.ca.pos.WordPOSMeasure;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

public class WordPOSCrossValidator
{
    private final TrainingParameters params;

    private POSTaggerEvaluationMonitor[] listeners;

    public WordPOSCrossValidator(TrainingParameters trainParam, POSTaggerEvaluationMonitor... listeners)
    {
        this.params = trainParam;
        this.listeners = listeners;
    }

    public void evaluate(ObjectStream<POSSample> samples, int nFolds) throws IOException
    {

        CrossValidationPartitioner<POSSample> partitioner = new CrossValidationPartitioner<>(samples, nFolds);

        while (partitioner.hasNext())
        {
            CrossValidationPartitioner.TrainingSampleStream<POSSample> trainingSampleStream = partitioner.next();

            System.out.println("构建词典...");
            HashSet<String> dict = WordPOSEvalTool.buildDict(trainingSampleStream);

            System.out.println("训练模型...");
            trainingSampleStream.reset();
            long start = System.currentTimeMillis();
            POSModel model = POSTaggerWordME.train(trainingSampleStream, params);
            System.out.println("训练时间： " + (System.currentTimeMillis()-start));

            System.out.println("评价模型...");
            POSTaggerFactory posFactory = new WordPOSTaggerFactory();
            POSTaggerWordME tagger = new POSTaggerWordME(model, posFactory);

            WordPOSEvalTool evaluator = new WordPOSEvalTool(tagger, listeners);

            WordPOSMeasure measure = new WordPOSMeasure(dict);
            evaluator.setMeasure(measure);

            start = System.currentTimeMillis();
            evaluator.evaluate(trainingSampleStream.getTestSampleStream());
            System.out.println("标注时间： " + (System.currentTimeMillis()-start));

            System.out.println(evaluator.getMeasure());
        }
    }
}
