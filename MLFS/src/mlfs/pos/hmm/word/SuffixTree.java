/*
 * SuffixTree.java
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
import java.util.Map.Entry;

import mlfs.pos.hmm.ngrams.UniGram;

/**
 * The Class SuffixTree.后缀树的目的是处理OOV
 * 基于的假设是具有相同后缀的词，更可能具有相同的词性
 */
public class SuffixTree {

	class TreeNode
	{
		public final char letter;
		public int times;
		public HashMap<Integer, Integer> tagFreq;
		public HashMap<Integer, Double> tagProb;
		public HashMap<Character, TreeNode> children;
		
		public TreeNode(char c)
		{
			this.letter = c;
			times = 0;
			children = new HashMap<Character, SuffixTree.TreeNode>();
			tagFreq = new HashMap<Integer, Integer>();
			tagProb = new HashMap<Integer, Double>();
		}
	}
	
	private TreeNode m_root;
	private double m_theta;
	
	public SuffixTree()
	{
		m_root = new TreeNode('$');
	}
	
	public void addWord(String word, int tag, int times)
	{
		String reverse = reverse(word);
		addToSuffixTree(reverse, tag, times);
	}
	
	public void calcProb(HashMap<UniGram, Integer> unigrams)
	{
		double averageProb = 1.0/unigrams.size();
		m_theta = 0.0;
		for (Entry<UniGram, Integer> entry : unigrams.entrySet())
			m_theta += Math.pow(entry.getKey().getProb()-averageProb, 2);
		m_theta = m_theta/(unigrams.size()-1);
		
		for (Entry<UniGram, Integer> entry : unigrams.entrySet())
		{
			int tag = entry.getKey().getTag();
			double p = entry.getKey().getProb();
			m_root.tagProb.put(tag, p);
		}
		
		HashMap<Character, TreeNode> allChildren = m_root.children;
		for (Entry<Character, TreeNode> entry : allChildren.entrySet())
		{
			calcNodeProb(entry.getValue(), m_root, unigrams.size());
		}
	}
	
	private void calcNodeProb( TreeNode node, TreeNode father, int numTag)
	{
		int sum = 0;
		for(Entry<Integer, Integer> entry : node.tagFreq.entrySet())
		{
			sum += entry.getValue();
		}
		
		for (int tag=1; tag<=numTag; tag++)
		{
			double p = 0;
			if (node.tagFreq.containsKey(tag))
				p = node.tagFreq.get(tag)/sum;
			p += m_theta*father.tagProb.get(tag);
			p /= 1+m_theta;
			node.tagProb.put(tag, p);
		}
		
		for (Entry<Character, TreeNode> entry : node.children.entrySet())
			calcNodeProb(entry.getValue(), node, numTag);
	}
	
	private void addToSuffixTree(String reverse, int tag, int times)
	{
		TreeNode cur = m_root;
		cur.times += times ;
		for (int i=0; i<reverse.length(); i++)
		{
			if (!cur.children.containsKey(reverse.charAt(i)))
				cur.children.put(reverse.charAt(i), new TreeNode(reverse.charAt(i)));
			
			cur = cur.children.get(reverse.charAt(i));
			cur.times += times;
			if (cur.tagFreq.containsKey(tag))
				cur.tagFreq.put(tag, cur.tagFreq.get(tag) + times);
			else
				cur.tagFreq.put(tag, times);
		}
	}
	
	/**
	 * Gets the prob.
	 *
	 * @param word the word
	 * @param tag the tag
	 * @return the prob
	 */
	public double getProb(String word, int tag)
	{
		String reverse = reverse(word);
		int len = reverse.length();
		int index = 0;
		TreeNode cur = m_root;
		while(index<len&&cur.children.containsKey(reverse.charAt(index)))
		{
			cur = cur.children.get(reverse.charAt(index));
			index++;
		}
		return cur.tagProb.get(tag);
	}
	
	private String reverse(String s)
	{
		int len = s.length();
		char[] chars = new char[len];
		for (int i=len-1; i>=0; i--)
			chars[len-1-i] =s.charAt(i);
		
		return new String(chars);
	}
	
}
