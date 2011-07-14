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
 * Last Update:Jul 14, 2011
 * 
 */
package mlfs.crf.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mlfs.crf.graph.Edge;
import mlfs.crf.graph.Graph;
import mlfs.crf.graph.Node;
import mlfs.util.Utils;

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
	
	private Map<String, Integer> FEAT_ID_MAP;
	
	/** The m_num tag. */
	private int m_numTag;
	
	private int m_numPred;
	
	private String m_templateFilePath;
	
	private List<String> m_templates;
	
	private CRFModel(){}
	
	/**
	 * Instantiates a new cRF model.
	 *
	 * @param templateFilePath the template file path
	 * @param tagMap the tag map
	 * @param parameters the parameters
	 * @param numPred the num pred
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFModel(String templateFilePath, Map<String, Integer> tagMap, double[] parameters, int numPred) throws IOException
	{
		m_numPred = numPred;
		m_numTag = tagMap.size();
		
		m_parameters = parameters;
		
		m_int2tag = new HashMap<Integer, String>();
		for (Entry<String, Integer> tagint : tagMap.entrySet())
		{
			m_int2tag.put(tagint.getValue(), tagint.getKey());
		}
		m_templateFilePath = templateFilePath;
	}
	
	public void save(String path) throws IOException
	{
		PrintWriter out = new PrintWriter(new FileWriter(path, true));
		StringBuilder sb = new StringBuilder();
		sb.append(m_numPred).append(' ').append(m_numTag).append(' ');
		for (double v : m_parameters)
			sb.append(v).append(' ');
		out.println(sb.toString());
		
		for (Entry<Integer, String> tid : m_int2tag.entrySet())
			out.println(tid.getKey() + " " + tid.getValue());
		
		out.println();
		List<String> templates = Utils.getAllLines(m_templateFilePath);
		for (String t : templates)
		{
			if (t.length() == 0)
				continue;
			if (t.charAt(0)=='B' || t.charAt(0)=='U')
				out.println(t);
		}
		out.close();
	}
	
	public static CRFModel load(String path) throws IOException
	{
		CRFModel model = new CRFModel();
		List<String> lines = Utils.getAllLines(path);
		int n = 0;
		//load char feat
		model.CHAR_FEAT = new HashMap<String, List<String>>();
		while (n<lines.size())
		{
			String line = lines.get(n++);
			if (line.equals(""))
				break;
			
			String[] feat = line.split("\\s+");
			List<String> all = new ArrayList<String>(feat.length);
			for (String f : feat)
				all.add(f);
			model.CHAR_FEAT.put(feat[0], all);
		}
		//load feature id map
		model.FEAT_ID_MAP = new HashMap<String, Integer>();
		while (n<lines.size())
		{
			String line = lines.get(n++);
			if (line.equals(""))
				break;
			String[] fid = line.split("\\s+");
			model.FEAT_ID_MAP.put(fid[0], Integer.parseInt(fid[1]));
		}
		//load parameter
		String line = lines.get(n++);
		String[] values = line.split("\\s+");
		model.m_numPred = Integer.parseInt(values[0]);
		model.m_numTag = Integer.parseInt(values[1]);
		model.m_parameters = new double[model.m_numPred*model.m_numTag];
		for (int i=2; i<values.length; i++)
			model.m_parameters[i-2] = Double.parseDouble(values[i]);
		//load id tag map
		model.m_int2tag = new HashMap<Integer, String>();
		while (n<lines.size())
		{
			line = lines.get(n++);
			if (line.equals(""))
				break;
			String[] idtag = line.split("\\s+");
			model.m_int2tag.put(Integer.parseInt(idtag[0]), idtag[1]);
		}
		//load templates 
		model.m_templates = new ArrayList<String>();
		while (n<lines.size())
		{
			line = lines.get(n++);
			model.m_templates.add(line);
		}
		
		return model;
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
		
		int len = e.labels.length;
		
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
	
	public List<String> getTemplates()
	{
		return m_templates;
	}
	
	public Map<String, Integer> getFeatIdMap()
	{
		return FEAT_ID_MAP;
	}
	
	public int getTagNum()
	{
		return m_numTag;
	}
}
