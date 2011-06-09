/*
 * HMMTagger.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 3, 2011
 * 
 */
package mlfs.pos.hmm.model;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import mlfs.pos.hmm.ngrams.BiGram;
import mlfs.pos.hmm.ngrams.TriGram;
import mlfs.pos.hmm.word.IVWord;
import mlfs.pos.hmm.word.SuffixTree;


public class HMMTagger {
	private Model m_model;
	private IVWord m_ivWord;
	private SuffixTree m_oovWord;

	private HashMap<TriGram, Double> m_triCache;
	private HashMap<BiGram, Double> m_biCache;
	private HashMap<String, HashMap<Integer, Double>> m_wordTagProbCache;
	
	public HMMTagger(Model model)
	{
		this.m_model = model;
		this.m_ivWord = model.getIVWord();
		this.m_oovWord = model.getOOVWord();
		
		this.m_biCache = new HashMap<BiGram, Double>();
		this.m_triCache = new HashMap<TriGram, Double>();
		this.m_wordTagProbCache = new HashMap<String, HashMap<Integer,Double>>();
	}
	
	
	/**
	 * Viterbi算法进行解码
	 *
	 * @param sentence 输入的一句话，用词分开
	 * @return 标注结果
	 * @throws ParseException the parse exception
	 */
	public ArrayList<String> viterbi(List<String> sentence) throws ParseException
	{
		int numTag = m_model.getNumTagger();
		int senteceLen = sentence.size();
		double[][][] theta = new double[numTag][numTag][senteceLen-1];//[当前tag][前一个tag][时间]
		int phi[][][] = new int[numTag][numTag][senteceLen-1];//[当前tag][前一个tag][时间]
		
		int startTag = m_model.tagStr2Int("<Start>");
		int t1 = startTag;//index 0
		int t2 = startTag;// index 1
		
		for (int tag=1; tag<=numTag; tag++)//index 2
		{
			theta[tag-1][t2-1][2] = getTriGramProb(new TriGram(t1, t2, tag)) + getEmissionProb(sentence.get(2), tag);
			phi[tag-1][t2-1][2] = startTag;
		}
		
		for (int tag=1; tag<=numTag&&senteceLen>4; tag++)//index 3，sentenceLen>4的要求是限制只有一个词作为一句话的情况
		{
			for (int preTag=1; preTag<=numTag; preTag++)
			{
				theta[tag-1][preTag-1][3] = theta[preTag-1][t2-1][2] + getTriGramProb(new TriGram(t2, preTag, tag)) + getEmissionProb(sentence.get(3), tag);
				phi[tag-1][preTag-1][3] = startTag;
			}
		}
		
		
		//don't handle <End>
		for (int w=4; w<senteceLen-1; w++)
		{
			String word = sentence.get(w);
			for (int tag=1; tag<=numTag; tag++)
			{
				for (int preTag=1; preTag<=numTag; preTag++)
				{
					double max = Double.NEGATIVE_INFINITY;
					for (int prePreTag=1; prePreTag<=numTag; prePreTag++)
					{
						double p = theta[preTag-1][prePreTag-1][w-1] + getTriGramProb(new TriGram(prePreTag, preTag, tag)) + getEmissionProb(word, tag);
						if (p > max)
							max = p;
					}
					theta[tag-1][preTag-1][w] = max;
				}
			}
			
			for (int tag=1; tag<=numTag; tag++)
			{
				for (int preTag = 1; preTag <= numTag; preTag++)
				{
					double max = Double.NEGATIVE_INFINITY;
					int nicePrePreTag = -1;
					for (int prePreTag=1; prePreTag<=numTag; prePreTag++)
					{
						double p = theta[preTag-1][prePreTag-1][w-1] + getTriGramProb(new TriGram(prePreTag, preTag, tag));
						if (p > max)
						{
							max = p;
							nicePrePreTag = prePreTag;
						}
					}
					phi[tag-1][preTag-1][w] = nicePrePreTag; 
				}
			}
		}
		
		Stack<String> reversePath = new Stack<String>();
		double max = Double.NEGATIVE_INFINITY;
		int nicePreTag = -1;
		int nicePrePreTag = -1;
		int endTag = m_model.tagStr2Int("<End>");
		for (int tag=1; tag<=numTag; tag++)
		{
			for (int pre=1; pre<=numTag; pre++)
			{
				double p = theta[tag-1][pre-1][senteceLen-2]  + getBiGramProb(new BiGram(tag, endTag));
				if (p > max)
				{
					max = p;
					nicePreTag = tag;
					nicePrePreTag = pre;
				}
			}
		}
		
		reversePath.push(m_model.tagInt2Str(nicePreTag));
		if (senteceLen > 4)//考虑一句话只有一个词的情况
			reversePath.push(m_model.tagInt2Str(nicePrePreTag));
		int times = senteceLen-2;
		while (times > 3)
		{
			int preTag = phi[nicePreTag-1][nicePrePreTag-1][times];
			nicePreTag = nicePrePreTag;
			nicePrePreTag = preTag;
			reversePath.push(m_model.tagInt2Str(preTag));
			times--;
		}
		
		ArrayList<String> path = new ArrayList<String>();
		while (reversePath.size()>0)
		{
			path.add(reversePath.pop());
		}
		return path;
	}
	
	private double calWordProb(String word, int tag)
	{			
		double p = 0.0;
		if (m_ivWord.isIVWord(word))
		{
			p = (m_ivWord.getProb(word, tag));
			if (p != 0.0)
				return Math.log(p);
		}	
		p = Math.log(m_oovWord.getProb(word, tag));
		return p;
	}
	private double getEmissionProb(String word, int tag)
	{
		 if (m_wordTagProbCache.containsKey(word))
		 {
			 HashMap<Integer, Double> tagProbMap = m_wordTagProbCache.get(word);
			 if (tagProbMap.containsKey(tag))
				 return tagProbMap.get(tag);
			 else
			 {
				 double p = calWordProb(word, tag);
				 tagProbMap.put(tag, p);
				 return p;
			 }
		 }
		 else
		 {
			 double p = calWordProb(word, tag);
			 HashMap<Integer, Double> map = new HashMap<Integer, Double>();
			 map.put(tag, p);
			 m_wordTagProbCache.put(word, map);
			 return p;
		 }
	}
	
	private double getTriGramProb(TriGram trigram) throws ParseException
	{
		if (m_triCache.containsKey(trigram))
			return m_triCache.get(trigram);
		
		double p = Math.log(m_model.getTriGramProb(trigram));
		m_triCache.put(trigram, p);
		
		return p;
	}
	
	private double getBiGramProb(BiGram bigram) throws ParseException
	{
		if (m_biCache.containsKey(bigram))
			return m_biCache.get(bigram);
		
		double p = Math.log(m_model.getBiGramProb(bigram));
		m_biCache.put(bigram, p);
		
		return p;
	}
}
