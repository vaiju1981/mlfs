/*
 * CorpusReader.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 5, 2011
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
