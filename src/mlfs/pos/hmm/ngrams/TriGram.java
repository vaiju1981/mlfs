/*
 * TriGram.java
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

public class TriGram {
	private int m_tag1;
	private int m_tag2;
	private int m_tag3;
	private double m_prob;
	
	public TriGram(int t1, int t2, int t3)
	{
		m_tag1 = t1;
		m_tag2 = t2;
		m_tag3 = t3;
	}
	
	@Override
	public int hashCode()
	{
		int hash = (m_tag1 << 20) | (m_tag2 << 10) | (m_tag3 );
		return hash;
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof TriGram)
		{
			TriGram trigram = (TriGram)o;
			return (m_tag1==trigram.m_tag1)&&(m_tag2==trigram.m_tag2)&&(m_tag3==trigram.m_tag3);
		}
		return false;
	}

	public double getProb() {
		return m_prob;
	}

	public void setProb(double m_prob) {
		this.m_prob = m_prob;
	}

	public int getTag1() {
		return m_tag1;
	}

	public int getTag2() {
		return m_tag2;
	}

	public int getTag3() {
		return m_tag3;
	}

}
