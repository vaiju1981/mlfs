/*
 * Graph.java 
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
package mlfs.crf.graph;

import java.util.List;

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

	/** 缓存图对象. */
	private static Graph m_graph;

	/**
	 * 建立图结构.
	 * 
	 * @param event
	 *            CRFEvent序列
	 * @param featureHandler
	 *            特征处理类
	 * @param parameter
	 *            参数
	 * @return 图对象
	 */
	public static Graph buildGraph(CRFEvent event, int numTag,
			double[] parameter) {
		if (m_graph == null) {
			m_graph = new Graph();
			m_graph.m_numTag = numTag;
		}

		GraphCacher cacher = GraphCacher.getInstance();
		FeatureCacher features = FeatureCacher.getInstance();

		int seqLen = event.labels.length;
		if (m_graph.m_seqLen < seqLen) {
			m_graph.m_seqLen = seqLen;
			m_graph.m_nodes = new Node[seqLen][numTag];
		}

		int fpos = event.FEATURE_CACHE_POS;
		for (int i = 0; i < seqLen; i++) {
			List<Integer> feats = features.getFeats(fpos++);
			for (int tag = 0; tag < numTag; tag++) {
				Node node = cacher.getNode();
				node.reInit(i, tag, event.labels[i]);
				node.setFeatures(feats);
				node.calLogProb(parameter, numTag);

				m_graph.m_nodes[i][tag] = node;
			}
		}

		for (int i = 1; i < seqLen; i++) {
			for (int preTag = 0; preTag < numTag; preTag++) {
				List<Integer> feats = features.getFeats(fpos++);
				for (int tag = 0; tag < numTag; tag++) {
					Edge edge = cacher.getEdge();
					edge.reInit(m_graph.m_nodes[i - 1][preTag],
							m_graph.m_nodes[i][tag]);
					edge.setFeatures(feats);
					edge.calcLogProbs(parameter, numTag);

					m_graph.m_nodes[i - 1][preTag].addRightEdge(edge);// add
																		// right
																		// edge
					m_graph.m_nodes[i][tag].addLeftEdge(edge);// add left edge
				}
			}
		}

		return m_graph;
	}

	/**
	 * 前向后向算法.
	 * 
	 * 只所以需要传参，而不是使用m_seqLen和m_numTag是因为m_seqLen和m_numTag数值是 缓存的而不是真实event的数值
	 * 
	 * @param seqLen
	 *            序列长度
	 * @param numTag
	 *            tag总数
	 */
	public void forwardBackword(int seqLen, int numTag) {
		for (int time = 0; time < seqLen; time++)
			for (int tag = 0; tag < numTag; tag++)
				m_nodes[time][tag].calcAlpha();

		for (int time = seqLen - 1; time >= 0; time--)
			for (int tag = 0; tag < numTag; tag++)
				m_nodes[time][tag].calcBeta();

		m_Z = 0.0;
		for (int tag = 0; tag < numTag; tag++)
			m_Z = Utils.logSum(m_Z, m_nodes[0][tag].getBeta(), tag == 0);
	}

	/**
	 * 梯度.这个方法，首先计算模型期望，然后用模型期望减去观测期望，结果即为似然估计的导数的相反数， 最后返回似然估计的相反数
	 * 
	 * 只所以需要传参，而不是使用m_seqLen和m_numTag是因为m_seqLen和m_numTag数值是 缓存的而不是真实event的数值
	 * 
	 * @param expectation
	 *            期望数组
	 * @param seqLen
	 *            序列长度
	 * @param numTag
	 *            tag总数
	 * @return 似然估计的相反数
	 */
	public double gradient(double[][] expectation, int seqLen, int numTag) {
		for (int i = 0; i < seqLen; i++) {
			for (int j = 0; j < numTag; j++) {
				Node node = m_nodes[i][j];
				// unigram
				double p = Math.exp(node.getAlpha() + node.getBeta()
						- node.getUnigramProb() - m_Z);
				List<Integer> feats = node.getFeatures();
				int sz = feats.size();
				for (int f = 0; f < sz; f++)
					expectation[feats.get(f)][j] += p;

				// bigram
				List<Edge> leftEdges = node.m_ledge;
				sz = leftEdges.size();
				for (int le = 0; le < sz; le++) {
					Edge e = leftEdges.get(le);
					p = Math.exp(e.m_lnode.getAlpha() + e.getBigramProb()
							+ e.m_rnode.getBeta() - m_Z);
					List<Integer> bFeats = e.getFeatures();
					int bfsize = bFeats.size();
					for (int f = 0; f < bfsize; f++)
						expectation[bFeats.get(f)][j] += p;
				}
			}
		}

		double res = 0.0;
		int preAns = -1;
		for (int i = 0; i < seqLen; i++) {
			int ans = m_nodes[i][0].m_ansTag;
			res += m_nodes[i][ans].getUnigramProb();
			List<Integer> unigramFeat = m_nodes[i][ans].getFeatures();
			int sz = unigramFeat.size();
			for (int f = 0; f < sz; f++)
				expectation[unigramFeat.get(f)][ans]--;

			List<Edge> leftEdges = m_nodes[i][ans].m_ledge;
			sz = leftEdges.size();
			for (int le = 0; le < sz; le++) {
				Edge e = leftEdges.get(le);
				if (e.m_lnode.m_y == preAns) {
					res += e.getBigramProb();
					List<Integer> bigramFeat = e.getFeatures();
					int bsize = bigramFeat.size();
					for (int f = 0; f < bsize; f++)
						expectation[bigramFeat.get(f)][ans]--;
				}
			}
			preAns = ans;
		}

		return m_Z - res;// loglikelihood的相反数
	}

	/**
	 * 获取所有的Node节点
	 * 
	 * @return the nodes
	 */
	public Node[][] getNodes() {
		return m_nodes;
	}

	public double getZ() {
		return m_Z;
	}
}
