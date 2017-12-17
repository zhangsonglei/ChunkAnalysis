package hust.tools.ca.model;

/**
 *<ul>
 *<li>Description: 组块
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月17日
 *</ul>
 */
public class Chunk {
	
	private String type;
	
	private String string;
	
	private int start;
	
	private int end;
	
	public Chunk(String type, String string, int start, int end) {
		this.type = type;
		this.string = string;
		this.start = start;
		this.end = end;
	}
	
	/**
	 * 返回组块类型
	 * @return	组块类型
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * 返回组块字符串
	 * @return	组块字符串
	 */
	public String getString() {
		return string;
	}
	
	/**
	 * 返回组块起始位置
	 * @return	组块起始位置
	 */
	public int getStart() {
		return start;
	}
	
	/**
	 * 返回组块结束位置
	 * @return	组块结束位置
	 */
	public int getEnd() {
		return end;
	}
	
	@Override
	public String toString() {
		return "["+ string + "]" + type;
	}
}
