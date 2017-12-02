package hust.tools.ca.stream;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import hust.tools.ca.parse.ChunkAnalysisParse;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

public class ChunkAnalysisSampleStream extends FilterObjectStream<String,ChunkAnalysisSample>{

	private static Logger logger = Logger.getLogger(ChunkAnalysisSampleStream.class.getName());
	/**
	 * 构造
	 * @param samples 样本流
	 */
	public ChunkAnalysisSampleStream(ObjectStream<String> samples) {
		super(samples);
	}

	/**
	 * 读取样本进行解析
	 * @return 
	 */	
	public ChunkAnalysisSample read() throws IOException {
		String sentence = samples.read();
		ChunkAnalysisParse context = new ChunkAnalysisParse(sentence);
		ChunkAnalysisSample sample = null;
		if(sentence != null){
			if(sentence.compareTo("") != 0){
				try{
					sample = context.parse();;
				}catch(Exception e){
					if (logger.isLoggable(Level.WARNING)) {
						
	                    logger.warning("Error during parsing, ignoring sentence: " + sentence);
	                }
					sample = new ChunkAnalysisSample(new String[]{},new String[]{},new String[]{});
				}

				return sample;
			}else {
				sample = new ChunkAnalysisSample(new String[]{},new String[]{},new String[]{});
				return sample;
			}
		}
		else{
			return null;
		}
	}
}

