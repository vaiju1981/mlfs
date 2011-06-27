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
 * Last Update:Jun 25, 2011
 * 
 */
package mlfs.crf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;

public class Features {

	private static Logger logger = Logger.getLogger(Features.class.getName());
	
	private TemplateHandler m_template;
	
	private Map<String, Integer> m_featIdMap;
	private int m_featCounter;
	
	private Map<String, Integer> m_tagMap;
	
	private int START;
	private int END;
	
	public Features(TemplateHandler templateHandler, Map<String, Integer> tagMap)
	{
		m_template = templateHandler;
		m_featCounter = 0;
		m_featIdMap = new HashMap<String, Integer>();
		m_tagMap = tagMap;
		START = m_tagMap.get("START");
		END = m_tagMap.get("END");
	}
	
	public void statisticFeat(List<CRFEvent> events)
	{
		logger.info("statistic featues in training file...");
		for (CRFEvent event : events)
		{
			int len = event.inputs.length;
			for (int i=0; i<=len; i++)//考虑len+1个，最后一个的tag为END
			{
				int preTag = START;
				int tag = END;
				if (i > 0)
					preTag = event.labels[i-1];
				if (i < len)
					tag = event.labels[i];
					
				
				List<String> unigramPred = m_template.getUnigramPred(event, i);
				List<String> bigramPred = m_template.getBigramPred(event, i);
				
					
				for (String predicate : unigramPred)
				{
					String unigramFeat = predicate + "#" + tag;
					
					if (!m_featIdMap.containsKey(unigramFeat))
					{
						m_featIdMap.put(unigramFeat, m_featCounter);
						m_featCounter++;
					}
				}
				for (String predicate : bigramPred)
				{
					String bigramFeat = predicate + "#" +preTag + "#" + tag;
				
					if (!m_featIdMap.containsKey(bigramFeat))
					{
						m_featIdMap.put(bigramFeat, m_featCounter);
						m_featCounter++;
					}
				}
			}
		}
	}
	
	public List<Integer> getFeatures(CRFEvent event)
	{
		List<Integer> feats = new ArrayList<Integer>();
		int len = event.inputs.length;
		for (int i=0; i<=len; i++)
		{
			int preTag;
			int tag;
			if (i==0)
				preTag = m_tagMap.get("START");
			else
				preTag = event.labels[i-1];
			
			if (i == len)
				tag = END;
			else
				tag = event.labels[i];
				
			
			List<String> unigramPred = m_template.getUnigramPred(event, i);
			List<String> bigramPred = m_template.getBigramPred(event, i);
			
			for (String predicate : unigramPred)
			{
				String unigramFeat = predicate + "#" + tag;
				
				if (m_featIdMap.containsKey(unigramFeat))
					feats.add(m_featIdMap.get(unigramFeat));
			}
			for (String predicate : bigramPred)
			{
				String bigramFeat = predicate + "#" +preTag + "#" + tag;
				
				if (m_featIdMap.containsKey(bigramFeat))
					feats.add(m_featIdMap.get(bigramFeat));
			}
		}
		return feats;
	} 
	
	public List<Integer> getFeatures(CRFEvent event, int preTag, int tag, int idx)
	{
		List<Integer> feats = new ArrayList<Integer>();
		List<String> unigramPred = m_template.getUnigramPred(event, idx);
		List<String> bigramPred = m_template.getBigramPred(event, idx);
		
		for (String predicate : unigramPred)
		{
			String unigramFeat = predicate + "#" + tag;
			
			if (m_featIdMap.containsKey(unigramFeat))
				feats.add(m_featIdMap.get(unigramFeat));
		}	
		for (String predicate : bigramPred)
		{
			String bigramFeat = predicate + "#" +preTag + "#" + tag;
			
			if (m_featIdMap.containsKey(bigramFeat))
				feats.add(m_featIdMap.get(bigramFeat));
		}
		return feats;
	}
	
	public int getFeatNum()
	{
		return m_featCounter;
	}
	
	public Map<String, Integer> getTagMap()
	{
		return m_tagMap;
	}
}
