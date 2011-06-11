package mlfs.textClassification.maxent.main;

import java.io.IOException;

import mlfs.textClassification.corpus.CorpusReader;

public class Trainer {

	public static void main(String[] args) throws IOException
	{
		CorpusReader corpus = new CorpusReader("corpus/textClassification/train.txt", 2, 50);
		corpus.getEvents();
	}
}
