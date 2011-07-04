package mlfs.chineseSeg.corpus;

import java.util.List;
import java.util.Map;

import mlfs.chineseSeg.crf.model.CHARACTER_FEATURE;
import mlfs.crf.model.CRFEvent;

public class Utils {
	
	private Map<String, List<String>> CHAR_FEAT;
	
	public Utils( Map<String, List<String>> charfeat)
	{
		CHAR_FEAT = charfeat;
	}
	
	public CRFEvent parseEvent(String line)
	{
		char[] chars = line.toCharArray();
		String[] inputs = new String[chars.length];
		int[] labels = new int[chars.length];
		CRFEvent e = new CRFEvent(inputs, labels);
		
		List<String> feats  = null;
		for (int i=0; i<chars.length; i++)
		{
			inputs[i] = chars[i] + "";
			if (CHAR_FEAT.containsKey(inputs[i]))
			{
				feats = CHAR_FEAT.get(inputs[i]);
				e.addCharFeat(feats.toArray(new String[feats.size()]));
			}
			else
			{
				String[] common = new String[]{CHARACTER_FEATURE.OTHERS.getValue()+"", "0", "0", "0", "?"};
				e.addCharFeat(common);
			}
		}
		
		return e;
	}
}
