package mlfs.textClassification.corpus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mlfs.maxent.model.Event;

public class TestCorpusReader {
	
	private BufferedReader m_reader;
	
	private Set<Integer> m_dict;
	
	public TestCorpusReader(String filePath, Set<Integer> set) throws FileNotFoundException
	{
		m_reader = new BufferedReader(new FileReader(new File(filePath)));
		m_dict = set;
	}
	
	
	public Event getEvent() throws IOException
	{
		String line = m_reader.readLine();
		if (line == null)
			return null;
		
		String[] splits = line.split("\\s+");
		int label = Integer.parseInt(splits[0]);
		ArrayList<Integer> preds = new ArrayList<Integer>();
		ArrayList<Integer> counts= new ArrayList<Integer>();
		for (int i=1; i<splits.length; i++)
		{
			String[] wordcount = splits[i].split(":");
			int word = Integer.parseInt(wordcount[0]);
			int count= Integer.parseInt(wordcount[1]);
			
			if (!m_dict.contains(word))
				continue;
			
			preds.add(word);
			counts.add(count);
		}
		
		int[] predcates = new int[preds.size()];
		int[] values = new int[counts.size()];
		for (int i=0; i<preds.size(); i++)
		{
			predcates[i] = preds.get(i);
			values[i] = counts.get(i);
		}
		
		return new Event(label, predcates, values);
	}
	
	public void close() throws IOException
	{
		m_reader.close();
	}
}
