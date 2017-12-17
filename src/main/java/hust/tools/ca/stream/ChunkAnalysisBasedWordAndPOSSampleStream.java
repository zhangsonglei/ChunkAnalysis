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
public class ChunkAnalysisBasedWordAndPOSSampleStream extends FilterObjectStream<String, AbstractChunkAnalysisSample>{

	private static Logger logger = Logger.getLogger(ChunkAnalysisBasedWordAndPOSSampleStream.class.getName());
	private AbstractChunkAnalysisParse context;
	
//	private BufferedWriter writer;
	
	/**
	 * 构造方法
	 * @param samples	输入流
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	public ChunkAnalysisBasedWordAndPOSSampleStream(ObjectStream<String> samples, boolean isBIEO) throws FileNotFoundException, UnsupportedEncodingException {
		super(samples);
		if(isBIEO)
			context = new ChunkAnalysisParseWithBIEO();
		else
			context = new ChunkAnalysisParseWithBIO();
		
//		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("E:\\chunk.samples")), "utf8"));
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
					sample = context.parse(sentence);
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) 	
						logger.warning("解析样本时出错, 忽略句子: " + sentence);
	                
					sample = new ChunkAnalysisBasedWordAndPOSSample(new String[]{},new String[]{},new String[]{});
				}
//				writer.write(sample.toString());
//				writer.newLine();
				return sample;
			}else 
				return new ChunkAnalysisBasedWordAndPOSSample(new String[]{},new String[]{},new String[]{});
		}else
			return null;
	}
	
	public void close() throws IOException {
		samples.close();
//		writer.close();
	}
	
	public void reset() throws IOException, UnsupportedOperationException {
	    samples.reset();
	}
}

