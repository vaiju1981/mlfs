package mlfs.maxent;

import java.util.logging.Logger;

import mlfs.maxent.model.Event;
import mlfs.maxent.model.MEModel;
import mlfs.maxent.model.TrainDataHandler;
import mlfs.numerical.AbstractLBFGS;

public class MELBFGS extends TrainModel{
	
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
		
		m_modelExpection = new double[m_numPredicates][m_numLabels];
		
		logger.info("calc observation expection matrix");
		m_observationExpection = new double[m_numPredicates][m_numLabels];
		calcObservationExpection();
		
		logger.info("call lbfgs...");
		LBFGS lbfgs = new LBFGS(m_numPredicates*m_numLabels, 5, numIter);
		double[] solutions = new double[m_numPredicates*m_numLabels];
		lbfgs.getSolution(solutions);
		lbfgs = null;
		
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
	
	private class LBFGS extends AbstractLBFGS
	{
		private final double constVari = 0-Math.log(Math.sqrt(Math.PI));
		
		public LBFGS(int dimetion, int m, int numIter) {
			super(dimetion, m, numIter);
		}

		@Override
		public double calFunctionVal(double[] x) {
			double f = 0.0;
			for (Event event : m_events)
			{
				double[] candProbs = calcCandProbs(event, x);
				f -= event.getSeenTimes()*Math.log(candProbs[event.m_label]);
			}
			if (USE_GAUSSIAN_SMOOTH)
			{
				for (int i=0; i<m_numPredicates; i++)
					for (int j=0; j<m_numLabels; j++)
						f -= constVari - x[i*m_numLabels+j];
			}
			
			return f;
		}

		@Override
		public void calGradientVal(double[] x, double[] g) 
		{
			for (int i=0; i<m_numPredicates; i++)
				for (int j=0; j<m_numLabels; j++)
					m_modelExpection[i][j] = 0.0;
			
			for (Event event : m_events)
			{
				double[] candProbs = calcCandProbs(event, x);
				for (int pid = 0; pid<event.m_predicates.length; pid++)
				{
					int predicate = event.m_predicates[pid];
					for (int label=0; label<m_numLabels; label++)
					{
						m_modelExpection[predicate][label] += event.getSeenTimes()*candProbs[label]*event.m_values[pid];
					}
				}
			}
			
			for (int i=0; i<m_numPredicates; i++)
				for (int j=0; j<m_numLabels; j++)
					if (!USE_GAUSSIAN_SMOOTH)
						g[i*m_numLabels+j] = m_modelExpection[i][j] - m_observationExpection[i][j];
					else
						g[i*m_numLabels+j] = m_modelExpection[i][j] - m_observationExpection[i][j] + 2*x[i*m_numLabels+j];
						
		}
		
		@Override
		protected void before()
		{
			
		}
		
		private double[] calcCandProbs(Event event, double[] solutions)
		{
			double[] candProbs = new double[m_numLabels];
			
			for (int pid=0; pid<event.m_predicates.length; pid++)
			{
				int predicate = event.m_predicates[pid];
				for (int label=0; label<m_numLabels; label++)
				{
					if (event.m_values[pid] > 0)
						candProbs[label] += solutions[predicate*m_numLabels + label] * event.m_values[pid];
					else
						candProbs[label] += solutions[predicate*m_numLabels + label]* SMOOTH_SEEN;
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
	}
}
