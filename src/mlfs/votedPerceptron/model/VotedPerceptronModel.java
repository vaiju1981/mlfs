/*
 * VotedPerceptronModel.java
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
 * Last Update:2011-7-6
   * 
   */
package mlfs.votedPerceptron.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VotedPerceptronModel {

	private List<Perceptron> m_parameters;
	
	private boolean m_positive;
	
	public VotedPerceptronModel(List<Perceptron> perceptrons, boolean positive)
	{
		this.m_parameters = perceptrons;
		this.m_positive = positive;
	}
	
	public void save(String path) throws IOException
	{
		DataOutputStream dos = new DataOutputStream(new FileOutputStream(new File(path)));
		
		int numFeat = m_parameters.get(0).vector.length;
		dos.writeBoolean(m_positive);
		dos.writeInt(m_parameters.size());
		dos.writeInt(numFeat);
		for (Perceptron p : m_parameters)
		{
			for (double v : p.vector)
				dos.writeDouble(v);
			dos.writeInt(p.correct);
		}
		
		dos.close();
	}
	
	public static VotedPerceptronModel load(String path) throws IOException
	{
		DataInputStream dis = new DataInputStream(new FileInputStream(new File(path)));
		boolean positive = dis.readBoolean();
		int numPerceptron = dis.readInt();
		int numFeat = dis.readInt();
		
		List<Perceptron> perceptrons = new ArrayList<Perceptron>(numPerceptron);
		for (int i=0; i<numPerceptron; i++)
		{
			double[] v = new double[numFeat];
			for (int j=0; j<numFeat; j++)
				v[j] = dis.readDouble();
			int c = dis.readInt();
			
			perceptrons.add(new Perceptron(v, c));
		}
		
		dis.close();
		return new VotedPerceptronModel(perceptrons, positive);
	}
	
	public int predict(VPEvent e)
	{
		int sum = 0;
		
		for (Perceptron perceptron : m_parameters)
		{
//			if (perceptron.correct < 2)
//				continue;
			double value = 0.0;
			for (int idx=e.predicates.length-1; idx>=0; idx--)
			{
				if (e.predicates[idx] >= perceptron.vector.length)
					continue;
				value += perceptron.vector[e.predicates[idx]] * e.values[idx];
			}
			if (value > 0)
			{
				sum += perceptron.correct;
			}
			else if (value < 0)
			{
				sum -= perceptron.correct;
			}
			
		}
		
		if (sum > 0)
			return 1;
		else if (sum < 0)
			return -1;
		else 
		{
			System.out.println("遇到未知情况，根据最大似然判断");
			if (m_positive)
				return 1;
			else
				return -1;
		}
	}
	
	public int getNumPerceptrons()
	{
		return m_parameters.size();
	}
}
