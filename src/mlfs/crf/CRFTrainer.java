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
 * Last Update:Jun 26, 2011
 * 
 */
package mlfs.crf;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;
import mlfs.numerical.AbstractLBFGS;

public class CRFTrainer {

	private static Logger logger = Logger.getLogger(CRFTrainer.class.getName());
	
	private Features m_featHandler;
	private int m_numFeat;
	
	private int m_numEvents;
	private List<CRFEvent> m_events;
	
	private double[] m_observationExpectation;
	private double[] m_modelExpectation;
	private double[] m_parameters;
	
	private Map<String, Integer> m_tagMap;
	private int m_numTag;
	
	public CRFTrainer(List<CRFEvent> events, Features featHandler) throws IOException
	{
		m_numFeat = featHandler.getFeatNum();
		m_featHandler = featHandler;
		
		m_numEvents = events.size();
		m_events = events;
		
		m_observationExpectation = new double[m_numFeat];
		m_modelExpectation = new double[m_numFeat];
		m_parameters = new double[m_numFeat];
		
		m_tagMap = featHandler.getTagMap();
		m_numTag = m_tagMap.size();
		
		logger.info("There are " + m_numFeat + " features in training file");
	}
	
	public void train()
	{
		logger.info("Calc oberservation expectation...");
		calcObservationExpectation();
		logger.info("L-BFGS...");
		CRF_LBFGS lbfgs = new CRF_LBFGS(m_numFeat);
		lbfgs.getSolution(m_parameters);
	}
	
	/**
	 * Calc observation expectation.
	 * 这里需要对未出现特征进行平滑！！！
	 */
	private void calcObservationExpectation()
	{
		int i = 0;
		for (CRFEvent event : m_events)
		{
			i++;
			List<Integer> feats = m_featHandler.getFeatures(event);
			for (int f : feats)
				m_observationExpectation[f] += 1.0;
		}
		
		for (double v : m_observationExpectation)
			v = v / m_numEvents;
	}
	
	/**
	 * Calc model expectation.
	 */
	private double[] calcModelExpectation()
	{
		double[] Zx = new double[m_numEvents];
		int k = 0;
		for (int i=0; i<m_modelExpectation.length; i++)
			m_modelExpectation[0] = 0.0;
		
		for (CRFEvent event : m_events)
		{
			int times = event.inputs.length;
			double[][][] M = new double[times+1][m_numTag][m_numTag];
			double[][] alpha = new double[m_numTag][times];
			double[][] beta = new double[m_numTag][times];
			double[] tmpRes = new double[m_numFeat];
			
			calcMatrix(M, event);
			forword(alpha, times, M);
			backword(beta, times, M);
			
			for (int t=0; t<times; t++)
			{
				for (int preTag=0; preTag<m_numTag; preTag++)
				{
					for (int tag=0; tag<m_numTag; tag++)
					{
						List<Integer> feats = m_featHandler.getFeatures(event, preTag, tag, t);
						for (int f : feats)
						{
							double tmp =  0.0;
							if (t == 0)
								tmp =  1.0 * M[t][preTag][tag] * beta[tag][t];
							else
								tmp =  alpha[preTag][t-1] * M[t][preTag][tag] * beta[tag][t];
								
							tmpRes[f] +=tmp;
							Zx[k] += tmp;
						}
					}
				}
			}
			for (int f=0; f<m_numFeat; f++)
				m_modelExpectation[f] += tmpRes[f] / Zx[k];
				                                       
			if (k==50)
				System.out.println("Debug");
			k++;
		}
		double a = 0.0;
		double idx = -1;
		for (int x=0; x<m_numFeat; x++)
		{
			if (Zx[x] > a)
			{
				a = Zx[x];
				idx = x;
			}
		}
		System.out.println(Zx+"\t"+idx);
		
		return Zx;
	}
	
	
	private void calcMatrix(double[][][] M, CRFEvent event)
	{
		int len = event.inputs.length;
		for (int i=0; i<=len; i++)
		{
			for (int preTag=0; preTag<m_numTag; preTag++)
			{
				for (int tag=0; tag<m_numTag; tag++)
				{
					List<Integer> feats = m_featHandler.getFeatures(event, preTag, tag, i);
					for (int f : feats)
						M[i][preTag][tag] += m_parameters[f];
				}
			}
		}
		
		for (int i=0; i<=len; i++)
		{
			for (int preTag=0; preTag<m_numTag; preTag++)
			{
				for (int tag=0; tag<m_numTag; tag++)
				{
					M[i][preTag][tag] = Math.exp(M[i][preTag][tag]);
				}
			}
		}
	}
	
	private void forword(double[][] alpha, int times, double[][][] M)
	{
		int time = 0;
		for (int y=0; y<m_numTag; y++)
			alpha[y][time] = 1.0*M[time][m_tagMap.get("START")][y];
		
		for (time=1; time<times; time++)
		{
			for (int preTag=0; preTag<m_numTag; preTag++)
			{
				for (int tag=0; tag<m_numTag; tag++)
				{
					alpha[tag][time] += alpha[preTag][time-1]*M[time][preTag][tag];
				}
			}
		}
	}
	
	private void backword(double[][] beta, int times, double[][][] M)
	{
		int time = times-1;
		for (int y=0; y<m_numTag; y++)
			beta[y][time] = M[time+1][y][m_tagMap.get("END")]*1.0;
		
		for (time=times-2; time>=0; time--)
		{
			for (int tag=0; tag<m_numTag; tag++)
			{
				for (int preTag=0; preTag<m_numTag; preTag++)
				{
					beta[preTag][time] += M[time+1][preTag][tag] * beta[tag][time+1];
				}
			}
		}
	}
	
	private class CRF_LBFGS extends AbstractLBFGS
	{
		public CRF_LBFGS(int dimetion) {
			super(dimetion);
		}
		
		double [] Zx;
		@Override
		protected void before()
		{
			Zx = calcModelExpectation();
		}

		@Override
		public double calFunctionVal(double[] x) {
			double f = 0.0;
			for (int i=0; i<m_numEvents; i++)
			{
				List<Integer> feats = m_featHandler.getFeatures(m_events.get(i));
				for (int feature : feats)
					f -= x[feature];
				
				f += Math.log(Zx[i]);
			}
			return f;
		}

		@Override
		public void calGradientVal(double[] x, double[] g) {
			
			for (int i=0; i<m_numFeat; i++)
			{
				g[i] = m_modelExpectation[i] - m_observationExpectation[i];
			}
			
		}
		
	}
}

