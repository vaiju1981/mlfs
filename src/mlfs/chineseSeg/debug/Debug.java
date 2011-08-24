/*
 * Debug.java 
 * 
 * Author : 罗磊，luoleicn@gmail.com
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Last Update:Jul 16, 2011
 * 
 */
package mlfs.chineseSeg.debug;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import mlfs.crf.Features;
import mlfs.crf.TemplateHandler;
import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;

public class Debug {
	
	public static void main(String[] args) throws IOException {
		
		if (args.length != 5)
		{
			System.out.println("java -jar crf_debug.jar templatefile trainfile devfile testfile numIter");
			System.exit(-1);
		}
		String templatePath = args[0];
		String trainPath = args[1];
		String devPath = args[2];
		String testPath = args[3];
		int numIter = Integer.parseInt(args[4]);
		
		String crfmodel = "CRF4MASON.model";
		
		TemplateHandler template = new TemplateHandler(templatePath);
		CorpusReader corpus = new CorpusReader(trainPath);
		List<CRFEvent> events = corpus.getAllTrainEvents();
		Features4mason featuresHandle = new Features4mason(template, corpus.getTagMap(), events, crfmodel);
		Map<String, Integer> featIdMap = featuresHandle.statisticFeat(events, crfmodel);
		List<CRFEvent> devEvents = corpus.getTrainEvents(devPath);
		List<CRFEvent> testEvents = corpus.getTrainEvents(testPath);
		
		int numTag = featuresHandle.getTagMap().size();
		for (CRFEvent e : devEvents)
			DebugHelper.saveToCache(e, numTag, template, featIdMap);
		for (CRFEvent e : testEvents)
			DebugHelper.saveToCache(e, numTag, template, featIdMap);
		
		CRFLBFGSTrainer4mason trainer = new CRFLBFGSTrainer4mason(events, devEvents, testEvents, featuresHandle) ;
		CRFModel model = trainer.train(numIter);
		model.save(crfmodel);
		
	}
	

}
