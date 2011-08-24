package mlfs.crf.main;


import java.io.IOException;
import java.util.List;

import mlfs.crf.CRFLBFGSTrainer;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Train {

	public static void main(String[] args) throws IOException
	{
		if (args.length != 3)
		{
			System.out.println("java -jar crfTrainer.jar templatefile trainfile model ");
			System.exit(-1);
		}
		String templateFile = args[0];
		String trainFile = args[1];
		String modelFle = args[2];
		
		TemplateHandler template = new TemplateHandler(templateFile);
		
		CorpusReader corpus = new CorpusReader(trainFile);
		List<CRFEvent> events = corpus.getAllTrainEvents();
		
		Features featuresHandle = new Features(template, corpus.getTagMap(), events, modelFle);
		
		CRFLBFGSTrainer trainer = new CRFLBFGSTrainer(events, featuresHandle) ;
		
		CRFModel model = trainer.train(1000);
		model.save(modelFle);
	
	}
}
