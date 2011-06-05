/*
 * Train.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This work is licensed under a Creative Commons Attribution 3.0 Unported License.
 * http://creativecommons.org/licenses/by/3.0/
 * 
 * Last Update:Jun 2, 2011
 * 
 */
package mlfs.pos.hmm.main;

import java.io.IOException;
import java.util.ArrayList;

import mlfs.pos.hmm.corpus.CorpusReader;
import mlfs.pos.hmm.corpus.WordTag;
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
