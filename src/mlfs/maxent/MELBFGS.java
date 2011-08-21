/*
 * MELBFGS.java 
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
 * Last Update:Jul 17, 2011
 * 
 */
package mlfs.maxent;

import java.util.logging.Logger;

import riso.numerical.LBFGS;

import mlfs.maxent.model.MEEvent;
import mlfs.maxent.model.MEModel;
import mlfs.maxent.model.TrainDataHandler;

public class MELBFGS extends METrainModel{
	
	private static Logger logger = Logger.getLogger(MELBFGS.class.getName());

	public MELBFGS(TrainDataHandler handler) {
		super(handler);
	}
	
	public MELBFGS(TrainDataHandler handler, boolean useGaussianSmooth) {
		super(handler, useGaussianSmooth);
	}

	@Override
	public MEModel train()
	{
		return train(50);
	}
	
	public MEModel train(int numIter) {
		
		logger.info("calc observation expection matrix");
		m_observationExpection = new double[m_numPredicates][m_numLabels];
		calcObservationExpection();
		
		logger.info("call lbfgs...");
		double[] solutions = new double[m_numPredicates*m_numLabels];
		
		int dimension = m_numPredicates*m_numLabels;
		double[] g = new double[dimension];
		double[] diag= new double[dimension];
		
		int n = dimension;
		int m = 5;
		
		int[] iprint = new int[2];
		iprint[0] = 1;
		iprint[1] = 0;
		
		boolean diagco= false;
		double eps= 1.0e-7;
		double xtol= 1.e-16;
		int icall=0;
		int[] iflag = new int[1];
		iflag[0]=0;
		
		//init x[]
		for (int i=0; i<n; i++)
			solutions[i] = 0.000;
		
		double f = 0.0;
		do
		{
			f = gradient(solutions, g);
			
			try
			{
				LBFGS.lbfgs ( n , m , solutions, f , g , diagco , diag , iprint , eps , xtol , iflag );
			}
			catch (LBFGS.ExceptionWithIflag e)
			{
				System.err.println( "Sdrive: lbfgs failed.\n"+e );
				System.exit(-1);
			}

			icall += 1;
		}
		while ( iflag[0] != 0 && icall <= numIter );
		
		m_parameters = new double[m_numPredicates][m_numLabels];
		for (int p=0; p<m_numPredicates; p++)
		{
			for (int l=0; l<m_numLabels; l++)
			{
				m_parameters[p][l] = solutions[p*m_numLabels+l];
			}
		}
		return new MEModel(m_parameters, m_numLabels, m_predicates, m_labels);
	}

	private double gradient(double[] x, double[] g)
	{
		//init
		double f = 0.0;
		for (int i=0; i<m_numPredicates; i++)
		{
			for (int j=0; j<m_numLabels; j++)
			{
				if (USE_GAUSSIAN_SMOOTH)
				{
					g[i*m_numLabels+j] =x[i*m_numLabels+j] - m_observationExpection[i][j];
					f += x[i*m_numLabels+j]*x[i*m_numLabels+j]/2;
				}
				else
					g[i*m_numLabels+j] = 0.0 - m_observationExpection[i][j];
			}
		}
		
		//calc
		for (MEEvent event : m_events)
		{
			double[] candProbs = calcCandProbs(event, x);
			if (candProbs[event.m_label] == 0)
				f -= Math.log(Double.MIN_VALUE);
			else
				f -=  Math.log(candProbs[event.m_label]);
			
			for (int pid = 0; pid<event.m_predicates.length; pid++)
			{
				int predicate = event.m_predicates[pid];
				for (int label=0; label<m_numLabels; label++)
				{
					//wana be zhangle's maxent?
					//uncomment both the fowllowing piece of code and the special code in function calcCandProbs
//					if (m_observationExpection[predicate][label] == 0)
//						continue;
					g[predicate*m_numLabels + label] += candProbs[label]*event.m_values[pid];
				}
			}
			
		}
		
		return f;
	}
	
	private double[] calcCandProbs(MEEvent event, double[] solutions)
	{
		double[] candProbs = new double[m_numLabels];
		
		for (int pid=0; pid<event.m_predicates.length; pid++)
		{
			int predicate = event.m_predicates[pid];
			for (int label=0; label<m_numLabels; label++)
			{
				//wana be zhangle's maxent?
//				if (m_observationExpection[predicate][label] == 0)
//					continue;
				candProbs[label] += solutions[predicate*m_numLabels + label] * event.m_values[pid];
			}
		}
		
		double normalize = 0.0;
		for (int label=0; label<m_numLabels; label++)
		{
			candProbs[label] = Math.exp(candProbs[label]);
			if (Double.isInfinite(candProbs[label]))
				candProbs[label] = Double.MAX_VALUE;
			normalize += candProbs[label];
		}
		
		for (int label=0; label<m_numLabels; label++)
		{
			candProbs[label] /= normalize;
		}
		
		return candProbs;
	}
}
