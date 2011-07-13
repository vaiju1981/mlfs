package mlfs.crf.graph;

import java.util.ArrayList;
import java.util.List;

import mlfs.crf.Features;
import mlfs.crf.model.CRFEvent;
import mlfs.util.Utils;

public class Graph {

	private Node[][] m_nodes;
	private int m_seqLen;
	private int m_numTag;
	private double m_Z;
	
	public static Graph buildGraph(CRFEvent event, Features featureHandler, double[] parameter)
	{
		Graph graph = new Graph();
		
		graph.m_seqLen = event.inputs.length;
		graph.m_numTag = featureHandler.getTagMap().size();
		graph.m_nodes = new Node[graph.m_seqLen][graph.m_numTag];
		
		for (int i=0; i<graph.m_seqLen; i++)
		{
			for (int tag=0; tag<graph.m_numTag; tag++)
			{
				Node node = new Node(i, tag, event.labels[i]);
				node.calcFeatures(event, i, featureHandler);//unigram
				node.calLogProb(parameter, graph.m_numTag);
				
				if (i != 0)//bigram
				{
					for (int preTag=0; preTag<graph.m_numTag; preTag++)
					{
						Edge edge = new Edge(graph.m_nodes[i-1][preTag], node);
						edge.calFeature(event, i, featureHandler);
						edge.calcLogProbs(parameter, graph.m_numTag);
						
						graph.m_nodes[i-1][preTag].addRightEdge(edge);//add right edge
						node.addLeftEdge(edge);// add left edge
					}
				}
				graph.m_nodes[i][tag] = node;
			}
		}
		
		return graph;
	}
	
	public void forwardBackword()
	{
		for (int time=0; time<m_seqLen; time++)
			for (int tag=0; tag<m_numTag; tag++)
			{
				m_nodes[time][tag].calcAlpha();
				System.out.println("alpha = " + m_nodes[time][tag].getAlpha());
			}
		
		for (int time=m_seqLen-1; time>=0; time--)
			for (int tag=0; tag<m_numTag; tag++)
			{
				m_nodes[time][tag].calcBeta();
				System.out.println("beta = " + m_nodes[time][tag].getBeta());
			}
		
		m_Z = 0.0;
		for (int tag=0; tag<m_numTag; tag++)
			m_Z = Utils.logSum(m_Z, m_nodes[0][tag].getBeta(), tag==0);
		System.out.println("Z = " + m_Z);
	}
	
	
	public double gradient(double[][] expectation)
	{
		for (int i=0; i<m_seqLen; i++)
		{
			for (int j=0; j<m_numTag; j++)
			{
				Node node = m_nodes[i][j];
				//unigram
				double p = Math.exp(node.getAlpha() + node.getBeta() - node.getUnigramProb() - m_Z);
				System.out.println("unigram exptectation " + p);
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
		System.out.print("Expectation : ");
		for (int i=0; i<6; i++)
		{
			for (int j=0; j<m_numTag; j++)
			{
				System.out.print(expectation[i][j] + " ");
			}
		}
		System.out.println();
		return m_Z - res;//loglikelihood的相反数
	}
	
	public Node[][] getNodes()
	{
		return m_nodes;
	}
}
