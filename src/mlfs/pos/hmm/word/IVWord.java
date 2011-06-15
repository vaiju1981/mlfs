/*
 * IVWord.java
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
