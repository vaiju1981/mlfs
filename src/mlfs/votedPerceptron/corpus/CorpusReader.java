package mlfs.votedPerceptron.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mlfs.util.Utils;
import mlfs.votedPerceptron.model.VPEvent;

public class CorpusReader {

	private String m_path;
	
	private int m_numFeat;
	
	public CorpusReader(String path)
	{
		this.m_path = path;
	}
	
	public List<VPEvent> getAllEvent() throws IOException
	{
		List<VPEvent> ret = new ArrayList<VPEvent>();
		
		List<String> lines = Utils.getAllLines(m_path);
		
		for (String line : lines)
		{
			String[] vec = line.split("\\s+");
			int label = 0;
			if (vec[0].equals("+1"))
				label = 1;
			else
				label = Integer.parseInt(vec[0]);
			
			int[] predicates = new int[vec.length-1];
			double[] values = new double[vec.length-1];
			for (int idx=1; idx<vec.length; idx++)
			{
				String[] xv = vec[idx].split(":");
				
				predicates[idx-1] = Integer.parseInt(xv[0]);
				values[idx-1] = Double.parseDouble(xv[1]);
			}
			
			ret.add(new VPEvent(predicates, values, label));
		}
		return ret;
	}
}
