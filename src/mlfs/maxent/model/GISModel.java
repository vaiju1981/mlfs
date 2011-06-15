package mlfs.maxent.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class GISModel implements Serializable{
	
	private static final long serialVersionUID = 1L;

//	private static double CONSTANT_C_INVERSE;
//	
//	private int m_numPredicates;
	
	private int m_numLabels;
	
	private double[][] m_parameters;
	
	private HashSet<Integer> m_predicates;
	private HashSet<Integer> m_labels;
	
	public GISModel(double cInverse, double[][] parameters, int numPreds, int numLabels, HashSet<Integer> predicates, HashSet<Integer> labels)
	{
//		CONSTANT_C_INVERSE = cInverse;
		m_parameters = parameters;
		
//		m_numPredicates = numPreds;
		m_numLabels = numLabels;
		
		m_predicates = predicates;
		m_labels = labels;
	}
	
	public GISModel(double[][] parameters, int numLabels, HashSet<Integer> predicates, HashSet<Integer> labels)
	{
		m_parameters = parameters;
		
		m_numLabels = numLabels;
		
		m_predicates = predicates;
		m_labels = labels;
	}
	
	public int label(Event event)
	{
		double[] candProbs = new double[m_numLabels];
		
		for (int pid=0; pid<event.m_predicates.length; pid++)
		{
			int predicate = event.m_predicates[pid];
			for (int label=0; label<m_numLabels; label++)
			{
				if (event.m_values != null)
					candProbs[label] += m_parameters[predicate][label] * event.m_values[pid];
				else
					candProbs[label] += m_parameters[predicate][label];
			}
			
		}
		
		double max = Double.NEGATIVE_INFINITY;
		int ans = -1;
		for (int i=0; i<m_numLabels; i++)
		{
			if (candProbs[i] > max)
			{
				max = candProbs[i];
				ans = i;
			}
		}
		
		return ans;
	}
	
	public void save(String path) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
		oos.writeObject(this);
		oos.close();
	}
	
	public static GISModel load(String path) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
		GISModel model = (GISModel)ois.readObject();
		ois.close();
		return model;
	}

	public Set<Integer> getPreds()
	{
		return this.m_predicates;
	}
	
}
