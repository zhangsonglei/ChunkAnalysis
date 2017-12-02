package hust.tools.ca.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.ObjectStream;

/**
 * 实现ObjectStream，完成其中的read操作，每次读取一颗完整的树
 * ObjectStream<String>接口ObjectStream<T>
 * @author 王馨苇
 *
 */
public class PlainTextByTreeStream  implements ObjectStream<String>{

	private final FileChannel channel;//缓冲道
	private final String encoding;
	private InputStreamFactory inputStreamFactory;
	private BufferedReader in;
	
	/**
	 * 构造
	 * @param inputStreamFactory 输入文件样本流
	 * @param charsetName 编码
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	public PlainTextByTreeStream(InputStreamFactory inputStreamFactory, String charsetName) throws UnsupportedOperationException, IOException{
		this(inputStreamFactory,Charset.forName(charsetName));
	}
	
	/**
	 * 构造
	 * @param inputStreamFactory 输入文件样本流
	 * @param charsetName 编码
	 * @throws UnsupportedOperationException 异常
	 * @throws IOException 异常
	 */
	public PlainTextByTreeStream(InputStreamFactory inputStreamFactory, Charset charsetName) throws UnsupportedOperationException, IOException{
		this.encoding = charsetName.name();
		this.inputStreamFactory  = inputStreamFactory;
		this.channel = null;
		this.reset();
	}
	/**
	 * 关闭文件
	 */
	public void close() throws IOException {
		if (this.in != null && this.channel == null) {
			this.in.close();
		} else if (this.channel != null) {
			this.channel.close();
		}
	}

	/**
	 * 读取训练语料若干行括号表达式表示树，拼接在一行形成括号表达式
	 * @return 拼接后的结果
	 */
	public String read() throws IOException {
		// 一次读取n行,jie
		String line = "";
		String readContent = "";
		int left = 0;
		int right = 0;
		while((line = this.in.readLine()) != null){
			if(line != "" && !line.equals("")){
				line = line.replaceAll("\n","");
				char[] c = line.trim().toCharArray();
				readContent += line.trim();
				for (int i = 0; i < c.length; i++) {
					if(c[i] == '('){
						left++;
					}else if(c[i] == ')'){
						right++;
					}
				}
				if(left == right){
					break;
				}
			}
		}
		return readContent;
	}

	/**
	 * 重置读取的位置
	 */
	public void reset() throws IOException, UnsupportedOperationException {
		
		if (this.inputStreamFactory != null) {
			this.in = new BufferedReader(
					new InputStreamReader(this.inputStreamFactory.createInputStream(), this.encoding));
		}else if (this.channel == null) {
			this.in.reset();
		} else {
			this.channel.position(0L);
			this.in = new BufferedReader(Channels.newReader(this.channel, this.encoding));
		}
	}
}