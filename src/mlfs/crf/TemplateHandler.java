/*
 * TemplateHandler.java 
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
package mlfs.crf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.RowColumn;
import mlfs.util.Utils;

/**
 * 处理用户手工编写的特征模板.
 */
public class TemplateHandler {
	
	/** 在每个序列前加入一个START标签. */
	private static String START = "START";
	
	/** 在每个序列后加入一个START标签. */
	private static String END = "END";
	
	/** 特征模板文件按的路径. */
	private final String m_path;
	
	/** unigram特征模板. */
	private List<List<RowColumn>> m_unigramPredList;
	
	/** bigram特征模板. */
	private List<List<RowColumn>> m_bigramPredList;
	
	/** 模板连接字符. */
	public static char PREDICATE_JOIN = '_';
	
	/**
	 * Instantiates a new template reader.
	 *
	 * @param path the path
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public TemplateHandler(String path) throws IOException
	{
		this.m_path = path;
		this.m_unigramPredList = new ArrayList<List<RowColumn>>();
		this.m_bigramPredList = new ArrayList<List<RowColumn>>();
		
		List<String> lines = Utils.getAllLines(path);
		read(lines);
	}
	
	/**
	 * 构造函数
	 *
	 * @param 给定特征模板的集合
	 */
	public TemplateHandler(List<String> lines)
	{
		this.m_path = null;
		this.m_unigramPredList = new ArrayList<List<RowColumn>>();
		this.m_bigramPredList = new ArrayList<List<RowColumn>>();
		
		read(lines);
	}
	/**
	 * 读取特征文件.
	 *
	 */
	private void read(List<String> lines)
	{
		Set<String> predSet = new HashSet<String>();
		
		for (String line : lines)
		{
			if (line.trim().length() == 0)//blank line
				continue;
			if (line.charAt(0) == '#')//comment
				continue;
			if (predSet.contains(line))//duplicate template
				continue;
			
			predSet.add(line);
			
			boolean isBigram = false;
			if (line.charAt(0) == 'U')
				line = line.substring(1).trim();
			else if (line.charAt(0) == 'B')
			{
				line = line.substring(1).trim();
				isBigram = true;
			}
			else
				throw new IllegalStateException("the template must be start with U or B");
			
			String[] rowcols = line.split("/");
			List<RowColumn> pred = new ArrayList<RowColumn>();
			for (String rowcol : rowcols)
			{
				String[] twodigit = rowcol.split(",");
				if (twodigit.length != 2)
					continue;
				
				int row = Integer.parseInt(twodigit[0]);
				int col = Integer.parseInt(twodigit[1]);
				pred.add(new RowColumn(row, col));
			}
			
			if (isBigram)
			{
				m_bigramPredList.add(pred);
			}
			else
			{
				m_unigramPredList.add(pred);
			}
			
		}
		
	}
	
	
	/**
	 * 根据给定的谓词模板，event以及event的中的坐标抽取谓词
	 *
	 * @param event the event
	 * @param idx 偏移量
	 * @param predList 谓词模板
	 * @return 抽取的谓词集合
	 */
	private List<String> predExtraction(CRFEvent event, int idx,  List<List<RowColumn>> predList)
	{
		List<String> predicates = new ArrayList<String>();
		
		for (List<RowColumn> onePredTemplate : predList)// for each feature template
		{
			StringBuilder sb = new StringBuilder();
			for (RowColumn rc : onePredTemplate)
			{
				int row = idx + rc.row;
				int col = rc.col;
				
				if (col == event.charFeat.get(0).size()-1)
					throw new IllegalStateException("You should never use the last column of train_file you have generated.");
				if (col > event.charFeat.get(0).size()-1)
					throw new ArrayIndexOutOfBoundsException("col = " + col);
				
				if (row <0)
					sb.append(rc.row).append(PREDICATE_JOIN).append(rc.col).append(PREDICATE_JOIN).append(START).append(row).append(PREDICATE_JOIN);
				else if (row > event.charFeat.size()-1)
					sb.append(rc.row).append(PREDICATE_JOIN).append(rc.col).append(PREDICATE_JOIN).append(END).append(row).append(PREDICATE_JOIN);
				else
					sb.append(rc.row).append(PREDICATE_JOIN).append(rc.col).append(PREDICATE_JOIN).append(event.charFeat.get(row).get(col)).append(PREDICATE_JOIN);
			}
			predicates.add(sb.toString());
		}
		return predicates;
	}
	
	/**
	 * Gets the unigram predicates.
	 *
	 * @param event the event
	 * @param idx the idx
	 * @return the unigram pred
	 */
	public List<String> getUnigramPred(CRFEvent event, int idx)
	{
		return predExtraction(event, idx, m_unigramPredList);
	}
	
	/**
	 * Gets the bigram predicates.
	 *
	 * @param event the event
	 * @param idx the idx
	 * @return the bigram pred
	 */
	public List<String> getBigramPred(CRFEvent event, int idx)
	{
		return predExtraction(event, idx, m_bigramPredList);
	}
	
	/**
	 * Gets the path.
	 *
	 * @return the path
	 */
	public String getPath()
	{
		return m_path;
	}
}
