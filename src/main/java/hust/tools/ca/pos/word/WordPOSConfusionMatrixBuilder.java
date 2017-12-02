package hust.tools.ca.pos.word;


import hust.tools.ca.pos.ConfusionMatrix;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;

/**
 * 根据词性标注结果生成词性标注混淆矩阵
 * 
 * @author 刘小峰
 *
 */
public class WordPOSConfusionMatrixBuilder implements POSTaggerEvaluationMonitor
{
    private ConfusionMatrix matrix = new ConfusionMatrix();

    @Override
    public void missclassified(POSSample reference, POSSample prediction)
    {
        updateMatrix(reference, prediction);
    }

    @Override
    public void correctlyClassified(POSSample reference, POSSample prediction)
    { 
        updateMatrix(reference, prediction);
    }
    
    private void updateMatrix(POSSample reference, POSSample prediction)
    {
        String[] refTags = reference.getTags();
        String[] preTags = prediction.getTags();
        
        matrix.add(refTags, preTags);
    }
    
    public ConfusionMatrix getMatrix()
    {
        return matrix;
    }
}
