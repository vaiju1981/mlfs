/*
 * CorpusReader.java 
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
 * Last Update:Jun 15, 2011
 * 
 */

package mlfs.textClassification.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import mlfs.maxent.model.MEEvent;
import mlfs.maxent.model.TrainDataHandler;

/**
 * The Class CorpusReaders
 * 这里使用的是路透社经过reuters21578Helper.jar经过处理的语料，
 * reuters21578Hepler.jar可以从项目主页上找到
 * 
 * 格式：
 * label filecontext
 * 并且经过stemming和去停用词
 */
public class CorpusReader {
	
	/** The logger. */
	private Logger logger = Logger.getLogger(CorpusReader.class.getName());
	
	/** The file path. */
	private String m_filePath;
	
	/** The predicates collection. */
	private HashSet<Integer> m_predicates;
	
	/** 通过阈值的谓词集合. */
	private	HashSet<Integer> m_passedPreds = new HashSet<Integer>();
	
	/** 通过阈值的label集合. */
	private	HashSet<Integer> m_passedLabels = new HashSet<Integer>();
	
	
	/** 每篇文档里feature要至少出现多少次. */
	private int m_cutoffPerDoc;
	
	/** 谓词至少要在多少个文档里出现. */
	private int m_cutoffDocs;

	/**
	 * Instantiates a new corpus reader.
	 *
	 * @param file the file
	 * @param cutoffPerDoc the cutoff per doc
	 * @param cutoffDocs the cutoff docs
	 */
	public CorpusReader(String file, int cutoffPerDoc, int cutoffDocs)
	{
		this.m_filePath = file;
		this.m_cutoffPerDoc = cutoffPerDoc;
		this.m_cutoffDocs = cutoffDocs;
		
		this.m_predicates = new HashSet<Integer>();
	}
	
	public CorpusReader(String file)
	{
		this.m_filePath = file;
		this.m_cutoffPerDoc = 0;
		this.m_cutoffDocs = 0;
		
		this.m_predicates = new HashSet<Integer>();
	}
	

	/**
	 * 在训练语料中进行统计.
	 *
	 *要求谓词满足的约束条件：
	 *	1、一篇文章里出现次数要大于阈值1
	 *	2、出现在的文章数大于阈值2
	 *
	 * @return the array list
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private ArrayList<MEEvent> statistics() throws IOException
	{
		logger.info("Analysising train data...");
		//第一遍，统计谓词出现在多少篇文档中
		//约束条件：
		//1、一篇文章里出现次数要大于阈值
		//2、出现在的文章数大于阈值
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_filePath)));
		String line = null;
		HashMap<Integer, Integer> wordsNum = new HashMap<Integer, Integer>();
		while ((line = reader.readLine()) != null)
		{
			String[] splits = line.split("\\s+");
			
			for (int i=1; i<splits.length; i++)
			{
				String[] wordcount = splits[i].split(":");
				int word = Integer.parseInt(wordcount[0]);
				m_predicates.add(word);
				
				if (wordsNum.containsKey(word))
					wordsNum.put(word, wordsNum.get(word)+1);
				else
					wordsNum.put(word, Integer.parseInt(wordcount[1]));
			}
		}
		reader.close();
		//第二遍 按照约束条件过滤
		reader = new BufferedReader(new FileReader(new File(m_filePath)));
		ArrayList<MEEvent> events = new ArrayList<MEEvent>();
		while ((line = reader.readLine()) != null)
		{
			String[] words = line.trim().split("\\s+");
			
			int label = Integer.parseInt(words[0]);
			
			ArrayList<Integer> predicates = new ArrayList<Integer>();
			ArrayList<Double> values = new ArrayList<Double>();
			for (int i=1; i<words.length; i++)
			{
				String[] wordCount = words[i].split(":");
				int word = Integer.parseInt(wordCount[0]);
				double count= Double.parseDouble(wordCount[1]);
				
				if (count>=m_cutoffPerDoc && wordsNum.get(word)>=m_cutoffDocs)
				{
					predicates.add(word);
					values.add(count);
				}
			}
			
			if (predicates.size() == 0)
				continue;
			
			int[] intPreds = new int[predicates.size()];
			double[] intVals = new double[values.size()];
			for (int i=0; i<intPreds.length; i++)
			{
				intPreds[i] = predicates.get(i);
				intVals[i] = values.get(i);
				
				m_passedPreds.add(predicates.get(i));
			}
			m_passedLabels.add(label);
			events.add(new MEEvent(label, intPreds, intVals));
		}
		reader.close();
		return events;
	}
	
	/**
	 * Gets the train data hadler.
	 *
	 * @return the train data hadler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TrainDataHandler getTrainDataHadler() throws IOException
	{
		ArrayList<MEEvent> events = statistics();
		
		int numPred = 0;
		Iterator<Integer> iter = m_predicates.iterator();
		while (iter.hasNext())
		{
			int n = iter.next();
			if (n > numPred)
				numPred = n;
		}
		numPred++;
		
		int numLabels = 0;
		iter = m_passedLabels.iterator();
		while (iter.hasNext())
		{
			int n = iter.next();
			if (n > numLabels)
				numLabels = n;
		}
		numLabels++;
		
		return new TrainDataHandler(m_passedPreds, m_passedLabels, events, numPred, numLabels);
	}

	
}
