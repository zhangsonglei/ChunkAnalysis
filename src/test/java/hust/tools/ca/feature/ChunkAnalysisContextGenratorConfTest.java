package hust.tools.ca.feature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.parse.AbstractChunkAnalysisParse;
import hust.tools.ca.parse.ChunkAnalysisParseWithBIEO;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;

/**
 *<ul>
 *<li>Description: 测试特征生成类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月5日
 *</ul>
 */
public class ChunkAnalysisContextGenratorConfTest {
	private String sentence = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";
	private String[] words;
	private String[] poses;
	private String[] chunkTags;
	private ChunkAnalysisBasedWordAndPOSContextGenratorConf contextGeneratorConf;
	private AbstractChunkAnalysisParse parse;
	
	private String[] features0 = new String[]{"w0=党中央", "p0=nt", "w1=国务院", "p1=nt", "w0w1=党中央国务院", "w2=关心", "p2=v", "w1w2=国务院关心", "p0p1=ntnt", "p1p2=ntv"};
	private String[] features1 = new String[]{"w0=国务院", "p0=nt", "w_1=党中央", "p_1=nt", "c_1=BNP_B", "w_1w0=党中央国务院", "w1=关心", "p1=v", "w0w1=国务院关心", "w2=西藏", "p2=ns", "w1w2=关心西藏", "p_1p0=ntnt", "p0p1=ntv", "p1p2=vns"};
	private String[] features2 = new String[]{"w0=关心", "p0=v", "w_1=国务院", "p_1=nt", "c_1=BNP_I", "w_1w0=国务院关心", "w_2=党中央", "p_2=nt", "c_2=BNP_B", "w_2w_1=党中央国务院", "c_2c_1=BNP_BBNP_I", "w1=西藏", "p1=ns", "w0w1=关心西藏", "w2=雪灾", "p2=n", "w1w2=西藏雪灾", "p_1p0=ntv", "p_2p_1=ntnt", "p0p1=vns", "p1p2=nsn"};
	private String[] features3 = new String[]{"w0=西藏", "p0=ns", "w_1=关心", "p_1=v", "c_1=O", "w_1w0=关心西藏", "w_2=国务院", "p_2=nt", "c_2=BNP_I", "w_2w_1=国务院关心", "c_2c_1=BNP_IO", "w1=雪灾", "p1=n", "w0w1=西藏雪灾", "w2=救灾", "p2=vn", "w1w2=雪灾救灾", "p_1p0=vns", "p_2p_1=ntv", "p0p1=nsn", "p1p2=nvn"};
	private String[] features4 = new String[]{"w0=雪灾", "p0=n", "w_1=西藏", "p_1=ns", "c_1=BNP_B", "w_1w0=西藏雪灾", "w_2=关心", "p_2=v", "c_2=O", "w_2w_1=关心西藏", "c_2c_1=OBNP_B", "w1=救灾", "p1=vn", "w0w1=雪灾救灾", "w2=工作", "p2=vn", "w1w2=救灾工作", "p_1p0=nsn", "p_2p_1=vns", "p0p1=nvn", "p1p2=vnvn"};
	private String[] features5 = new String[]{"w0=救灾", "p0=vn", "w_1=雪灾", "p_1=n", "c_1=BNP_I", "w_1w0=雪灾救灾", "w_2=西藏", "p_2=ns", "c_2=BNP_B", "w_2w_1=西藏雪灾", "c_2c_1=BNP_BBNP_I", "w1=工作", "p1=vn", "w0w1=救灾工作", "p_1p0=nvn", "p_2p_1=nsn", "p0p1=vnvn"};
	private String[] features6 = new String[]{"w0=工作", "p0=vn", "w_1=救灾", "p_1=vn", "c_1=BNP_B", "w_1w0=救灾工作", "w_2=雪灾", "p_2=n", "c_2=BNP_I", "w_2w_1=雪灾救灾", "c_2c_1=BNP_IBNP_B", "p_1p0=vnvn", "p_2p_1=nvn"};
	
	@Before
	public void setUp() throws Exception {
		parse = new ChunkAnalysisParseWithBIEO();
		AbstractChunkAnalysisSample sample = parse.parse(sentence);
		words = sample.getWords();
		poses = sample.getPoses();
		chunkTags = sample.getChunkTags();
		contextGeneratorConf = new ChunkAnalysisBasedWordAndPOSContextGenratorConf();
	}

	@Test
	public void testGetContext() {
		assertArrayEquals(features0, contextGeneratorConf.getContext(0, words, poses, chunkTags, null));
		assertArrayEquals(features1, contextGeneratorConf.getContext(1, words, poses, chunkTags, null));
		assertArrayEquals(features2, contextGeneratorConf.getContext(2, words, poses, chunkTags, null));
		assertArrayEquals(features3, contextGeneratorConf.getContext(3, words, poses, chunkTags, null));
		assertArrayEquals(features4, contextGeneratorConf.getContext(4, words, poses, chunkTags, null));
		assertArrayEquals(features5, contextGeneratorConf.getContext(5, words, poses, chunkTags, null));
		assertArrayEquals(features6, contextGeneratorConf.getContext(6, words, poses, chunkTags, null));
	}
}
