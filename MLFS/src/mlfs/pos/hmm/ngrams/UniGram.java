/*
 * UniGram.java
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
 * Last Update:2011-6-11
   * 
   */
package mlfs.pos.hmm.ngrams;

public class UniGram {
	private int m_tag;
	private double m_prob;

	public UniGram(int tag)
	{
		m_tag = tag;
	}
	
	@Override
	public int hashCode()
	{
		return m_tag;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof UniGram)
			return m_tag == ((UniGram)o).m_tag;
		return false;
	}

	public double getProb() {
		return m_prob;
	}

	public void setProb(double m_prob) {
		this.m_prob = m_prob;
	}
	
	public int getTag()
	{
		return m_tag;
	}
}
