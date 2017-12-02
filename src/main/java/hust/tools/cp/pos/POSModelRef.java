package hust.tools.cp.pos;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 基准词性标注模型
 * 
 * 每个词采用最常见的词性标注
 * 
 * @author 刘小峰
 *
 */
public class POSModelRef
{
    private HashMap<String, String> word2MostFreqTag = new HashMap<String, String>();
    
    private String mostFreqTag = "";
    
    /**
     * 从外部模型文件创建模型
     */
    public POSModelRef(DataInput in) throws IOException
    {
        read(in);
    }
    
    /**
     * 从外部模型文件创建模型
     */
    public POSModelRef(InputStream in) throws IOException
    {
        read(new DataInputStream(in));
    }
    
    /**
     * 从模型数据构建模型
     * 
     * @param word2MostFreqTag 每个词的最可能词性映射
     * @param mostFreqTag 模型中最常见的词性
     */
    public POSModelRef(HashMap<String, String> word2MostFreqTag, String mostFreqTag)
    {
        this.word2MostFreqTag = word2MostFreqTag;
        this.mostFreqTag = mostFreqTag;
    }

    /**
     * 获得一个词的最常见词性
     * 
     * 若该词在模型中不存在，返回训练集中最常见的词性
     * @param word 待标注词性的词
     * @return 词的最常见词性，若该词在模型中不存在，返回训练集中最常见的词性
     */
    public String getMostFreqTag(String word)
    {
        if(word2MostFreqTag.containsKey(word))
            return word2MostFreqTag.get(word);
        
        return mostFreqTag;
    }
    
    /**
     * 获得模型中最常见的词性
     * 
     * @return 模型中最常见的词性
     */
    public String getMostFreqTag()
    {
        return mostFreqTag;
    }
    
    public void write(DataOutput out) throws IOException
    {
        out.writeUTF(mostFreqTag);
        
        out.writeInt(word2MostFreqTag.size());
        for(Map.Entry<String, String> e : word2MostFreqTag.entrySet())
        {
            out.writeUTF(e.getKey());
            out.writeUTF(e.getValue());
        }
    }
    
    private void read(DataInput in) throws IOException
    {
        mostFreqTag = in.readUTF();
        
        int sz = in.readInt();
        
        for(int i=0; i<sz; i++)
        {
            String word = in.readUTF();
            String tag = in.readUTF();
            
            word2MostFreqTag.put(word, tag);
        }
    }
}
