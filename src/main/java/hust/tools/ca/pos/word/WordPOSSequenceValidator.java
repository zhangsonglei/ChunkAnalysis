package hust.tools.ca.pos.word;

import opennlp.tools.util.SequenceValidator;

public class WordPOSSequenceValidator implements SequenceValidator<String>
{
    public boolean validSequence(int i, String[] inputSequence, String[] outcomesSequence, String outcome)
    {
        return true;
    }
}
