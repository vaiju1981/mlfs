/*
 * Node.java 
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
 * Last Update:Jul 12, 2011
 * 
 */
package mlfs.crf.graph;

import java.util.ArrayList;
import java.util.List;

import mlfs.util.Utils;

/**
 * 图中的节点
 */
public class Node {

	//未保存序列的x，因为不需要
	
	/** tag. */
	public int m_y;
	
	/** 标准答案的tag. */
	public int m_ansTag;
	
	/** alpha. */
	private double m_alpha;
	
	/** beta . */
	private double m_beta;
	
	/** unigram概率, 未归一化. */
	private double m_unigramProb;
	
	/** 满足的unigram特征. */
	private List<Integer> m_features;
	
	/** 链入的边. */
	public final List<Edge> m_ledge;
	
	/** 链出的边. */
	public final List<Edge> m_redge;
	
	/**
	 * Instantiates a new node.
	 *
	 * @param pos 偏移量 
	 * @param tag 标签
	 * @param ansTag 标准答案的标签
	 */
	public Node(int pos, int tag, int ansTag)
	{
		m_y = tag;
		m_ansTag = ansTag;
		
		m_ledge = new ArrayList<Edge>();
		m_redge = new ArrayList<Edge>();
	}
	
	public void reInit(int pos, int tag, int ansTag)
	{
		m_y = tag;
		m_ansTag = ansTag;
		
		m_ledge.clear();
		m_redge.clear();
	}
	
	/**
	 * Adds the left edge.
	 *
	 * @param e 指向当前节点的边
	 */
	public void addLeftEdge(Edge e)
	{
		m_ledge.add(e);
	}
	
	/**
	 * Adds the right edge.
	 *
	 * @param e 由当前节点指出的边
	 */
	public void addRightEdge(Edge e)
	{
		m_redge.add(e);
	}
	
	/**
	 * 计算unigram概率
	 *
	 * @param parameter 模型参数
	 */
	public void calLogProb(double[] parameter, int tagSize)
	{
		m_unigramProb = 0.0;
		int sz = m_features.size();
		for (int i=0; i<sz; i++)
		{
			m_unigramProb += parameter[m_features.get(i)*tagSize + m_y];
		}
	}

	public void setFeatures(List<Integer> lst)
	{
		m_features = lst;
	}
	/**
	 * 前向算法
	 */
	public void calcAlpha()
	{
		m_alpha = 0.0;
		boolean flg =  true;
		int sz = m_ledge.size();
		for (int i=0; i<sz; i++)
		{
			Edge edge = m_ledge.get(i);
			m_alpha = Utils.logSum(m_alpha, edge.m_lnode.m_alpha + edge.getBigramProb(), flg);
			flg = false;
		}
		m_alpha += m_unigramProb;
	}
	
	/**
	 * 后向算法
	 */
	public void calcBeta()
	{
		m_beta = 0.0;
		boolean flg = true;
		int sz = m_redge.size();
		for (int i=0; i<sz; i++)
		{
			Edge edge = m_redge.get(i);
			m_beta = Utils.logSum(m_beta, edge.getBigramProb() + edge.m_rnode.m_beta, flg);
			flg = false;
		}
		m_beta += m_unigramProb;
	}
	
	/**
	 * Gets the alpha.
	 *
	 * @return the alpha
	 */
	public double getAlpha()
	{
		return m_alpha;
	}
	
	/**
	 * Gets the beta.
	 *
	 * @return the beta
	 */
	public double getBeta()
	{
		return m_beta;
	}
	
	/**
	 * Gets the unigram prob.
	 *
	 * @return the unigram prob
	 */
	public double getUnigramProb()
	{
		return m_unigramProb;
	}
	
	/**
	 * 返回当前node满足的unigram特征
	 *
	 * @return the features
	 */
	public List<Integer> getFeatures()
	{
		return m_features;
	}
}
