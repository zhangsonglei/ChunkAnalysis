package hust.tools.ca.event;

import java.io.IOException;
import hust.tools.ca.feature.ChunkAnalysisBasedWordAndPOSContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSME;
import hust.tools.ca.model.ChunkAnalysisBasedWordAndPOSModel;
import hust.tools.ca.stream.ChunkAnalysisBasedWordAndPOSSample;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.Sequence;
import opennlp.tools.ml.model.SequenceStream;
import opennlp.tools.util.ObjectStream;

/**
 *<ul>
 *<li>Description: 基于词和词性的事件生成类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBasedWordAndPOSSampleSequenceStream implements SequenceStream {

	/**
	 * 上下文生成器
	 */
	private ChunkAnalysisContextGenerator contextGenerator;
	
	private ObjectStream<AbstractChunkAnalysisSample> sampleStream;
	
	/**
	 * 构造方法
	 * @param sampleStream		样本流
	 * @param contextgenerator	上下文生成器
	 * @throws IOException 
	 */
	public ChunkAnalysisBasedWordAndPOSSampleSequenceStream(ObjectStream<AbstractChunkAnalysisSample> sampleStream) throws IOException {
		this(sampleStream, new ChunkAnalysisBasedWordAndPOSContextGeneratorConf());
	}
	
	public ChunkAnalysisBasedWordAndPOSSampleSequenceStream(ObjectStream<AbstractChunkAnalysisSample> sampleStream, ChunkAnalysisContextGenerator contextGenerator) {
		this.sampleStream = sampleStream;
		this.contextGenerator = contextGenerator;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Sequence read() throws IOException {
		ChunkAnalysisBasedWordAndPOSSample sample = (ChunkAnalysisBasedWordAndPOSSample) sampleStream.read();

		if (sample != null) {
			String[] words = sample.getTokens();
			Object[] poses = sample.getAditionalContext();
			String tags[] = sample.getTags();
			Event[] events = new Event[words.length];

			for (int i = 0; i < words.length; i++) {
				String[] context = contextGenerator.getContext(i, words, tags, poses);
				events[i] = new Event(tags[i], context);
			}
            
			Sequence<AbstractChunkAnalysisSample> sequence = new Sequence<AbstractChunkAnalysisSample>(events, sample);
			return sequence;
		}

		return null;
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Event[] updateContext(Sequence sequence, AbstractModel model) {
		Sequence<AbstractChunkAnalysisSample> pss = sequence;
		ChunkAnalysisBasedWordAndPOSME tagger = null;
		try {
			tagger = new ChunkAnalysisBasedWordAndPOSME(new ChunkAnalysisBasedWordAndPOSModel("x-unspecified", model, null));
		} catch (IOException e) {
			e.printStackTrace();
		}

        String[] words = pss.getSource().getTokens();
        Object[] objectPoses = pss.getSource().getAditionalContext();
        String[] stringPoses = new String[objectPoses.length];
        for(int i = 0; i < stringPoses.length; i++)
        	stringPoses[i] = (String) objectPoses[i];
        
        String[] tags = tagger.tag(pss.getSource().getTokens(), stringPoses);
        Event[] events = new Event[words.length];
                
        ChunkAnalysisBasedWordAndPOSSampleEventStream.generateEvents(words, tags, objectPoses, contextGenerator).toArray(events);
        
        return events;
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		sampleStream.reset();
	}

	@Override
	public void close() throws IOException {
		sampleStream.close();
	}
	
}

