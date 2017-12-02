package hust.tools.cp.pos.word;


import java.io.IOException;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.postag.POSContextGenerator;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.TagDictionary;
import opennlp.tools.util.SequenceValidator;


public class WordPOSTaggerFactory extends POSTaggerFactory
{

    public WordPOSTaggerFactory()
    {
    }


    public WordPOSTaggerFactory(Dictionary ngramDictionary, TagDictionary posDictionary)
    {
        super(ngramDictionary, posDictionary);
    }


    public POSContextGenerator getPOSContextGenerator()
    {
        try
        {
            return new WordPOSContextGeneratorConf();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public POSContextGenerator getPOSContextGenerator(int cacheSize)
    {
        try
        {
            return new WordPOSContextGeneratorConf();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public SequenceValidator<String> getSequenceValidator()
    {
        return new WordPOSSequenceValidator();
    }

}
