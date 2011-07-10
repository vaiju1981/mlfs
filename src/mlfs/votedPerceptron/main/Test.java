package mlfs.votedPerceptron.main;

import java.io.IOException;
import java.util.List;

import mlfs.votedPerceptron.corpus.CorpusReader;
import mlfs.votedPerceptron.model.VPEvent;
import mlfs.votedPerceptron.model.VotedPerceptronModel;

public class Test {
	
	public static void main(String[] args) throws IOException {
		
		System.out.println("Loading model...");
		VotedPerceptronModel model = VotedPerceptronModel.load("votedpercetpron.model");
		
		int t = 0;
		int f = 0;
		System.out.println("loading all test events");
		CorpusReader reader = new CorpusReader("corpus/votedperceptron/a1a.t");
		System.out.println("Predict...");
		List<VPEvent> events = reader.getAllEvent();
		for (VPEvent e : events)
		{
			int label = model.predict(e);
			if (label == e.label)
				t++;
			else
				f++;
		}
		
		System.out.println("" + 1.0*t/(t+f));	
	}

}
