package hust.tools.ca.stream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hust.tools.ca.parse.ChunkAnalysisParse;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

/**
 *<ul>
 *<li>Description: 组块分析样本流 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisSampleStream extends FilterObjectStream<String,ChunkAnalysisSample>{

	private static Logger logger = Logger.getLogger(ChunkAnalysisSampleStream.class.getName());

	public ChunkAnalysisSampleStream(ObjectStream<String> samples) {
		super(samples);
	}

	/**
	 * 读取训练语料进行解析
	 * @return 样本
	 */	
	public ChunkAnalysisSample read() throws IOException {
		String sentence = samples.read();
		
		if(sentence != null){
			ChunkAnalysisSample sample = null;
			ChunkAnalysisParse context = new ChunkAnalysisParse(sentence);
			
			try{
				sample = context.parse();
			}catch(Exception e){
				if (logger.isLoggable(Level.WARNING)) 
					logger.warning("解析样本时出错, 忽略句子: " + sentence);
			}

			return sample;
		}else
			return null;
	}
}

