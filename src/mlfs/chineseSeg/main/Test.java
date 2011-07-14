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

import java.io.IOException;
import java.util.List;

import mlfs.chineseSeg.corpus.Parser;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Test {

	public static void main(String[] args) throws IOException {
		
		CRFModel model = CRFModel.load("CRF.model");
		Parser parser = new Parser(model);
		String sentence = "中国";
		CRFEvent e = parser.parseEvent(sentence);
		List<String> labels = model.label(e);
		for (int i=0; i<labels.size(); i++)
			System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
		
	}
}
