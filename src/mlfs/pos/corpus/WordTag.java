/*
 * WordTag.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 3, 2011
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
