package mlfs.chineseSeg.main;


import java.io.IOException;
import java.util.List;

import mlfs.chineseSeg.corpus.CorpusProcessing;
import mlfs.chineseSeg.corpus.Utils;
import mlfs.crf.CRFGISTrainer;
import mlfs.crf.CRFLBFGSTrainer;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Train {

	public static void main(String[] args) throws IOException
	{
//		CorpusProcessing processing = new CorpusProcessing("corpus/chineseSegment/pku_training.utf8");
//		processing.buildTrainFile();
//		processing = null;
		
		TemplateHandler template = new TemplateHandler("chinese_segment_feature_template.txt");
		
		CorpusReader corpus = new CorpusReader("CHINESE_SEGMENT_CRF.train");
		List<CRFEvent> events = corpus.getAllEvents();
		
		Features featuresHandle = new Features(template, corpus.getTagMap());
		featuresHandle.statisticFeat(events);
		
//		CRFLBFGSTrainer trainer = new CRFLBFGSTrainer(events, featuresHandle) ;
		
		CRFGISTrainer trainer = new CRFGISTrainer(events, featuresHandle);
		
		CRFModel model = trainer.train();
		
		Utils utils = new Utils(model.getCharFeat());
		
//		String sentence = "迈向充满希望的新世纪——一九九八年新年讲话（附图片１张）";
		String sentence = "共同创造美好的新世纪——二○○一年新年贺词";
		CRFEvent e = utils.parseEvent(sentence);
		
		List<String> labels = model.label(e);
		
		for (int i=0; i<labels.size(); i++)
		{
			System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
		}
	}
}
