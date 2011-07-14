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
 * Last Update:Jul 13, 2011
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
import mlfs.crf.graph.Edge;
import mlfs.crf.graph.Graph;
import mlfs.crf.graph.Node;

/**
 * The Class CRFModel.
 */
public class CRFModel {

	/** 保存所有训练语料中的CRFEvent特征序列. */
	private Map<String, List<String>> CHAR_FEAT;
	
	/** 数字对应tag的map. */
	private Map<Integer, String> m_int2tag;
	
	/** 参数. */
	private double[] m_parameters;
	
//	private Features m_featureHandler;
	
	/** The m_num tag. */
	private int m_numTag;
	
	
	/**
	 * Instantiates a new cRF model.
	 *
	 * @param charfeats 通过训练文件统计出的所有input以及对应的特征列表
	 * @param tagMap the tag map
	 * @param parameters the parameters
	 * @param featureHandler the feature handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFModel(Map<String, Integer> tagMap, double[] parameters/*, Features featureHandler*/) throws IOException
	{
//		CHAR_FEAT = charfeats;
		m_numTag = tagMap.size();
		
		m_parameters = parameters;
		
		m_int2tag = new HashMap<Integer, String>();
		for (Entry<String, Integer> tagint : tagMap.entrySet())
		{
			m_int2tag.put(tagint.getValue(), tagint.getKey());
		}
//		m_featureHandler = featureHandler;
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
		Graph graph = Graph.buildGraph(e, m_numTag, m_parameters);
		List<String> labels = new ArrayList<String>();
		
		int len = e.inputs.length;
		
		double[][] delta = new double[m_numTag][len];
		int[][] phi = new int[m_numTag][len];
		
		Node[][] nodes = graph.getNodes();
		int lastIdx = -1;
		for (int i=0; i<len; i++)
		{
			lastIdx = -1;
			double max = Double.NEGATIVE_INFINITY;
			for (int j=0; j<m_numTag; j++)
			{
				Node node = nodes[i][j];
				List<Edge> leftNodes = node.m_ledge;
				for (Edge edge : leftNodes)
				{
					double v = delta[edge.m_lnode.m_y][i-1] + edge.getBigramProb() + node.getUnigramProb();
					if (v > max)
					{
						max = v;
						lastIdx = edge.m_lnode.m_y;
					}
				}
				phi[j][i] = lastIdx;
				delta[j][i] = lastIdx==-1 ? 0.0 : max;
			}
		}
		
		double max = Double.NEGATIVE_INFINITY;
		for (int tag=0; tag<m_numTag; tag++)
		{
			if (delta[tag][len-1] > max)
			{
				max = delta[tag][len-1];
				lastIdx = tag;
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
	
	public Map<String, List<String>> getCharFeat()
	{
		return CHAR_FEAT;
	}
}
