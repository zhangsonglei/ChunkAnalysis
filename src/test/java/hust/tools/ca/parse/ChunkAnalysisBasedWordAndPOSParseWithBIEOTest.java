package hust.tools.ca.parse;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;

/**
 *<ul>
 *<li>Description: 测试解析训练语料的类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月5日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSParseWithBIEOTest {
	
	private AbstractChunkAnalysisParse parse;

	//句子开始结尾都不属于组块的句子
	private String sentence1 = "忠诚/a  的/u  [共产主义/n  战士/n]BNP  ，/w  久经考验/l  的/u  [无产阶级/n  革命家/n]BNP  [刘/nr  澜涛/nr  同志/n]BNP  逝世/v";		
	private String[] words1 = new String[]{"忠诚", "的", "共产主义", "战士", "，", "久经考验", "的", "无产阶级", "革命家", "刘", "澜涛", "同志", "逝世"};
	private String[] poses1 = new String[]{"a", "u", "n", "n", "w", "l", "u", "n", "n", "nr", "nr", "n", "v"};
	private String[] tags1 = new String[]{"O", "O", "BNP_B", "BNP_E", "O", "O", "O", "BNP_B", "BNP_E", "BNP_B", "BNP_I", "BNP_E", "O"};
	
	//句子开始为组块的句子
	private String sentence2 = "[目前/t  为止/v]BTP  ，/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  因/p  饿/a  死亡/v  ，/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w  ";
	private String[] words2 = new String[]{"目前", "为止", "，", "灾区", "没有", "一", "人", "因", "冻", "因", "饿", "死亡", "，", "大部分", "牲畜", "也", "没有", "出", "问题", "。"};
	private String[] poses2 = new String[]{"t", "v", "w", "n", "v", "m", "n", "p", "v", "p", "a", "v", "w", "m", "n", "d", "d", "v", "n", "w"};
	private String[] tags2 = new String[]{"BTP_B", "BTP_E", "O", "O", "O", "BNP_B", "BNP_E", "O", "O", "O", "O", "O", "O", "BNP_B", "BNP_E", "BDP_B", "BDP_E", "BVP_B", "BVP_E", "O",};
	
	//句子开始结尾都为组块的句子
	private String sentence3 = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";
	private String[] words3 = new String[]{"党中央", "国务院", "关心", "西藏", "雪灾", "救灾", "工作"};
	private String[] poses3 = new String[]{"nt", "nt", "v", "ns", "n", "vn", "vn"};
	private String[] tags3 = new String[]{"BNP_B", "BNP_E", "O", "BNP_B", "BNP_E", "BNP_B", "BNP_E"};
	
	private ChunkAnalysisBasedWordAndPOSSample sample1;
	private ChunkAnalysisBasedWordAndPOSSample sample2;
	private ChunkAnalysisBasedWordAndPOSSample sample3;
	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEO();
		sample1 = (ChunkAnalysisBasedWordAndPOSSample) parse.parse(sentence1);
		sample2 = (ChunkAnalysisBasedWordAndPOSSample) parse.parse(sentence2);
		sample3 = (ChunkAnalysisBasedWordAndPOSSample) parse.parse(sentence3);
	}

	@Test
	public void testGetWords() {
		assertArrayEquals(words1, sample1.getTokens());
		assertArrayEquals(words2, sample2.getTokens());
		assertArrayEquals(words3, sample3.getTokens());
	}
	
	@Test
	public void testGetPoses() {
		assertArrayEquals(poses1, sample1.getAditionalContext());
		assertArrayEquals(poses2, sample2.getAditionalContext());
		assertArrayEquals(poses3, sample3.getAditionalContext());
	}
	
	@Test
	public void testGetTags() {
		assertArrayEquals(tags1, sample1.getTags());
		assertArrayEquals(tags2, sample2.getTags());
		assertArrayEquals(tags3, sample3.getTags());
	}
}
