/*
 * WordTag.java
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

package mlfs.pos.corpus;

/**
 *一个类封装了一个词和它对应的词性
 * 
 */
public class WordTag {

	private String m_word;
	
	private String m_tag;
	
	public WordTag(String m_word, String m_tag) {
		this.m_word = m_word;
		this.m_tag = m_tag;
	}

	public String getWord() {
		return m_word;
	}

	public String getTag() {
		return m_tag;
	}
}
