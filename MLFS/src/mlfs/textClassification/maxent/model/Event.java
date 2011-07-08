/*
 * Event.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 11, 2011
 * 
 */
package mlfs.textClassification.maxent.model;

/**
 * The Class Event.一个evnet是训练语料中的一个sample
 * 一个event包括一个上下文x和一个输出标签y，一个上下
 * 文包括若干个谓词
 */
public class Event {
	
	/** 输出标签，相当于Fi(x,y)中的y. */
	private int m_ans;
	/** 每一个上下文x包括若干谓词. */
	private int[] m_predicate;
	/** 这个event在训练语料中出现多少次. */
	private int m_seen;
	            
	public Event(int ans, int[] predicates)
	{
		this.m_ans = ans;
		this.m_predicate = predicates;
	}
	
	public void addSeen()
	{
		this.m_seen++;
	}
	
	public void addSeen(int times)
	{
		this.m_seen += times;
	}
	
	public int getTimes()
	{
		return m_seen;
	}
}
