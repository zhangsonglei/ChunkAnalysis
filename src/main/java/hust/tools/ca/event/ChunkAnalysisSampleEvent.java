package hust.tools.ca.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.stream.ChunkAnalysisSample;
import opennlp.tools.ml.model.Event;
import opennlp.tools.util.AbstractEventStream;
import opennlp.tools.util.ObjectStream;

/**
 * 事件生成类
 * @author 王馨苇
 *
 */
public class ChunkAnalysisSampleEvent extends AbstractEventStream<ChunkAnalysisSample>{

	private ChunkAnalysisContextGenerator contextgenerator;
	
	/**
	 * 构造
	 * @param samples 样本流
	 * @param generator 上下文产生器
	 */
	public ChunkAnalysisSampleEvent(ObjectStream<ChunkAnalysisSample> sampleStream,ChunkAnalysisContextGenerator contextgenerator) {
		super(sampleStream);
		this.contextgenerator = contextgenerator;
	}

	@Override
	protected Iterator<Event> createEvents(ChunkAnalysisSample sample) {
		String[] words = sample.getWords();
		String[] poses = sample.getPoses();
		String[] chunkTags = sample.getTags();
		String[][] aditionalContext = sample.getAditionalContext();
		List<Event> events = generateEvents(words,poses, chunkTags, aditionalContext);
        return events.iterator();
	}

	/**
	 * 产生事件列表
	 * @param words 			词语序列
	 * @param poses 			词性序列
	 * @param chunkTags			组块标记
	 * @param aditionalContext	格外信息
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

