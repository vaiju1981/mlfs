///*
// * CorpusReader.java 
// * 
// * Author : 罗磊，luoleicn@gmail.com
// * 
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * Last Update:Jun 22, 2011
// * 
// */
//package mlfs.chineseSeg.corpus;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import mlfs.crf.model.Event;
//
//public class CorpusReaderImpl {
//
//	private String m_path;
//	private Map<String, Integer> m_dict;
//	private int m_wordCount;
//	private Map<String, Integer> m_labelMap;
//	
//	public CorpusReaderImpl(String path)
//	{
//		this.m_path = path;
//		this.m_dict = new HashMap<String, Integer>();
//		this.m_wordCount = 0;
//		
//		m_labelMap = new HashMap<String, Integer>();
//		m_labelMap.put("B", 1);
//		m_labelMap.put("M", 2);
//		m_labelMap.put("E", 3);
//		m_labelMap.put("S", 4);
//	}
//	
//	public List<Event> getAllEvents() throws IOException
//	{
//		List<Event> events = new ArrayList<Event>();
//		
//		BufferedReader in  = new BufferedReader(new FileReader(new File(m_path)));
//		String line = null;
//		List<Integer> labels = new ArrayList<Integer>();
//		List<Integer> predicates = new ArrayList<Integer>();
//		while ((line = in.readLine()) != null)
//		{
//			if (line.equals(""))
//			{
//				int[] ws = new int[predicates.size()];
//				int[] ls  = new int[labels.size()];
//				for (int i=0; i<predicates.size(); i++)
//				{
//					ws[i] = predicates.get(i);
//					ls[i] = labels.get(i);
//				}
//				
//				Event e = new Event(ls, ws);
//				events.add(e);
//				
//				predicates.clear();
//				labels.clear();
//			}
//			else
//			{
//				String[] vec = line.split("\\s+");
//				String word = vec[0];
//				String label = vec[1];
//				
//				Integer wid = m_dict.get(word);
//				if (wid == null)
//				{
//					wid = m_wordCount;
//					m_dict.put(word, wid);
//					m_wordCount++;
//				}
//				predicates.add(wid);
//				labels.add(m_labelMap.get(label));
//			}
//			
//		}
//		
//		in.close();
//		return events;
//	}
//	
//	public Map<String, Integer> getTrainDict()
//	{
//		return m_dict;
//	}
//	
//	public Map<String, Integer> getLabelMap()
//	{
//		return m_labelMap;
//	}
//}
