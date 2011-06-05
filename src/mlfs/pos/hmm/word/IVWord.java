/*
 * IVWord.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 2, 2011
 * 
 */
package mlfs.pos.hmm.word;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class IVWord {
	private HashMap<Integer, HashMap<String, Double>> m_tagWordProb;
	private HashSet<String> m_dict;
	
	public IVWord()
	{
		m_tagWordProb = new HashMap<Integer, HashMap<String,Double>>();
		m_dict = new HashSet<String>();
	}
	
	public void addWord(String word, int tag, int times)
	{
		m_dict.add(word);
		if (m_tagWordProb.containsKey(tag))
		{
			HashMap<String, Double> wordFreq = m_tagWordProb.get(tag);
			if (wordFreq.containsKey(word))
				wordFreq.put(word, wordFreq.get(word)+times);
			else
				wordFreq.put(word, 1.0* times);
		}
		else
		{
			HashMap<String, Double>wordFreq = new HashMap<String, Double>();
			wordFreq.put(word, 1.0* times);
			m_tagWordProb.put(tag, wordFreq);
		}
	}
	
	public void calcProb()
	{
		for (Entry<Integer, HashMap<String, Double>> entry : m_tagWordProb.entrySet())
		{
			int tag = entry.getKey();
			HashMap<String, Double> wordProbs = entry.getValue();
			
			int sum = 0;
			for (Entry<String, Double> wordProb : wordProbs.entrySet())
			{
				sum += wordProb.getValue();
			}
			for (Entry<String, Double> wordProb : wordProbs.entrySet())
			{
				double times = wordProb.getValue();
				wordProb.setValue(times/sum);
			}
		}
	}
	
	public boolean isIVWord(String word)
	{
		return m_dict.contains(word);
	}
	
	public double getProb(String word, int tag)
	{
		HashMap<String, Double> wordProb = m_tagWordProb.get(tag);
		if (wordProb.containsKey(word))
			return wordProb.get(word);
		else
			return 0.0;//即使是IV的词，给定这个词性，发射概率也可能为0
	}
}
