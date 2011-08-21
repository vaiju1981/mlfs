/*
 * GIS.java 
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
 * Last Update:Jun 15, 2011
 * 
 */
package mlfs.maxent;

import java.util.logging.Logger;
import mlfs.maxent.model.MEEvent;
import mlfs.maxent.model.MEModel;
import mlfs.maxent.model.TrainDataHandler;

/**
 * The Class GIS.训练GISModel
 */
public class GIS extends METrainModel{
	
	private Logger logger = Logger.getLogger(GIS.class.getName());
	
	/** 似然值的收敛标准 CONVERGENCE. */
	private static double CONVERGENCE = 0.0001;
	
	/** 牛顿法迭代次数. */
	private static int NUM_NEWTON_ITER = 10;
	
	/** GIS计算的常数C. */
	private int CONSTANT_C;
	
	/** GIS常数C的倒数. */
	private double CONSTANT_C_INVERSE;

	/**
	 * Instantiates a new GIS.
	 *
	 * @param handler 训练语料得到的data handler
	 */
	public GIS(TrainDataHandler handler)
	{
		super(handler);
	}
	/**
	 * Instantiates a new gIS.
	 * 使用高斯平滑会导致参数的求解无法使用解析解更新
	 * 只能使用牛顿法更新，进而导致训练速度减慢
	 * 一般来说会提高模型效果，但具体能否提高效果也要看实际应用
	 * 
	 * @param handler the handler
	 * @param useGaussianSmooth  是否使用高斯平滑
	 */
	public GIS(TrainDataHandler handler, boolean useGaussianSmooth)
	{
		super(handler, useGaussianSmooth);
	}
	
	@Override
	public MEModel train() {
		return train(100);
	}
	/**
	 * Train.
	 *
	 * @param numIter 迭代参数
	 * @return GIS model
	 */
	public MEModel train(int numIter)
	{
		logger.info("Calc Constant C");
		CONSTANT_C = calcContantC();
		CONSTANT_C_INVERSE = 1.0/CONSTANT_C;
		
		logger.info("calc observation expection matrix");
		m_observationExpection = new double[m_numPredicates][m_numLabels];
		calcObservationExpection();
		//simple smooth
		for (int i=0; i<m_numPredicates; i++)
			for (int j=0; j<m_numLabels; j++)
				if (m_observationExpection[i][j] == 0)
					m_observationExpection[i][j] = METrainModel.SMOOTH_SEEN;
					
		logger.info("Start to iterate " + numIter + " times");
		iterate(numIter);
		
		return new MEModel(m_parameters, m_numLabels, m_predicates, m_labels);
	}
	
	/**
	 * 计算GIS常数C
	 *
	 * @return the int
	 */
	private int calcContantC()
	{
		int c = -1;
		for (MEEvent event : m_events)
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
				for (double v : event.m_values)
					satisfiedFeat += v;
				if (satisfiedFeat > c)
					c = satisfiedFeat;
			}
		}
		
		return c;
	}
	

	/**
	 * 迭代求解
	 *
	 * @param numIter 迭代次数
	 */
	private void iterate(int numIter)
	{
		m_parameters = new double[m_numPredicates][m_numLabels];
		m_modelExpection = new double[m_numPredicates][m_numLabels];
		
		double preloglikelihood = 0.0;
		double curloglikelihood = Double.MAX_VALUE;
		for (int i=0; i<numIter; i++)
		{
			preloglikelihood = curloglikelihood;
			curloglikelihood = 0.0;
			clearModelException();
			for (MEEvent event : m_events)
			{
				double[] candProbs = calcCandProbs(event);
				for (int pid = 0; pid<event.m_predicates.length; pid++)
				{
					int predicate = event.m_predicates[pid];
					for (int label=0; label<m_numLabels; label++)
					{
						m_modelExpection[predicate][label] += candProbs[label]*event.m_values[pid];
					}
				}
				curloglikelihood += Math.log(candProbs[event.m_label]);
			}
			
			updateParameters();
			logger.info("" + (i+1) + " loglikelihood : " + curloglikelihood);
			if (i > 1 && curloglikelihood-preloglikelihood<CONVERGENCE)
				break;
		}
	}
	
	/**
	 * 计算给定一个event的上下文谓词集合，计算这个上下文产生各个label的条件概率
	 *
	 * @param event 给定的event 
	 * @return the double[]条件概率
	 */
	private double[] calcCandProbs(MEEvent event)
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

	/**
	 * Update parameters.
	 */
	private void updateParameters()
	{
		for (int predicate=0; predicate<m_numPredicates; predicate++)
			for (int label=0; label<m_numLabels; label++)
			{
				if (!USE_GAUSSIAN_SMOOTH)
					m_parameters[predicate][label] += CONSTANT_C_INVERSE * Math.log(m_observationExpection[predicate][label]/m_modelExpection[predicate][label]);
				else
				{
					double delta = 0.0;
					double tmp = 0.0;
					for (int i=0; i<NUM_NEWTON_ITER; i++)
					{
						tmp = delta;
						double f = Math.exp(CONSTANT_C*delta)*m_modelExpection[predicate][label] + (m_parameters[predicate][label]+delta)/0.5 - m_observationExpection[predicate][label];
						double f_derivative =  Math.exp(CONSTANT_C*delta)*m_modelExpection[predicate][label]*CONSTANT_C + 20;//20 = 1/0.5
						delta += 0-f/f_derivative;
						
						if (i>0 && Math.abs(delta - tmp) < CONVERGENCE)
							break;
					}
					m_parameters[predicate][label] += delta;
				}
					
			}
	}
	
	private void clearModelException()
	{
			for (int i=0; i<m_numPredicates; i++)
				for (int j=0; j<m_numLabels; j++)
					m_modelExpection[i][j] = 0.0;
	}
}