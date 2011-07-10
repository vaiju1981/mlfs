package mlfs.votedPerceptron.main;

import java.io.IOException;
import java.util.List;

import mlfs.votedPerceptron.VPTrainer;
import mlfs.votedPerceptron.corpus.CorpusReader;
import mlfs.votedPerceptron.model.VPEvent;
import mlfs.votedPerceptron.model.VotedPerceptronModel;

public class Train {

	public static void main(String[] args) throws IOException {
		CorpusReader reader = new CorpusReader("corpus/votedperceptron/a1a.txt");
		List<VPEvent> events = reader.getAllEvent();
		
		VPTrainer trainer = new VPTrainer(events);
		VotedPerceptronModel model = trainer.train(1);
		
		System.out.println(model.getNumPerceptrons());
		model.save("votedpercetpron.model");
	}
}
