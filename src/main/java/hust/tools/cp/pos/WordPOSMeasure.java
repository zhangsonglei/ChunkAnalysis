package hust.tools.cp.pos;

import java.util.HashSet;

/**
 * 基于词的词性标注效果度量指标
 * 
 * 系统预测结果和参考结果具有相同的词序列
 * 
 * @author 刘小峰
 * 
 */
public final class WordPOSMeasure
{
    /**
     * |target| = true positives + false negatives <br>
     * 目标或参考结果数
     */
    private long target;

    /**
     * 正确预测结果数
     */
    private long truePositive;

    private long sentences;
    private long sentencesOK;

    private HashSet<String> dictionary;

    private long targetIV;
    private long targetOOV;

    private long truePositiveIV;
    private long truePositiveOOV;

    public WordPOSMeasure(HashSet<String> dict)
    {
        this.dictionary = dict;
    }

    public WordPOSMeasure()
    {

    }

    /**
     * 准确率或查准率P
     * 
     * @return 查准率
     */
    public double getPrecisionScore()
    {
        return target > 0 ? (double) truePositive / (double) target : 0;
    }

    /**
     * 登录词准确率或查全率Riv
     * 
     * @return 登录词查全率
     */
    public double getPrecisionScoreIV()
    {
        return targetIV > 0 ? (double) truePositiveIV / (double) targetIV : 0;
    }

    /**
     * 未登录词准确率或查全率Roov
     * 
     * @return 未登录词查全率
     */
    public double getPrecisionScoreOOV()
    {
        return targetOOV > 0 ? (double) truePositiveOOV / (double) targetOOV : 0;
    }

    /**
     * 句子正确率SA
     * 
     * @return 句子正确率
     */
    public double getSentenceAccuracy()
    {
        return sentences > 0 ? (double) sentencesOK / (double) sentences : 0;
    }

    /**
     * 根据参考结果和系统预测更新评价指标
     * 
     * @param words
     *            词序列
     * @param references
     *            参考结果的标注词性序列
     * @param predictions
     *            系统预测结果的词性标注序列
     */
    public void updateScores(final String[] words, final String[] references, final String[] predictions)
    {
        sentences++;

        if (references.length == predictions.length)
        {
            boolean okSent = true;
            for (int i = 0; i < references.length; i++)
            {
                if (!references[i].equals(predictions[i]))
                    okSent = false;
            }

            if (okSent)
                sentencesOK++;
        }

        truePositive += countTruePositivesWithDictionary(words, references, predictions);

        target += references.length;
    }

    /**
     * 合并其他评价结果
     * 
     * @param measure
     *            待合并评价结果
     */
    public void mergeInto(final WordPOSMeasure measure)
    {
        this.target += measure.target;
        this.truePositive += measure.truePositive;

        this.sentences += measure.sentences;
        this.sentencesOK += measure.sentencesOK;

        this.targetIV += measure.targetIV;
        this.truePositiveIV += measure.truePositiveIV;

        this.targetOOV += measure.targetOOV;
        this.truePositiveOOV += measure.truePositiveOOV;
    }

    /**
     * 产生可读的评价结果串
     * 
     * @return 可读的评价结果串
     */
    @Override
    public String toString()
    {
        return "Precision: " + Double.toString(getPrecisionScore()) + "\n" + "PIV: " + Double.toString(getPrecisionScoreIV()) + "\n" + "POOV: " + Double.toString(getPrecisionScoreOOV()) + "\n" + "SentenceAccuray: " + Double.toString(getSentenceAccuracy());
    }

    private int countTruePositivesWithDictionary(final String[] words, final String[] references, final String[] predictions)
    {
        int truePositives = 0;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++)
        {
            boolean isIV = true;

            if (dictionary != null)
            {
                isIV = dictionary.contains(words[referenceIndex]);

                if (isIV)
                    targetIV++;
                else
                    targetOOV++;
            }

            if (references[referenceIndex].equals(predictions[referenceIndex]))
            {
                truePositives++;

                if (dictionary != null)
                {
                    if (isIV)
                        truePositiveIV++;
                    else
                        truePositiveOOV++;
                }
            }

        }

        return truePositives;
    }

    /**
     * 根据参考结果和系统结果统计正确标注的单词数。
     * 
     * 
     * @param references
     *            参考结果的标注词性序列
     * @param predictions
     *            系统预测结果的词性标注序列
     * @return 正确标注的单词数
     */
    static int countTruePositives(final String[] references, final String[] predictions)
    {
        int truePositives = 0;

        for (int referenceIndex = 0; referenceIndex < references.length; referenceIndex++)
        {
            if (references[referenceIndex].equals(predictions[referenceIndex]))
                truePositives++;
        }

        return truePositives;
    }

    /**
     * 根据参考结果和系统结果统计标注准确率。
     * 
     * @param references
     *            参考结果的标注词性序列
     * @param predictions
     *            系统预测结果的词性标注序列
     * @return NaN，如果参考为空
     */
    public static double precision(final String[] references, final String[] predictions)
    {

        if (predictions.length > 0)
        {
            return countTruePositives(references, predictions) / (double) predictions.length;
        }
        else
        {
            return Double.NaN;
        }
    }

}
