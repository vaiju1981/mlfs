/*
 * Test.java 
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
package mlfs.chineseSeg.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import mlfs.chineseSeg.corpus.Parser;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Test {

	public static void main(String[] args) throws IOException {
		
		System.out.println("Loading...");
		CRFModel model = CRFModel.load("CRF.model");
		System.out.println("Tagging");
		Parser parser = new Parser(model);
		String sentence = "2001年1月1日零时，随着新世纪钟声的响起，北京中华世纪坛礼花齐放，万民欢腾。";
//		CRFEvent e = parser.parseEvent(sentence);
//		List<String> labels = model.label(e);
//		for (int i=0; i<labels.size(); i++)
//			System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
		
		BufferedReader in = new BufferedReader(new FileReader(new File("corpus/chineseSegment/pku_test.utf8")));
		PrintWriter out = new PrintWriter(new File("out"));
		while ((sentence = in.readLine()) != null)
		{
			if (sentence.trim().length() == 0)
				continue;
			CRFEvent e = parser.parseEvent(sentence);
			
			List<String> labels = model.label(e);
			
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<labels.size(); i++)
			{
				System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
				if (labels.get(i).equals("B"))
					sb.append(sentence.charAt(i));
				else if (labels.get(i).equals("M"))
					sb.append(sentence.charAt(i));
				else if (labels.get(i).equals("E"))
					sb.append(sentence.charAt(i)).append(' ');
				else if (labels.get(i).equals("S"))
					sb.append(sentence.charAt(i)).append(' ');
			}
			
			out.println(sb.toString());
		}
		in.close();
		out.close();
	}
}
