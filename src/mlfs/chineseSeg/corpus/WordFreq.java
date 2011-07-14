/*
 * WordFreq.java 
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
package mlfs.chineseSeg.corpus;


public class WordFreq implements Comparable<WordFreq>{
	
	public final char letter;
	
	public final int freq;
	
	public WordFreq(char letter, int freq)
	{
		this.letter = letter;
		this.freq = freq;
	}
	
	@Override
	public int compareTo(WordFreq o) {
		return freq - o.freq;
	}
}
