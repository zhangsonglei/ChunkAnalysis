package hust.tools.ca.feature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisAndPOSBasedWordParseWithBIEO;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;

public class ChunkAnalysisAndPOSContextGenratorConfTest {

	private String sentence = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";
	private String[] words;
	private String[] chunkTags;
	private ChunkAnalysisAndPOSBasedWordContextGeneratorConf contextGeneratorConf;
	private AbstractChunkAnalysisParse parse;
	
	private String[] features0 = new String[]{"w0=党中央", "pf0=党中", "af0=中央", "w1=国务院", "w0w1=党中央国务院", "w2=关心", "w1w2=国务院关心", "w0w1w2=党中央国务院关心"};
	private String[] features1 = new String[]{"w0=国务院", "pf0=国务", "af0=务院",  "w_1=党中央", "p_1=nt", "c_1=BNP_B", "w_1w0=党中央国务院", "w0p_1=国务院nt", "w_1p_1=党中央nt", "w_1w0p_1=党中央国务院nt", "w_1w1=党中央关心", "w1=关心", "w0w1=国务院关心", "w2=西藏", "w1w2=关心西藏", "w0w1w2=国务院关心西藏"};
	private String[] features2 = new String[]{"w0=关心", "pf0=关心", "af0=关心", "w_1=国务院", "p_1=nt", "c_1=BNP_E", "w_1w0=国务院关心", "w0p_1=关心nt", "w_1p_1=国务院nt", "w_1w0p_1=国务院关心nt", "w_1w1=国务院西藏", "w_2=党中央", "p_2=nt", "c_2=BNP_B", "w_2w_1=党中央国务院", "p_2p_1=ntnt", "c_2c_1=BNP_BBNP_E", "w_2w_1w0=党中央国务院关心", "w0p_2=关心nt", "w_1w0w1=国务院关心西藏", "w1=西藏", "w0w1=关心西藏", "w2=雪灾", "w1w2=西藏雪灾", "w0w1w2=关心西藏雪灾"};
	private String[] features3 = new String[]{"w0=西藏", "pf0=西藏", "af0=西藏", "w_1=关心", "p_1=v", "c_1=O", "w_1w0=关心西藏", "w0p_1=西藏v", "w_1p_1=关心v", "w_1w0p_1=关心西藏v", "w_1w1=关心雪灾", "w_2=国务院", "p_2=nt", "c_2=BNP_E", "w_2w_1=国务院关心", "p_2p_1=ntv", "c_2c_1=BNP_EO", "w_2w_1w0=国务院关心西藏", "w0p_2=西藏nt", "w_1w0w1=关心西藏雪灾", "w1=雪灾", "w0w1=西藏雪灾", "w2=救灾", "w1w2=雪灾救灾", "w0w1w2=西藏雪灾救灾"};
	private String[] features4 = new String[]{"w0=雪灾", "pf0=雪灾", "af0=雪灾", "w_1=西藏", "p_1=ns", "c_1=BNP_B", "w_1w0=西藏雪灾", "w0p_1=雪灾ns", "w_1p_1=西藏ns", "w_1w0p_1=西藏雪灾ns", "w_1w1=西藏救灾", "w_2=关心", "p_2=v", "c_2=O", "w_2w_1=关心西藏", "p_2p_1=vns", "c_2c_1=OBNP_B", "w_2w_1w0=关心西藏雪灾", "w0p_2=雪灾v", "w_1w0w1=西藏雪灾救灾","w1=救灾", "w0w1=雪灾救灾", "w2=工作", "w1w2=救灾工作", "w0w1w2=雪灾救灾工作"};
	private String[] features5 = new String[]{"w0=救灾", "pf0=救灾", "af0=救灾", "w_1=雪灾", "p_1=n", "c_1=BNP_E", "w_1w0=雪灾救灾", "w0p_1=救灾n", "w_1p_1=雪灾n", "w_1w0p_1=雪灾救灾n", "w_1w1=雪灾工作", "w_2=西藏", "p_2=ns", "c_2=BNP_B", "w_2w_1=西藏雪灾", "p_2p_1=nsn", "c_2c_1=BNP_BBNP_E", "w_2w_1w0=西藏雪灾救灾", "w0p_2=救灾ns", "w_1w0w1=雪灾救灾工作", "w1=工作", "w0w1=救灾工作"};
	private String[] features6 = new String[]{"w0=工作", "pf0=工作", "af0=工作", "w_1=救灾", "p_1=vn", "c_1=BNP_B", "w_1w0=救灾工作", "w0p_1=工作vn", "w_1p_1=救灾vn", "w_1w0p_1=救灾工作vn", "w_2=雪灾", "p_2=n", "c_2=BNP_E", "w_2w_1=雪灾救灾",  "p_2p_1=nvn", "c_2c_1=BNP_EBNP_B", "w_2w_1w0=雪灾救灾工作", "w0p_2=工作n"};
	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisAndPOSBasedWordParseWithBIEO();
		ChunkAnalysisBasedWordSample sample = parse.parse(sentence);
		words = sample.getWords();
		chunkTags = sample.getChunkTags();
		contextGeneratorConf = new ChunkAnalysisAndPOSBasedWordContextGeneratorConf();
	}

	@Test
	public void testGetContext() {
		assertArrayEquals(features0, contextGeneratorConf.getContext(0, words, chunkTags, null));
		assertArrayEquals(features1, contextGeneratorConf.getContext(1, words, chunkTags, null));
		assertArrayEquals(features2, contextGeneratorConf.getContext(2, words, chunkTags, null));
		assertArrayEquals(features3, contextGeneratorConf.getContext(3, words, chunkTags, null));
		assertArrayEquals(features4, contextGeneratorConf.getContext(4, words, chunkTags, null));
		assertArrayEquals(features5, contextGeneratorConf.getContext(5, words, chunkTags, null));
		assertArrayEquals(features6, contextGeneratorConf.getContext(6, words, chunkTags, null));
	}

}
