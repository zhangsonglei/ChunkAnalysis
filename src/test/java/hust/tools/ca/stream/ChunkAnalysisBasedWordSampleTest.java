package hust.tools.ca.stream;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.model.Chunk;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIEOS;
import hust.tools.ca.parse.ChunkAnalysisBasedWordParseWithBIO;

/**
 *<ul>
 *<li>Description: 基于词的组块分析单元测试
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月27日
 *</ul>
 */
public class ChunkAnalysisBasedWordSampleTest {

	private String sentence = "[目前/t  为止/v]BTP  ，/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  因/p  饿/a  死亡/v  ，/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w";
	private Chunk[] chunks = new Chunk[]{
			new Chunk("BTP", new String[]{"目前", "为止"}, 0, 1),
			new Chunk("BNP", new String[]{"一", "人"}, 5, 6),
			new Chunk("BNP", new String[]{"大部分", "牲畜"}, 13, 14),
			new Chunk("BDP", new String[]{"也", "没有"}, 15, 16),
			new Chunk("BVP", new String[]{"出", "问题"}, 17, 18)};
	private AbstractChunkAnalysisParse parse;
	private AbstractChunkAnalysisSample sampleWithBIEO;
	private AbstractChunkAnalysisSample sampleWithBIO;
	private AbstractChunkAnalysisSample sampleWithBIEOS;


	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisBasedWordParseWithBIEO();
		sampleWithBIEO = parse.parse(sentence);
		parse = new ChunkAnalysisBasedWordParseWithBIO();
		sampleWithBIO = parse.parse(sentence);
		parse = new ChunkAnalysisBasedWordParseWithBIEOS();
		sampleWithBIEOS = parse.parse(sentence);
	}

	@Test
	public void testToChunk() {
		Chunk[] temps = sampleWithBIEO.toChunk();
		assertArrayEquals(chunks, temps);
		temps = sampleWithBIEOS.toChunk();
		assertArrayEquals(chunks, temps);
		temps = sampleWithBIO.toChunk();
		assertArrayEquals(chunks, temps);
	}
	
	@Test
	public void testToString() {
		String string = "[目前  为止]BTP  ，  灾区  没有  [一  人]BNP  因  冻  因  饿  死亡  ，  [大部分  牲畜]BNP  [也  没有]BDP  [出  问题]BVP  。";
		assertEquals(string, sampleWithBIEOS.toString());
		assertEquals(string, sampleWithBIEO.toString());
		assertEquals(string, sampleWithBIO.toString());
	}
}
