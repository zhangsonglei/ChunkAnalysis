package hust.tools.ca.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.util.InputStreamFactory;

/**
 *<ul>
 *<li>Description: 获取输入文件流的工厂类
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class FileInputStreamFactory implements InputStreamFactory {

	private File file;

	/**
	 * 构造方法
	 * @param file	输入文件
	 * @throws FileNotFoundException
	 */
	public FileInputStreamFactory(File file) throws FileNotFoundException {
		if(!file.exists())
			throw new FileNotFoundException("文件：" + file + "读取失败");
		else
			this.file = file;
	}
	
	/**
	 * 返回样本输入流
	 * @return 样本输入流
	 */
	public InputStream createInputStream() throws IOException {
		return new FileInputStream(file);
	}
}
