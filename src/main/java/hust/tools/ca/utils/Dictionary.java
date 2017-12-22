package hust.tools.ca.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *<ul>
 *<li>Description: 词/字典类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月8日
 *</ul>
 */
public class Dictionary {
		
	private HashSet<String> dict;
	
	/**
	 * 默认构造方法
	 */
	public Dictionary() {
		dict = new HashSet<>();
	}
	
	/**
	 * 构造方法
	 * @param words	待添加的词集合
	 */
	public Dictionary(Collection<String> tokens) {
		this();
		dict.addAll(tokens);
	}

	/**
	 * 构造方法
	 * @param tokens	待添加的词或字数组
	 */
	public Dictionary(String[] tokens) {
		this(Arrays.asList(tokens));
	}
	
	/**
	 * 构造方法
	 * @param tokens	待添加的词或字列表
	 */
	public Dictionary(List<String> tokens) {
		this();
		
		for(String token : tokens)
			add(token);
	}
	
	/**
	 * 返回词或字典的大小
	 * @return	词或字典的大小
	 */
	public int size() {
		return dict.size();
	}
	
	/**
	 * 向词典中添加元素，添加成功返回true，否则返回false
	 * @param token
	 * @return
	 */
	public boolean add(String token) {
		if(!contains(token)) {
			dict.add(token);
			if(contains(token))
				return true;
		}
		
		return false;
	}
	
	public boolean remove(String token) {
		if(contains(token)) {
			dict.remove(token);
			if(!contains(token))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 查询词典是否包含给定词
	 * @param token	待查询的词或字
	 * @return		true-存在/false-不存在
	 */
	public boolean contains(String token) {
		return dict.contains(token);
	}
	
	/**
	 * 返回词典的迭代器
	 * @return	迭代器
	 */
	public Iterator<String> iterator() {
		return dict.iterator();
	}
}
