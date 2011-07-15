package mlfs.chineseSeg.main;


import java.io.IOException;
import java.util.List;

import mlfs.chineseSeg.corpus.CorpusProcessing;
import mlfs.crf.CRFLBFGSTrainer;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Train {

	public static void main(String[] args) throws IOException
	{
		//从train语料中统计特征，这里注释掉是因为已经统计过了，并生成CHINESE_SEGMENT_CRF.train，没必要反复统计
		CorpusProcessing processing = new CorpusProcessing("icwb2-data/training/pku_training.utf8");
		processing.buildTrainFile();
		processing = null;
		
		TemplateHandler template = new TemplateHandler("chinese_segment_feature_template.txt");
		
		CorpusReader corpus = new CorpusReader("CHINESE_SEGMENT_CRF.train");
		String crfmodel = "CRF.model";
		List<CRFEvent> events = corpus.getAllEvents();
		
		Features featuresHandle = new Features(template, corpus.getTagMap(), events, crfmodel);
		
		CRFLBFGSTrainer trainer = new CRFLBFGSTrainer(events, featuresHandle) ;
		
		CRFModel model = trainer.train();
		model.save(crfmodel);
	
	}
}
