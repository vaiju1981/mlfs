/*
 * TemplateReader.java 
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
 * Last Update:Jun 24, 2011
 * 
 */
package mlfs.crf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.RowColumn;

/**
 * The Class TemplateReader.
 */
public class TemplateHandler {
	
	private Logger logger = Logger.getLogger(TemplateHandler.class.getName());
	private static String START = "START";
	private static String END = "END";
	private String m_path;
	
	private List<List<RowColumn>> m_unigramPredList;
	private List<List<RowColumn>> m_bigramPredList;
	
	
	/**
	 * Instantiates a new template reader.
	 *
	 * @param path the path
	 * @throws IOException 
	 */
	public TemplateHandler(String path) throws IOException
	{
		this.m_path = path;
		this.m_unigramPredList = new ArrayList<List<RowColumn>>();
		this.m_bigramPredList = new ArrayList<List<RowColumn>>();
		
		logger.info("Reading all templates...");
		read();
	}
	
	/**
	 * Read.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void read() throws IOException
	{
		Set<String> predSet = new HashSet<String>();
		BufferedReader reader = new BufferedReader(new FileReader(new File(m_path)));
		String line = null;
		while ((line = reader.readLine()) != null)
		{
			if (line.trim().length() == 0)//blank line
				continue;
			if (line.charAt(0) == '#')//comment
				continue;
			if (predSet.contains(line))//duplicate pred
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
		
		reader.close();
	}
	
	private List<String> predExtraction(CRFEvent event, int idx,  List<List<RowColumn>> predList)
	{
		List<String> predicates = new ArrayList<String>();
		
		for (List<RowColumn> onePredTemplate : predList)// for each feature template
		{
			StringBuilder sb = new StringBuilder();
			sb.append("");
			for (RowColumn rc : onePredTemplate)
			{
				int row = idx + rc.row;
				int col = rc.col;
				
				if (col == event.charFeat[0].length-1)
					throw new IllegalStateException("You should never use the last column of train_file you have generated.");
				if (col > event.charFeat[0].length-1)
					throw new ArrayIndexOutOfBoundsException("col = " + col);
				
				if (row <0)
					sb.append(START).append('_');
				else if (row > event.charFeat.length-1)
					sb.append(END).append('_');
				else
					sb.append(event.charFeat[row][col]).append('_');
			}
			predicates.add(sb.toString());
		}
		return predicates;
	}
	
	public List<String> getUnigramPred(CRFEvent event, int idx)
	{
		return predExtraction(event, idx, m_unigramPredList);
	}
	
	public List<String> getBigramPred(CRFEvent event, int idx)
	{
		return predExtraction(event, idx, m_bigramPredList);
	}
	
	
}
