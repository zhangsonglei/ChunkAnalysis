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
public class ChunkAnalysisBasedWordAndPOSParseWithBIEOSTest {
	
	private AbstractChunkAnalysisParse parse;

	//句子开始结尾都不属于组块的句子
	private String sentence1 = "[Friday/NNP]NP  ['s/POS  Market/NNP  Activity/NN]NP";		
	private String[] words1 = new String[]{"Friday", "'s", "Market", "Activity"};
	private String[] poses1 = new String[]{"NNP", "POS", "NNP", "NN"};
	private String[] tags1 = new String[]{"NP_S", "NP_B", "NP_I", "NP_E"};
	
	//句子开始为组块的句子
	private String sentence2 = "[The/DT  dollar/NN]NP  [posted/VBD]VP  [gains/NNS]NP  [in/IN]PP  [quiet/JJ  trading/NN]NP  [as/IN]SBAR  [concerns/NNS]NP  [about/IN]PP  [equities/NNS]NP  [abated/VBN]VP  ./.";
	private String[] words2 = new String[]{"The", "dollar", "posted", "gains", "in", "quiet", "trading", "as", "concerns", "about", "equities", "abated", "."};
	private String[] poses2 = new String[]{"DT", "NN", "VBD", "NNS", "IN", "JJ", "NN", "IN", "NNS", "IN", "NNS", "VBN", "."};
	private String[] tags2 = new String[]{"NP_B", "NP_E", "VP_S", "NP_S", "PP_S", "NP_B", "NP_E", "SBAR_S", "NP_S", "PP_S", "NP_S", "VP_S", "O"};
	
	//句子开始结尾都为组块的句子
	private String sentence3 = "And/CC  [ship/NN  lines/NNS]NP  [carrying/VBG]VP  [containers/NNS]NP  [are/VBP  also/RB  trying/VBG  to/TO  raise/VB]VP  [their/PRP$  rates/NNS]NP  ./.";
	private String[] words3 = new String[]{"And", "ship", "lines", "carrying", "containers", "are", "also", "trying", "to", "raise", "their", "rates", "."};
	private String[] poses3 = new String[]{"CC", "NN", "NNS", "VBG", "NNS", "VBP", "RB", "VBG", "TO", "VB", "PRP$", "NNS", "."};
	private String[] tags3 = new String[]{"O", "NP_B", "NP_E", "VP_S", "NP_S", "VP_B", "VP_I", "VP_I", "VP_I", "VP_E", "NP_B", "NP_E", "O"};
	
	private ChunkAnalysisBasedWordAndPOSSample sample1;
	private ChunkAnalysisBasedWordAndPOSSample sample2;
	private ChunkAnalysisBasedWordAndPOSSample sample3;
	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisBasedWordAndPOSParseWithBIEOS();
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
		assertArrayEquals(poses1, sample1.getPoses());
		assertArrayEquals(poses2, sample2.getPoses());
		assertArrayEquals(poses3, sample3.getPoses());
	}
	
	@Test
	public void testGetTags() {
		assertArrayEquals(tags1, sample1.getTags());
		assertArrayEquals(tags2, sample2.getTags());
		assertArrayEquals(tags3, sample3.getTags());
	}
}
