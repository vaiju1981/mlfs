/*
 * CRFLBFGSTrainer.java 
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
 * Last Update:Jul 4, 2011
 * 
 */
package mlfs.crf;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;
import mlfs.numerical.AbstractLBFGS;

/**
 * The Class CRFLBFGSTrainer.
 * LBFGS求解CRF参数
 */
public class CRFLBFGSTrainer extends CRFTrainer{
	
	/** The logger. */
	private static Logger logger = Logger.getLogger(CRFLBFGSTrainer.class.getName());
	
	/**
	 * Instantiates a new cRFLBFGS trainer.
	 *
	 * @param events the events
	 * @param featHandler the feat handler
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public CRFLBFGSTrainer(List<CRFEvent> events, Features featHandler)
			throws IOException {
		super(events, featHandler);
	}

	/* (non-Javadoc)
	 * @see mlfs.crf.CRFTrainer#train()
	 */
	@Override
	public CRFModel train() throws IOException
	{
		return train(600);
	}
	
	/* (non-Javadoc)
	 * @see mlfs.crf.CRFTrainer#train(int)
	 */
	@Override
	public CRFModel train(int numIter) throws IOException
	{
		logger.info("Calc oberservation expectation...");
		m_observationExpectation = new double[m_numFeat][m_numTag];
		calcObservationExpectation();
		
		logger.info("L-BFGS...");
		m_modelExpectation = new double[m_numFeat][m_numTag];
		CRF_LBFGS lbfgs = new CRF_LBFGS(m_numFeat*m_numTag, 5, numIter);
		double[] solutions = new double[m_numFeat*m_numTag];
		lbfgs.getSolution(solutions);
		lbfgs = null;
		
		m_observationExpectation = null;
		m_modelExpectation = null;
		m_parameters = new double[m_numFeat][m_numTag];
		for (int i=0; i<m_numFeat; i++)
			for (int j=0; j<m_numTag; j++)
				m_parameters[i][j] = solutions[i*m_numTag+j];
		return new CRFModel(CRFEvent.CHAR_FEAT, m_featHandler.getFeatMap(), m_tagMap, m_parameters, m_featHandler.getTemplateFile());
	}
	
	/**
	 * The Class CRF_LBFGS.
	 */
	private class CRF_LBFGS extends AbstractLBFGS
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
		
		/** The log zx. */
		double [] logZx;

		/* 给定参数x，求解目标方程值
		 * @see mlfs.numerical.AbstractLBFGS#calFunctionVal(double[])
		 */
		@Override
		public double calFunctionVal(double[] x) {
			logZx = calcModelExpectation(x);
			
			double f = 0 - calcloglikelihood(logZx, x);
			return f;
		}

		/* 
		 * 给定参数x，求解目标方程对xi的导数
		 * @see mlfs.numerical.AbstractLBFGS#calGradientVal(double[], double[])
		 */
		@Override
		public void calGradientVal(double[] x, double[] g) {
			
			for (int i=0; i<m_numFeat; i++)
			{
				for (int j=0; j<m_numTag; j++)
				{
//					if (j==START || j==END)
//						continue;
					g[i*m_numTag+j] = (m_modelExpectation[i][j] - m_observationExpectation[i][j])/m_numEvents;
				}
			}
			
		}
		
	}
}

