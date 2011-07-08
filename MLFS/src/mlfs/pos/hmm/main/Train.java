/*
 * Train.java
   *  
 * Author: 罗磊，luoleicn@gmail.com
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
 * Last Update:2011-6-11
   * 
   */
package mlfs.pos.hmm.main;

import java.io.IOException;
import java.util.ArrayList;

import mlfs.pos.corpus.CorpusReader;
import mlfs.pos.corpus.WordTag;
import mlfs.pos.hmm.model.Model;

/**
 * 从训练集中，进行统计，并保存统计信息
 */
public class Train {

	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.out.println("train.txt lexicon.txt ngrams.txt");
			System.exit(-1);
		}
		ArrayList<ArrayList<WordTag>> allData = new ArrayList<ArrayList<WordTag>>();
		
		CorpusReader corpus = new CorpusReader(args[0]);
		ArrayList<WordTag> sentence = null;
		while ((sentence = corpus.getSequence()) != null)
		{
			allData.add(sentence);
		}
		
		Model model = new Model(args[1], args[2]);
		model.saveModel(allData);
	}

}
