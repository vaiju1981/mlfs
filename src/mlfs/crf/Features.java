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
 * Last Update:Jul 4, 2011
 * 
 */
package mlfs.crf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;

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
	
	/** 连接字符. */
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
		
		statisticFeat(events);
		logger.info("Feature Number : " + m_featCounter*m_tagMap.size());
	}
	
	/**
	 * 统计训练语料中出现过的所有特征集合
	 *
	 * @param events the events
	 */
	public void statisticFeat(List<CRFEvent> events)
	{
		logger.info("statistic featues in training file...");
		int numTag = m_tagMap.size();
		for (CRFEvent event : events)
		{
			int len = event.inputs.length;
			for (int i=0; i<len; i++)
			{
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
				
				if (i != 0)
				{
					List<String> bigramPred = m_template.getBigramPred(event, i);
					for (String predicate : bigramPred)
					{
						
						for (int preTag=0; preTag<numTag; preTag++)
						{
							String bigramFeat = predicate + FEATURE_JOIN +preTag;
							
							if (!m_featIdMap.containsKey(bigramFeat))
							{
								m_featIdMap.put(bigramFeat, m_featCounter);
								m_featCounter++;
							}
						}
					}
				}//i != 0
			}
		}
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
	public String getTemplateFilePath()
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
		if (idx<0 || idx>=event.inputs.length)
			throw new IllegalArgumentException("idx 必须在 0～" +(event.inputs.length-1) + "之间");
		
		List<Integer> feats = new ArrayList<Integer>();
		List<String> unigramPred = m_template.getUnigramPred(event, idx);
		
		for (String predicate : unigramPred)
		{
			String unigramFeat = predicate;
			
			if (m_featIdMap.containsKey(unigramFeat))
				feats.add(m_featIdMap.get(unigramFeat));
		}	
		return feats;
	}
	
	/**
	 * Gets the bigram feat.
	 *
	 * @param bigramPred the bigram pred
	 * @param preTag the pre tag
	 * @return the bigram feat
	 */
	public List<Integer> getBigramFeat( List<String> bigramPred, int preTag)
	{
		List<Integer> feats = new ArrayList<Integer>();
		for (String predicate : bigramPred)
		{
			String bigramFeat = predicate + FEATURE_JOIN +preTag;
			
			if (m_featIdMap.containsKey(bigramFeat))
				feats.add(m_featIdMap.get(bigramFeat));
		}
		return feats;
	}
	
	public List<Integer>getBigramFeat(CRFEvent event, int preTag, int idx)
	{
		if (idx<=0||idx>=event.inputs.length)
			throw new IllegalArgumentException("idx必须在1～"+(event.inputs.length-1)+"之间");
		List<String> preds = m_template.getBigramPred(event, idx);
		return getBigramFeat(preds, preTag);
	}
}
