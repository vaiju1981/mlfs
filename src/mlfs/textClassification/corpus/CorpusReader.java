/*
 * CorpusReader.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under the Creative Commons Attribution 3.0 Unported License. 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/3.0/ 
 * 
 * Last Update:Jun 11, 2011
 * 
 */
package mlfs.textClassification.corpus;

import java.awt.Event;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

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
	
	private String m_filePath;
	
	/** 谓词->数字. */
	private HashMap<String, Integer> m_dict;
	
	private HashMap<String, Integer> m_label2idMap;
	
	private int m_numPredicates;
	private int m_numLabels;
	
	private int m_cutoffPerDoc;
	private int m_cutoffDocs;

	public CorpusReader(String file, int cutoffPerDoc, int cutoffDocs)
	{
		this.m_filePath = file;
		this.m_cutoffPerDoc = cutoffPerDoc;
		this.m_cutoffDocs = cutoffDocs;
		
		this.m_dict = new HashMap<String, Integer>();
		this.m_label2idMap = new HashMap<String, Integer>();
		this.m_numPredicates = 0;
		this.m_numLabels = 0;
	}
	
	public ArrayList<Event> getEvents() throws IOException
	{
		HashMap<Integer, Integer> predDocs = new HashMap<Integer, Integer>();
		//create tmp file
		File tmpFile = File.createTempFile("corpusReader", null);
//		tmpFile.deleteOnExit();
		PrintWriter tmpWriter = new PrintWriter(tmpFile);
		
		//第一遍，统计有那些谓词通过了
		//约束条件：
		//1、一篇文章里出现次数要大于阈值1
		//2、出现在的文章数大于阈值2
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_filePath)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			HashMap<Integer, Integer> linePreCounter = new HashMap<Integer, Integer>();
			
			String[] words = line.split("\\s+");
			
			Integer label = m_label2idMap.get(words[0]);
			if (label == null)
			{
				m_numLabels++;
				label = m_numLabels;
				m_label2idMap.put(words[0], label);
			}
			//skip label, start from index 1
			for (int i=1; i<words.length; i++)
			{
				String word = words[i];
				
				Integer predicate = m_dict.get(word);
				if (predicate == null)
				{
					m_numPredicates++;
					predicate = m_numPredicates;
					m_dict.put(word, predicate);
				}
				
				if (linePreCounter.containsKey(predicate))
					linePreCounter.put(predicate, linePreCounter.get(predicate)+1);
				else
					linePreCounter.put(predicate, 1);
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(label.intValue()).append("\t");
			for (Entry<Integer, Integer> predSeen : linePreCounter.entrySet())
			{
				Integer predicate = predSeen.getKey();
				Integer seen = predSeen.getValue();
				
				if (seen > m_cutoffPerDoc)
				{
					if (predDocs.containsKey(predicate))
						predDocs.put(predicate, predDocs.get(predicate)+1);
					else
						predDocs.put(predicate, 1);
					for (int i=0; i<seen; i++)
						sb.append(predicate).append(" ");
				}
			}
			tmpWriter.println(sb.toString());
		}
		reader.close();
		tmpWriter.close();
		
		ArrayList<Event> events = new ArrayList<Event>();
		reader = new BufferedReader(new FileReader(tmpFile));
		reader.close();
//		tmpFile.delete();
		
		return events;
	}
	
	private static void checkAndPlus1(Map<String, Integer> map, String w )
	{
		if (map.containsKey(w))
			map.put(w, map.get(w)+1);
		else
			map.put(w, 1);
	}
}
