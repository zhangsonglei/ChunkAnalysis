package hust.tools.cp.pos;

/**
 * 词性标注器接口
 * 
 * @author 刘小峰
 */
public interface POSTagger
{

    /**
     * 给句子中词进行词性标注
     *
     * @param sentence 待标注词性的句子，由单词组成
     * @return 和句子中单词对应的词性序列
     */
    public String[] tag(String[] sentence);
}
