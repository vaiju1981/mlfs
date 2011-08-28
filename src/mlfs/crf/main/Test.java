/*
 * Test.java 
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
 * Last Update:Jul 14, 2011
 * 
 */
package mlfs.crf.main;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import mlfs.crf.corpus.CorpusReader;
import mlfs.crf.model.CRFEvent;
import mlfs.crf.model.CRFModel;
import mlfs.crf.model.Path;

public class Test {

	public static void usage()
	{
		System.out.println("java -jar crfTester [options] modelfile testfile outputFile");
		System.out.println("options:");
		System.out.println("-n, -n=INT, get n-best results");
	}
	
	public static void main(String[] args) throws IOException {
		
		int nbest = 1;
		String modelFile = null;
		String testFile = null;
		String outFile = null;
		
		int argsPos = 0;
		while (argsPos < args.length)
		{
			if (args[argsPos].equals("-n"))
			{
				argsPos++;
				nbest = Integer.parseInt(args[argsPos]);
			}
			else if (modelFile == null)
				modelFile = args[argsPos];
			else if (testFile == null)
				testFile = args[argsPos];
			else if (outFile == null)
				outFile = args[argsPos];
			
			argsPos++;
		}
		if (modelFile==null || testFile==null || outFile==null || nbest<=0)
		{
			usage();
			System.exit(-1);
		}
		
		System.out.println("modelfile = " + modelFile + " testFile = " + testFile + " outFile = " + outFile + " n-best = " + nbest + "-best");
		System.out.println("Loading...");
		CRFModel model = CRFModel.load(modelFile);
		System.out.println("Tagging");
		
		CorpusReader corpus = new CorpusReader(testFile, model);
		List<CRFEvent> events = corpus.getAllTestEvents();
		
		PrintWriter out = new PrintWriter(new File(outFile));
		for (CRFEvent e : events)
		{
			if (nbest == 1)
			{
				List<String> labels = model.label(e);
				StringBuilder sb = new StringBuilder();
				for (int i=0; i<labels.size(); i++)
					sb.append(labels.get(i)).append(' ');
				out.println(sb.toString());
			}
			else //nbest>1
			{
				List<Path> paths = model.getNBest(e, nbest);
				for (Path path : paths)
				{
					out.println(path.toString());
				}
				out.println();
			}
		}
		out.close();
		System.out.println("Done!");
	}
}
