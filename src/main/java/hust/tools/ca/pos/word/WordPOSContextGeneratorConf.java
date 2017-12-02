package hust.tools.ca.pos.word;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import opennlp.tools.postag.POSContextGenerator;

/**
 * 基于配置的上下文特征生成
 * 
 * @author 刘小峰
 *
 */
public class WordPOSContextGeneratorConf implements POSContextGenerator
{
    protected final String SE = "*SE*";
    protected final String SB = "*SB*";

    private boolean w_2Set;
    private boolean w_1Set;
    private boolean w0Set;
    private boolean w1Set;
    private boolean w2Set;

    private boolean w_2w_1Set;
    private boolean w_1w0Set;
    private boolean w0w1Set;
    private boolean w1w2Set;

    private boolean w_1w1Set;

    private boolean t_1Set;
    private boolean t_2t_1Set;

    public WordPOSContextGeneratorConf() throws IOException
    {
        Properties featureConf = new Properties();
        InputStream featureStream = WordPOSContextGeneratorConf.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/pos/word/feature.properties");
        featureConf.load(featureStream);

        init(featureConf);
    }

    private void init(Properties config) throws IOException
    {
        w_2Set = (config.getProperty("feature.w_2", "true").equals("true"));
        w_1Set = (config.getProperty("feature.w_1", "true").equals("true"));
        w0Set = (config.getProperty("feature.w0", "true").equals("true"));
        w1Set = (config.getProperty("feature.w1", "true").equals("true"));
        w2Set = (config.getProperty("feature.w2", "true").equals("true"));

        w_2w_1Set = (config.getProperty("feature.w_2w_1", "true").equals("true"));
        w_1w0Set = (config.getProperty("feature.w_1w0", "true").equals("true"));
        w0w1Set = (config.getProperty("feature.w0w1", "true").equals("true"));
        w1w2Set = (config.getProperty("feature.w1w2", "true").equals("true"));

        w_1w1Set = (config.getProperty("feature.w_1w1", "true").equals("true"));

        t_2t_1Set = (config.getProperty("feature.t_2t_1", "true").equals("true"));
        t_1Set = (config.getProperty("feature.t_1", "true").equals("true"));
    }

    public String[] getContext(int index, String[] sequence, String[] priorDecisions, Object[] additionalContext)
    {
        // System.out.println("WordPOSContextGenerator");
        return getContext(index, sequence, priorDecisions);
    }

    public String[] getContext(int index, Object[] tokens, String[] tags)
    {
        String w1, w2 = null, w0, w_1, w_2 = null;
        String t_1, t_2;
        t_1 = t_2 = null;

        w0 = tokens[index].toString();
        if (tokens.length > index + 1)
        {
            w1 = tokens[index + 1].toString();
            if (tokens.length > index + 2)
                w2 = tokens[index + 2].toString();
            else
                w2 = SE; // Sentence End

        }
        else
        {
            w1 = SE; // Sentence End
        }

        if (index - 1 >= 0)
        {
            w_1 = tokens[index - 1].toString();
            t_1 = tags[index - 1];

            if (index - 2 >= 0)
            {
                w_2 = tokens[index - 2].toString();
                t_2 = tags[index - 2];
            }
            else
            {
                w_2 = SB; // Sentence Beginning
            }
        }
        else
        {
            w_1 = SB; // Sentence Beginning
        }

        List<String> e = new ArrayList<>();

        if (w0Set)
            e.add("w0=" + w0);

        if (w_1 != null)
        {
            if (w_1Set)
                e.add("w_1=" + w_1);

            if (w_1w0Set)
                e.add("w_1w0=" + w_1 + "," + w0);

            if (t_1 != null)
            {
                if (t_1Set)
                    e.add("t_1=" + t_1);
            }

            if (w_2 != null)
            {
                if (w_2Set)
                    e.add("w_2=" + w_2);

                if (w_2w_1Set)
                    e.add("w_2w_1=" + w_2 + "," + w_1);

                if (t_2 != null)
                {
                    if (t_2t_1Set)
                        e.add("t_2t_1=" + t_2 + "," + t_1);
                }
            }

            if (w1 != null)
                if (w_1w1Set)
                    e.add("w_1w1=" + w_1 + "," + w1);
        }

        if (w1 != null)
        {
            if (w1Set)
                e.add("w1=" + w1);

            if (w0w1Set)
                e.add("w0w1=" + w0 + "," + w1);

            if (w2 != null)
            {
                if (w2Set)
                    e.add("w2=" + w2);

                if (w1w2Set)
                    e.add("w1w2=" + w1 + "," + w2);
            }
        }

        String[] contexts = e.toArray(new String[e.size()]);

        return contexts;
    }

    @Override
    public String toString()
    {
        return "WordPOSContextGeneratorConf ["+ "w_2Set=" + w_2Set + ", w_1Set=" + w_1Set + ", w0Set=" + w0Set + ", w1Set=" + w1Set + ", w2Set=" + w2Set + ", w_2w_1Set=" + w_2w_1Set + ", w_1w0Set=" + w_1w0Set + ", w0w1Set=" + w0w1Set + ", w1w2Set=" + w1w2Set + ", w_1w1Set=" + w_1w1Set + ", t_1Set=" + t_1Set + ", t_2t_1Set=" + t_2t_1Set + "]";
    }

    
}
