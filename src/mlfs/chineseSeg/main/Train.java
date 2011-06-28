package mlfs.chineseSeg.main;


import java.io.IOException;
import java.util.List;

import mlfs.crf.CRFTrainer;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;

public class Train {

	public static void main(String[] args) throws IOException
	{
//		CorpusProcessing corpus = new CorpusProcessing("corpus/chineseSegment/pku_training.utf8");
//		corpus.work();
//		corpus = null;
		
		TemplateHandler template = new TemplateHandler("chinese_segment_feature_template.txt");
		
		CorpusReader corpus = new CorpusReader("CHINESE_SEGMENT_CRF.train");
		List<CRFEvent> events = corpus.getAllEvents();
		
		Features featuresHandle = new Features(template, corpus.getTagMap());
		featuresHandle.statisticFeat(events);
		
		CRFTrainer trainer = new CRFTrainer(events, featuresHandle) ;
		trainer.train();
	}
}
