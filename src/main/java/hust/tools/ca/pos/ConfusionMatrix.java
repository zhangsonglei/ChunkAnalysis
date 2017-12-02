package hust.tools.ca.pos;

import java.util.HashMap;

/**
 * 词性标注混淆矩阵
 * 
 * 要求参考标注和预测标注的词要一一对应。
 * 
 * @author 刘小峰
 *
 */
public class ConfusionMatrix
{
    private HashMap<String, HashMap<String,Integer>> countTag2Tag = new HashMap<String, HashMap<String,Integer>>();
    
    /**
     * 添加标注结果进行统计
     * 
     * @param refTags 参考标注词性结果
     * @param preTags 预测标注词性结果
     */
    public void add(String[] refTags, String[] preTags)
    {
        int refLen = refTags.length;
        int preLen = preTags.length;
        
        if(refLen != preLen)
            return;
        
        for(int i=0; i<refLen; i++)
        {
            String refTag = refTags[i];
            String preTag = preTags[i];
            
            add(refTag, preTag);
        }
    }


    public void add(String refTag, String preTag)
    {
        HashMap<String,Integer> tagCount;
        if(countTag2Tag.containsKey(refTag))
            tagCount = countTag2Tag.get(refTag);
        else
            tagCount = new HashMap<String,Integer>();
        
        int count = 0;
        if(tagCount.containsKey(preTag))
            count = tagCount.get(preTag);
        count++;
        tagCount.put(preTag, count);
        
        countTag2Tag.put(refTag, tagCount);
    }
    
    /**
     * 所有词性
     * @return 所有词性
     */
    public String[] getTags()
    {
        return countTag2Tag.keySet().toArray(new String[countTag2Tag.keySet().size()]);
    }
    
    /**
     * 一个词性被标注为另外一个词性的数量
     * @param sourceTag 源词性
     * @param targetTag 目标词性
     * @return
     */
    public int getConfusionValue(String sourceTag, String targetTag)
    {
        if(!countTag2Tag.containsKey(sourceTag))
            return 0;
        
        HashMap<String,Integer> tagCount = countTag2Tag.get(sourceTag);
        if(!tagCount.containsKey(targetTag))
            return 0;
        
        return tagCount.get(targetTag);
    }
    
    public String toString()
    {
        String buf = new String(" ");
        
        String[] tags = getTags();
        for(int i=0; i<tags.length; i++)
        {
            buf += "\t" + tags[i];
        }
        
        buf += "\n";
        
        for(int i=0; i<tags.length; i++)
        {
            buf += tags[i];
            for(int j=0; j<tags.length; j++)
            {
                String ref = tags[i];
                String pre = tags[j];
                
                int c = getConfusionValue(ref, pre);
                
                buf += "\t" + c;
            }
            
            buf += "\n";
        }
        
        return buf;
    }
}
