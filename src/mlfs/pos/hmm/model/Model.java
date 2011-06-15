/*
 * Model.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import mlfs.pos.corpus.WordTag;
import mlfs.pos.hmm.ngrams.BiGram;
import mlfs.pos.hmm.ngrams.TriGram;
import mlfs.pos.hmm.ngrams.UniGram;
import mlfs.pos.hmm.word.IVWord;
import mlfs.pos.hmm.word.SuffixTree;

public class Model {

	private String m_lexiconFilePath;
	private String m_nGramsFilePath;
	
	/** NGram以及对应的出现次数 */
	private HashMap<UniGram, Integer> m_uniGramFreq;
	private HashMap<BiGram, Integer> m_biGramFreq;
	private HashMap<TriGram, Integer> m_triGramFreq;
	
	
	/** NGram以及对应的转移概率*/
	private HashMap<UniGram, Double> m_uniGramProbCache;
	private HashMap<BiGram, Double> m_biGramProbCache;
	private HashMap<TriGram, Double> m_triGramProbCache;;
	
	/** ID和TagString的两个对应表 */
	private HashMap<String, Integer> m_tagIdMap;
	private HashMap<Integer, String> m_idTagMap;
	
	private int m_tagCounter;
	
	/** 三个线性插值参数. */
	private double m_lambda1;
	private double m_lambda2;
	private double m_lambda3;
	
	/** tags总数，多次出现的重复计数. */
	private int m_numTags;
	
	private IVWord m_ivWord;
	private SuffixTree m_oovWord;
	
	public Model(String lexiconFilePath, String nGramsFilePath)
	{
		this.m_lexiconFilePath = lexiconFilePath;
		this.m_nGramsFilePath = nGramsFilePath;
		
		m_uniGramFreq = new HashMap<UniGram, Integer>();
		m_biGramFreq = new HashMap<BiGram, Integer>();
		m_triGramFreq = new HashMap<TriGram, Integer>();
		
		m_uniGramProbCache = new HashMap<UniGram, Double>();
		m_biGramProbCache = new HashMap<BiGram, Double>();
		m_triGramProbCache = new HashMap<TriGram, Double>();
		
		m_numTags = 0;
		m_tagCounter = 0;
		m_tagIdMap = new HashMap<String, Integer>();
		m_idTagMap = new HashMap<Integer, String>();
	}
	
	private void writeLexicon(ArrayList<ArrayList<WordTag>> data) throws IOException
	{
		HashMap<String, HashMap<String, Integer>> lexicon = new HashMap<String,  HashMap<String, Integer>>();
		
		for (ArrayList<WordTag> sentence: data)
		{
			for (WordTag wordTag : sentence)
			{
				String word = wordTag.getWord();
				String tag = wordTag.getTag();
				
				if (lexicon.containsKey(word))
				{
					HashMap<String, Integer> tagFreq = lexicon.get(word);
					if (tagFreq.containsKey(tag))
						tagFreq.put(tag, tagFreq.get(tag)+1);
					else
						tagFreq.put(tag, 1);
				}
				else
				{
					HashMap<String, Integer> newTagFreq = new HashMap<String, Integer>();
					newTagFreq.put(tag, 1);
					lexicon.put(word, newTagFreq);
				}
			}
		}
		
		PrintWriter out = new PrintWriter(new File(m_lexiconFilePath));
		for (Entry<String, HashMap<String, Integer>> entry : lexicon.entrySet())
		{
			String word = entry.getKey();
			HashMap<String, Integer> tagFreq = entry.getValue();
			
			for (Entry<String, Integer> tagFreqPair : tagFreq.entrySet())
			{
				out.println(word+"\t"+tagFreqPair.getKey()+"\t"+tagFreqPair.getValue());
			}
		}
		out.close();
	}
	
	private void writeNGrams(ArrayList<ArrayList<WordTag>> data) throws FileNotFoundException
	{
		HashMap<String, Integer> uniGramFreq = new HashMap<String, Integer>();
		HashMap<String, Integer> biGramFreq   = new HashMap<String, Integer>();
		HashMap<String, Integer> triGramFreq   = new HashMap<String, Integer>();
		
		for (ArrayList<WordTag> sentence : data)
		{
			for (int i=0; i<sentence.size(); i++)
			{
				String tag = sentence.get(i).getTag();
				addNGram(uniGramFreq, tag);
				if (i>=1)
				{
					String preTag = sentence.get(i-1).getTag();
					addNGram(biGramFreq, preTag+" "+tag);
				}
				if (i >= 2)
				{
					String prePreTag = sentence.get(i-2).getTag();
					String preTag = sentence.get(i-1).getTag();
					addNGram(triGramFreq, prePreTag+" "+preTag+" "+tag);
				}
			}
		}
		
		PrintWriter out = new PrintWriter(new File(m_nGramsFilePath));
		for (Entry<String, Integer> entry : uniGramFreq.entrySet())
		{
			out.println(entry.getKey()+" " + entry.getValue());
		}
		for (Entry<String, Integer> entry : biGramFreq.entrySet())
		{
			out.println(entry.getKey()+" " + entry.getValue());
		}
		for (Entry<String, Integer> entry : triGramFreq.entrySet())
		{
			out.println(entry.getKey()+" " + entry.getValue());
		}
		out.close();
	}
	
	private void addNGram(HashMap<String, Integer> NGramFreq, String t)
	{
		if (NGramFreq.containsKey(t))
			NGramFreq.put(t, NGramFreq.get(t)+1);
		else
			NGramFreq.put(t, 1);
	}

	private void checkTagId(String tag)
	{
		if (!m_tagIdMap.containsKey(tag))
		{
			m_tagCounter++;
			m_tagIdMap.put(tag, m_tagCounter);
			m_idTagMap.put(m_tagCounter, tag);
		}
	}
	
	private void addUniGram( String t, int n)
	{
		checkTagId(t);
		
		m_numTags += n;
		UniGram target = new UniGram(m_tagIdMap.get(t));
		if (m_uniGramFreq.containsKey(target))
			m_uniGramFreq.put(target, m_uniGramFreq.get(target)+n);
		else
			m_uniGramFreq.put(target, n);
	}
	
	private void addBiGram(String t1, String t2, int n)
	{
		checkTagId(t1);
		checkTagId(t2);
		
		BiGram target = new BiGram(m_tagIdMap.get(t1), m_tagIdMap.get(t2));
		if (m_biGramFreq.containsKey(target))
			m_biGramFreq.put(target, m_biGramFreq.get(target)+n);
		else
			m_biGramFreq.put(target, n);
	}
	
	private void addTriGram(String t1, String t2, String t3, int n)
	{
		checkTagId(t1);
		checkTagId(t2);
		checkTagId(t3);
		
		TriGram target = new TriGram(m_tagIdMap.get(t1), m_tagIdMap.get(t2), m_tagIdMap.get(t3));
		if (m_triGramFreq.containsKey(target))
			m_triGramFreq.put(target, m_triGramFreq.get(target)+n);
		else
			m_triGramFreq.put(target, n);
	}
	
	private void loadNGram() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_nGramsFilePath)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String[] vec = line.split("\\s+");
			if (vec.length == 2)
				addUniGram(vec[0], Integer.parseInt(vec[1]));
			else if (vec.length == 3)
				addBiGram(vec[0], vec[1], Integer.parseInt((vec[2])));
			else if (vec.length == 4)
				addTriGram(vec[0], vec[1], vec[2],	 Integer.parseInt(vec[3]));
		}
		reader.close();
		
		calcLambda();
		
		calcUniGramProb();
	}
	
	private void calcLambda()
	{
		m_lambda1 = 0.0;
		m_lambda2 = 0.0;
		m_lambda3 = 0.0;
		
		for (Entry<TriGram, Integer> entry : m_triGramFreq.entrySet())
		{
			TriGram trigram = entry.getKey();
			int t1 = trigram.getTag1();
			int t2 = trigram.getTag2();
			int t3 = trigram.getTag3();
			
			int inc = entry.getValue();
			
			double t3Prob=0.0, t2t3Prob=0.0, t1t2t3Prob=0.0;
			UniGram t3unigram = new UniGram(t3);
			BiGram t2t3bigram = new BiGram(t2, t3);
			if (m_uniGramProbCache.containsKey(t3unigram))
				t3Prob = m_uniGramProbCache.get(t3unigram);
			else
			{
				t3Prob =(m_uniGramFreq.get(t3unigram) - 1)/(double)(m_numTags-1);
				m_uniGramProbCache.put(t3unigram, t3Prob);
			}
			if (m_biGramProbCache.containsKey(t2t3bigram))
				t2t3Prob = m_biGramProbCache.get(t2t3bigram);
			else
			{
				t2t3Prob = (m_biGramFreq.get(t2t3bigram) - 1)/(double)(m_uniGramFreq.get(new UniGram(t2)) - 1);
				m_biGramProbCache.put(t2t3bigram, t2t3Prob);
			}
			if (m_triGramProbCache.containsKey(trigram))
				t1t2t3Prob = m_triGramProbCache.get(trigram);
			else{
				t1t2t3Prob = (inc - 1)/(double)(m_biGramFreq.get(new BiGram(t1, t2)) - 1) ;
				m_triGramProbCache.put(trigram, t1t2t3Prob);
			}
			
			if (t3Prob > t2t3Prob && t3Prob > t1t2t3Prob)
				m_lambda1 += inc;
			else if (t2t3Prob > t3Prob && t2t3Prob > t1t2t3Prob)
				m_lambda2 += inc;
			else if (t1t2t3Prob > t3Prob && t1t2t3Prob > t2t3Prob)
				m_lambda3 += inc;
			
			double total = m_lambda1 + m_lambda2 + m_lambda3;
			m_lambda1 /= total;
			m_lambda2 /= total;
			m_lambda3 /= total;
		}
	}

	private void calcUniGramProb()
	{
		int sum = 0;
		
		for (Entry<UniGram, Integer> entry : m_uniGramFreq.entrySet())
		{
			double p = 1.0*entry.getValue()/m_numTags;
			entry.getKey().setProb(p);
			m_uniGramProbCache.put(entry.getKey(), p);
		}
		
	}
	private void loadLexicon() throws IOException
	{
		m_ivWord = new IVWord();
		m_oovWord = new SuffixTree();
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_lexiconFilePath)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String[] vec = line.split("\\s+");
			String word = vec[0];
			int tag = m_tagIdMap.get(vec[1]);
			int times = Integer.parseInt(vec[2]);
			
			m_ivWord.addWord(word, tag, times);
			m_oovWord.addWord(word, tag, times);
		}
		reader.close();
		m_ivWord.calcProb();
		m_oovWord.calcProb(m_uniGramFreq);
	}
	
	public void loadModel() throws IOException
	{
		loadNGram();
		loadLexicon();
	}
	
	public void saveModel(ArrayList<ArrayList<WordTag>> data) throws IOException
	{
		writeNGrams(data);
		writeLexicon(data);
	}
	
	public int tagStr2Int(String t) throws ParseException
	{
		if (m_tagIdMap.containsKey(t))
			return m_tagIdMap.get(t);
		else
			throw new ParseException("tagStr2Int : "+t+" 在训练语料中不存在", 0);
	}
	public String tagInt2Str(int t) throws ParseException
	{
		if (m_idTagMap.containsKey(t))
			return m_idTagMap.get(t);
		else
			throw new ParseException("tagInt2Str " + t+" 在训练语料中不存在", 0);
	}
	
	public int getNumTagger()
	{
		return m_tagCounter;
	}

	public IVWord getIVWord() {
		return m_ivWord;
	}

	public SuffixTree getOOVWord() {
		return m_oovWord;
	}

	public HashMap<UniGram, Integer> getUniGramFreq() {
		return m_uniGramFreq;
	}

	public HashMap<BiGram, Integer> getBiGramFreq() {
		return m_biGramFreq;
	}

	public HashMap<TriGram, Integer> getTriGramFreq() {
		return m_triGramFreq;
	}
	
	public double getTriGramProb(TriGram trigram) throws ParseException
	{
		double p = 0.0;
		BiGram t2t3bigram = new BiGram(trigram.getTag2(), trigram.getTag3());
		BiGram t1t2bigram = new BiGram(trigram.getTag1(), trigram.getTag2());
		UniGram t2unigram = new UniGram(trigram.getTag2());
		if (!m_biGramProbCache.containsKey(t2t3bigram))
		{
			if (m_biGramFreq.containsKey(t2t3bigram)&&m_uniGramFreq.containsKey(t2unigram))
				m_biGramProbCache.put(t2t3bigram, 1.0*m_biGramFreq.get(t2t3bigram)/m_uniGramFreq.get(t2unigram));
			else 
				m_biGramProbCache.put(t2t3bigram, 0.0);
		}
		if (!m_triGramProbCache.containsKey(trigram))
		{
			if (m_triGramFreq.containsKey(trigram)&&m_biGramFreq.containsKey(t1t2bigram))
				m_triGramProbCache.put(trigram, 1.0*m_triGramFreq.get(trigram)/m_biGramFreq.get(t1t2bigram));
			else
				m_triGramProbCache.put(trigram, 0.0);
		}
			
		p += m_lambda1*m_uniGramProbCache.get(new UniGram(trigram.getTag3()));
		p += m_lambda2*m_biGramProbCache.get(t2t3bigram);
		p += m_lambda3*m_triGramProbCache.get(trigram);
		
		return p;
	}
	
	public double getBiGramProb(BiGram bigram) throws ParseException
	{
		if (!m_biGramProbCache.containsKey(bigram))
		{
			m_biGramProbCache.put(bigram, 1.0*m_biGramFreq.get(bigram)/m_uniGramFreq.get(new UniGram(bigram.getTag1())));
		}
		
		return m_biGramProbCache.get(bigram);
	}
	
}
