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
 * Last Update:Jul 3, 2011
 * 
 */
package mlfs.crf.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;

/**
 * The Class CorpusReader.
 */
public class CorpusReader {
	
	/** The logger. */
	private Logger logger = Logger.getLogger(CorpusReader.class.getName());
	
	/** 路径. */
	private String m_path;
	
	/** tag和数字对应. */
	private Map<String, Integer> m_tagIdMap;
	
	/** tag总数. */
	private int m_tagCounter;
	
	/**
	 * Instantiates a new corpus reader.
	 *
	 * @param path the path
	 */
	public CorpusReader(String path)
	{
		m_path = path;
		m_tagCounter = 0;
		m_tagIdMap = new HashMap<String, Integer>();
		
	}
	
	/**
	 * Gets the all events.
	 *
	 * @return the all events
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<CRFEvent> getAllEvents()throws IOException
	{
		logger.info("Loading all events...");
		List<CRFEvent> events = new ArrayList<CRFEvent>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_path)));
		List<String> sentence = new ArrayList<String>();
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			if (line.equals(""))
			{
				int sz = sentence.size();
				if (sz == 0)
					continue;
				int[] labels = new int[sz];
				CRFEvent event = new CRFEvent(labels);
				for (int i=0; i<sz; i++)
				{
					String[] vec = sentence.get(i).split("\\s+");
					Integer label = m_tagIdMap.get(vec[vec.length-1]);
					if (label == null)
					{
						label = m_tagCounter;
						m_tagIdMap.put(vec[vec.length-1], label);
						m_tagCounter++;
					}
					event.labels[i] = label;
					event.addCharFeat(sentence.get(i).split("\\s+"));
				}
				events.add(event);
				sentence.clear();
			}
			else
			{
				sentence.add(line);
			}
		}
		
		//last sentence
		int sz = sentence.size();
		if (sz != 0)
		{
			int[] labels = new int[sz];
			CRFEvent event = new CRFEvent(labels);
			for (int i = 0; i < sz; i++) {
				String[] vec = sentence.get(i).split("\\s+");
				Integer label = m_tagIdMap.get(vec[vec.length - 1]);
				if (label == null) {
					label = m_tagCounter;
					m_tagIdMap.put(vec[vec.length - 1], label);
					m_tagCounter++;
				}
				event.labels[i] = label;
				event.addCharFeat(sentence.get(i).split("\\s+"));
			}
			events.add(event);
		}
		reader.close();
		return events;
	}
	
	/**
	 * Gets the tag map.
	 *
	 * @return the tag map
	 */
	public Map<String, Integer> getTagMap()
	{
		return m_tagIdMap;
	}
	
}
