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
 * Last Update:Jul 16, 2011
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
import java.util.Map.Entry;
import java.util.logging.Logger;

import mlfs.chineseSeg.crf.model.Resource;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

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
	
	/** 特征cache. */
	private FeatureCacher m_cacher;
	
	/** 特征id的map. */
	private Map<String, Integer> m_featIdMap;
	
	/** 模板类. */
	private TemplateHandler m_template;
	
	/**字符特征. */
	private Map<String, List<String>> CHAR_FEAT;
	
	/**
	 * Instantiates a new corpus reader for trainer
	 *
	 * @param path the path
	 */
	public CorpusReader(String path)
	{
		m_path = path;
		m_tagCounter = 0;
		m_tagIdMap = new HashMap<String, Integer>();
	}
	
	public CorpusReader(String path, CRFModel model)
	{
		m_path = path;
		m_tagCounter = model.getTagNum();
		m_cacher = FeatureCacher.getInstance();
		CHAR_FEAT = model.getCharFeat();		
		m_featIdMap = model.getFeatIdMap();
		m_template = new TemplateHandler(model.getTemplates());
		
		m_tagIdMap = new HashMap<String, Integer>();
		Map<Integer, String> int2tag = model.getIdTag();
		for (Entry<Integer, String> inttag : int2tag.entrySet())
		{
			m_tagIdMap.put(inttag.getValue(), inttag.getKey());
		}
	}
	
	/**
	 * Gets the all events.
	 *
	 * @return the all events
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<CRFEvent> getAllTrainEvents()throws IOException
	{
		return getTrainEvents(m_path);
	}
	
	public List<CRFEvent> getAllTestEvents()throws IOException
	{
		return getTestEvents(m_path);
	}
	/**
	 * Gets the all events.
	 *
	 * @return the all events
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public List<CRFEvent> getTrainEvents(String path)throws IOException
	{
		logger.info("Loading all events...");
		List<CRFEvent> events = new ArrayList<CRFEvent>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
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
	
	public List<CRFEvent> getTestEvents(String path)throws IOException
	{
		List<CRFEvent> events = new ArrayList<CRFEvent>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(path)));
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
//						m_tagCounter++;这里是在测试阶段，不增加tagCounter
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
//					m_tagCounter++;这里是在测试阶段，不增加tagCounter
				}
				event.labels[i] = label;
				event.addCharFeat(sentence.get(i).split("\\s+"));
			}
			events.add(event);
		}
		reader.close();
		
		m_cacher.clear();
		int CACHE_POS = 0;
		for (CRFEvent e : events)
		{
			e.FEATURE_CACHE_POS = CACHE_POS;
			List<Integer> fvec = null;
			for (int i=0; i<e.charFeat.size(); i++)//unigram
			{
				fvec = new ArrayList<Integer>();
				List<String> unigramPred = m_template.getUnigramPred(e, i);
				for (String predicate : unigramPred)
				{
					String unigramFeat = predicate;
					
					if (m_featIdMap.containsKey(unigramFeat))
					{
						fvec.add(m_featIdMap.get(unigramFeat));
					}
					
				}
				m_cacher.add(fvec);
				CACHE_POS++;
			}
			for (int i=1; i<e.charFeat.size(); i++)//bigram
			{
				List<String> bigramPred = m_template.getBigramPred(e, i);
				for (int preTag=0; preTag<m_tagCounter; preTag++)
				{
					fvec = new ArrayList<Integer>();
					for (String predicate : bigramPred)
					{
						String bigramFeat = predicate + Features.FEATURE_JOIN +preTag;
						
						if (m_featIdMap.containsKey(bigramFeat))
							fvec.add(m_featIdMap.get(bigramFeat));
					}
					m_cacher.add(fvec);
					CACHE_POS++;
				}
			}
		}
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
