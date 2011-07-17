///*
// * ComparableEvent.java 
// * 
// * Author : 罗磊，luoleicn@gmail.com
// * 
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * Last Update:Jun 15, 2011
// * 
// */
//package mlfs.maxent.model;
//
//import java.util.Arrays;
//
///**
// * The Class ComparableEvent.
// * 
// * 这个类是允许Sample属于多个类的event，
// * 只允许单类的情况请使用mlfs.maxent.model.Event
// * 
// * 这个类允许event之间的排序
// * 
// */
//public class ComparableEvent extends Event implements Comparable<ComparableEvent>{
//	
//	/** 这个event在训练语料中出现多少次，在分类问题中，一个event可能允许属于多个类. */
//	protected int m_xyseen = 1;
//	protected int m_xseen = 1;
//
//	/**
//	 * Instantiates a new comparable event.
//	 *
//	 * @param label the label
//	 * @param predicates the predicates
//	 */
//	public ComparableEvent(int label, int[] predicates) {
//		
//		super(label, null);
//		Arrays.sort(predicates);
//		m_predicates = predicates;
//		
//	}
//	
//	/**
//	 * Instantiates a new comparable event.
//	 *
//	 * @param label the label
//	 * @param predicates the predicates
//	 * @param values the values
//	 */
//	public ComparableEvent(int label, int[] predicates, int[] values)
//	{
//		super(label, null, null);
//		
//		quickSort(predicates, values, 0, predicates.length-1);
//		m_predicates = predicates;
//		m_values = values;
//	}
//	
//	public int getXSeenTimes()
//	{
//		return m_xseen;
//	}
//
//	/* 当前event出现了多少次
//	 * @see mlfs.maxent.model.Event#getSeenTimes()
//	 */
//	@Override
//	public int getXYSeenTimes()
//	{
//		return m_xyseen;
//	}
//	
//	/**
//	 * 增加1次x出现次数
//	 */
//	public void addXSeen()
//	{
//		this.m_xseen++;
//	}
//	
//	/**
//	 * 增加1次xy出现次数
//	 *
//	 */
//	public void addXYSeen()
//	{
//		this.m_xyseen++;
//	}
//	
//	
//	/* 排序的时候不考虑y
//	 * @see java.lang.Comparable#compareTo(java.lang.Object)
//	 */
//	@Override
//	public int compareTo(ComparableEvent o) {
//		
////		if (m_label != o.m_label)
////			return m_label - o.m_label;
//		
//		int smallLen = m_predicates.length > o.m_predicates.length? o.m_predicates.length:m_predicates.length;
//		
//		int i=0;
//		while (i<smallLen)
//		{
//			if (m_predicates[i] != o.m_predicates[i])
//				return m_predicates[i] - o.m_predicates[i];
//			
//			if (m_values[i] != o.m_values[i])
//				return m_values[i] - o.m_values[i];
////			else if (m_values!=null && o.m_values==null && m_values[i] != 1)
////				return m_values[i] - 1;
////			else if (m_values==null && o.m_values!=null && o.m_values[i] != 1)
////				return 1-o.m_values[i];
//			
//			i++;
//		}
//		
//		if (smallLen != m_predicates.length)
//			return 1;
//		else if (smallLen != o.m_predicates.length)
//			return -1;
//		
//		return 0;
//	}
//	
//	/* (non-Javadoc)
//	 * @see java.lang.Object#equals(java.lang.Object)
//	 */
//	@Override
//	public boolean equals(Object o)
//	{
//		if (o instanceof ComparableEvent)
//		{
//			ComparableEvent ce = (ComparableEvent)o;
//			if (this.m_label != ce.m_label)
//				return false;
//			
//			if (this.m_predicates.length != ce.m_predicates.length)
//				return false;
//			
//			for (int i=0; i<this.m_predicates.length; i++)
//				if (this.m_predicates[i] != ce.m_predicates[i])
//					return false;
//			
//			for (int i=0; i<this.m_values.length; i++)
//				if (this.m_values[i] != ce.m_values[i])
//					return false;
//			
//			return true;
//		}
//		return false;
//	}
//
//	public boolean sameX(ComparableEvent ce)
//	{
//		if (ce == null)
//			return false;
//		
//		if (this.m_predicates.length != ce.m_predicates.length)
//			return false;
//		
//		for (int i=0; i<this.m_predicates.length; i++)
//			if (this.m_predicates[i] != ce.m_predicates[i])
//				return false;
//		
//		for (int i=0; i<this.m_values.length; i++)
//			if (this.m_values[i] != ce.m_values[i])
//				return false;
//		
//		return true;
//	}
//	
//	public boolean sameY(ComparableEvent ce)
//	{
//		if (ce == null)
//			return false;
//		return this.m_label == ce.m_label;
//	}
//	
//	/**
//	 * Quick sort.对a进行快拍，b中的元素变化和a保持一致。
//	 * 
//	 * 形式上类似与a数组是key，b数组是value，按照key排序
//	 *
//	 * @param a the a
//	 * @param b the b
//	 * @param l the l
//	 * @param r the r
//	 */
//	private void quickSort(int[] a, int[] b, int l, int r)
//	{
//		int i=l, j=r;
//		int key = a[(l+r)/2];
//		
//		while (i < j)
//		{
//			for (; a[i] < key; i++);
//			for (; a[j] > key; j--);
//			
//			if (i < j)
//			{
//				int tmp = a[i];
//				a[i] = a[j];
//				a[j] = tmp;
//				
//				tmp = b[i];
//				b[i] = b[j];
//				b[j] = tmp;
//				
//				i++;
//				j--;
//			}
//		}
//		
//		if (i < r)
//			quickSort(a, b, l, i);
//		if (j > l)
//			quickSort(a, b, j, r);
//	}
//}
