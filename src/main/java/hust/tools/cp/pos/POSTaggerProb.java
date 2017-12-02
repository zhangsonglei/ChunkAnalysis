
package hust.tools.cp.pos;

/**
 * 概率词性标注器接口
 * 
 * @author 刘小峰
 */
public interface POSTaggerProb extends POSTagger
{
    /**
     * 给句子中词进行词性标注，并返回至多k个概率最高的标注序列
     * 
     * 返回多个序列按照概率非升序排列
     *
     * @param sentence 待标注词性的句子，由单词组成
     * @param k 至多k个
     * @return 至多k个概率最高的标注序列
     */
    public String[][] tag(String[] sentence, int k);
}
