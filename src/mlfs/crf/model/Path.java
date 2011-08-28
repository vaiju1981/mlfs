/*
 * Path.java 
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
 * Last Update:Aug 28, 2011
 * 
 */
package mlfs.crf.model;

import java.util.List;

/**
 * The Class Path.
 * 这个类用于保存N-Best的中的一条路径
 */
public class Path {

	/** 该路径的概率. */
	public double m_prob;
	
	/** 路径标签. */
	public String[] m_tags;
	
	public Path(double prob, String[] tags)
	{
		this.m_prob = prob;
		this.m_tags = tags;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(m_prob).append(' ');
		for (int i=0; i<m_tags.length; i++)
			sb.append(m_tags[i]).append(' ');
		return sb.toString();
	}
	
}
