/*
 * CorpusProcessing.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
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
 * Last Update:Jul 3, 2011
 * 
 */
package mlfs.chineseSeg.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import mlfs.chineseSeg.crf.model.CHARACTER_FEATURE;
import mlfs.chineseSeg.crf.model.Resource;
import mlfs.util.FixedHeap;

/**
 * The Class CorpusProcessing.
 * 语料加工，待加工语料符合pku语料格式，加工后产生以.train结尾的训练文件
 * 
 * 1)字符信息特征：
 * 1	阿拉伯数字
 * 2	中文数字
 * 3	英文字母
 * 4	标点符号
 * 5	如果标记为S的概率大于等于95%，则将该字加入到single集合中
 * 6	如果标记为B的概率大于95%则将该字加入到prefix集合中
 * 7	如果标记为E的概率大于95%，则将该字加入到suffix集合中
 * 8	计算在长词中的频率，最后选取频率最大的35个字最为longset的成员（长度大于6个子为长词）
 * 9	Others
 * 
 * 2)字符对应的词典begin特征：
 * 对该句子以候选字符开始的所有子串，取包含在词典中长度最大的子串长度为该字符的begin信息。
 * 例如：语料中有如下句子：北京举行新年音乐会，对于其中的“新”字，该句子以新字开始的子串有
 * “新”、“新年”、“新年音”、“新年音乐”、“新年音乐会”共5个。在词典中查找以上所有子串，包含在
 * 词典中长度最长的子串是“新年”，所以在该句子中“新”字的begin信息为2。
 * 
 * 3)字符对应的词典middle特征：
 * 于组成句子的其他候选字符，对该句子包含该字符且不以该字符开始和结束的所有子串，取包含在
 * 词典中长度最大子串的长度为该字符的middle信息。
 * 例如：语料中有如下句子：北京举行新年音乐会，对于其中的“乐”字，包好“乐”字且不是开始和结尾
 * 的子串有“北京举行新年音乐会”、“京举行新年音乐会”、“举行新年音乐会”、“行新年音乐会”、“新年音乐会”、
 * “年音乐会”、“音乐会”共7个。在词典中查找以上所有子串，包含在词典中长度最长的子串是“音乐会”，
 * 所以在该句子中“乐”字的middle信息为3。
 * 
 * 4)字符对应的词典end特征：
 * 对该句子的以候选字符结尾的所有子串，取包含在词典中长度最大的子串长度为该字符的end信息。
 * 例如：语料中有如下句子：北京举行新年音乐会，对于其中的“行”字，该句子以“行”字结尾的子串有
 * “行”、“举行”、“京举行”、“北京举行”共4个。在词典中查找以上所有子串，包含在词典中长度最长的
 * 子串是“举行”，所以在该句子中“行”字的end信息为2。
 * 
 * TAG:
 * B(0), M(1), E(2), S(3);
 */
public class CorpusProcessing {
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(CorpusProcessing.class.getName());

	/** 如果出现为某个tag的频率超过这个阈值，则把对应的feature（feature 2、3、4）置一. */
	private static double FREQ_THESHOLE = 0.95;
	
	/** 选取feature1属于长词的个数. */
	private static int NUM_LONGWORD = 35;
	
	/** 生成的train文件的文件名. */
	private static String CHINESE_SEGMENT_CRF_TRAIN = "CHINESE_SEGMENT_CRF.train";
	
	/** train文件的语料地址. */
	private String m_path;
	
	/** 资源. */
	private Resource m_resource;
	
	private Map<Character, Integer> m_begCounter;
	
	private Map<Character, Integer> m_midCounter;
	
	private Map<Character, Integer> m_endCounter;
	
	private Map<Character, Integer> m_singleCounter;
	
	/** 统计字符出现的次数. */
	private Map<Character, Integer> m_longword;
	
	/** 具有长词特征的字符集合. */
	private Set<Character> m_longwordLetters;
	
	/**
	 * Instantiates a new corpus processing.
	 *
	 * @param path 训练语料路径
	 */
	public CorpusProcessing(String path)
	{
		this.m_path = path;
		m_begCounter = new HashMap<Character, Integer>();
		m_midCounter = new HashMap<Character, Integer>();
		m_endCounter = new HashMap<Character, Integer>();
		m_singleCounter = new HashMap<Character, Integer>();
		
		m_longword = new HashMap<Character, Integer>();
		m_longwordLetters = new HashSet<Character>();
		
		logger.info("load resource...");
		m_resource = Resource.getInstance();
	}
	
	/**
	 * First pass.
	 * 第一遍过训练文件，统计信息
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void firstPass() throws IOException
	{
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_path)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String[] words = line.split("\\s+");//词集合
			for (String w : words)
			{
				char[] charSeq = w.toCharArray();//字集合
				
				int len = charSeq.length;
				for (int i=0; i<len; i++)
				{
					char key = charSeq[i];
					if (i==0&&len==1)//single
					{
						Integer singleTimes = m_singleCounter.get(key);
						if (singleTimes == null)
							m_singleCounter.put(key, 1);
						else
							m_singleCounter.put(key, singleTimes+1);
					}
					else if (i == 0&&len>1)//beg
					{
						Integer begTimes = m_begCounter.get(key);
						if (begTimes == null)
							m_begCounter.put(key, 1);
						else
							m_begCounter.put(key, begTimes+1);
					}
					else if (i == len-1)//end
					{
						Integer endTimes = m_endCounter.get(key);
						if (endTimes == null)
							m_endCounter.put(key, 1);
						else
							m_endCounter.put(key, endTimes+1);
					}
					else//middle
					{
						Integer midTimes = m_midCounter.get(key);
						if (midTimes == null)
							m_midCounter.put(key, 1);
						else
							m_midCounter.put(key, midTimes+1);
					}
					
					if (len >= 6)
					{
						Integer longTimes = m_longword.get(key);
						if (longTimes == null)
							m_longword.put(key, 1);
						else
							m_longword.put(key, longTimes+1);
					}
				}
			}
		}
		reader.close();
	}
	
	/**
	 * 生成以.train结尾的训练文件
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void buildTrainFile() throws IOException
	{
		logger.info("first pass ...");
		firstPass();
		
		//get top 35 most freq long word
		FixedHeap<WordFreq> heap = new FixedHeap<WordFreq>(NUM_LONGWORD, FixedHeap.SORT.MAX_ELEMENT);
		for (Entry<Character, Integer> wf : m_longword.entrySet())
		{
			if (m_resource.isDigit(wf.getKey()))
				continue;
			if (m_resource.isChineseDigit(wf.getKey()))
				continue;
			if (m_resource.isLetter(wf.getKey()))
				continue;
			if (m_resource.isPunctuation(wf.getKey()))
				continue;
			heap.add(new WordFreq(wf.getKey(), wf.getValue()));
		}
		
		logger.info("top " + NUM_LONGWORD + " freq letters in long word : ");
		List<WordFreq> lst = heap.asList();
		StringBuilder sb = new StringBuilder();
		for (WordFreq wf : lst)
		{
			sb.append(wf.letter + ":" + wf.freq).append(" ");
			m_longwordLetters.add(wf.letter);
		}
		logger.info(sb.toString());
					
		File outfile = new File(CHINESE_SEGMENT_CRF_TRAIN);
		if (!outfile.exists())
			outfile.createNewFile();
		
		PrintWriter writer = new PrintWriter(outfile);
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_path)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			String[] words = line.split("\\s+");//词集合
			for (String w : words)
			{
				char[] charSeq = w.toCharArray();//字集合
				for (int i=0; i<charSeq.length; i++)
				{
					char key = charSeq[i];
					int feature;
					char tag;
					if (charSeq.length == 1)
						tag = 'S';
					else if (i == 0)
						tag = 'B';
					else if (i == charSeq.length-1)
						tag = 'E';
					else
						tag = 'M';
					
					//freq information
					int freq = 0;
					int begfreq = 0;
					int midfreq = 0;
					int endfreq = 0;
					int singlefreq = 0;
					if (m_begCounter.containsKey(key))
						begfreq = m_begCounter.get(key);
					if (m_midCounter.containsKey(key))
						midfreq = m_midCounter.get(key);
					if (m_endCounter.containsKey(key))
						endfreq = m_endCounter.get(key);
					if (m_singleCounter.containsKey(key))
						singlefreq = m_singleCounter.get(key);
					freq = begfreq + midfreq + endfreq + singlefreq;
					
					if (m_resource.isDigit(key))
						feature = CHARACTER_FEATURE.DIGIT.getValue();
					else if (m_resource.isChineseDigit(key))
						feature = CHARACTER_FEATURE.CHINESE_DIGIT.getValue();
					else if (m_resource.isLetter(key))
						feature = CHARACTER_FEATURE.LETTER.getValue();
					else if (m_resource.isPunctuation(key))
						feature = CHARACTER_FEATURE.PUNCTUATION.getValue();
					else if (1.0*begfreq/freq >= FREQ_THESHOLE)
						feature = CHARACTER_FEATURE.PREFIX.getValue();
					else if (1.0*endfreq/freq >= FREQ_THESHOLE)
						feature = CHARACTER_FEATURE.SUFFIX.getValue();
					else if (1.0*singlefreq/freq >= FREQ_THESHOLE)
						feature = CHARACTER_FEATURE.SINGLE.getValue();
					else if (m_longwordLetters.contains(key))
						feature = CHARACTER_FEATURE.LONGEST.getValue();
					else
						feature = CHARACTER_FEATURE.OTHERS.getValue();
						
					StringBuilder data = new StringBuilder();
					data.append(key).append('\t');
					data.append(feature).append("\t");
					data.append(tag);
					writer.println(data.toString());
				}
			}
			writer.println();
		}
		
		reader.close();
		writer.close();
	}

	/**
	 * Gets the new train file.
	 *
	 * @return the new train file
	 */
	public File getNewTrainFile()
	{
		return new File(CHINESE_SEGMENT_CRF_TRAIN);
	}
	
	
}
