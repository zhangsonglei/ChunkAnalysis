package hust.tools.ca.event;

import java.io.IOException;

import hust.tools.ca.feature.ChunkAnalysisBasedWordContextGeneratorConf;
import hust.tools.ca.feature.ChunkAnalysisContextGenerator;
import hust.tools.ca.model.ChunkAnalysisBasedWordME;
import hust.tools.ca.model.ChunkAnalysisBasedWordModel;
import hust.tools.ca.stream.AbstractChunkAnalysisSample;
import hust.tools.ca.stream.ChunkAnalysisBasedWordSample;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.Sequence;
import opennlp.tools.ml.model.SequenceStream;
import opennlp.tools.util.ObjectStream;

public class ChunkAnalysisBasedWordSampleSequenceStream implements SequenceStream {

	private ChunkAnalysisContextGenerator contextGenerator;
	
    private ObjectStream<AbstractChunkAnalysisSample> stream;

    public ChunkAnalysisBasedWordSampleSequenceStream(ObjectStream<AbstractChunkAnalysisSample> stream) throws IOException {
        this(stream, new ChunkAnalysisBasedWordContextGeneratorConf());
    }

    public ChunkAnalysisBasedWordSampleSequenceStream(ObjectStream<AbstractChunkAnalysisSample> stream, ChunkAnalysisContextGenerator contextGenerator)
            throws IOException {
        this.stream = stream;
        this.contextGenerator = contextGenerator;
    }
	
	@SuppressWarnings("rawtypes")
	@Override
	public Sequence read() throws IOException {
		ChunkAnalysisBasedWordSample sample = (ChunkAnalysisBasedWordSample) stream.read();

		if (sample != null) {
			String[] words = sample.getTokens();
			String tags[] = sample.getTags();
			Event[] events = new Event[words.length];

			for (int i = 0; i < words.length; i++) {
				String[] context = contextGenerator.getContext(i, words, tags, null);
				events[i] = new Event(tags[i], context);
			}
            
			Sequence<AbstractChunkAnalysisSample> sequence = new Sequence<AbstractChunkAnalysisSample>(events, sample);
			return sequence;
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Event[] updateContext(Sequence sequence, AbstractModel model) {
		Sequence<AbstractChunkAnalysisSample> pss = sequence;
		ChunkAnalysisBasedWordME tagger = null;
		try {
			tagger = new ChunkAnalysisBasedWordME(new ChunkAnalysisBasedWordModel("x-unspecified", model, null));
		} catch (IOException e) {
			e.printStackTrace();
		}
        String[] words = pss.getSource().getTokens();
        String[] tags = tagger.tag(pss.getSource().getTokens());
        Event[] events = new Event[words.length];
                
        ChunkAnalysisBasedWordSampleEventStream.generateEvents(words, tags, contextGenerator).toArray(events);
        
        return events;
	}
	
	@Override
	public void reset() throws IOException, UnsupportedOperationException {
		stream.reset();
	}

	@Override
	public void close() throws IOException {
		stream.close();
	}
}
