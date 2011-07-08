/*
 * HMMTagger.java
   *  
 * Author: 罗磊，luoleicn@gmail.com
   * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
   * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
   * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
   * 
 * Last Update:2011-6-11
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
		double[][] theta = new double[numTag][senteceLen-1];
		int phi[][] = new int[numTag][senteceLen-1];
		
		int t1 = m_model.tagStr2Int("<Start>");
		int t2 = m_model.tagStr2Int("<Start>");
		
		for (int tag=1; tag<=numTag; tag++)
		{
			theta[tag-1][2] = getTriGramProb(new TriGram(t1, t2, tag)) + getEmissionProb(sentence.get(2), tag);
			phi[tag-1][0] = -1;
			phi[tag-1][1] = m_model.tagStr2Int("<Start>");
			phi[tag-1][2] = m_model.tagStr2Int("<Start>");
		}
		
		//don't handle <End>
		for (int w=3; w<senteceLen-1; w++)
		{
			String word = sentence.get(w);
			for (int tag=1; tag<=numTag; tag++)
			{
				double max = Double.NEGATIVE_INFINITY;
				for (int preTag=1; preTag<=numTag; preTag++)
				{
					int prePreTag = phi[preTag-1][w-1];
		
					double p = theta[preTag-1][w-1] + getTriGramProb(new TriGram(prePreTag, preTag, tag)) + getEmissionProb(word, tag);
					if (p > max)
						max = p;
				}
				theta[tag-1][w] = max;
			}
			
			for (int tag=1; tag<=numTag; tag++)
			{
				double max = Double.NEGATIVE_INFINITY;
				int nicePreTag = -1;
				for (int preTag = 1; preTag <= numTag; preTag++)
				{
					int prePreTag = phi[preTag-1][w-1];
					double p = theta[preTag-1][w-1] + getTriGramProb(new TriGram(prePreTag, preTag, tag));
					if (p > max)
					{
						max = p;
						nicePreTag = preTag;
					}
				}
				phi[tag-1][w] = nicePreTag; 
			}
		}
		
		Stack<String> reversePath = new Stack<String>();
		double max = Double.NEGATIVE_INFINITY;
		int nicePreTag = -1;
		for (int tag=1; tag<=numTag; tag++)
		{
			double p = theta[tag-1][senteceLen-2]  + getBiGramProb(new BiGram(tag, m_model.tagStr2Int("<End>")));
			if (p > max)
			{
				max = p;
				nicePreTag = tag;
			}
		}
		reversePath.push(m_model.tagInt2Str(nicePreTag));
		int times = senteceLen-2;
		while (times > 2)
		{
			int preTag = phi[nicePreTag-1][times];
			nicePreTag = preTag;
			reversePath.push(m_model.tagInt2Str(nicePreTag));
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

