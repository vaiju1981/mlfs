/*
 * Graph.java
   *  
 * Author: 罗磊，luoleicn@gmail.com
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
 * Last Update:2011-7-14
   * 
   */
package mlfs.crf.graph;

import java.util.List;

import mlfs.crf.Features;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.cache.GraphCacher;
import mlfs.crf.model.CRFEvent;
import mlfs.util.Utils;

/**
 * 有一个CRFEvent建立起来的图
 */
public class Graph {

	/** 节点数组. */
	private Node[][] m_nodes;
	
	/** 序列长度. */
	private int m_seqLen;
	
	/** 总共的tag总数. */
	private int m_numTag;
	
	/** 归一化因子. */
	private double m_Z;
	
	/**
	 * 建立图结构.
	 *
	 * @param event CRFEvent序列
	 * @param featureHandler 特征处理类
	 * @param parameter 参数
	 * @return 图对象
	 */
	public static Graph buildGraph(CRFEvent event,  int numTag, double[] parameter)
	{
		GraphCacher cacher = GraphCacher.getInstance();
		FeatureCacher features = FeatureCacher.getInstance();
		
		Graph graph = new Graph();
		
		graph.m_seqLen = event.inputs.length;
		graph.m_numTag = numTag;
		graph.m_nodes = new Node[graph.m_seqLen][graph.m_numTag];
		System.out.println("Size = " + features.size());
		int fpos = event.FEATURE_CACHE_POS;
		for (int i=0; i<graph.m_seqLen; i++)
		{
			List<Integer> feats = features.getFeats(fpos++);
			for (int tag=0; tag<graph.m_numTag; tag++)
			{
				Node node = cacher.getNode();
				node.reInit(i, tag, event.labels[i]);
//				node.calcFeatures(event, i, featureHandler);//unigram
				node.setFeatures(feats);
				node.calLogProb(parameter, graph.m_numTag);
				
				graph.m_nodes[i][tag] = node;
			}
		}
				
		for (int i=1; i<graph.m_seqLen; i++)
		{
			for (int preTag=0; preTag<graph.m_numTag; preTag++)
			{
				List<Integer> feats = features.getFeats(fpos++);
				for (int tag=0; tag<graph.m_numTag; tag++)
				{
					Edge edge = cacher.getEdge();
					edge.reInit(graph.m_nodes[i-1][preTag], graph.m_nodes[i][tag]);
//					edge.calFeature(event, i, featureHandler);
					edge.setFeatures(feats);
					edge.calcLogProbs(parameter, graph.m_numTag);
					
					graph.m_nodes[i-1][preTag].addRightEdge(edge);//add right edge
					graph.m_nodes[i][tag].addLeftEdge(edge);// add left edge
				}
			}
		}
		
		return graph;
	}
	
	/**
	 * 前向后向算法.
	 */
	public void forwardBackword()
	{
		for (int time=0; time<m_seqLen; time++)
			for (int tag=0; tag<m_numTag; tag++)
			{
				m_nodes[time][tag].calcAlpha();
//				System.out.println("m_nodes[" + time + "][" + tag + "] alpha : "+m_nodes[time][tag].getAlpha());
			}
		
		for (int time=m_seqLen-1; time>=0; time--)
			for (int tag=0; tag<m_numTag; tag++)
			{
				m_nodes[time][tag].calcBeta();
//				System.out.println("m_nodes[" + time + "][" + tag + "] beta" + m_nodes[time][tag].getBeta());
			}
		
		m_Z = 0.0;
		for (int tag=0; tag<m_numTag; tag++)
			m_Z = Utils.logSum(m_Z, m_nodes[0][tag].getBeta(), tag==0);
//		System.out.println("Z = " + m_Z);
	}
	
	
	/**
	 * 梯度.这个方法，首先计算模型期望，然后用模型期望减去观测期望，结果即为似然估计的导数的相反数，
	 * 最后返回似然估计的相反数
	 *
	 * @param expectation 期望数组
	 * @return 似然估计的相反数
	 */
	public double gradient(double[][] expectation)
	{
		for (int i=0; i<m_seqLen; i++)
		{
			for (int j=0; j<m_numTag; j++)
			{
				Node node = m_nodes[i][j];
				//unigram
				double p = Math.exp(node.getAlpha() + node.getBeta() - node.getUnigramProb() - m_Z);
				List<Integer> feats = node.getFeatures();
				for (int f : feats)
					expectation[f][j] += p;
				
				//bigram
				List<Edge> leftEdges = node.m_ledge;
				for (Edge e : leftEdges)
				{
					p = Math.exp(e.m_lnode.getAlpha() + e.getBigramProb() + e.m_rnode.getBeta() - m_Z);
					List<Integer> bFeats = e.getFeatures();
					for (int f : bFeats)
						expectation[f][j] += p;
				}
			}
		}
		
		double res = 0.0;
		int preAns = -1;
		for (int i=0; i<m_seqLen; i++)
		{
			int ans = m_nodes[i][0].m_ansTag;
			res += m_nodes[i][ans].getUnigramProb();
			List<Integer> unigramFeat = m_nodes[i][ans].getFeatures();
			for (int f : unigramFeat)
				expectation[f][ans]--;
			
			List<Edge> leftEdges = m_nodes[i][ans].m_ledge;
			for (Edge e : leftEdges)
			{
				if (e.m_lnode.m_y == preAns)
				{
					res += e.getBigramProb();
					List<Integer> bigramFeat = e.getFeatures();
					for (int f : bigramFeat)
						expectation[f][ans]--;
				}
			}
			preAns = ans;
		}
		
//		System.out.print("Expectation : ");
//		for (int i=0; i<1; i++)
//		{
//			for (int j=0; j<m_numTag; j++)
//			{
//				System.out.print(expectation[i][j] + " ");
//			}
//		}
//		System.out.println();
		
//		System.out.println("s = " + res);
		return m_Z - res;//loglikelihood的相反数
	}
	
	/**
	 * 获取所有的Node节点
	 *
	 * @return the nodes
	 */
	public Node[][] getNodes()
	{
		return m_nodes;
	}
}
