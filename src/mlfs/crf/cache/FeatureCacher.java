package mlfs.crf.cache;

import java.util.ArrayList;
import java.util.List;

public class FeatureCacher {

	private List<List<Integer>> m_featureCache;
	
	private static FeatureCacher m_cacher;
	
	private FeatureCacher()
	{
		m_featureCache = new ArrayList<List<Integer>>();
	}
	
	public static FeatureCacher getInstance()
	{
		if (m_cacher == null) {
			m_cacher = new FeatureCacher();
		}
		return m_cacher;
	}
	
	public void add(List<Integer> lst)
	{
		m_featureCache.add(lst);
	}
	
	public List<Integer> getFeats(int pos)
	{
		return m_featureCache.get(pos);
	}
	
	public int size()
	{
		return m_featureCache.size();
	}
	
	public void clear()
	{
		m_featureCache.clear();
	}
}
