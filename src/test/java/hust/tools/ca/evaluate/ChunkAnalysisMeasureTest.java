package hust.tools.ca.evaluate;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.parse.ChunkAnalysisParseWithBIEO;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import hust.tools.ca.utils.Dictionary;

/**
 *<ul>
 *<li>Description: 测试评价指标 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月8日
 *</ul>
 */
public class ChunkAnalysisMeasureTest {
	
	private ChunkAnalysisMeasure measure;
	
	private ChunkAnalysisParseWithBIEO parse;
	
	String[] words = new String[]{"忠诚", "的", "共产主义", "战士", "，", "久经考验", "无产阶级", "革命家", "同志", "逝世", 
			"目前", "为止", "灾区", "没有", "一", "人", "因", "冻", "饿", "死亡", "大部分", "也", "出", "问题", "。",
			"党中央", "国务院", "关心", "西藏", "雪灾", "救灾", "工作"};
	
	private List<String> refList;
	private String ref1 = "忠诚/a  的/u  [共产主义/n  战士/n]BNP  ，/w  久经考验/l  的/u  [无产阶级/n  革命家/n]BNP  [刘/nr  澜涛/nr  同志/n]BNP  逝世/v";		
	private String ref2 = "[目前/t  为止/v]BTP  ，/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  因/p  饿/a  死亡/v  ，/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w  ";
	private String ref3 = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";
	
	private List<String> preList;
	private String pre1 = "忠诚/a  [的/u  共产主义/n  战士/n]BNP  ，/w  久经考验/l  的/u  [无产阶级/n  革命家/n]BNP  [刘/nr  澜涛/nr]BNP  同志/n  逝世/v";	
	private String pre2 = "[目前/t  为止/v]BVP  ，/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  [因/p  饿/a]BTP  死亡/v  ，/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w  ";
	private String pre3 = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";

	private String[] chunkType = new String[]{"O", "BNP", "BTP", "BVP", "BDP"};
	
	@Before
	public void setUp() throws Exception {
		Dictionary wordDict = new Dictionary(words);
		parse = new ChunkAnalysisParseWithBIEO();
		measure = new ChunkAnalysisMeasure(wordDict);
		
		refList = new ArrayList<>(); refList.add(ref1);	refList.add(ref2); refList.add(ref3);
		preList = new ArrayList<>(); preList.add(pre1); preList.add(pre2); preList.add(pre3);
		
		ChunkAnalysisBasedWordAndPOSSample ref;
		ChunkAnalysisBasedWordAndPOSSample pre;
		for(int i = 0; i < refList.size(); i++) {
			ref = parse.parse(refList.get(i), false);
			pre = parse.parse(preList.get(i), false);
			measure.add(ref, pre);
		}
	}

	@Test
	public void testGetTotalWordCounts() {
		assertEquals(40L, measure.getTotalWordCounts());
	}
	
	@Test
	public void testGetCorrectTaggedWordCounts() {
		assertEquals(30L, measure.getCorrectTaggedWordCounts());
	}
	
	@Test
	public void testGetOOVs() {
		assertEquals(3L, measure.getOOVs());
	}
	
	@Test
	public void testGetCorrectTaggedOOVs() {
		assertEquals(1L, measure.getCorrectTaggedOOVs());
	}

	@Test
	public void testGetAccuracy() {
		assertTrue(30.0/40 == measure.getAccuracy());
	}

	@Test
	public void testGetOOVAccuracy() {
		assertTrue(1.0/3 == measure.getOOVAccuracy());
	}

	@Test
	public void testGetRecall() {
		assertTrue(8.0/11 == measure.getRecall());
	}

	@Test
	public void testGetRecallString() {
		assertTrue(14.0/17 == measure.getRecall(chunkType[0]));
		assertTrue(6.0/8 == measure.getRecall(chunkType[1]));
		assertTrue(0.0/1 == measure.getRecall(chunkType[2]));
		assertTrue(1.0/1 == measure.getRecall(chunkType[3]));
		assertTrue(1.0/1 == measure.getRecall(chunkType[4]));
		
		assertTrue(0.0 == measure.getRecall("BQP"));//不存在的组块标记
	}

	@Test
	public void testGetPrecision() {
		assertTrue(8.0/12 == measure.getPrecision());
	}

	@Test
	public void testGetPrecisionString() {
		assertTrue(14.0/15 == measure.getPrecision(chunkType[0]));
		assertTrue(6.0/8 == measure.getPrecision(chunkType[1]));
		assertTrue(0.0/1 == measure.getPrecision(chunkType[2]));
		assertTrue(1.0/2 == measure.getPrecision(chunkType[3]));
		assertTrue(1.0/1 == measure.getPrecision(chunkType[4]));
		
		assertTrue(0.0 == measure.getPrecision("BQP"));//不存在的组块标记
	}

	@Test
	public void testGetF() {
		assertEquals(16.0/23, measure.getF(), 0.000000000000001);
	}

	@Test
	public void testGetFString() {
		assertEquals(7.0/8, measure.getF(chunkType[0]), 0.000000000000001);
		assertTrue(0.75 == measure.getPrecision(chunkType[1]));
		assertTrue(0.0 == measure.getF(chunkType[2]));
		assertTrue(2.0/3 == measure.getF(chunkType[3]));
		assertTrue(1.0 == measure.getF(chunkType[4]));
		
		assertTrue(0.0 == measure.getF("BQP"));//不存在的组块标记		
	}

}
