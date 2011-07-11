/*
 * Features.java 
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
 * Last Update:Jul 10, 2011
 * 
 */
package mlfs.crf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.UnigramBigram;

/**
 * The Class Features.
 */
public class Features {

	/** The logger. */
	private static Logger logger = Logger.getLogger(Features.class.getName());
	
	/** 模板操作类. */
	private TemplateHandler m_template;
	
	/** 特征和数字的对应map. */
	private Map<String, Integer> m_featIdMap;
	
	/** 特征总数. */
	private int m_featCounter;
	
	/** tag和id对应的map. */
	private Map<String, Integer> m_tagMap;
	
	/** START tag的int值. */
	private int START;
	
	/** END tag的int值. */
	private int END;
	
	private int m_numTag;
	
	/** 连接符. */
	public static char FEATURE_JOIN = '#';
	
	/**
	 * Instantiates a new features.
	 *
	 * @param templateHandler the template handler
	 * @param tagMap the tag map
	 */
	public Features(TemplateHandler templateHandler, Map<String, Integer> tagMap, List<CRFEvent> events)
	{
		m_template = templateHandler;
		m_featCounter = 0;
		m_featIdMap = new HashMap<String, Integer>();
		m_tagMap = tagMap;
		m_numTag = tagMap.size();
		START = m_tagMap.get("START");
		END = m_tagMap.get("END");
		
		statisticFeat(events);
		buildFeatures(events);
	}
	
	/**
	 * 统计训练语料中出现过的所有特征集合
	 *
	 * @param events the events
	 */
	private void statisticFeat(List<CRFEvent> events)
	{
		logger.info("statistic featues in training file...");
		for (CRFEvent event : events)
		{
			int len = event.inputs.length;
			for (int i=0; i<=len; i++)//最后一个tag是end
			{
				int preTag = START;
				int tag = END;
				if (i > 0)
					preTag = event.labels[i-1];
				if (i < len)
					tag = event.labels[i];
					
				List<String> unigramPred = m_template.getUnigramPred(event, i);
				for (String predicate : unigramPred)
				{
					String unigramFeat = predicate;
					
					if (!m_featIdMap.containsKey(unigramFeat))
					{
						m_featIdMap.put(unigramFeat, m_featCounter);
						m_featCounter++;
					}
				}
				
				List<String> bigramPred = m_template.getBigramPred(event, i);
				for (String predicate : bigramPred)
				{
					String bigramFeat = predicate + FEATURE_JOIN +preTag;
				
					if (!m_featIdMap.containsKey(bigramFeat))
					{
						m_featIdMap.put(bigramFeat, m_featCounter);
						m_featCounter++;
					}
				}
			}
		}
	}
	
	/**
	 * Builds the features.
	 *
	 * @param events the events
	 */
	private void buildFeatures(List<CRFEvent> events)
	{
		logger.info("Build features...");
		for (CRFEvent event : events)
		{
			int len = event.inputs.length;
			for (int i=0; i<=len; i++)//最后一个tag是end
			{
					
				List<String> unigramPred = m_template.getUnigramPred(event, i);
				List<Integer> unigrams = new ArrayList<Integer>();
				for (String predicate : unigramPred)
				{
					String unigramFeat = predicate;
					
					if (m_featIdMap.containsKey(unigramFeat))
						unigrams.add(m_featIdMap.get(unigramFeat));
				}
				
				List<List<Integer>> bigrams = new ArrayList<List<Integer>>(m_numTag);
				for (int preTag=0; preTag<m_numTag; preTag++)
				{
					List<Integer> bigramFeats = new ArrayList<Integer>();
					List<String> bigramPred = m_template.getBigramPred(event, i);
					for (String predicate : bigramPred)
					{
						String bigramFeat = predicate + FEATURE_JOIN +preTag;
						
						if (m_featIdMap.containsKey(bigramFeat))
							bigramFeats.add(m_featIdMap.get(bigramFeat));
					}
					bigrams.add(bigramFeats);
				}
				event.unigramBigramFeats.add(new UnigramBigram(unigrams, bigrams));
			}
			
		}
	}
	/**
	 * 给定前一个tag，event以及针对event的哪一个，获取满足的特征集合
	 *
	 * @param event the event
	 * @param preTag the pre tag
	 * @param idx the idx
	 * @return the features
	 */
	public List<Integer> getFeatures(CRFEvent event, int preTag, int idx)
	{
		List<Integer> feats = event.unigramBigramFeats.get(idx).getUnigramFeats();
		
		List<Integer> bigramFeats = event.unigramBigramFeats.get(idx).getBigramFeats().get(preTag);
		
		feats.addAll(bigramFeats);
		
		return feats;
	}
	
	/**
	 * 获取特征总数.
	 *
	 * @return the feat num
	 */
	public int getFeatNum()
	{
		return m_featCounter;
	}
	
	/**
	 * Gets the tag map.
	 *
	 * @return the tag map
	 */
	public Map<String, Integer> getTagMap()
	{
		return m_tagMap;
	}
	
	/**
	 * Gets the template file.
	 *
	 * @return the template file
	 */
	public String getTemplateFile()
	{
		return m_template.getPath();
	}
	
	/**
	 * Gets the feat map.
	 *
	 * @return the feat map
	 */
	public Map<String, Integer> getFeatMap()
	{
		return m_featIdMap;
	}
	
	/**
	 * Gets the unigram feat.
	 *
	 * @param event the event
	 * @param idx the idx
	 * @return the unigram feat
	 */
	public List<Integer> getUnigramFeat(CRFEvent event, int idx)
	{
		return event.unigramBigramFeats.get(idx).getUnigramFeats();
	}
	
	/**
	 * Gets the bigram feat.
	 *
	 * @param bigramPred the bigram pred
	 * @param preTag the pre tag
	 * @return the bigram feat
	 */
	public List<Integer> getBigramFeat( CRFEvent event, int preTag, int idx)
	{
		return event.unigramBigramFeats.get(idx).getBigramFeats().get(preTag);
	}
}
