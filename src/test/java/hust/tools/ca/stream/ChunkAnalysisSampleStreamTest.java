package hust.tools.ca.stream;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import hust.tools.ca.parse.ChunkAnalysisParse;

/**
 *<ul>
 *<li>Description: 测试流式读取类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月5日
 *</ul>
 */
public class ChunkAnalysisSampleStreamTest {

	private ChunkAnalysisSampleStream sampleStream;
	private String sentence1 = "忠诚/a  的/u  [共产主义/n  战士/n]BNP  ,/w  久经考验/l  的/u  [无产阶级/n  革命家/n]BNP  [刘/nr  澜涛/nr  同志/n]BNP  逝世/v";		
	private String sentence2 = "[党中央/nt  国务院/nt]BNP  关心/v  [西藏/ns  雪灾/n]BNP  [救灾/vn  工作/vn]BNP";
	private String sentence3 = "[目前/t  为止/v]BTP  ,/w  灾区/n  没有/v  [一/m  人/n]BNP  因/p  冻/v  因/p  饿/a  死亡/v  ,/w  [大部分/m  牲畜/n]BNP  [也/d  没有/d]BDP  [出/v  问题/n]BVP  。/w  ";

	private List<ChunkAnalysisSample> sampleList = new ArrayList<>();

	@Before
	public void setUp() throws Exception {
		File file = new File(this.getClass().getClassLoader().getResource("test/train.txt").getFile());
		FileInputStreamFactory inputStreamFactory = new FileInputStreamFactory(file);
		PlainTextFileStream fileStream = new PlainTextFileStream(inputStreamFactory, "utf8");
		sampleStream = new ChunkAnalysisSampleStream(fileStream);
		
		sampleList.add(new ChunkAnalysisParse(sentence1).parse());
		sampleList.add(new ChunkAnalysisParse(sentence2).parse());
		sampleList.add(new ChunkAnalysisParse(sentence3).parse());
	}

	@Test
	public void test() throws IOException {
		List<ChunkAnalysisSample> list = new ArrayList<>();
		
		ChunkAnalysisSample sample = null;
		while((sample = sampleStream.read()) != null) {
			list.add(sample);
		}
		
		assertEquals(list, sampleList);
	}
}