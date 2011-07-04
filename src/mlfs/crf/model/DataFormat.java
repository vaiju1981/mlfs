///*
// * DataFormat.java 
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
//package mlfs.crf.model;
//
///**
// * The Class DataFormat.
// * 训练文件格式
// */
//public class DataFormat {
//
//	/** The letter. */
//	public final char letter;
//	
//	/** The features. */
//	public final int[] features;
//	
//	/** The tag. */
//	public final int tag;
//	
//	public DataFormat(char letter, int[] features, int tag)
//	{
//		this.letter = letter;
//		this.features = features;
//		this.tag = tag;
//	}
//	
//	@Override
//	public String toString()
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append(this.letter).append('\t');
//		for (int i=0; i<features.length; i++)
//			sb.append(features[i]).append("\t");
//		sb.append(this.tag);
//		
//		return sb.toString();
//	}
//}
