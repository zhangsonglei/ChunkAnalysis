package hust.tools.ca.feature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.parse.ChunkAnalysisParse;
import hust.tools.ca.stream.ChunkAnalysisSample;

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
	private ChunkAnalysisContextGenratorConf contextGeneratorConf;
	
	private String[] features0 = new String[]{"w0=党中央", "p0=nt", "r0=3", "w1=国务院", "p1=nt", "r1=3", "w2=关心", "p2=v"};
	private String[] features1 = new String[]{"w0=国务院", "p0=nt", "r0=3", "w_1=党中央", "p_1=nt", "r_1=3", "w1=关心", "p1=v", "r1=2", "w2=西藏", "p2=ns", "c_1=BNP_B"};
	private String[] features2 = new String[]{"w0=关心", "p0=v", "r0=2", "w_1=国务院", "p_1=nt", "r_1=3", "w_2=党中央", "p_2=nt", "w1=西藏", "p1=ns", "r1=2", "w2=雪灾", "p2=n", "c_1=BNP_E"};
	private String[] features3 = new String[]{"w0=西藏", "p0=ns", "r0=2", "w_1=关心", "p_1=v", "r_1=2", "w_2=国务院", "p_2=nt", "w1=雪灾", "p1=n", "r1=2", "w2=救灾", "p2=vn", "c_1=O"};
	private String[] features4 = new String[]{"w0=雪灾", "p0=n", "r0=2", "w_1=西藏", "p_1=ns", "r_1=2", "w_2=关心", "p_2=v", "w1=救灾", "p1=vn", "r1=2", "w2=工作", "p2=vn", "c_1=BNP_B"};
	private String[] features5 = new String[]{"w0=救灾", "p0=vn", "r0=2", "w_1=雪灾", "p_1=n", "r_1=2", "w_2=西藏", "p_2=ns", "w1=工作", "p1=vn", "r1=2", "c_1=BNP_E"};
	private String[] features6 = new String[]{"w0=工作", "p0=vn", "r0=2", "w_1=救灾", "p_1=vn", "r_1=2", "w_2=雪灾", "p_2=n", "c_1=BNP_B"};
	
	@Before
	public void setUp() throws Exception {
		ChunkAnalysisSample sample = new ChunkAnalysisParse(sentence).parse();
		words = sample.getWords();
		poses = sample.getPoses();
		chunkTags = sample.getChunkTags();
		contextGeneratorConf = new ChunkAnalysisContextGenratorConf();
	}

	@Test
	public void testGetContext() {
		assertArrayEquals(contextGeneratorConf.getContext(0, words, poses, chunkTags, null), features0);
		assertArrayEquals(contextGeneratorConf.getContext(1, words, poses, chunkTags, null), features1);
		assertArrayEquals(contextGeneratorConf.getContext(2, words, poses, chunkTags, null), features2);
		assertArrayEquals(contextGeneratorConf.getContext(3, words, poses, chunkTags, null), features3);
		assertArrayEquals(contextGeneratorConf.getContext(4, words, poses, chunkTags, null), features4);
		assertArrayEquals(contextGeneratorConf.getContext(5, words, poses, chunkTags, null), features5);
		assertArrayEquals(contextGeneratorConf.getContext(6, words, poses, chunkTags, null), features6);
	}
}
