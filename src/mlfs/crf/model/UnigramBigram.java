/*
 * UnigramBigram.java 
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
 * Last Update:Jul 10, 2011
 * 
 */
package mlfs.crf.model;

import java.util.List;
import java.util.Map;

public class UnigramBigram  {

	private List<Integer> m_unigrams;
	private List<List<Integer>> m_bigrams;
	
	public UnigramBigram(List<Integer> u, List<List<Integer>> b)
	{
		this.m_unigrams = u;
		this.m_bigrams = b;
	}
	
	public List<Integer> getUnigramFeats()
	{
		return m_unigrams;
	}
	
	public List<List<Integer>> getBigramFeats()
	{
		return m_bigrams;
	}

}
