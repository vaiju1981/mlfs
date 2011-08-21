/*
 * MEModel.java 
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
 * Last Update:Jun 28, 2011
 * 
 */
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

/**
 * The Class MEModel.
 */
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
	
	/**
	 * Instantiates a new mE model.
	 *
	 * @param parameters the parameters
	 * @param numLabels the num labels
	 * @param predicates the predicates
	 * @param labels the labels
	 */
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
	public int label(MEEvent event)
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
	
	/**
	 * Gets the preds.
	 *
	 * @return the preds
	 */
	public Set<Integer> getPreds()
	{
		return this.m_predicates;
	}
}
