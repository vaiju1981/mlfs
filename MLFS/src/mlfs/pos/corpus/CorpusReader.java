/*
 * CorpusReader.java
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * The Class CorpusReader.
 * 语料的格式是：
 * num1 word1 	tag1
 * num2 word2 	tag2
 * 每句话用空行隔开
 */
public class CorpusReader {

	private BufferedReader m_reader;
	
	/**
	 * 构造函数
	 *
	 * @param filePath 训练语料的路径
	 * @throws FileNotFoundException the file not found exception
	 */
	public CorpusReader(String filePath) throws FileNotFoundException
	{
		m_reader = new BufferedReader(new FileReader(new File(filePath)));
	}
	
	/**
	 * 从语料库中读取一句话的分词和词性标注。
	 * 
	 * 注意：这里会自动给每句话前面加上两个<Start>，后面加上一个<End>
	 *
	 * @return 从语料库中读出的一句话以及对应的词性标注
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ArrayList<WordTag> getSequence() throws IOException
	{
		ArrayList<WordTag> sentence = new ArrayList<WordTag>();
		sentence.add(new WordTag("<Start>", "<Start>"));
		sentence.add(new WordTag("<Start>", "<Start>"));
		
		String line = null;
		while ((line = m_reader.readLine()) != null && line.trim().length() != 0)
		{
			String[] vec = line.split("\\s+");
			sentence.add(new WordTag(vec[1], vec[2]));		
		}
		sentence.add(new WordTag("<End>", "<End>"));
		
		if (sentence.size() == 3)
			return null;
		return sentence;
	}
}
