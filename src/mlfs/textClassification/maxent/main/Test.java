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
 * Last Update:Jun 15, 2011
 * 
 */
package mlfs.textClassification.maxent.main;

import java.io.IOException;

import mlfs.maxent.model.MEEvent;
import mlfs.maxent.model.MEModel;
import mlfs.textClassification.corpus.TestCorpusReader;

/**
 * The Class Test.
 */
public class Test {

	/**
	 * 展示了如何使用最大熵库以及建立好的model，进行分类测试
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws ClassNotFoundException the class not found exception
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		if (args.length != 2)
		{
			System.out.println("java -jar maxentTest.jar modelfile testfile");
			System.exit(-1);
		}
		
		MEModel newModel = MEModel.load(args[0]);
		
		int t=0, f=0;
		TestCorpusReader test = new TestCorpusReader(args[1], newModel.getPreds());
		MEEvent e = null;
		while ((e = test.getEvent()) != null)
		{
			int label = newModel.label(e);
			System.out.println(label);
			if (label == e.m_label)
				t++;
			else
				f++;
		}
		System.out.println(" Percentage : " + ((double)t/(t+f)));
		test.close();
	}
}
