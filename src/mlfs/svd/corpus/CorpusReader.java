package mlfs.svd.corpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mlfs.svd.models.SVDInstance;
import mlfs.util.Utils;

public class CorpusReader {
	
	private Map<String, Integer> userMap;
	private Map<String, Integer> itemMap;
	
	public List<SVDInstance> readTrainData(String file) throws IOException {
		
		userMap = new HashMap<String, Integer>();
		itemMap = new HashMap<String, Integer>();
		
		List<SVDInstance> instances = new ArrayList<SVDInstance>();
		
		List<String> lines = Utils.getAllLines(file);
		
		for (String line : lines) {
			String[] splits = line.split("\\s+");
			String rawUid = splits[0];
			String rawIid = splits[1];
			
			SVDInstance ins = new SVDInstance();
			
			if (userMap.containsKey(rawUid)) {
				ins.userId = userMap.get(rawUid);
			}
			else {
				ins.userId = userMap.size();
				userMap.put(rawUid, ins.userId);
			}
			
			if (itemMap.containsKey(rawIid)) {
				ins.itemId = itemMap.get(rawIid);
			}
			else {
				ins.itemId = itemMap.size();
				itemMap.put(rawIid, ins.itemId);
			}
				
			ins.rating = Integer.parseInt(splits[2]);
			
			instances.add(ins);
		}
		
		return instances;
	}
	
	public List<SVDInstance> readTestData(String file) throws IOException {
		List<SVDInstance> instances = new ArrayList<SVDInstance>();
		
		List<String> lines = Utils.getAllLines(file);
		
		for (String line : lines) {
			String[] splits = line.split("\\s+");
			SVDInstance ins = new SVDInstance();
			ins.userId = Integer.parseInt(splits[0]);
			ins.itemId = Integer.parseInt(splits[1]);
			ins.rating = Integer.parseInt(splits[2]);
		
			instances.add(ins);
		}
		
		return instances;
	}
	
	public int getNumUsers() {
		
		return userMap.size();
	}
	
	public int getNumItems() {
		
		return itemMap.size();
	}


	public Map<String, Integer> getUserMap() {
		return userMap;
	}

	public Map<String, Integer> getItemMap() {
		return itemMap;
	}
	

}
