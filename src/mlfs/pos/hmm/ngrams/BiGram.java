/*
 * BiGram.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 5, 2011
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
