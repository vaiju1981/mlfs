package mlfs.svd.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import mlfs.util.Utils;

public class SVDModel {
	
	private Map<String, Integer> userMap;
	private Map<String, Integer> itemMap;
	
	private double[] parameters;
	
	private int numUsers;
	private int numItems;
	
	private int K;
	
	public SVDModel (Map<String, Integer> users, Map<String, Integer> items, double[] parameters) {
		this.userMap = users;
		this.itemMap = items;
		this.parameters = parameters;
		this.numUsers = users.size();
		this.numItems = items.size();
		
		this.K = parameters.length / (numUsers + numItems);
	}
	
	public SVDModel (String file) throws IOException { 
		
		List<String> lines = Utils.getAllLines(file);
		this.numUsers = Integer.parseInt(lines.get(0));
		this.numItems = Integer.parseInt(lines.get(1));
		this.K = Integer.parseInt(lines.get(2));
		
		this.userMap = new HashMap<String, Integer>();
		for (int i=0; i<numUsers; i++) {
			String[] splits = lines.get(3+i).split("\\s+");
			userMap.put(splits[0], Integer.parseInt(splits[1]));
		}
			
		this.itemMap = new HashMap<String, Integer>();
		for (int i=0; i<numItems; i++) {
			String[] splits = lines.get(3+numUsers+i).split("\\s+");
			itemMap.put(splits[0], Integer.parseInt(splits[1]));
		}
		
		this.parameters = new double[lines.size() - 3 - numUsers - numItems];
		for (int i=3+numUsers+numItems; i<lines.size(); i++)
			parameters[i-3 - numUsers - numItems] = Double.parseDouble(lines.get(i));
	}
	
	public double predict(SVDInstance ins) {
		int uid = userMap.get(""+ins.userId);
		int iid = itemMap.get(""+ins.itemId);
		
		int userIdx = uid * K;
		int itemIdx = (numUsers + iid) * K;
		
		double rating = 0.0;
		for (int i=0; i<K; i++) {
			rating += parameters[userIdx] * parameters[itemIdx];
			userIdx++;
			itemIdx++;
		}
		
		return rating;
	}
	
	public void save(String file) throws FileNotFoundException {
		PrintWriter out = new PrintWriter(new File(file));
		out.println(numUsers);
		out.println(numItems);
		out.println(K);
		for (Entry<String, Integer> user : userMap.entrySet())
			out.println(user.getKey() + "\t" + user.getValue());
		for (Entry<String, Integer> item : itemMap.entrySet())
			out.println(item.getKey() + "\t" + item.getValue());
		for (int i=0; i<parameters.length; i++)
			out.println(parameters[i]);
		
		out.close();
	}
	
}
