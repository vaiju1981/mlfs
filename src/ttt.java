import java.io.IOException;

import mlfs.maxent.GIS;
import mlfs.maxent.model.ComparableEvent;
import mlfs.maxent.model.Event;
import mlfs.maxent.model.GISModel;
import mlfs.maxent.model.TrainDataHandler;
import mlfs.textClassification.corpus.CorpusReader;
import mlfs.textClassification.corpus.TestCorpusReader;


public class ttt {

	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		CorpusReader corpus = new CorpusReader("corpus/textClassification/train.txt", 1, 2);
//		CorpusReader corpus = new CorpusReader("corpus/libsvmdata/train.txt", 0, 0);
		TrainDataHandler handler = corpus.getTrainDataHadler();
		GIS gis = new GIS(handler);
		GISModel model = gis.train(100);
		
		model.save("maxent.model");
		
		GISModel newModel = GISModel.load("maxent.model");
		
		int t=0, f=0;
		TestCorpusReader test = new TestCorpusReader("corpus/textClassification/train.txt", newModel.getPreds());
		Event e = null;
		while ((e = test.getEvent()) != null)
		{
			int label = model.label(e);
			if (label == e.m_label)
				t++;
			else
				f++;
			System.out.println(label + "->" + e.m_label + " Percentage : " + ((double)t/(t+f)));
		}
		test.close();
		
		
	}
}
