package hust.tools.ca.beamsearch;

import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import hust.tools.ca.feature.BeamSearchChunkAnalysisContextGenerator;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.util.Cache;
import opennlp.tools.util.Sequence;

/**
 *<ul>
 *<li>Description: beamSearch方法求最优解 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisBeamSearch<T> implements ChunkAnalysisSequenceClassificationModel<T> {

	/**
	 * beamsearch的束大小参数名
	 */
	public static final String BEAM_SIZE_PARAMETER = "BeamSize";
	
	/**
	 * 空上下文
	 */
	private static final Object[] EMPTY_ADDITIONAL_CONTEXT = new Object[0];
	
	/**
	 * beamsearch的束大小
	 */
	protected int size;
	
	/**
	 * 最大熵模型
	 */
	protected MaxentModel model;
	
	/**
	 * 每个类别的概率
	 */
	private double[] probs;
	
	/**
	 * 提供固定大小，预分配，最近最少使用的替换缓存
	 */
	private Cache<String[], double[]> contextsCache;
	
	/**
	 * 零的对数的默认值
	 */
	private static final double zeroLog = -100000.0;

	/**
	 * 构造方法
	 * @param size	beamsearch的束大小
	 * @param model	最大熵模型
	 */
	public ChunkAnalysisBeamSearch(int size, MaxentModel model) {
		this(size, model, 0);
	}

	/**
	 * 构造方法
	 * @param size		beamsearch的束大小
	 * @param model		最大熵模型
	 * @param cacheSize	缓存大小
	 */
	public ChunkAnalysisBeamSearch(int size, MaxentModel model, int cacheSize) {
		this.size = size;
		this.model = model;
		
		if (cacheSize > 0) 
			contextsCache = new Cache<String[], double[]>(cacheSize);

		probs = new double[model.getNumOutcomes()];
	}
	
	
	@Override
	public Sequence bestSequence(T[] words, T[] poses, Object[] additionalContext, BeamSearchChunkAnalysisContextGenerator<T> contextGenerator,
			ChunkAnalysisSequenceValidator<T> validator) {
		Sequence[] sequences = this.bestSequences(1, words, poses, additionalContext, contextGenerator, validator);
		
		return sequences.length > 0 ? sequences[0] : null;
	}

	
	@Override
	public Sequence[] bestSequences(int numSequences, T[] words, T[] poses, Object[] additionalContext, double minSequenceScore,
			BeamSearchChunkAnalysisContextGenerator<T> contextGenerator, ChunkAnalysisSequenceValidator<T> validator) {
		PriorityQueue<Sequence> prev = new PriorityQueue<Sequence>(size);
		PriorityQueue<Sequence> next = new PriorityQueue<Sequence>(size);
		prev.add(new Sequence());
		if (additionalContext == null) {
			additionalContext = EMPTY_ADDITIONAL_CONTEXT;
		}

		int numSeq;
		int seqIndex;
		for (numSeq = 0; numSeq < words.length; ++numSeq) {
			int topSequences = Math.min(size, prev.size());

			for (seqIndex = 0; prev.size() > 0 && seqIndex < topSequences; ++seqIndex) {
				Sequence top = prev.remove();
				List<String> tmpOutcomes = top.getOutcomes();
				String[] outcomes = (String[]) tmpOutcomes.toArray(new String[tmpOutcomes.size()]);
				String[] contexts = contextGenerator.getContext(numSeq, words, poses, outcomes, additionalContext);
				double[] scores;
				if (this.contextsCache != null) {
					scores = (double[]) this.contextsCache.get(contexts);
					if (scores == null) {
						scores = this.model.eval(contexts, this.probs);
						this.contextsCache.put(contexts, scores);
					}
				} else {
					scores = this.model.eval(contexts, this.probs);
				}

				double[] temp_scores = new double[scores.length];
				System.arraycopy(scores, 0, temp_scores, 0, scores.length);
				Arrays.sort(temp_scores);
				double min = temp_scores[Math.max(0, scores.length - this.size)];

				int p;
				String out;
				Sequence ns;
				for (p = 0; p < scores.length; ++p) {
					if (scores[p] >= min) {
						out = this.model.getOutcome(p);
						if (validator.validSequence(numSeq, words, poses, outcomes, out)) {
							ns = new Sequence(top, out, scores[p]);
							if (ns.getScore() > minSequenceScore) {
								next.add(ns);
							}
						}
					}
				}

				if (next.size() == 0) {
					for (p = 0; p < scores.length; ++p) {
						out = this.model.getOutcome(p);
						if (validator.validSequence(numSeq, words, poses, outcomes, out)) {
							ns = new Sequence(top, out, scores[p]);
							if (ns.getScore() > minSequenceScore) {
								next.add(ns);
							}
						}
					}
				}
			}

			prev.clear();
			PriorityQueue<Sequence> tmp = prev;
			prev = next;
			next = tmp;
		}

		numSeq = Math.min(numSequences, prev.size());
		Sequence[] arg24 = new Sequence[numSeq];

		for (seqIndex = 0; seqIndex < numSeq; ++seqIndex) {
			arg24[seqIndex] = (Sequence) prev.remove();
		}

		return arg24;
	}

	@Override
	public Sequence[] bestSequences(int numSequences, T[] words, T[] poses, Object[] additionalContext, 
			BeamSearchChunkAnalysisContextGenerator<T> contextGenerator, ChunkAnalysisSequenceValidator<T> validator) {
		
		return this.bestSequences(numSequences, words, poses, additionalContext, zeroLog, contextGenerator, validator);
	}

	@Override
	public String[] getOutcomes() {
		String[] outcomes = new String[model.getNumOutcomes()];

		for (int i = 0; i < outcomes.length; ++i) {
			outcomes[i] = model.getOutcome(i);
		}

		return outcomes;
	}

}

