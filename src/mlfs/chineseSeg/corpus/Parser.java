package mlfs.chineseSeg.corpus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mlfs.chineseSeg.crf.model.CHARACTER_FEATURE;
import mlfs.chineseSeg.crf.model.Resource;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.cache.FeatureCacher;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Parser {
	
	private Map<String, List<String>> CHAR_FEAT;
	private Resource m_resource;
	private FeatureCacher m_cacher;
	private Map<String, Integer> m_featIdMap;
	private TemplateHandler m_template;
	private int m_numTag;
	
	public Parser(CRFModel model)
	{
		CHAR_FEAT = model.getCharFeat();
		m_resource = Resource.getInstance();
		m_cacher = FeatureCacher.getInstance();
		
		m_featIdMap = model.getFeatIdMap();
		m_template = new TemplateHandler(model.getTemplates());
		m_numTag = model.getTagNum();
	}
	
	public CRFEvent parseEvent(String line)
	{
		char[] chars = line.toCharArray();
		String[] inputs = new String[chars.length];
		int[] labels = new int[chars.length];
		CRFEvent e = new CRFEvent(labels);
		
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
		
		e.FEATURE_CACHE_POS = 0;
		m_cacher.clear();
		List<Integer> fvec = null;
		for (int i=0; i<chars.length; i++)//unigram
		{
			fvec = new ArrayList<Integer>();
			List<String> unigramPred = m_template.getUnigramPred(e, i);
			for (String predicate : unigramPred)
			{
				String unigramFeat = predicate;
				
				if (m_featIdMap.containsKey(unigramFeat))
				{
					fvec.add(m_featIdMap.get(unigramFeat));
				}
				
			}
			m_cacher.add(fvec);
		}
		for (int i=1; i<chars.length; i++)//bigram
		{
			List<String> bigramPred = m_template.getBigramPred(e, i);
			for (int preTag=0; preTag<m_numTag; preTag++)
			{
				fvec = new ArrayList<Integer>();
				for (String predicate : bigramPred)
				{
					String bigramFeat = predicate + Features.FEATURE_JOIN +preTag;
					
					if (m_featIdMap.containsKey(bigramFeat))
						fvec.add(m_featIdMap.get(bigramFeat));
				}
				m_cacher.add(fvec);
			}
		}
		
		return e;
	}
}
