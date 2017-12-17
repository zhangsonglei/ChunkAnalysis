package hust.tools.ca.stream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisParseWithBIO;
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
@SuppressWarnings("unused")
public class ChunkAnalysisBasedWordSampleStream extends FilterObjectStream<String, AbstractChunkAnalysisSample>{

	private static Logger logger = Logger.getLogger(ChunkAnalysisBasedWordSampleStream.class.getName());
	private AbstractChunkAnalysisParse paeser;
	
	/**
	 * 构造方法
	 * @param samples	输入流
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public ChunkAnalysisBasedWordSampleStream(ObjectStream<String> samples, boolean isBIEO) throws FileNotFoundException, UnsupportedEncodingException {
		super(samples);
		if(isBIEO)
			paeser = new ChunkAnalysisParseWithBIEO();
		else
			paeser = new ChunkAnalysisParseWithBIO();
	}

	/**
	 * 读取训练语料进行解析
	 * @return 样本
	 */	
	public AbstractChunkAnalysisSample read() throws IOException {
		String sentence = samples.read();
		
		if(sentence != null){
			AbstractChunkAnalysisSample sample = null;
			if(sentence.compareTo("") != 0){
				try{
					sample = paeser.parse(sentence);
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING))
						logger.warning("解析样本时出错, 忽略句子: " + sentence);
	                
					sample = new ChunkAnalysisBasedWordSample(new String[]{}, new String[]{});
				}

				return sample;
			}else 
				return new ChunkAnalysisBasedWordSample(new String[]{}, new String[]{});
		}else
			return null;
	}
	
	public void close() throws IOException {
		samples.close();
	}
	
	public void reset() throws IOException, UnsupportedOperationException {
	    samples.reset();
	}
}

