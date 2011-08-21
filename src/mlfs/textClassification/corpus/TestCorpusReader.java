/*
 * TestCorpusReader.java 
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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import mlfs.maxent.model.MEEvent;

/**
 * The Class TestCorpusReader.
 * 读取分类测试数据，这里的读取数据方法和训练语句读取的方法不同，
 * 主要是因为训练数据的读取需要用cutoff阈值卡数据，但测试语料不需要进行
 * 这种卡阈值的操作，而且需要对测试语料中的数据去掉训练语料中没有的feature
 * 
 */
public class TestCorpusReader {
	
	/** The m_reader. */
	private BufferedReader m_reader;
	
	/** The m_dict. */
	private Set<Integer> m_dict;
	
	/**
	 * Instantiates a new test corpus reader.
	 *
	 * @param filePath the file path
	 * @param set the set
	 * @throws FileNotFoundException the file not found exception
	 */
	public TestCorpusReader(String filePath, Set<Integer> set) throws FileNotFoundException
	{
		m_reader = new BufferedReader(new FileReader(new File(filePath)));
		m_dict = set;
	}
	
	
	/**
	 * Gets the event.
	 *
	 * @return the event
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public MEEvent getEvent() throws IOException
	{
		String line = m_reader.readLine();
		if (line == null)
			return null;
		
		String[] splits = line.split("\\s+");
		int label = Integer.parseInt(splits[0]);
		ArrayList<Integer> preds = new ArrayList<Integer>();
		ArrayList<Double> counts= new ArrayList<Double>();
		for (int i=1; i<splits.length; i++)
		{
			String[] wordcount = splits[i].split(":");
			int word = Integer.parseInt(wordcount[0]);
			double count= Double.parseDouble(wordcount[1]);
			
			if (!m_dict.contains(word))
				continue;
			
			preds.add(word);
			counts.add(count);
		}
		
		int[] predcates = new int[preds.size()];
		double[] values = new double[counts.size()];
		for (int i=0; i<preds.size(); i++)
		{
			predcates[i] = preds.get(i);
			values[i] = counts.get(i);
		}
		
		return new MEEvent(label, predcates, values);
	}
	
	/**
	 * Close.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void close() throws IOException
	{
		m_reader.close();
	}
}
