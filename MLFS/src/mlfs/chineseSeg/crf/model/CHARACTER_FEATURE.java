/*
 * CHARACTER_FEATURE.java 
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
 * Last Update:Jul 3, 2011
 * 
 */
package mlfs.chineseSeg.crf.model;

public enum CHARACTER_FEATURE{
	DIGIT(1), CHINESE_DIGIT(2), LETTER(3), PUNCTUATION(4), SINGLE(5), PREFIX(6), SUFFIX(7), LONGEST(8), OTHERS(9);
	private int m_value;
	private CHARACTER_FEATURE(int v){
		m_value = v;
	}
	public int getValue(){
		return m_value;
	}
}
