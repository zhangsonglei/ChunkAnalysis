package hust.tools.ca.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 *<ul>
 *<li>Description: 基于词和词性的事件生成类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSampleEventStream extends AbstractEventStream<AbstractChunkAnalysisSample>{

	/**
	 * 上下文生成器
	 */
	private ChunkAnalysisContextGenerator contextgenerator;
	
	/**
	 * 构造方法
	 * @param sampleStream		样本流
	 * @param contextgenerator	上下文生成器
	 */
	public ChunkAnalysisBasedWordAndPOSSampleEventStream(ObjectStream<AbstractChunkAnalysisSample> sampleStream, ChunkAnalysisContextGenerator contextgenerator) {
		super(sampleStream);
		this.contextgenerator = contextgenerator;
	}

	@Override
	protected Iterator<Event> createEvents(AbstractChunkAnalysisSample sample) {
		ChunkAnalysisBasedWordAndPOSSample wordAndPOSSample = (ChunkAnalysisBasedWordAndPOSSample) sample;
		String[] words = wordAndPOSSample.getTokens();
		Object[] poses = wordAndPOSSample.getAditionalContext();
		String[] chunkTags = wordAndPOSSample.getTags();
		List<Event> events = generateEvents(words, chunkTags, poses, contextgenerator);
        return events.iterator();
	}

	/**
	 * 产生事件列表
	 * @param words 			词语数组
	 * @param poses 			词性数组
	 * @param chunkTags			组块标记数组
	 * @param aditionalContext	其他上下文信息
	 * @return	事件列表
	 */
	public static List<Event> generateEvents(String[] words, String[] chunkTags, Object[] poses, ChunkAnalysisContextGenerator contextgenerator) {
		List<Event> events = new ArrayList<Event>(words.length);
		for (int i = 0; i < words.length; i++) {			
			//产生事件的部分
			String[] context = contextgenerator.getContext(i, words, chunkTags, poses);
            events.add(new Event(chunkTags[i], context));
		}
		
		return events;
	}
}

