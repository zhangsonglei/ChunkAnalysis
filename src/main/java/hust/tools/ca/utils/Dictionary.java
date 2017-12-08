package hust.tools.ca.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 *<ul>
 *<li>Description: 词典类 
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
	public Dictionary(Collection<String> words) {
		this();
		dict.addAll(words);
	}

	/**
	 * 构造方法
	 * @param words	待添加的词数组
	 */
	public Dictionary(String[] words) {
		this(Arrays.asList(words));
	}
	
	/**
	 * 构造方法
	 * @param words	待添加的词列表
	 */
	public Dictionary(List<String> words) {
		this();
		
		for(String word : words)
			add(word);
	}
	
	/**
	 * 返回词典的大小
	 * @return	词典大小
	 */
	public int size() {
		return dict.size();
	}
	
	/**
	 * 向词典中添加元素，添加成功返回true，否则返回false
	 * @param words
	 * @return
	 */
	public boolean add(String word) {
		if(!contains(word)) {
			dict.add(word);
			if(contains(word))
				return true;
		}
		
		return false;
	}
	
	public boolean remove(String word) {
		if(contains(word)) {
			dict.remove(word);
			if(!contains(word))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 查询词典是否包含给定词
	 * @param word	待查询的词
	 * @return		true-存在/false-不存在
	 */
	public boolean contains(String word) {
		return dict.contains(word);
	}
	
	/**
	 * 返回词典的迭代器
	 * @return	迭代器
	 */
	public Iterator<String> iterator() {
		return dict.iterator();
	}
}
