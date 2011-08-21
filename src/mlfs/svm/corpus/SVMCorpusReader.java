package mlfs.svm.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mlfs.svm.model.SVMEvent;
import mlfs.util.Utils;

public class SVMCorpusReader {
	
	private String m_filePath;
	private int m_numClz;
	
	public SVMCorpusReader(String filePath)
	{
		this.m_filePath = filePath;
		this.m_numClz = 0;
	}

	public List<SVMEvent> getEvents() throws IOException
	{
		List<SVMEvent> events = new ArrayList<SVMEvent>();
		List<String> lines = Utils.getAllLines(m_filePath);
		Set<String> allClz = new HashSet<String>();
		
		for (String line : lines)
		{
			String[] splits = line.split("\\s+");
			if (!allClz.contains(splits[0]))
			{
				allClz.add(splits[0]);
				m_numClz++;
			}
			
			int label = Integer.parseInt(splits[0]);
			int[] index = new int[splits.length-1];
			double[] values = new double[splits.length-1];
			for (int i=1; i<splits.length; i++)
			{
				String[] idxVal = splits[i].split(":");
				index[i-1] = Integer.parseInt(idxVal[0]);
				values[i-1] = Double.parseDouble(idxVal[1]);
			}
			events.add(new SVMEvent(label, index, values));
		}
		return events;
	}
	
	public int getNumClz()
	{
		return m_numClz;
	}
}
