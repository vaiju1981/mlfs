/*
 * TriGram.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 3, 2011
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
