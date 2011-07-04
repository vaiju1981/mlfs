/*
 * Resource.java 
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

import java.util.HashSet;
import java.util.Set;

/**
 * The Class Resource.
 */
public class Resource {

	/** 数字*/
	private Set<Character> m_digit;
	
	/** 中文数字. */
	private Set<Character> m_digitChinese;
	
	/** 字符. */
	private Set<Character> m_letters;
	
	/** 标点. */
	private Set<Character> m_punctuation;
	
	/** The m_resource. */
	private static Resource m_resource;
	
	/**
	 * Instantiates a new resource.
	 */
	private Resource()
	{
		init();
	}
	
	/**
	 * Gets the single instance of Resource.
	 *
	 * @return single instance of Resource
	 */
	public static Resource getInstance()
	{
		if (m_resource == null)
			m_resource = new Resource();
		return m_resource;
	}
	
	/**
	 * Checks if is digit.
	 *
	 * @param c the c
	 * @return true, if is digit
	 */
	public boolean isDigit(char c)
	{
		return m_digit.contains(c);
	}

	/**
	 * Checks if is chinese digit.
	 *
	 * @param c the c
	 * @return true, if is chinese digit
	 */
	public boolean isChineseDigit(char c)
	{
		return m_digitChinese.contains(c);
	}
	
	/**
	 * Checks if is letter.
	 *
	 * @param c the c
	 * @return true, if is letter
	 */
	public boolean isLetter(char c)
	{
		return m_letters.contains(c);
	}
	
	/**
	 * Checks if is punctuation.
	 *
	 * @param c the c
	 * @return true, if is punctuation
	 */
	public boolean isPunctuation(char c)
	{
		return m_punctuation.contains(c);
	}
	
	/**
	 * Inits the.
	 */
	private void init()
	{
		m_digit = new HashSet<Character>();
		m_digit.add('0');
		m_digit.add('1');
		m_digit.add('2');
		m_digit.add('3');
		m_digit.add('4');
		m_digit.add('5');
		m_digit.add('6');
		m_digit.add('7');
		m_digit.add('8');
		m_digit.add('9');

		m_digit.add('０');
		m_digit.add('１');
		m_digit.add('２');
		m_digit.add('３');
		m_digit.add('４');
		m_digit.add('５');
		m_digit.add('６');
		m_digit.add('７');
		m_digit.add('８');
		m_digit.add('９');

		m_digitChinese = new HashSet<Character>();
		m_digitChinese.add('零');
		m_digitChinese.add('壹');
		m_digitChinese.add('貳');
		m_digitChinese.add('三');
		m_digitChinese.add('肆');
		m_digitChinese.add('伍');
		m_digitChinese.add('陸');
		m_digitChinese.add('柒');
		m_digitChinese.add('捌');
		m_digitChinese.add('玖');
		m_digitChinese.add('拾');
		
		m_digitChinese.add('零');
		m_digitChinese.add('壹');
		m_digitChinese.add('贰');
		m_digitChinese.add('叁');
		m_digitChinese.add('肆');
		m_digitChinese.add('伍');
		m_digitChinese.add('陆');
		m_digitChinese.add('柒');
		m_digitChinese.add('捌');
		m_digitChinese.add('玖');
		m_digitChinese.add('拾');
		m_digitChinese.add('佰');
		m_digitChinese.add('仟');
		m_digitChinese.add('万');
		m_digitChinese.add('亿');
		m_digitChinese.add('〇');
		m_digitChinese.add('一');
		m_digitChinese.add('二');
		m_digitChinese.add('三');
		m_digitChinese.add('四');
		m_digitChinese.add('五');
		m_digitChinese.add('六');
		m_digitChinese.add('七');
		m_digitChinese.add('八');
		m_digitChinese.add('九');
		m_digitChinese.add('十');
		m_digitChinese.add('百');
		m_digitChinese.add('千');
		
		m_letters = new HashSet<Character>();
		m_letters.add('a');
		m_letters.add('b');
		m_letters.add('c');
		m_letters.add('d');
		m_letters.add('e');
		m_letters.add('f');
		m_letters.add('g');
		m_letters.add('h');
		m_letters.add('I');
		m_letters.add('j');
		m_letters.add('k');
		m_letters.add('l');
		m_letters.add('m');
		m_letters.add('n');
		m_letters.add('o');
		m_letters.add('p');
		m_letters.add('q');
		m_letters.add('r');
		m_letters.add('s');
		m_letters.add('t');
		m_letters.add('u');
		m_letters.add('v');
		m_letters.add('w');
		m_letters.add('x');
		m_letters.add('y');
		m_letters.add('z');

		m_letters.add('A');
		m_letters.add('B');
		m_letters.add('C');
		m_letters.add('D');
		m_letters.add('E');
		m_letters.add('F');
		m_letters.add('G');
		m_letters.add('H');
		m_letters.add('I');
		m_letters.add('J');
		m_letters.add('K');
		m_letters.add('L');
		m_letters.add('M');
		m_letters.add('N');
		m_letters.add('O');
		m_letters.add('P');
		m_letters.add('Q');
		m_letters.add('R');
		m_letters.add('S');
		m_letters.add('T');
		m_letters.add('U');
		m_letters.add('V');
		m_letters.add('W');
		m_letters.add('X');
		m_letters.add('Y');
		m_letters.add('Z');

		m_letters.add('ａ');
		m_letters.add('ｂ');
		m_letters.add('ｃ');
		m_letters.add('ｄ');
		m_letters.add('ｅ');
		m_letters.add('ｆ');
		m_letters.add('ｇ');
		m_letters.add('ｈ');
		m_letters.add('ｉ');
		m_letters.add('ｊ');
		m_letters.add('ｋ');
		m_letters.add('ｌ');
		m_letters.add('ｍ');
		m_letters.add('ｎ');
		m_letters.add('ｏ');
		m_letters.add('ｐ');
		m_letters.add('ｑ');
		m_letters.add('ｒ');
		m_letters.add('ｓ');
		m_letters.add('ｔ');
		m_letters.add('ｕ');
		m_letters.add('ｖ');
		m_letters.add('ｗ');
		m_letters.add('ｘ');
		m_letters.add('ｙ');
		m_letters.add('ｚ');

		m_letters.add('Ａ');
		m_letters.add('Ｂ');
		m_letters.add('Ｃ');
		m_letters.add('Ｄ');
		m_letters.add('Ｅ');
		m_letters.add('Ｆ');
		m_letters.add('Ｇ');
		m_letters.add('Ｈ');
		m_letters.add('Ｉ');
		m_letters.add('Ｊ');
		m_letters.add('Ｋ');
		m_letters.add('Ｌ');
		m_letters.add('Ｍ');
		m_letters.add('Ｎ');
		m_letters.add('Ｏ');
		m_letters.add('Ｐ');
		m_letters.add('Ｑ');
		m_letters.add('Ｒ');
		m_letters.add('Ｓ');
		m_letters.add('Ｔ');
		m_letters.add('Ｕ');
		m_letters.add('Ｖ');
		m_letters.add('Ｗ');
		m_letters.add('Ｘ');
		m_letters.add('Ｙ');
		m_letters.add('Ｚ');

		m_punctuation = new HashSet<Character>();
		m_punctuation.add('!');
		m_punctuation.add('！');
		m_punctuation.add('"');
		m_punctuation.add('＂');
		m_punctuation.add('#');
		m_punctuation.add('＃');
		m_punctuation.add('$');
		m_punctuation.add('＄');
		m_punctuation.add('%');
		m_punctuation.add('％');
		m_punctuation.add('&');
		m_punctuation.add('＆');
		m_punctuation.add('\'');
		m_punctuation.add('＇');
		m_punctuation.add('(');
		m_punctuation.add('（');
		m_punctuation.add(')');
		m_punctuation.add('）');
		m_punctuation.add('*');
		m_punctuation.add('＊');
		m_punctuation.add('+');
		m_punctuation.add('＋');
		m_punctuation.add(',');
		m_punctuation.add('，');
		m_punctuation.add('-');
		m_punctuation.add('－');
		m_punctuation.add('.');
		m_punctuation.add('．');
		m_punctuation.add('/');
		m_punctuation.add('／');
		m_punctuation.add(':');
		m_punctuation.add('：');
		m_punctuation.add(';');
		m_punctuation.add('；');
		m_punctuation.add('<');
		m_punctuation.add('＜');
		m_punctuation.add('=');
		m_punctuation.add('＝');
		m_punctuation.add('>');
		m_punctuation.add('＞');
		m_punctuation.add('?');
		m_punctuation.add('？');
		m_punctuation.add('@');
		m_punctuation.add('＠');
		m_punctuation.add('[');
		m_punctuation.add('［');
		m_punctuation.add('\\');
		m_punctuation.add('＼');
		m_punctuation.add(']');
		m_punctuation.add('］');
		m_punctuation.add('^');
		m_punctuation.add('＾');
		m_punctuation.add('_');
		m_punctuation.add('＿');
		m_punctuation.add('`');
		m_punctuation.add('｀');
		m_punctuation.add('{');
		m_punctuation.add('｛');
		m_punctuation.add('|');
		m_punctuation.add('｜');
		m_punctuation.add('}');
		m_punctuation.add('｝');
		m_punctuation.add('~');
		m_punctuation.add('～');
		
		m_punctuation.add('！');
		m_punctuation.add('！');
		m_punctuation.add('“');
		m_punctuation.add('“');
		m_punctuation.add('#');
		m_punctuation.add('＃');
		m_punctuation.add('￥');
		m_punctuation.add('￥');
		m_punctuation.add('%');
		m_punctuation.add('％');
		m_punctuation.add('&');
		m_punctuation.add('＆');
		m_punctuation.add('‘');
		m_punctuation.add('’');
		m_punctuation.add('（');
		m_punctuation.add('（');
		m_punctuation.add('）');
		m_punctuation.add('）');
		m_punctuation.add('*');
		m_punctuation.add('×');
		m_punctuation.add('+');
		m_punctuation.add('＋');
		m_punctuation.add('，');
		m_punctuation.add('，');
		m_punctuation.add('-');
		m_punctuation.add('－');
		m_punctuation.add('。');
		m_punctuation.add('。');
		m_punctuation.add('、');
		m_punctuation.add('＼');
		m_punctuation.add('：');
		m_punctuation.add('：');
		m_punctuation.add('；');
		m_punctuation.add('；');
		m_punctuation.add('《');
		m_punctuation.add('《');
		m_punctuation.add('=');
		m_punctuation.add('＝');
		m_punctuation.add('》');
		m_punctuation.add('》');
		m_punctuation.add('？');
		m_punctuation.add('？');
		m_punctuation.add('@');
		m_punctuation.add('＠');
		m_punctuation.add('【');
		m_punctuation.add('【');
		m_punctuation.add('】');
		m_punctuation.add('】');
		m_punctuation.add('…');
		m_punctuation.add('…');
		m_punctuation.add('—');
		m_punctuation.add('—');
		m_punctuation.add('·');
		m_punctuation.add('·');
		m_punctuation.add('{');
		m_punctuation.add('|');
		m_punctuation.add('｜');
		m_punctuation.add('}');
		m_punctuation.add('｝');
		m_punctuation.add('~');
		m_punctuation.add('～');
		m_punctuation.add('”');
		m_punctuation.add('”');
		m_punctuation.add('「');
	}
	
}
