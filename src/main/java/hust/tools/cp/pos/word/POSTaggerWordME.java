package hust.tools.cp.pos.word;

import java.io.File;
import java.io.IOException;
import java.util.List;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

import hust.tools.cp.pos.POSTaggerProb;

/**
 * 基于词的最大熵中文词性标注器
 * 
 * 该词性标注器的输入为分词后的句子
 * 
 * @author 刘小峰
 * 
 */
public class POSTaggerWordME implements POSTaggerProb
{
    public final static int DEFAULT_BEAM_SIZE = 3;

    protected POSContextGenerator contextGen;

    protected int size;

    private Sequence bestSequence;

    private SequenceClassificationModel<String> model;

    private SequenceValidator<String> sequenceValidator;

    public POSTaggerWordME(File model) throws IOException
    {
        this(new POSModel(model), new WordPOSTaggerFactory());
    }
    
    public POSTaggerWordME(File model, POSTaggerFactory factory) throws IOException
    {
        this(new POSModel(model), factory);
    }

    public POSTaggerWordME(POSModel model, POSTaggerFactory factory)
    {
        int beamSize = DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }

        contextGen = factory.getPOSContextGenerator(beamSize);
        size = beamSize;

        sequenceValidator = factory.getSequenceValidator();

        if (model.getPosSequenceModel() != null)
        {
            this.model = model.getPosSequenceModel();
        }
        else
        {
            this.model = new opennlp.tools.ml.BeamSearch<>(beamSize, model.getPosModel(), 0);
        }

    }

    @Override
    public String[] tag(String[] sentence)
    {
        bestSequence = model.bestSequence(sentence, null, contextGen, sequenceValidator);
        List<String> t = bestSequence.getOutcomes();
        return t.toArray(new String[t.size()]);
    }

    @Override
    public String[][] tag(String[] sentence, int k)
    {
        Sequence[] bestSequences = model.bestSequences(k, sentence, null, contextGen, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++)
        {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
        }

        return tags;
    }

    /**
     * 训练最大熵基于词的词性标注模型
     * 
     * @param sampleStream
     *            训练语料
     * @param params
     *            最大熵模型训练参数
     * 
     * @throws java.io.IOException
     */
    public static POSModel train(ObjectStream<POSSample> sampleStream, TrainingParameters params) throws IOException
    {
        POSModel model = null;

        POSTaggerFactory posFactory = new WordPOSTaggerFactory();
        model = POSTaggerME.train("zh", sampleStream, params, posFactory);

        return model;
    }

}
