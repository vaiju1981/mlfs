/*
 * UniGram.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 2, 2011
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
