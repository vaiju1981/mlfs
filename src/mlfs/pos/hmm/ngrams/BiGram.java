/*
 * BiGram.java
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

/**
 * The Class BiGram.
 */
public class BiGram {
	
	/** The m_tag1. */
	private int m_tag1;
	
	/** The m_tag2. */
	private int m_tag2; 
	
	/** The m_prob. */
	private double m_prob;

	/**
	 * 建立一个BiGram
	 *
	 * @param t1 the t1
	 * @param t2 the t2
	 */
	public BiGram(int t1, int t2)
	{
		m_tag1 = t1;
		m_tag2 = t2;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		int hash = (m_tag1 <<16) | m_tag2;
		return hash;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof BiGram)
		{
			BiGram bigram = (BiGram)o;
			return (m_tag1==bigram.m_tag1)&&(m_tag2==bigram.m_tag2);
		}
		return false;
	}
	
	/**
	 * Gets the prob.
	 *
	 * @return the prob
	 */
	public double getProb() {
		return m_prob;
	}
	
	/**
	 * Sets the prob.
	 *
	 * @param prob the new prob
	 */
	public void setProb(double prob) {
		this.m_prob = prob;
	}
	
	/**
	 * Gets the tag1.
	 *
	 * @return the tag1
	 */
	public int getTag1() {
		return m_tag1;
	}
	
	/**
	 * Gets the tag2.
	 *
	 * @return the tag2
	 */
	public int getTag2() {
		return m_tag2;
	}	
	
	
}
