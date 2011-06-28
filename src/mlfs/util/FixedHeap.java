/*
 * FixedHeap.java 
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
 * Last Update:Jun 23, 2011
 * 
 */
package mlfs.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class FixedHeap.
 * 保留固定大小的堆容器，可制定保留最大的若干元素还是保留最小的若干元素
 *
 * @param <T> the generic type
 */
public class FixedHeap<T extends Comparable<T>> {
	
	public enum SORT{
		/** 保留大的元素. */
		MAX_ELEMENT,
		/** 保留小的元素. */
		MIN_ELEMENT
	}
	
	/** The m_array. */
	private Object[] m_array;
	
	/** The m_capacity. */
	private int m_capacity;
	
	/** The m_size. */
	private int m_size;
	
	/** The m_sort. */
	private SORT m_sort;
	
	/**
	 * Instantiates a new fixed heap.
	 *
	 * @param size the size
	 * @param sort the sort
	 */
	public FixedHeap(int size, SORT sort)
	{
		if (size <= 0)
			throw new IllegalArgumentException("size = " + size);
		
		m_array = new Object[size];
		m_capacity = size;
		m_sort = sort;
		m_size = 0;
	}
	
	/**
	 * Adds the.
	 *
	 * @param t the t
	 */
	public void add(T t)
	{
		if (m_size == m_capacity)
		{
			if (compare((T)m_array[0], t, m_sort) > 0)
				return;
			else 
			{
				int fatherIndex = 0;
				int leftSonIndex = 1;
				int rigtSonIndex = 2;
				m_array[0] = t;
				while (leftSonIndex < m_capacity)
				{
					int niceSonIndex = leftSonIndex;
					T leftSon = (T)m_array[fatherIndex*2+1];
					T rightSon = null;
					if (leftSonIndex+1 < m_capacity )
						rightSon = (T)m_array[fatherIndex*2+2];
					if (rightSon!=null && compare(leftSon, rightSon, m_sort) > 0)
						niceSonIndex = rigtSonIndex;
					
					if (compare((T)m_array[niceSonIndex], (T)m_array[fatherIndex], m_sort) > 0)
						break;
					T tmp =  (T)m_array[fatherIndex];
					m_array[fatherIndex] = m_array[niceSonIndex];
					m_array[niceSonIndex] = tmp;
					
					fatherIndex = niceSonIndex;
					leftSonIndex = fatherIndex*2+1;
					rigtSonIndex = fatherIndex*2+2;
				}
			}
		}
		else
		{
			m_array[m_size] = t;
			int curIndex = m_size;
			int fatherIndex = -1;
			if (m_size%2 ==0)
				fatherIndex = curIndex/2-1;
			else
				fatherIndex = curIndex/2;
			
			while (fatherIndex>= 0 && curIndex >0)
			{
				if ( compare((T)m_array[fatherIndex], (T)m_array[curIndex], m_sort) > 0)
				{
					T tmp =  (T)m_array[fatherIndex];
					m_array[fatherIndex] = m_array[curIndex];
					m_array[curIndex] = tmp;
					
					curIndex = fatherIndex;
					if (curIndex % 2 == 0) 
						fatherIndex = curIndex/2-1;
					else 
						fatherIndex = curIndex / 2;
					
				}
				else
					break;
			}
			
			m_size++;
		}
		
	}
	
	/**
	 * Compare.
	 * 如果两个元素相等，返回0；
	 * 否则
	 * 对于sort == SORT.MAX_ELEMENT，采用小根堆策略，如果t1>t2返回1，如果t1<t2返回-1
	 * 对于sort == SORT.MIN_ELEMENT，采用大根堆策略，如果t1<t2返回1，如果t1>t2返回-1
	 *
	 * @param t1 the t1
	 * @param t2 the t2
	 * @param sort the sort
	 * @return the int
	 */
	private int compare(T t1, T t2, SORT sort)
	{
		if (t1.compareTo(t2) == 0)
			return 0;
		
		if (sort == SORT.MAX_ELEMENT)
		{
			//保留大元素，使用小根堆实现
			if (t1.compareTo(t2) > 0)
				return 1;
			else
				return -1;
		}
		else if (sort == SORT.MIN_ELEMENT)
		{
			//保留小元素，使用大根堆实现
			if (t1.compareTo(t2)<0)
				return 1;
			else 
				return -1;
		}
		//never reach
		return 0;
	}
	
	public List<T> asList()
	{
		List<T> ret = new ArrayList<T>();
		for (int i=0; i<m_size; i++)
			ret.add((T)m_array[i]);
		return ret;
	}
	
	public static void main(String[] args)
	{
		int[] a = {1, 5, 3, 2, 8, 0, 9, 4, 7, 6};
		
		FixedHeap<Integer> heap = new FixedHeap<Integer>(4, FixedHeap.SORT.MIN_ELEMENT);
		for (int i=0; i<a.length; i++)
			heap.add(a[i]);
		
		List<Integer> res = heap.asList();
		for (int i=0; i<res.size(); i++)
			System.out.print(res.get(i) + " ");
		System.out.println();
	}
}
