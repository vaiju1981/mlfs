package mlfs.chineseSeg.main;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import mlfs.chineseSeg.corpus.Utils;
import mlfs.crf.CRFLBFGSTrainer;
import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Train {

	public static void main(String[] args) throws IOException
	{
//		CorpusProcessing processing = new CorpusProcessing("icwb2-data/training/pku_training.utf8");
//		processing.buildTrainFile();
//		processing = null;
		
		TemplateHandler template = new TemplateHandler("chinese_segment_feature_template.txt");
		
		CorpusReader corpus = new CorpusReader("CHINESE_SEGMENT_CRF.train");
		List<CRFEvent> events = corpus.getAllEvents();
		
		Features featuresHandle = new Features(template, corpus.getTagMap(), events);
		
		CRFLBFGSTrainer trainer = new CRFLBFGSTrainer(events, featuresHandle) ;
		
//		CRFGISTrainer trainer = new CRFGISTrainer(events, featuresHandle);
		
		CRFModel model = trainer.train();
		
		Utils utils = new Utils(model.getCharFeat());
		
//		String sentence = "迈向充满希望的新世纪——一九九八年新年讲话（附图片１张）";
//		String sentence = "中国人民进入新世纪的主要任务，就是继续推进现代化建设，完成祖国统一，维护世界和平与促进共同发展。";
		String sentence = "中国";
//		CRFEvent e = utils.parseEvent(sentence);
//		List<String> labels = model.label(e);
//		for (int i=0; i<labels.size(); i++)
//			System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
		
		BufferedReader in = new BufferedReader(new FileReader(new File("icwb2-data/testing/pku_test.utf8")));
		PrintWriter out = new PrintWriter(new File("out"));
		while ((sentence = in.readLine()) != null)
		{
			if (sentence.trim().length() == 0)
				continue;
			CRFEvent e = utils.parseEvent(sentence);
			
			List<String> labels = model.label(e);
			
			StringBuilder sb = new StringBuilder();
			for (int i=0; i<labels.size(); i++)
			{
				System.out.println(sentence.charAt(i)+"\t"+labels.get(i));
				if (labels.get(i).equals("B"))
					sb.append(sentence.charAt(i));
				else if (labels.get(i).equals("M"))
					sb.append(sentence.charAt(i));
				else if (labels.get(i).equals("E"))
					sb.append(sentence.charAt(i)).append(' ');
				else if (labels.get(i).equals("S"))
					sb.append(sentence.charAt(i)).append(' ');
			}
			
			out.println(sb.toString());
		}
		in.close();
		out.close();
	}
}
