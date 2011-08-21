/*
 * Event.java
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

package mlfs.maxent.model;

/**
 * The Class Event.
 * 
 * 一个evnet是训练语料中的一个sample
 *  一个event包括一个上下文x和一个输出标签y，一个上下文包括若干个谓词
 * 
 * 注意：这个类只允许单类情况，即一个event只能属于一个类别，
 * 多类情况，请使用mlfs.maxent.model.ComparableEvent
 */
public class MEEvent {
	
	/** 输出标签，相当于Fi(x,y)中的y. */
	public int m_label;
	/** 每一个上下文x包括若干谓词. */
	public int[] m_predicates;
	/** 对应于每个(谓词i,label)出现多少次. */
	public double[] m_values;
	            
	public MEEvent(int label, int[] predicates)
	{
		this.m_label = label;
		this.m_predicates = predicates;
		this.m_values = null;
	}
	 
	public MEEvent(int label, int[] predicates, double[] values)
	{
		this.m_label = label;
		this.m_predicates = predicates;
		this.m_values = values;
	}
	
//	public int getSeenTimes()
//	{
//		return 1;
//	}
//	
//	public int getXYSeenTimes()
//	{
//		return 1;
//	}
	
}
