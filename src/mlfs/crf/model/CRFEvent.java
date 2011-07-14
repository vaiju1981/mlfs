/*
 * CRFEvent.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Last Update:Jul 14, 2011
 * 
 */
package mlfs.crf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class CRFEvent.
 */
public class CRFEvent {

	//没有保存输入，是因为charFeat.get(i).get(0)即为第i个输入
	
	/** 标签. */
	public int[] labels;
	
	/** 每一个输入对应一个特征序列,由用户自己提供的特征文件读取出来. */
	public List<List<String>> charFeat;
	
	/** 在featureCacher中的偏移量. */
	public int FEATURE_CACHE_POS = -1;
	
	/**
	 * Instantiates a new cRF event.
	 *
	 * @param labels the labels
	 */
	public CRFEvent(int[] labels)
	{
		this.labels = labels;
		charFeat = new ArrayList<List<String>>();
	}
	
	/**
	 * 添加特征序列，如果输入不在CHAR_FEAT中，则向CHAR_FEAT加入.
	 *
	 * @param feats the feats
	 */
	public void addCharFeat(String[] feats)
	{
		List<String> featsLst = new ArrayList<String>();
		for (String s : feats)
			featsLst.add(s);
		
		charFeat.add(featsLst);
	}
	
}
