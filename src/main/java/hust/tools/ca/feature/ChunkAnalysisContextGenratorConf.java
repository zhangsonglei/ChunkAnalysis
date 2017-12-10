package hust.tools.ca.feature;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *<ul>
 *<li>Description: 特征生成类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisContextGenratorConf implements ChunkAnalysisContextGenerator {
	
	//原子特征模版
	private boolean p0Set;		//当前词性标注
	private boolean w0Set;		//当前词语
	private boolean defaultSet;	//当前词的组块标注
	private boolean c_1Set;		//前一个词的组块标注
	private boolean p_1Set;		//前一个词的词性标注
	private boolean p_2Set;		//前面第二个词的词性标注
	private boolean p1Set;		//后一个词的词性标注
	private boolean p2Set;		//后面第二个词的词性标注
	private boolean w_1Set;		//前一个词
	private boolean w_2Set;		//前面第二个词
	private boolean w1Set;		//后一个词
	private boolean w2Set;		//后面第二个词
	private boolean r0Set;		//当前词的音节数
	private boolean r_1Set;		//前一个词的音节数
	private boolean r1Set;		//后一个词的音节数
	
	//组合特征模版
	private boolean p0p1Set;
	private boolean p_1p0Set;
	private boolean p_1p0p1Set;
	private boolean p1c_1p0Set;
	private boolean c_1p0p_1Set;
	private boolean p_1p1Set;
	private boolean p0c_1Set;
	private boolean w0p2Set;
	private boolean w1p0Set;
	private boolean w_1p0Set;
	private boolean p0w1p_1Set;
	private boolean p_1p1p2Set;
	private boolean p0p1p2Set;
	private boolean p0p2Set;
	private boolean p0p_1w_2Set;
	private boolean p0p_1w1Set;
	private boolean p0p_1w0Set;
	private boolean p_1r_1r0Set;
	private boolean r_1c_1r0Set;
	private boolean r_1p_1p0Set;

	/**
	 * 构造方法
	 * @throws IOException
	 */
	public ChunkAnalysisContextGenratorConf() throws IOException {
		Properties featureConf = new Properties();
        InputStream featureStream = ChunkAnalysisContextGenratorConf.class.getClassLoader().getResourceAsStream("properties/feature.properties");
        featureConf.load(featureStream);
        
        init(featureConf);
	}
	
	/**
	 * 构造方法
	 * @param properties 配置文件
	 */
	public ChunkAnalysisContextGenratorConf(Properties properties){
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
        p_2Set = (config.getProperty("feature.p_2", "true").equals("true"));
        p_1Set = (config.getProperty("feature.p_1", "true").equals("true"));
        p0Set = (config.getProperty("feature.p0", "true").equals("true"));
        p1Set = (config.getProperty("feature.p1", "true").equals("true"));
        p2Set = (config.getProperty("feature.p2", "true").equals("true"));
        c_1Set = (config.getProperty("feature.c_1", "true").equals("true")); 
        r0Set = (config.getProperty("feature.r0", "true").equals("true"));
        r_1Set = (config.getProperty("feature.r_1", "true").equals("true"));
        r1Set = (config.getProperty("feature.r1", "true").equals("true"));
        defaultSet = (config.getProperty("feature.default", "true").equals("true"));
        
        //组合特征
    	p0p1Set = (config.getProperty("feature.p0p1", "true").equals("true"));
    	p_1p0Set = (config.getProperty("feature.p_1p0", "true").equals("true"));
    	p_1p0p1Set = (config.getProperty("feature.p_1p0p1", "true").equals("true"));
    	p1c_1p0Set = (config.getProperty("feature.p1c_1p0", "true").equals("true"));
    	c_1p0p_1Set = (config.getProperty("feature.c_1p0p_1", "true").equals("true"));
    	p_1p1Set = (config.getProperty("feature.p_1p1", "true").equals("true"));
    	p0c_1Set = (config.getProperty("feature.p0c_1", "true").equals("true"));
    	w0p2Set = (config.getProperty("feature.w0p2", "true").equals("true"));
    	w1p0Set = (config.getProperty("feature.w1p0", "true").equals("true"));
    	w_1p0Set = (config.getProperty("feature.w_1p0", "true").equals("true"));
    	p0w1p_1Set = (config.getProperty("feature.p0w1p_1", "true").equals("true"));
    	p_1p1p2Set = (config.getProperty("feature.p_1p1p2", "true").equals("true"));
    	p0p1p2Set = (config.getProperty("feature.p0p1p2", "true").equals("true"));
    	p0p2Set = (config.getProperty("feature.p0p2", "true").equals("true"));
    	p0p_1w_2Set = (config.getProperty("feature.p0p_1w_2", "true").equals("true"));
    	p0p_1w1Set = (config.getProperty("feature.p0p_1w1", "true").equals("true"));
    	p0p_1w0Set = (config.getProperty("feature.p0p_1w0", "true").equals("true"));
    	p_1r_1r0Set = (config.getProperty("feature.p_1r_1r0", "true").equals("true"));
    	r_1c_1r0Set = (config.getProperty("feature.r_1c_1r0", "true").equals("true"));
    	r_1p_1p0Set = (config.getProperty("feature.r_1p_1p0", "true").equals("true"));
	}

	@Override
	public String[] getContext(int index, String[] words, String[] poses, String[] chunkTags, Object[] additionalContext) {
		return getContext(index, words, poses, chunkTags);
	}

	/**
	 * 特征生成方法
	 * @param index 	当前位置
	 * @param words 	词语序列
 	 * @param poses 	词性标记
	 * @param chunkTags	组块标记序列
	 * @return
	 */
	private String[] getContext(int index, String[] words, String[] poses, String[] chunkTags) {
		String w1, w2, w0, w_1, w_2, r0, r1, r_1;
		w1 = w2 = w0 = w_1 = w_2 = r0 = r1 = r_1 = null;
        
		String p1, p2, p0, p_1, p_2;
		p1 = p2 = p0 = p_1 = p_2 = null;
        
        String c_1 = null;
        
        List<String> features = new ArrayList<String>();
        w0 = words[index];
        p0 = poses[index];
        r0 = String.valueOf(w0.length());
        
        if (words.length > index + 1) {
            w1 = words[index + 1];
            p1 = poses[index + 1];
            r1 = String.valueOf(w1.length());
            
            if (words.length > index + 2) {
                w2 = words[index + 2];
                p2 = poses[index + 2];
            }
        }

        if (index - 1 >= 0) {
            w_1 = words[index - 1];
            p_1 = poses[index - 1];
            c_1 = chunkTags[index - 1];
            r_1 = String.valueOf(w_1.length());
            
            if (index - 2 >= 0) {
                w_2 = words[index - 2];
                p_2 = poses[index - 2];
            }
        }
        
        //原子特征
        if (w0Set) {
            features.add("w0=" + w0);
        }
        if(p0Set){
        	features.add("p0=" + p0);
        }
        if(r0Set) {
        	features.add("r0=" + r0);
        }
        if (w_1 != null) {
            if (w_1Set) {
                features.add("w_1=" + w_1);
            }
            if(p_1Set){
            	features.add("p_1=" + p_1);
            }
            if(r_1Set) {
            	features.add("r_1=" + r_1);
            }

            if (w_2 != null) {
                if (w_2Set) {
                    features.add("w_2=" + w_2);
                }
                if (p_2Set) {
                    features.add("p_2=" + p_2);
                }
            }
        }
        if (w1 != null) {
            if (w1Set) {
                features.add("w1=" + w1);
            }
            if (p1Set) {
                features.add("p1=" + p1);
            }
            if(r1Set) {
            	features.add("r1=" + r1);
            }
            
            if (w2 != null) {
                if (w2Set) {
                    features.add("w2=" + w2);
                }
                if (p2Set) {
                    features.add("p2=" + p2);
                }
            }
        }
        if(c_1 != null){
        	if (c_1Set) {
                features.add("c_1=" + c_1);
            }
        }
        
        //复合特征
        if(w_1 != null) {
        	if(r_1c_1r0Set)
        		features.add("r_1c_1r0=" + r_1 + c_1 + r0);
        	
        	//训练语料中存在词性
        	if(p0 != null) {
        		if(p0c_1Set)
            		features.add("p0c_1=" + p0 + c_1);
        		if(w_1p0Set)
            		features.add("w_1p0=" + w_1 + p0);
        		if(p_1p0Set)
            		features.add("p_1p0=" + p_1 + p0);
        		if(c_1p0p_1Set)
        			features.add("c_1p0p_1=" + c_1 + p0 + p_1);
        		if(p0p_1w0Set)
            		features.add("p0p_1w0=" + p0 + p_1 + w0);
            	if(p_1r_1r0Set)
            		features.add("p_1r_1r0=" + p_1 + r_1 + r0);
            	if(r_1p_1p0Set) 
            		features.add("r_1p_1p0=" + r_1 + p_1 + p0);
            	
            	if(w1 != null) {
            		if(p_1p0p1Set)
            			features.add("p_1p0p1=" + p_1 + p0 + p1);
            		if(p_1p1Set)
            			features.add("p_1p1=" + p_1 + p1);
            		if(p0w1p_1Set) 
            			features.add("p0w1p_1=" + p0 + w1 + p_1);
            		if(p1c_1p0Set)
            			features.add("p1c_1p0=" + p1 + c_1 + p0);
            		if(p0p_1w1Set)
            			features.add("p0p_1w1=" + p0 + p_1 + w1);

            		if(w2 != null) {
            			if(p_1p1p2Set)
            				features.add("p_1p1p2=" + p_1 + p1 + p2);
            		}
            	}
            	
            	if(w_2 != null) {
        			if(p0p_1w_2Set)
        				features.add("p0p_1w_2=" + p0 + p_1 + w_2);
        		}
        	}
        }
        
        //训练语料中带词性
        if(p1 != null) {
        	if(w1p0Set)
        		features.add("w1p0=" + w1 + p0);
        	if(p0p1Set)
        		features.add("p0p1=" + p0 + p1);
        	
        	if(p2!=null) {
        		if(p0p1p2Set)
        			features.add("p0p1p2=" + p0 + p1 + p2);
        		if(p0p2Set)
        			features.add("p0p2=" + p0 + p2);
        		if(w0p2Set) 
        			features.add("w0p2=" + w0 + p2);
        	}
        }

        String[] contexts = features.toArray(new String[features.size()]);

        return contexts;
	}

	@Override
	public String toString() {
		return "ChunkAnalysisContextGenratorConf{" + "w_2Set=" + w_2Set + ", w_1Set=" + w_1Set + 
                ", w0Set=" + w0Set + ", w1Set=" + w1Set + ", w2Set=" + w2Set + 
                ", r_1Set=" + r_1Set + ", r0Set=" + r0Set + 
                ", r1Set=" + r1Set +
                ", c_1Set=" + c_1Set +               
                ", p_2Set=" + p_2Set + ", p_1Set=" + p_1Set + ", p0Set=" + p0Set + 
                ", p1Set=" + p1Set + ", p2Set=" + p2Set + 
                ", defaultSet=" + defaultSet + 
                '}';
	}
}
