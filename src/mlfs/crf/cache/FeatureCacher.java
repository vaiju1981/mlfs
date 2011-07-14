/*
 * FeatureCacher.java 
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
 * Last Update:Jul 14, 2011
 * 
 */
package mlfs.crf.cache;

import java.util.ArrayList;
import java.util.List;

/**
 * 负责各个unigram和bigram的缓存
 */
public class FeatureCacher {

	/** The m_feature cache. */
	private List<List<Integer>> m_featureCache;
	
	/** 单例模式. */
	private static FeatureCacher m_cacher;
	
	/**
	 * Instantiates a new feature cacher.
	 */
	private FeatureCacher()
	{
		m_featureCache = new ArrayList<List<Integer>>();
	}
	
	/**
	 * Gets the single instance of FeatureCacher.
	 *
	 * @return single instance of FeatureCacher
	 */
	public static FeatureCacher getInstance()
	{
		if (m_cacher == null) {
			m_cacher = new FeatureCacher();
		}
		return m_cacher;
	}
	
	/**
	 * 加入缓存
	 *
	 * @param lst the lst
	 */
	public void add(List<Integer> lst)
	{
		m_featureCache.add(lst);
	}
	
	/**
	 * 返回给定偏移量的特征集合
	 *
	 * @param pos the pos
	 * @return the feats
	 */
	public List<Integer> getFeats(int pos)
	{
		return m_featureCache.get(pos);
	}
	
	/**
	 * 共多少缓存
	 *
	 * @return the int
	 */
	public int size()
	{
		return m_featureCache.size();
	}
	
	/**
	 * 清空缓存
	 */
	public void clear()
	{
		m_featureCache.clear();
	}
}
