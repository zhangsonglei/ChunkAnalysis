package hust.tools.ca.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *<ul>
 *<li>Description: 基于词的组块分析模型特征生成类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordContextGeneratorConf implements ChunkAnalysisContextGenerator {
	
	//原子特征模版
	private boolean w_1Set;		//前一个词
	private boolean w_2Set;		//前面第二个词
	private boolean w0Set;		//当前词语
	private boolean w1Set;		//后一个词
	private boolean w2Set;		//后面第二个词
	
	private boolean af0Set;		//当前词后缀
	private boolean pf0Set;		//当前词前缀

	private boolean c_2Set;		//前面第二个词的组块标注
	private boolean c_1Set;		//前一个词的组块标注
	
	//组合特征模版
	private boolean w_2w_1Set;
	private boolean w_1w0Set;
	private boolean w0w1Set;
	private boolean w1w2Set;
	private boolean w_1w1Set;
	private boolean w_2w_1w0Set;
	private boolean w_1w0w1Set;
	private boolean w0w1w2Set;
	private boolean c_2c_1Set;

	/**
	 * 构造方法
	 * @throws IOException
	 */
	public ChunkAnalysisBasedWordContextGeneratorConf() throws IOException {
		Properties featureConf = new Properties();
        InputStream featureStream = ChunkAnalysisBasedWordContextGeneratorConf.class.getClassLoader().getResourceAsStream("properties/feature.properties");
        featureConf.load(featureStream);
        
        init(featureConf);
	}
	
	/**
	 * 构造方法
	 * @param properties 配置文件
	 */
	public ChunkAnalysisBasedWordContextGeneratorConf(Properties properties){
        init(properties);
	}
	
	/**
	 * 根据配置参数初始化特征模版
	 * @param config	配置参数
	 */
	private void init(Properties config) {
		//原子特征
		w_2Set = (config.getProperty("feature.w_2", "true").equals("true"));
        w_1Set = (config.getProperty("feature.w_1", "true").equals("true"));
        w0Set = (config.getProperty("feature.w0", "true").equals("true"));
        w1Set = (config.getProperty("feature.w1", "true").equals("true"));
        w2Set = (config.getProperty("feature.w2", "true").equals("true"));
        
        pf0Set = (config.getProperty("feature.pf0", "true").equals("true"));
        af0Set = (config.getProperty("feature.af0", "true").equals("true"));
       
        c_2Set = (config.getProperty("feature.c_2", "true").equals("true"));
        c_1Set = (config.getProperty("feature.c_1", "true").equals("true")); 

        //组合特征
        w_2w_1Set = (config.getProperty("feature.w_2w_1", "true").equals("true"));
        w_1w0Set = (config.getProperty("feature.w_1w0", "true").equals("true"));
    	w0w1Set = (config.getProperty("feature.w0w1", "true").equals("true"));
    	w1w2Set = (config.getProperty("feature.w1w2", "true").equals("true"));
    	w_1w1Set = (config.getProperty("feature.w_1w1", "true").equals("true"));
    	w_2w_1w0Set = (config.getProperty("feature.w_2w_1w0", "true").equals("true"));
    	w_1w0w1Set = (config.getProperty("feature.w_1w0w1", "true").equals("true"));
    	w0w1w2Set = (config.getProperty("feature.w0w1w2", "true").equals("true"));
    	
    	c_2c_1Set = (config.getProperty("feature.c_2c_1", "true").equals("true"));
	}

	@Override
	public String[] getContext(int index, String[] words, String[] chunkTags, Object[] additionalContext) {
		return getContext(index, words, chunkTags);
	}

	/**
	 * 特征生成方法
	 * @param index 	当前位置
	 * @param words 	词语序列
	 * @param chunkTags	组块标记序列
	 * @return
	 */
	private String[] getContext(int index, String[] words, String[] chunkTags) {
		String w_2, w_1, w0, w1, w2, c_2, c_1, pf0, af0;
		w_2 = w_1 = w0 = w1 = w2 = c_2 = c_1 = pf0 = af0 = null;
		
        List<String> features = new ArrayList<String>();
        w0 = words[index];
        
        if(w0.length() > 1) {
        	pf0 = w0.substring(0, 2);
        	af0 = w0.substring(w0.length() - 2, w0.length());       	
        }else
        	pf0 = af0 = w0;
        
        if (words.length > index + 1) {
            w1 = words[index + 1];
            
            if (words.length > index + 2) {
                w2 = words[index + 2];
            }
        }

        if (index - 1 >= 0) {
            w_1 = words[index - 1];
            c_1 = chunkTags[index - 1];
            
            if (index - 2 >= 0) {
                w_2 = words[index - 2];
                c_2 = chunkTags[index - 2];
            }
        }
        
        if(w0Set)
            features.add("w0=" + w0);
        if(af0Set)
            features.add("af0=" + af0);
        if(pf0Set)
            features.add("pf0=" + pf0);
        
        if(w_1 != null) {
            if(w_1Set) 
                features.add("w_1=" + w_1);
            if(c_1Set)
            	features.add("c_1=" + c_1);
            if(w_1w0Set) 
        		features.add("w_1w0=" + w_1 + w0);
 
            if(w1 != null) {
            	if(w_1w1Set)
        			features.add("w_1w1=" + w_1 + w1);
            }
            
            
            if(w_2 != null) {
            	if(w_2Set) 
                    features.add("w_2=" + w_2);
                if(w_2w_1Set) 
            		features.add("w_2w_1=" + w_2 + w_1);
                if(w_2w_1w0Set)
                	features.add("w_2w_1w0=" + w_2 + w_1 + w0);
                if(c_2Set) 
                    features.add("c_2=" + c_2);
                if(c_2c_1Set) 
            		features.add("c_2c_1=" + c_2 + c_1);
   
                if(w1 != null) {
                	if(w_1w0w1Set)
                		features.add("w_1w0w1=" + w_1 + w0 + w1);
                }
            }
        }
        
        if(w1 != null) {
            if(w1Set) 
                features.add("w1=" + w1);
            if(w0w1Set)
        		features.add("w0w1=" + w0 + w1);

            if(w2 != null) {
            	if(w2Set) 
                    features.add("w2=" + w2);
                if(w1w2Set)
            		features.add("w1w2=" + w1 + w2);
                if(w0w1w2Set)
            		features.add("w0w1w2=" + w0 + w1 + w2);
            }
        }

        String[] contexts = features.toArray(new String[features.size()]);

        return contexts;
	}

	@Override
	public String toString() {
		return "ChunkAnalysisContextGenratorConf{" + "w_2Set=" + w_2Set + ", w_1Set=" + w_1Set + 
                ", w0Set=" + w0Set + ", w1Set=" + w1Set + ", w2Set=" + w2Set +              
                ", c_2Set=" + c_2Set + ", c_1Set=" + c_1Set + 
                '}';
	}
}
