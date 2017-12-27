package hust.tools.ca.stream;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.model.Chunk;
import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIEO;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIEOS;
import hust.tools.ca.parse.ChunkAnalysisBasedWordAndPOSParseWithBIO;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析单元测试
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月27日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSampleTest {

	private String sentence = "[目前/t  为止/v]BTP  ，/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  因/p  饿/a  死亡/v  ，/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w";
	private Chunk[] chunks = new Chunk[]{
			new Chunk("BTP", new String[]{"目前/t", "为止/v"}, 0, 1),
			new Chunk("BNP", new String[]{"一/m", "人/n"}, 5, 6),
			new Chunk("BNP", new String[]{"大部分/m", "牲畜/n"}, 13, 14),
			new Chunk("BDP", new String[]{"也/d", "没有/d"}, 15, 16),
			new Chunk("BVP", new String[]{"出/v", "问题/n"}, 17, 18)};
	private AbstractChunkAnalysisParse parse;
	private AbstractChunkAnalysisSample sampleWithBIEO;
	private AbstractChunkAnalysisSample sampleWithBIO;
	private AbstractChunkAnalysisSample sampleWithBIEOS;


	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEO();
		sampleWithBIEO = parse.parse(sentence);
		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIO();
		sampleWithBIO = parse.parse(sentence);
		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEOS();
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
		assertEquals(sentence, sampleWithBIEOS.toString());
		assertEquals(sentence, sampleWithBIEO.toString());
		assertEquals(sentence, sampleWithBIO.toString());
	}
}
