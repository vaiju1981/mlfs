/*
 * Trainer.java 
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
 * Last Update:Jun 15, 2011
 * 
 */
package mlfs.textClassification.maxent.main;

import java.io.IOException;

import mlfs.maxent.GIS;
import mlfs.maxent.model.GISModel;
import mlfs.maxent.model.TrainDataHandler;
import mlfs.textClassification.corpus.CorpusReader;

public class Trainer {

	/**
	 * 展示了如何使用最大熵模型库，训练一个model
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException
	{
		CorpusReader corpus = new CorpusReader("corpus/textClassification/train.txt", 1, 2);
		TrainDataHandler handler = corpus.getTrainDataHadler();
		GIS gis = new GIS(handler, true);
		GISModel model = gis.train(100);
		
		model.save("maxent.model");
	}
}
