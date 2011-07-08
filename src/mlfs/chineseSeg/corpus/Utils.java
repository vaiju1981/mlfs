package mlfs.chineseSeg.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mlfs.chineseSeg.crf.model.CHARACTER_FEATURE;
import mlfs.chineseSeg.crf.model.Resource;
import mlfs.crf.model.CRFEvent;

public class Utils {
	
	private Map<String, List<String>> CHAR_FEAT;
	private Resource m_resource;
	
	public Utils( Map<String, List<String>> charfeat)
	{
		CHAR_FEAT = charfeat;
		m_resource = Resource.getInstance();
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
				System.out.println(inputs[i]);
				int type;
				if (m_resource.isDigit(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.DIGIT.getValue();
				else if (m_resource.isChineseDigit(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.CHINESE_DIGIT.getValue();
				else if (m_resource.isPunctuation(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.PUNCTUATION.getValue();
				else if (m_resource.isLetter(inputs[i].charAt(0)))
					type = CHARACTER_FEATURE.LETTER.getValue();
				else 
					type = CHARACTER_FEATURE.OTHERS.getValue();
				
				List<String> common = new ArrayList<String>();
				common.add(inputs[i]);
				common.add(type+"");
				common.add("0");
				common.add("0");
				common.add("0");
				common.add("?");
				
				e.charFeat.add(common);
			}
		}
		
		return e;
	}
}
