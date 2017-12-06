package hust.tools.ca.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import hust.tools.ca.utils.CommonUtils;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;

/**
 *<ul>
 *<li>Description: 实现ObjectStream接口，读取普通文本中的语料 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class PlainTextFileStream  implements ObjectStream<String> {

	private final FileChannel channel;				//缓冲道
	private final String encoding;					//样本文件编码
	private InputStreamFactory inputStreamFactory;
	private BufferedReader in;

	/**
	 * 构造方法
	 * @param inputStreamFactory			输入流的工厂
	 * @param charsetName					字符编码
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public PlainTextFileStream(InputStreamFactory inputStreamFactory, String charsetName) throws UnsupportedOperationException, IOException{
		this(inputStreamFactory, Charset.forName(charsetName));
	}
	
	/**
	 * 构造方法
	 * @param inputStreamFactory			输入流的工厂
	 * @param charsetName					字符编码
	 * @throws UnsupportedOperationException
	 * @throws IOException
	 */
	public PlainTextFileStream(InputStreamFactory inputStreamFactory, Charset charsetName) throws UnsupportedOperationException, IOException{
		this.encoding = charsetName.name();
		this.inputStreamFactory  = inputStreamFactory;
		this.channel = null;
		this.reset();
	}
	
	/**
	 * 关闭流
	 */
	public void close() throws IOException {
		if (this.in != null && this.channel == null) {
			this.in.close();
		} else if (this.channel != null) {
			this.channel.close();
		}
	}

	/**
	 * 读取训练语料若干行
	 * @return 拼接后的结果
	 */
	public String read() throws IOException {
		String line = null;
		while((line = this.in.readLine()) != null){
			line = CommonUtils.ToDBC(line).trim();
			if(!line.equals(""))
				return line;
		}
		
		return line;
	}

	/**
	 * 重置读取的位置
	 */
	public void reset() throws IOException, UnsupportedOperationException {
		if (this.inputStreamFactory != null)
			this.in = new BufferedReader(
					new InputStreamReader(this.inputStreamFactory.createInputStream(), this.encoding));
		else if (this.channel == null)
			this.in.reset();
		else {
			this.channel.position(0L);
			this.in = new BufferedReader(Channels.newReader(this.channel, this.encoding));
		}
	}
}