package hust.tools.ca.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGenerator;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 *<ul>
 *<li>Description: 事件生成类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisSampleEventBasedWordAndPOS extends AbstractEventStream<ChunkAnalysisBasedWordAndPOSSample>{

	/**
	 * 上下文生成器
	 */
	private ChunkAnalysisBasedWordAndPOSContextGenerator contextgenerator;
	
	/**
	 * 构造方法
	 * @param sampleStream		样本流
	 * @param contextgenerator	上下文生成器
	 */
	public ChunkAnalysisSampleEventBasedWordAndPOS(ObjectStream<ChunkAnalysisBasedWordAndPOSSample> sampleStream,ChunkAnalysisBasedWordAndPOSContextGenerator contextgenerator) {
		super(sampleStream);
		this.contextgenerator = contextgenerator;
	}

	@Override
	protected Iterator<Event> createEvents(ChunkAnalysisBasedWordAndPOSSample sample) {
		String[] words = sample.getWords();
		String[] poses = sample.getPoses();
		String[] chunkTags = sample.getChunkTags();
		String[][] aditionalContext = sample.getAditionalContext();
		List<Event> events = generateEvents(words,poses, chunkTags, aditionalContext);
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
	private List<Event> generateEvents(String[] words, String[] poses, String[] chunkTags, String[][] aditionalContext) {
		List<Event> events = new ArrayList<Event>(words.length);
		for (int i = 0; i < words.length; i++) {			
			//产生事件的部分
			String[] context = contextgenerator.getContext(i, words, poses, chunkTags, aditionalContext);
            events.add(new Event(chunkTags[i], context));
		}
		
		return events;
	}
}

