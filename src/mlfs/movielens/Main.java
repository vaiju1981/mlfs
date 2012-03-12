package mlfs.movielens;

import java.io.IOException;
import java.util.List;

import mlfs.svd.corpus.CorpusReader;
import mlfs.svd.models.SVDInstance;
import mlfs.svd.models.SVDModel;
import mlfs.svd.models.SVDTrainer;

public class Main {

	public static void main(String[] args) throws IOException {
		CorpusReader reader = new CorpusReader();
		
		System.out.println("Loading Training data...");
		List<SVDInstance> trainData = reader.readTrainData("corpus/ml-100k/all.train");
		
		SVDTrainer trainer = new SVDTrainer(trainData, reader.getNumUsers(), reader.getNumItems(), 150);
		System.out.println("Starting Training...");
		trainer.setLambda(1.0);
		trainer.train(100);
		
		SVDModel model = new SVDModel(reader.getUserMap(), reader.getItemMap(), trainer.getParameters());
		model.save("basic_svd.model");
		
		model = new SVDModel("basic_svd.model");
		double rmse = 0.0;
		System.out.println("Loading test data...");
		List<SVDInstance> testData = reader.readTestData("corpus/ml-100k/all.test");
		for (SVDInstance ins : testData) {
			double r = model.predict(ins);
			System.out.println(ins.rating + " : " + r);
			rmse += (ins.rating - r) * (ins.rating - r);
		}
		rmse = Math.sqrt(rmse/testData.size());
		System.out.println("RMSE : " + rmse);
	}
}
