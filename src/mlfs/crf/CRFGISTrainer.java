///*
// * CRFGISTrainer.java 
// * 
// * Author : 罗磊，luoleicn@gmail.com
// * 
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * Last Update:Jul 4, 2011
// * 
// */
//package mlfs.crf;
//
//import java.io.IOException;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.logging.Logger;
//
//import mlfs.crf.model.CRFEvent;
//import mlfs.crf.model.CRFModel;
//import mlfs.maxent.model.Event;
//
///**
// * The Class CRFGISTrainer.
// * GIS求解CRF参数
// */
//public class CRFGISTrainer extends CRFTrainer{
//	
//	/** The logger. */
//	private Logger logger = Logger.getLogger(CRFGISTrainer.class.getName());
//	
//	/** 似然值的收敛标准 CONVERGENCE. */
//	private static double CONVERGENCE = 0.0001;
//	
//	/** GIS算法中的C. */
//	private double CONSTANT_C;
//	
//	/** 1/C. */
//	private double CONSTANT_INVERSE_C;
//	
//	/**
//	 * Instantiates a new cRFGIS trainer.
//	 *
//	 * @param events the events
//	 * @param featHandler the feat handler
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public CRFGISTrainer(List<CRFEvent> events, Features featHandler)
//			throws IOException {
//		super(events, featHandler);
//	}
//
//	/* (non-Javadoc)
//	 * @see mlfs.crf.CRFTrainer#train(int)
//	 */
//	@Override
//	public CRFModel train(int numIter) throws IOException {
//		
//		logger.info("calc constant c...");
//		calcConstantC();
//		logger.info("C = " + CONSTANT_C);
//		
//		logger.info("calc observation expectation matrix");
//		m_observationExpectation = new double[m_numFeat][m_numTag];
//		calcObservationExpectation();
//		
//		logger.info("Start to iterate " + 100 + " times");
//		iterate(numIter);
//		
//		return new CRFModel(CRFEvent.CHAR_FEAT, m_featHandler.getFeatMap(), m_tagMap, m_parameters, m_featHandler.getTemplateFilePath());
//	}
//	
//	/* (non-Javadoc)
//	 * @see mlfs.crf.CRFTrainer#train()
//	 */
//	@Override
//	public CRFModel train() throws IOException {
//		return train(100);
//	}
//	
//	/**
//	 * 计算常数C
//	 */
//	private void calcConstantC()
//	{
//		int max = -1;
//		
//		for (CRFEvent e : m_events)
//		{
//			Set<Integer> set = new HashSet<Integer>();
//			for (int i=0; i<e.inputs.length; i++)
//			{
//				List<Integer> feats = null;
//				if (i == 0)
//					feats = m_featHandler.getFeatures(e, m_tagMap.get("START"), i);
//				else
//					feats = m_featHandler.getFeatures(e, e.labels[i-1], i);
//				
//				set.addAll(feats);
//			}
//			if (set.size() > max)
//				max = set.size();
//		}
//		
//		CONSTANT_C = max;
//		CONSTANT_INVERSE_C = 1.0/CONSTANT_C;
//	}
//	
//	/**
//	 * 迭代求解.
//	 *
//	 * @param numIter 迭代次数
//	 */
//	private void iterate(int numIter)
//	{
//		m_modelExpectation = new double[m_numFeat][m_numTag];
//		double[] solutions = new double[m_numFeat*m_numTag];
//		
//		double preloglikelihood = 0.0;
//		double curloglikelihood = Double.MAX_VALUE;
//		for (int i=0; i<numIter; i++)
//		{
//			preloglikelihood = curloglikelihood;
//			curloglikelihood = 0.0;
//			
//			double[] logZx = calcModelExpectation(solutions);
//			curloglikelihood = calcloglikelihood(logZx, solutions);
//				
//			updateParameters(solutions);
//			logger.info("" + (i+1) + " loglikelihood : " + curloglikelihood);
//			if (i > 1 && curloglikelihood-preloglikelihood<CONVERGENCE)
//				break;
//		}
//		
//		m_observationExpectation = null;
//		m_modelExpectation = null;
//		m_parameters = new double[m_numFeat][m_numTag];
//		for (int i=0; i<m_numFeat; i++)
//			for (int j=0; j<m_numTag; j++)
//				m_parameters[i][j] = solutions[i*m_numTag+j];
//	}
//	
//	/**
//	 * Update parameters.
//	 *
//	 * @param x the x
//	 */
//	private void updateParameters(double[] x)
//	{
//		for (int i=0; i<m_numFeat; i++)
//		{
//			for (int j=0; j<m_numTag; j++)
//			{
//				//不跳过START和END
////				if (j==START || j==END)
////					continue;
////				if (m_observationExpectation[i][j] == 0)
////				{
////					System.out.println("obsvExp = 0\t" + i + "\t" + j);
////				}
////				if (m_modelExpectation[i][j] == 0)
////				{
////					System.out.println("modelExpt = 0\t" + i +"\t" + j +"\t obsvexp = " + m_observationExpectation[i][j]);
////				}
//				x[i*m_numTag+j] += CONSTANT_INVERSE_C * Math.log(m_observationExpectation[i][j]/m_modelExpectation[i][j]);
//			}
//		}
//	}
//}
