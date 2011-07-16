/*
 * CRFTrainer.java 
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
 * Last Update:Jul 8, 2011
 * 
 */
package mlfs.crf;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.graph.Graph;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

/**
 * The Class CRFTrainer.
 * CRF训练模型的抽象方法，GIS求参和LBFGS求参均调用这个类
 */
public abstract class CRFTrainer {
	
	/** The logger. */
	private Logger logger = Logger.getLogger(CRFTrainer.class.getName());
	
	/** 特征总数. */
	protected int m_numFeat;
	
	/** 训练语料event总数. */
	protected int m_numEvents;
	
	/** 训练语料中的所有event. */
	protected List<CRFEvent> m_events;
	
	/** 模型估测期望. */
	protected double[][] m_modelExpectation;
	
	/** 模型参数. */
	protected double[] m_parameters;
	
	/** tag和整数对应的map. */
	protected Map<String, Integer> m_tagMap;
	
	/** tag总数. */
	protected int m_numTag;
	
	/**
	 * Instantiates a new cRF trainer.
	 *
	 * @param events the events
	 * @param featHandler the feat handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFTrainer(List<CRFEvent> events, Features featHandler) throws IOException
	{
		m_numFeat = featHandler.getFeatNum();
		
		m_numEvents = events.size();
		m_events = events;
	
		m_tagMap = featHandler.getTagMap();
		m_numTag = m_tagMap.size();
		
		logger.info("There are " + m_numFeat + " predicates in training file");
	}
	
	public CRFTrainer(List<CRFEvent> events, int numFeat, Map<String, Integer> tagMap)
	{
		m_numFeat = numFeat;
		
		m_numEvents = events.size();
		m_events = events;
	
		m_tagMap = tagMap;
		m_numTag = m_tagMap.size();
		
		logger.info("There are " + m_numFeat + " predicates in training file");
	}
	
	/**
	 * Train.使用默认的迭代求参次数，训练crf模型
	 *
	 * @return the cRF model
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract CRFModel train() throws IOException;
	
	/**
	 * Train.指定迭代次数，训练crf模型
	 *
	 * @param n the n
	 * @return the cRF model
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public abstract CRFModel train(int n) throws IOException;
	
	/**
	 * 计算模型估计期望.
	 *
	 * @param solutions the solutions
	 * @return the double[]
	 */
	protected double calcModelExpectation(double[] solutions)
	{
		for (int i=0; i<m_numFeat; i++)
			for (int j=0; j<m_numTag; j++)
				m_modelExpectation[i][j] = 0.0;
		
		double negLoglikelihood = 0.0;
		for (int i=0; i<m_numEvents; i++)
		{
			CRFEvent event = m_events.get(i);
			int len = event.labels.length;
			Graph graph = Graph.buildGraph(event, m_numTag, solutions);
			graph.forwardBackword(len, m_numTag);
			negLoglikelihood += graph.gradient(m_modelExpectation, len, m_numTag);
		}
		return negLoglikelihood;

	}
	
}
