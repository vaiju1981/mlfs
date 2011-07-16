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
package mlfs.chineseSeg.debug;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import mlfs.crf.TemplateHandler;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.model.CRFEvent;

/**
 * The Class Features.
 */
public class Features4mason {
	

	/** The logger. */
	private static Logger logger = Logger.getLogger(Features4mason.class.getName());
	
	/** 模板操作类. */
	private TemplateHandler m_template;
	
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
	 * @throws IOException 
	 */
	public Features4mason(TemplateHandler templateHandler, Map<String, Integer> tagMap, List<CRFEvent> events, String modelPath) throws IOException
	{
		m_template = templateHandler;
		m_featCounter = 0;
		m_tagMap = tagMap;
		
	}
	
	/**
	 * 统计训练语料中出现过的所有特征集合
	 *
	 * @param events the events
	 * @throws IOException 
	 */
	public Map<String, Integer> statisticFeat(List<CRFEvent> events, String modelPath) throws IOException
	{
		Map<String, Integer> featIdMap = new HashMap<String, Integer>();
		logger.info("statistic featues in training file...");
		PrintWriter writer = new PrintWriter(new File(modelPath));
			
		Set<String> dict = new HashSet<String>();
		int numTag = m_tagMap.size();
		
		FeatureCacher cacher = FeatureCacher.getInstance();
		for (CRFEvent event : events)
		{
			event.FEATURE_CACHE_POS = cacher.size();
			
			int len = event.labels.length;
			List<Integer> feats = null;
			for (int i=0; i<len; i++)//unigram
			{
				feats = new ArrayList<Integer>();
				List<String> unigramPred = m_template.getUnigramPred(event, i);
				for (String predicate : unigramPred)
				{
					String unigramFeat = predicate;
					
					if (!featIdMap.containsKey(unigramFeat))
					{
						featIdMap.put(unigramFeat, m_featCounter);
						m_featCounter++;
					}
					feats.add(featIdMap.get(unigramFeat));
				}
				cacher.add(feats);
			}
			for (int i=1; i<len; i++)//bigram
			{
				List<String> bigramPred = m_template.getBigramPred(event, i);
				for (int preTag=0; preTag<numTag; preTag++)
				{
					feats = new ArrayList<Integer>();
					for (String predicate : bigramPred)
					{
						String bigramFeat = predicate + FEATURE_JOIN +preTag;
						
						if (!featIdMap.containsKey(bigramFeat))
						{
							featIdMap.put(bigramFeat, m_featCounter);
							m_featCounter++;
						}
						feats.add(featIdMap.get(bigramFeat));
					}
					cacher.add(feats);
				}
			}
			//持久化
			for (List<String> chars : event.charFeat)
			{
				if (!dict.contains(chars.get(0)))
				{
					dict.add(chars.get(0));
					StringBuilder sb = new StringBuilder();
					for (String s : chars)
						sb.append(s).append(' ');
					writer.println(sb.toString());
				}
			}
			event.charFeat = null;
		}
		writer.println();//隔开charFeat和fidmap
		for (Entry<String, Integer> fid : featIdMap.entrySet())
		{
			writer.println(fid.getKey() + " " + fid.getValue());
		}
		writer.println();//隔开fidmap和剩余内容
			
		writer.close();
		logger.info("Feature Number : " + m_featCounter*m_tagMap.size());
		return featIdMap;
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
	
}
