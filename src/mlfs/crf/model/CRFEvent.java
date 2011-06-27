package mlfs.crf.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CRFEvent {

	public final static Map<Character, List<String>> CHAR_FEAT = new HashMap<Character, List<String>>();
	
	public final String[] inputs;
	public final int[] labels;
	public final String[][] charFeat;
	
	public CRFEvent(String[] inputs, int[] labels)
	{
		if (labels.length != inputs.length)
			throw new IllegalArgumentException("labels.length != inputs.length");
		
		this.labels = labels;
		this.inputs = inputs;
		charFeat = new String[labels.length][];
	}
	
	public void addCharFeat(int index, String[] feats)
	{
		charFeat[index] = feats;
	}
	
}
