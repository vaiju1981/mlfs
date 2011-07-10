/*
 * CRFModel.java 
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
package mlfs.crf.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;

/**
 * The Class CRFModel.
 */
public class CRFModel {

	/** 保存所有训练语料中的CRFEvent特征序列. */
	private Map<String, List<String>> CHAR_FEAT;
	
	/** 特征对应数字的map. */
	private Map<String, Integer> m_featIdMap;
	
	/** tag对应数字的map. */
	private Map<String, Integer> m_tagMap;
	
	/** 数字对应tag的map. */
	private Map<Integer, String> m_int2tag;
	
	/** 参数. */
	private double[][] m_parameters;
	
	/** The m_template handler. */
	private TemplateHandler m_templateHandler;
	
	/** The m_num tag. */
	private int m_numTag;
	
	/** The STAR t_ tag. */
	private int START_TAG;
	
	/** The EN d_ tag. */
	private int END_TAG;
	
	/**
	 * Instantiates a new cRF model.
	 *
	 * @param charfeats 通过训练文件统计出的所有input以及对应的特征列表
	 * @param featIdMap the feat id map
	 * @param tagMap the tag map
	 * @param parameters the parameters
	 * @param templatePath 模板文件的路径
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFModel(Map<String, List<String>> charfeats, Map<String, Integer> featIdMap,Map<String, Integer> tagMap, double[][] parameters, String templatePath) throws IOException
	{
		CHAR_FEAT = charfeats;
		m_featIdMap = featIdMap;
		m_tagMap = tagMap;
		m_numTag = m_tagMap.size();
		
		m_parameters = parameters;
		
		m_int2tag = new HashMap<Integer, String>();
		for (Entry<String, Integer> tagint : m_tagMap.entrySet())
		{
			m_int2tag.put(tagint.getValue(), tagint.getKey());
		}
		
		m_templateHandler = new TemplateHandler(templatePath);
		
		START_TAG = m_tagMap.get("START");
		END_TAG = m_tagMap.get("END");
	}
	
	
	/**
	 * 预测一个给定一个CRFEvent的状态序列
	 * 使用维特比解码算法
	 *
	 * @param e the e
	 * @return the list
	 */
	public List<String> label(CRFEvent e)
	{
		List<String> labels = new ArrayList<String>();
		
		int len = e.inputs.length;
		
		double[][] delta = new double[m_numTag][len];
		int[][] phi = new int[m_numTag][len];
		
		int lastIdx = -1;
		for (int t=0; t<=len; t++)
		{
			if (t == 0)
			{
				List<Integer> feats = getFeats(e, t, m_tagMap.get("START"));
				for (int tag=0; tag<m_numTag; tag++)
				{
					if (tag==START_TAG || tag==END_TAG)
						continue;
					delta[tag][t] = calcLogProb(feats, tag);
					phi[tag][t] = -1;
				}
			}
			else if (t == len)
			{
				double max = Double.NEGATIVE_INFINITY;
				for (int preTag=0; preTag<m_numTag; preTag++)
				{
					if (preTag==START_TAG || preTag==END_TAG)
						continue;
					List<Integer> feats = getFeats(e, t, preTag);
					double tmp = calcLogProb(feats, m_tagMap.get("END"));
					if (tmp + delta[preTag][t-1] > max)
					{
						max = tmp + delta[preTag][t-1];
						lastIdx = preTag;
					}
				}
			}
			else
			{
				//delta
				for (int tag=0; tag<m_numTag; tag++)
				{
					if (tag==START_TAG || tag==END_TAG)
						continue;
					double max = Double.NEGATIVE_INFINITY;
					for (int preTag=0; preTag<m_numTag; preTag++)
					{
						if (preTag==START_TAG || preTag==END_TAG)
							continue;
						List<Integer> feats = getFeats(e, t, preTag);
						double tmp = calcLogProb(feats, tag);
						if (tmp + delta[preTag][t-1] > max)
						{
							max = tmp + delta[preTag][t-1];
							phi[tag][t] = preTag;
						}
					}
					delta[tag][t] = max;
				}
//				//phi
//				for (int tag=0; tag<m_numTag; tag++)
//				{
//					if (tag==START_TAG || tag==END_TAG)
//						continue;
//					double max = Double.NEGATIVE_INFINITY;
//					for (int preTag=0; preTag<m_numTag; preTag++)
//					{
//						if (preTag==START_TAG || preTag==END_TAG)
//							continue;
//						List<Integer> bigramFeat = getBigramFeats(e, t, preTag);
//						double tmp = calcLogProb(bigramFeat, tag);
//						if (tmp + delta[preTag][t-1] > max)
//						{
//							max = tmp + delta[preTag][t-1];
//							phi[tag][t] = preTag;
//						}
//					}
//				}
			}
		}
		
		int[] stack = new int[len];
		stack[len-1] = lastIdx;
		for (int t = len-1; t>0; t--)
			stack[t-1] = phi[stack[t]][t];
		
		for(int i=0; i<len; i++)
			labels.add(m_int2tag.get(stack[i]));
		
		return labels;
	}
	
	/**
	 * Gets the feats.
	 *
	 * @param e the e
	 * @param i the i
	 * @param preTag the pre tag
	 * @return the feats
	 */
	private List<Integer> getFeats(CRFEvent e, int i, int preTag)
	{
		List<Integer> uFeats = getUnigramFeats(e, i);
		List<Integer> bFeats = getBigramFeats(e, i, preTag);
		
		List<Integer> feat = new ArrayList<Integer>(uFeats.size()+bFeats.size());
		feat.addAll(uFeats);
		feat.addAll(bFeats);
		
		return feat;
	}
	
	/**
	 * Gets the unigram feats.
	 *
	 * @param e the e
	 * @param i the i
	 * @return the unigram feats
	 */
	private List<Integer> getUnigramFeats(CRFEvent e, int i)
	{
		List<Integer> feat = new ArrayList<Integer>();
		List<String> unigramPred = m_templateHandler.getUnigramPred(e, i);
		
		for (String u : unigramPred)
		{
			if (m_featIdMap.containsKey(u))
				feat.add(m_featIdMap.get(u));
		}
		
		return feat;
	}
	
	/**
	 * Gets the bigram feats.
	 *
	 * @param e the e
	 * @param i the i
	 * @param preTag the pre tag
	 * @return the bigram feats
	 */
	private List<Integer> getBigramFeats(CRFEvent e, int i, int preTag)
	{
		List<Integer> feat = new ArrayList<Integer>();
		
		List<String> bigramPred = m_templateHandler.getBigramPred(e, i);
		
		for (String b : bigramPred)
		{
			b = b+"#"+preTag;
			if (m_featIdMap.containsKey(b))
				feat.add(m_featIdMap.get(b));
		}
		
		return feat;
	}
	
	/**
	 * Calc log prob.
	 *
	 * @param feats the feats
	 * @param tag the tag
	 * @return the double
	 */
	private double calcLogProb(List<Integer> feats, int tag)
	{
		double res = 0.0;
		for (int f : feats)
			res += m_parameters[f][tag];
		
		return res;
	}
	
	/**
	 * Gets the char feat.
	 *
	 * @return the char feat
	 */
	public Map<String, List<String>> getCharFeat()
	{
		return CHAR_FEAT;
	}
}
