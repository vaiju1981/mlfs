/*
 * Test.java
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
import java.text.ParseException;
import java.util.ArrayList;
import mlfs.pos.corpus.CorpusReader;
import mlfs.pos.corpus.WordTag;
import mlfs.pos.hmm.model.HMMTagger;
import mlfs.pos.hmm.model.Model;
import mlfs.pos.hmm.word.IVWord;

public class Test {
	
	public static void main(String[] args) throws ParseException, IOException
	{
		if (args.length != 3)
		{
			System.out.println("test.txt lexicon.txt ngram.txt");
			System.exit(-1);
		}
		int ivT = 0;
		int oovT = 0;
		int ivF = 0;
		int oovF = 0;
		
		Model model = new Model(args[1], args[2]);
		model.loadModel();
		IVWord ivword = model.getIVWord();
		HMMTagger tagger = new HMMTagger(model);
		
		CorpusReader corpus = new CorpusReader(args[0]);
		ArrayList<WordTag> sentence = null;
		while ((sentence = corpus.getSequence()) != null)
		{
			ArrayList<String> words = new ArrayList<String>();
			ArrayList<String> ans = new ArrayList<String>();
			
			for (WordTag wt : sentence)
			{
				words.add(wt.getWord());
				ans.add(wt.getTag());
			}
			ArrayList<String> res = tagger.viterbi(words);
			for (int i=0; i<res.size(); i++)
			{
				if (res.get(i).equals(ans.get(i+2)))
				{
					if (ivword.isIVWord(words.get(i+2)))
						ivT++;
					else
						oovT++;
				}
				else
				{
					if (ivword.isIVWord(words.get(i+2)))
						ivF++;
					else
						oovF++;
				}
			}
		}
		
		System.out.println("ivT = " + ivT + " ivF = " + ivF + " percentage : " + 1.0*ivT/(ivT+ivF));
		System.out.println("oovT = " + oovT + " oovF = " + oovF + " percentage : " + 1.0*oovT/(oovT+oovF));
		System.out.println("T = " + (ivT+oovT) + " F = " + (ivF+oovF) + " percentage : " + 1.0*(ivT+oovT)/(ivT+ivF+oovT+oovF));
	}
}

