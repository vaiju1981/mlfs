/*
 * GIS.java
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
 * Last Update:2011-6-12
   * 
   */
package mlfs.maxent;

import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

import mlfs.maxent.model.Event;
import mlfs.maxent.model.GISModel;
import mlfs.maxent.model.TrainDataHandler;

public class GIS {
	
	private static Logger logger = Logger.getLogger(GIS.class.getName());
	
	private static double SMOOTH_SEEN = 0.1;
	private static double CONVERGENCE = 0.0001;
	
	private TrainDataHandler m_trainData;
	
	private int m_numLabels;
	private int m_numPredicates;
	private List<Event> m_events;
	
	private double[][] m_observationExpection;
	private double[][] m_modelExpection;
	
	private double[][] m_parameters;
	private int CONSTANT_C;
	private double CONSTANT_C_INVERSE;

	private HashSet<Integer> m_predicates;
	private HashSet<Integer> m_labels;
	
	public GIS(TrainDataHandler handler)
	{
		this.m_trainData = handler;
		
		this.m_numPredicates = handler.getNumPredicates();
		this.m_numLabels = handler.getNumLabels();
		this.m_events = m_trainData.getEvents();
		
		this.m_predicates = m_trainData.getPredicates();
		this.m_labels = m_trainData.getLabels();
	}
	
	public GISModel train(int numIter)
	{
		logger.info("Calc Constant C");
		CONSTANT_C = calcContantC();
		CONSTANT_C_INVERSE = 1.0/CONSTANT_C;
		
		logger.info("calc observation expection matrix");
		m_observationExpection = new double[m_numPredicates][m_numLabels];
		calcObservationExpection();
		
		logger.info("Start to iterate " + numIter + " times");
		iterate(numIter);
		
		return new GISModel(CONSTANT_C_INVERSE, m_parameters, m_numPredicates, m_numLabels, m_predicates, m_labels);
	}
	
	private int calcContantC()
	{
		int c = -1;
		for (Event event : m_events)
		{
			if (event.m_values == null)
			{
				int satisfiedFeat = event.m_predicates.length;
				if (satisfiedFeat > c)
					c = satisfiedFeat;
			}
			else 
			{
				int satisfiedFeat = 0;
				for (int v : event.m_values)
					satisfiedFeat += v;
				if (satisfiedFeat > c)
					c = satisfiedFeat;
			}
		}
		
		return c;
	}
	

	private void calcObservationExpection()
	{
		int[][] predLabels = new int[m_numPredicates][m_numLabels];
		
		for (Event event : m_events)
		{
			for (int pid=0; pid<event.m_predicates.length; pid++)
			{
				int predicate = event.m_predicates[pid];
				predLabels[predicate][event.m_label] += event.getSeenTimes() * event.m_values[pid];
			}
		}
		
		for (int i=0; i<m_numPredicates; i++)
		{
			for (int j=0; j<m_numLabels; j++)
			{
				if (predLabels[i][j] > 0)
					m_observationExpection[i][j] = predLabels[i][j];
				else
					m_observationExpection[i][j] = SMOOTH_SEEN;
			}
		}
	}
	
	private void iterate(int numIter)
	{
		m_parameters = new double[m_numPredicates][m_numLabels];
		double preloglikelihood = 0.0;
		double curloglikelihood = Double.MAX_VALUE;
		for (int i=0; i<numIter; i++)
		{
			preloglikelihood = curloglikelihood;
			curloglikelihood = 0.0;
			m_modelExpection = new double[m_numPredicates][m_numLabels];
			for (Event event : m_events)
			{
				double[] candProbs = calcCandProbs(event);
				for (int pid = 0; pid<event.m_predicates.length; pid++)
				{
					int predicate = event.m_predicates[pid];
					for (int label=0; label<m_numLabels; label++)
					{
						m_modelExpection[predicate][label] += event.getSeenTimes()*candProbs[label]*event.m_values[pid];
					}
				}
//				System.out.println(event.getSeenTimes() +" " + Math.log(candProbs[event.m_label]));
				curloglikelihood += event.getSeenTimes()*Math.log(candProbs[event.m_label]);
			}
			
			updateParameters();
			logger.info("" + (i+1) + " loglikelihood : " + curloglikelihood);
		}
	}
	
	private double[] calcCandProbs(Event event)
	{
		double[] candProbs = new double[m_numLabels];
		
		for (int pid=0; pid<event.m_predicates.length; pid++)
		{
			int predicate = event.m_predicates[pid];
			for (int label=0; label<m_numLabels; label++)
			{
				if (event.m_values[pid] > 0)
					candProbs[label] += m_parameters[predicate][label] * event.m_values[pid];
				else
					candProbs[label] += m_parameters[predicate][label] * SMOOTH_SEEN;
					
			}
		}
		
		double normalize = 0.0;
		for (int label=0; label<m_numLabels; label++)
		{
			candProbs[label] = Math.exp(candProbs[label]);
			normalize += candProbs[label];
		}
		
		for (int label=0; label<m_numLabels; label++)
		{
			candProbs[label] /= normalize;
		}
		
		return candProbs;
	}

	private void updateParameters()
	{
		for (int predicate=0; predicate<m_numPredicates; predicate++)
			for (int label=0; label<m_numLabels; label++)
			{
				m_parameters[predicate][label] += CONSTANT_C_INVERSE * Math.log(m_observationExpection[predicate][label]/m_modelExpection[predicate][label]);
			}
	}
}
