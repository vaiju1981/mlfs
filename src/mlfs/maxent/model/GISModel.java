///*
// * GISModel.java 
// * 
// * Author : 罗磊，luoleicn@gmail.com
// * 
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// * 
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// * 
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// * 
// * Last Update:Jun 15, 2011
// * 
// */
//package mlfs.maxent.model;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.ObjectInputStream;
//import java.io.ObjectOutputStream;
//import java.io.Serializable;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * The Class GISModel.
// * 采用GIS训练方式的训练模型，
// * 模型能够：
// * 1、保存参数信息
// * 2、进行分类
// */
//public class GISModel extends MEModel implements Serializable {
//	
//	/** The Constant serialVersionUID. */
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * Instantiates a new gIS model.
//	 *
//	 * @param parameters the parameters
//	 * @param numPreds the num preds
//	 * @param numLabels the num labels
//	 * @param predicates the predicates
//	 * @param labels the labels
//	 */
//	public GISModel(double[][] parameters, int numPreds, int numLabels, HashSet<Integer> predicates, HashSet<Integer> labels)
//	{
//		super(parameters, numPreds, numLabels, predicates, labels);
//	}
//	
//	/**
//	 * Instantiates a new gIS model.
//	 *
//	 * @param parameters the parameters
//	 * @param numLabels the num labels
//	 * @param predicates the predicates
//	 * @param labels the labels
//	 */
//	public GISModel(double[][] parameters, int numLabels, HashSet<Integer> predicates, HashSet<Integer> labels)
//	{
//		super(parameters, numLabels, predicates, labels);
//	}
//	
//	/**
//	 * Save.持久化当前模型
//	 *
//	 * @param path the path
//	 * @throws FileNotFoundException the file not found exception
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 */
//	public void save(String path) throws FileNotFoundException, IOException
//	{
//		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(path)));
//		oos.writeObject(this);
//		oos.close();
//	}
//	
//	/**
//	 * Load.从文件中加载当前模型
//	 *
//	 * @param path the path
//	 * @return the gIS model
//	 * @throws FileNotFoundException the file not found exception
//	 * @throws IOException Signals that an I/O exception has occurred.
//	 * @throws ClassNotFoundException the class not found exception
//	 */
//	public static GISModel load(String path) throws FileNotFoundException, IOException, ClassNotFoundException
//	{
//		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File(path)));
//		GISModel model = (GISModel)ois.readObject();
//		ois.close();
//		return model;
//	}
//
//	/**
//	 * Gets the preds.
//	 *
//	 * @return the preds
//	 */
//	public Set<Integer> getPreds()
//	{
//		return this.m_predicates;
//	}
//	
//}
