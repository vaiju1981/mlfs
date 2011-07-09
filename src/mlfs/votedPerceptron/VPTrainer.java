package mlfs.votedPerceptron;

import java.util.ArrayList;
import java.util.List;

import mlfs.votedPerceptron.model.Perceptron;
import mlfs.votedPerceptron.model.VPEvent;
import mlfs.votedPerceptron.model.VotedPerceptronModel;

public class VPTrainer {

	private List<Perceptron> m_parameters;
	
	private List<VPEvent> m_events;
	
	private int m_numFeat;
	
	private boolean m_positive;
	
	public VPTrainer(List<VPEvent> events)
	{
		m_parameters = new ArrayList<Perceptron>();
		m_events = events;
		
		int positive = 0, negtive = 0;
		m_numFeat = 0;
		for (VPEvent e : events)
		{
			if (e.label!=1 && e.label != -1)
				throw new IllegalArgumentException("一个实例的label必须是1或-1！");
			
			//统计最大的feature编号
			for (int x : e.predicates)
			{
				if (x > m_numFeat)
					m_numFeat = x;
			}
			
			if (e.label > 0)
				positive++;
			else
				negtive++;
		}
		m_numFeat += 1;
		
		if (positive >= negtive)
			m_positive = true;
		else
			m_positive = false;
	}
	
	public VotedPerceptronModel train(int numIter)
	{
		double[] w = new double[m_numFeat];
		int c = 0;
		for (int t=0; t<numIter; t++)
		{
			for (VPEvent event : m_events)
			{
				double predict = 0.0;
				for (int idx=event.predicates.length-1; idx>=0; idx--)
				{
					predict += w[event.predicates[idx]] * event.values[idx];
				}
				
				if (predict>0 && event.label==1)
					c++;
				else if (predict<0 && event.label==-1)
					c++;
				else 
				{
					Perceptron perceptron = new Perceptron(w, c);
					m_parameters.add(perceptron);
					
					for (int idx=event.predicates.length-1; idx>=0; idx--)
					{
						w[event.predicates[idx]] += event.label * event.values[idx];
					}
					
					c = 1;
				}
						
			}
			
		}
		
		return new VotedPerceptronModel(m_parameters, m_positive);
	}
}
