/*
 * Parser.java 
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
 * Last Update:Jul 14, 2011
 * 
 */
package mlfs.chineseSeg.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mlfs.chineseSeg.crf.model.CHARACTER_FEATURE;
import mlfs.chineseSeg.crf.model.Resource;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

/**
 *把一行文本，转化为CRFEvent 
 */
public class Parser {
	
	/**字符特征. */
	private Map<String, List<String>> CHAR_FEAT;
	
	/** 资源. */
	private Resource m_resource;
	
	/** 特征cache. */
	private FeatureCacher m_cacher;
	
	/** 特征id的map. */
	private Map<String, Integer> m_featIdMap;
	
	/** 模板类. */
	private TemplateHandler m_template;
	
	/** tag总数. */
	private int m_numTag;
	
	/**
	 * Instantiates a new parser.
	 *
	 * @param model the model
	 */
	public Parser(CRFModel model)
	{
		CHAR_FEAT = model.getCharFeat();
		m_resource = Resource.getInstance();
		m_cacher = FeatureCacher.getInstance();
		
		m_featIdMap = model.getFeatIdMap();
		m_template = new TemplateHandler(model.getTemplates());
		m_numTag = model.getTagNum();
	}
	
	/**
	 * Parses the event.
	 *
	 * @param line the line
	 * @return the cRF event
	 */
	public CRFEvent parseEvent(String line)
	{
		char[] chars = line.toCharArray();
		String[] inputs = new String[chars.length];
		int[] labels = new int[chars.length];
		CRFEvent e = new CRFEvent(labels);
		
		List<String> feats  = null;
		for (int i=0; i<chars.length; i++)
		{
			inputs[i] = chars[i] + "";
			if (CHAR_FEAT.containsKey(inputs[i]))
			{
				feats = CHAR_FEAT.get(inputs[i]);
				e.addCharFeat(feats.toArray(new String[feats.size()]));
			}
			else
			{
				int type;
				if (m_resource.isDigit(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.DIGIT.getValue();
				else if (m_resource.isChineseDigit(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.CHINESE_DIGIT.getValue();
				else if (m_resource.isPunctuation(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.PUNCTUATION.getValue();
				else if (m_resource.isLetter(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.LETTER.getValue();
				else 
					type = CHARACTER_FEATURE.OTHERS.getValue();
				
				List<String> common = new ArrayList<String>();
				common.add(inputs[i]);
				common.add(type+"");
				common.add("?");
				
				e.charFeat.add(common);
			}
		}
		
		e.FEATURE_CACHE_POS = 0;
		m_cacher.clear();
		List<Integer> fvec = null;
		for (int i=0; i<chars.length; i++)//unigram
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
		}
		for (int i=1; i<chars.length; i++)//bigram
		{
			List<String> bigramPred = m_template.getBigramPred(e, i);
			for (int preTag=0; preTag<m_numTag; preTag++)
			{
				fvec = new ArrayList<Integer>();
				for (String predicate : bigramPred)
				{
					String bigramFeat = predicate + Features.FEATURE_JOIN +preTag;
					
					if (m_featIdMap.containsKey(bigramFeat))
						fvec.add(m_featIdMap.get(bigramFeat));
				}
				m_cacher.add(fvec);
			}
		}
		
		return e;
	}
}
