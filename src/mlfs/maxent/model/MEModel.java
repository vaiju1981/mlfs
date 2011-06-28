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

public class MEModel implements Serializable{
	
	/** The Constant serialVersionUID. */
	protected static final long serialVersionUID = 1L;
	
	/** The m_num labels. */
	protected int m_numLabels;
	
	/** The m_parameters. */
	protected double[][] m_parameters;
	
	/** The m_predicates. */
	protected HashSet<Integer> m_predicates;
	
	/** The m_labels. */
	protected HashSet<Integer> m_labels;
	
	public MEModel(double[][] parameters, int numLabels, HashSet<Integer> predicates, HashSet<Integer> labels)
	{
		m_parameters = parameters;
		
		m_numLabels = numLabels;
		
		m_predicates = predicates;
		m_labels = labels;
	}
	
	/**
	 * Label分类.
	 *
	 * @param event the event
	 * @return the int
	 */
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

	/**
	 * Save.持久化当前模型
	 *
	 * @param path the path
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void save(String path) throws FileNotFoundException, IOException
	{
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
		oos.writeObject(this);
		oos.close();
	}
	
	/**
	 * Load.从文件中加载当前模型
	 *
	 * @param path the path
	 * @return the gIS model
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static MEModel load(String path) throws FileNotFoundException, IOException, ClassNotFoundException
	{
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
		MEModel model = (MEModel)ois.readObject();
		ois.close();
		return model;
	}
	
	public Set<Integer> getPreds()
	{
		return this.m_predicates;
	}
}
