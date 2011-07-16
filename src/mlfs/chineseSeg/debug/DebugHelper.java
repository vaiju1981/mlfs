package mlfs.chineseSeg.debug;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.graph.Edge;
import mlfs.crf.graph.Graph;
import mlfs.crf.graph.Node;
import mlfs.crf.model.CRFEvent;

public class DebugHelper {

	
	public static void saveToCache(CRFEvent e, int numTag, TemplateHandler template, Map<String, Integer> featIdMap)
	{
		FeatureCacher cacher = FeatureCacher.getInstance();
		
		e.FEATURE_CACHE_POS = cacher.size();
			
		int len = e.labels.length;
		List<Integer> feats = null;
		for (int i=0; i<len; i++)//unigram
		{
			feats = new ArrayList<Integer>();
			List<String> unigramPred = template.getUnigramPred(e, i);
			for (String predicate : unigramPred)
			{
				String unigramFeat = predicate;
					
				if (featIdMap.containsKey(unigramFeat))
					feats.add(featIdMap.get(unigramFeat));
			}
			cacher.add(feats);
		}
		for (int i=1; i<len; i++)//bigram
		{
			List<String> bigramPred = template.getBigramPred(e, i);
			for (int preTag=0; preTag<numTag; preTag++)
			{
				feats = new ArrayList<Integer>();
				for (String predicate : bigramPred)
				{
					String bigramFeat = predicate + Features.FEATURE_JOIN +preTag;
						
					if (featIdMap.containsKey(bigramFeat))
						feats.add(featIdMap.get(bigramFeat));
				}
				cacher.add(feats);
			}
		}
		e.charFeat = null;
	}
	
	/**
	 * Label.
	 *
	 * @param e the e
	 * @param numTag the num tag
	 * @param parameters the parameters
	 * @return 标注的tag序列，是tag对应的int形式
	 */
	public static int[] label(CRFEvent e, int numTag, double[] parameters)
	{
		Graph graph = Graph.buildGraph(e, numTag, parameters);
		
		int len = e.labels.length;
		
		double[][] delta = new double[numTag][len];
		int[][] phi = new int[numTag][len];
		
		Node[][] nodes = graph.getNodes();
		int lastIdx = -1;
		for (int i=0; i<len; i++)
		{
			lastIdx = -1;
			for (int j=0; j<numTag; j++)
			{
				double max = Double.NEGATIVE_INFINITY;
				Node node = nodes[i][j];
				List<Edge> leftNodes = node.m_ledge;
				for (Edge edge : leftNodes)
				{
					double v = delta[edge.m_lnode.m_y][i-1] + edge.getBigramProb() + node.getUnigramProb();
					if (v > max)
					{
						max = v;
						lastIdx = edge.m_lnode.m_y;
					}
				}
				phi[j][i] = lastIdx;
				delta[j][i] = lastIdx==-1 ? node.getUnigramProb() : max;
			}
		}
		
		double max = Double.NEGATIVE_INFINITY;
		for (int tag=0; tag<numTag; tag++)
		{
			if (delta[tag][len-1] > max)
			{
				max = delta[tag][len-1];
				lastIdx = tag;
			}
		}
		
		int[] stack = new int[len];
		stack[len-1] = lastIdx;
		for (int t = len-1; t>0; t--)
			stack[t-1] = phi[stack[t]][t];
		
		return stack;
	}
	
	public static double evaluate(List<CRFEvent> events, int numTag, double[] x)
	{
		int t = 0, f = 0;
		int sz = events.size();
		for (int i=0; i<sz; i++)
		{
			CRFEvent e = events.get(i);
			int[] prediction = label(e, numTag, x);
			int seqLen = prediction.length;
			for (int j=0; j<seqLen; j++)
			{
				if (prediction[j] == e.labels[j])
					t++;
				else
					f++;
			}
		}
		return 1.0*t/(t+f);
	}
	
	public static double evaluateSeg(List<CRFEvent> events, int numTag, double[] x, Map<Integer, String> tagMap)
	{
		//int t = 0, f = 0;
		int word_gold = 0, word_pred = 0, word_reco = 0;
		int sz = events.size();
		for (int i=0; i<sz; i++)
		{
			CRFEvent e = events.get(i);
			int[] prediction = label(e, numTag, x);
			int seqLen = prediction.length;
			String[] predictBMES = new String[seqLen];//预测的BMES序列
			String[] ansBMES = new String[seqLen];//答案的BMES序列
			for (int j=0; j<seqLen; j++)
			{
				predictBMES[j] = tagMap.get(prediction[j]);
				ansBMES[j] = tagMap.get(e.labels[j]);
			}
			
			List<String> wordseg_gold = segCount(ansBMES);
			List<String> wordseg_pred = segCount(predictBMES);
			word_gold += wordseg_gold.size();
			word_pred += wordseg_pred.size();
			word_reco += segReco(wordseg_gold, wordseg_pred);
			
		}
		
		return 2.0*word_reco/(word_gold+word_pred);
	}
	
	public static List<String> segCount(String[] seqBMES)
	{
		List<String> segResult = new ArrayList<String>();
		int beginPosition = 0;
		for(int i =1; i < seqBMES.length; i++)
		{
			String seqTag = seqBMES[i];
			if(seqTag.startsWith("B") || seqTag.startsWith("S"))
			{
				String oneWord = String.format("%d_%d", beginPosition, i-1);
				segResult.add(oneWord);
				beginPosition = i;
			}
		}
		
		segResult.add(String.format("%d_%d", beginPosition, seqBMES.length-1));	
		
		return segResult;
	}
	
	public static int segReco(List<String> wordSeg1, List<String> wordSeg2)
	{
		int count = 0;
		for(String word1 :wordSeg1)
		{
			for(String word2 : wordSeg2)
			{
				if(word1.equals(word2))
				{
					count++;
				}
			}
		}
		return count;
	}
	
	
	
}
