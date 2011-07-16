/*
 * CRFLBFGSTrainer4mason.java 
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
 * Last Update:Jul 16, 2011
 * 
 */
package mlfs.chineseSeg.debug;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import mlfs.crf.CRFLBFGSTrainer;
import mlfs.crf.CRFTrainer;
import mlfs.crf.Features;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.cache.GraphCacher;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

/**
 * The Class CRFLBFGSTrainer.
 * LBFGS求解CRF参数
 */
public class CRFLBFGSTrainer4mason extends CRFTrainer{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(CRFLBFGSTrainer4mason.class.getName());
	
	private List<CRFEvent> m_devEvents;
	private List<CRFEvent> m_testEvents;
	private String m_tempaltePath;
	
	private Map<Integer, String> m_idTagMap;
	
	/**
	 * Instantiates a new cRFLBFGS trainer.
	 *
	 * @param events the events
	 * @param devEvents the dev events
	 * @param testEvent the test event
	 * @param featHandler the feat handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFLBFGSTrainer4mason(List<CRFEvent> events, List<CRFEvent> devEvents, List<CRFEvent> testEvent, Features4mason featHandler)
			throws IOException {
		super(events, featHandler.getFeatNum(), featHandler.getTagMap());
		m_tempaltePath = featHandler.getTemplateFilePath();
		m_devEvents = devEvents;
		m_testEvents = testEvent;
		
		m_idTagMap = new HashMap<Integer, String>();
		Map<String, Integer> tagIdMap = featHandler.getTagMap();
		for (Entry<String, Integer> entry : tagIdMap.entrySet())
			m_idTagMap.put(entry.getValue(), entry.getKey());
	}

	/* (non-Javadoc)
	 * @see mlfs.crf.CRFTrainer#train(int)
	 */
	@Override
	public CRFModel train(int numIter) throws IOException
	{
		logger.info("L-BFGS...");
		m_modelExpectation = new double[m_numFeat][m_numTag];
		CRF_LBFGS lbfgs = new CRF_LBFGS(m_numFeat*m_numTag, 5, numIter);
		m_parameters = new double[m_numFeat*m_numTag];
		lbfgs.getSolution(m_parameters);
		
		logger.info("Finish Training...");
		lbfgs = null;
		m_modelExpectation = null;
		FeatureCacher fcacher = FeatureCacher.getInstance();
		fcacher.clear();
		GraphCacher gcacher = GraphCacher.getInstance();
		gcacher.clear();
		
		return new CRFModel(m_tempaltePath, m_tagMap, m_parameters, m_numFeat);
	}
	
	/**
	 * The Class CRF_LBFGS.
	 */
	private class CRF_LBFGS extends AbstractLBFGS4mason
	{
		
		/**
		 * Instantiates a new cR f_ lbfgs.
		 *
		 * @param dimetion the dimetion
		 * @param m the m
		 * @param numIter the num iter
		 */
		public CRF_LBFGS(int dimetion, int m, int numIter)
		{
			super(dimetion, m, numIter);
		}
		
		/* 给定参数x，求解目标方程值
		 * @see mlfs.numerical.AbstractLBFGS#calFunctionVal(double[])
		 */
		@Override
		public double calFunctionVal(double[] x) {
			System.out.println("F value : dev " + DebugHelper.evaluateSeg(m_devEvents, m_numTag, x, m_idTagMap) + " test " + DebugHelper.evaluateSeg(m_testEvents, m_numTag, x, m_idTagMap));
			double f  = calcModelExpectation(x);
			
			for (double parameter : x)
				f += parameter*parameter/2.0;//高斯平滑
			return f;
		}

		/* 
		 * 给定参数x，求解目标方程对xi的导数
		 * @see mlfs.numerical.AbstractLBFGS#calGradientVal(double[], double[])
		 */
		@Override
		public void calGradientVal(double[] x, double[] g) {
			for (int i=0; i<m_numFeat; i++)
				for (int j=0; j<m_numTag; j++)
					g[i*m_numTag+j] = m_modelExpectation[i][j]+ x[i*m_numTag+j];
		}
		
	}

	@Override
	public CRFModel train() throws IOException {
		return train(500);
	}
}

