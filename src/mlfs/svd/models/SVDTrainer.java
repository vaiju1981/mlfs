/*
 * SVDTrainer.java
 *  
 * Author: 罗磊，luoleicn@gmail.com
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
 * Last Update:2012-3-10
 * 
 */
package mlfs.svd.models;

import java.util.List;

import mlfs.numerical.AbstractLBFGS;

public class SVDTrainer {

	private List<SVDInstance> instances;
	private int numInstances;
	private int numUsers;
	private int numItems;
	private int K;
	private double lambda;
	private double[] parameters;
	private int numFeatures;

	/** 减少计算，记录中间变量. */
	private double[] deltaArray;

	public SVDTrainer(List<SVDInstance> ins, int numUsers, int numItems, int k) {

		this.numFeatures = (numUsers + numItems) * k;
		this.instances = ins;
		this.numInstances = ins.size();
		this.numUsers = numUsers;
		this.numItems = numItems;
		this.K = k;
		this.lambda = 0.5;

		this.deltaArray = new double[numInstances];
		this.parameters = new double[numFeatures];
	}

	public void train(int epochs) {
		
		for (int i=0; i<parameters.length; i++)
			parameters[i] = 0.1;
		
		LBFGS lbfgs = new LBFGS(numFeatures, 5, epochs);
		lbfgs.getSolution(parameters);
		
	}

	public void setLambda(double lambda) {
		this.lambda = lambda;
	}

	class LBFGS extends AbstractLBFGS {

		public LBFGS(int dimetion, int m, int numIter) {
			super(dimetion, m, numIter);
		}

		@Override
		public double calFunctionVal(double[] x) {
			double funVal = 0.0;

			int numUserFeatures = numUsers * K;
			for (int i = 0; i < numInstances; i++) {
				SVDInstance ins = instances.get(i);
				deltaArray[i] = ins.rating;
				deltaArray[i] -= dot(x, ins.userId * K, (ins.userId + 1) * K,
						numUserFeatures + ins.itemId * K, numUserFeatures
								+ (ins.itemId + 1) * K);

				funVal += deltaArray[i] * deltaArray[i];
			}

			double sum = 0.0;
			for (int i = 0; i < numFeatures; i++) {
					sum += x[i] * x[i];
			}
			
			funVal += 0.5 * lambda * sum;

			return funVal;
		}

		@Override
		public void calGradientVal(double[] x, double[] g) {

			int numUserFeatures = numUsers * K;
			
			for (int i=0; i<g.length; i++)
				g[i] = lambda * x[i];
			
			for (int i=0; i<numInstances; i++) {
				int uid = instances.get(i).userId;
				int iid = instances.get(i).itemId;
				
				for (int j=0; j<K; j++) {
					g[uid*K + j] -= 2 * deltaArray[i] * x[numUserFeatures + iid * K + j];
					g[numUserFeatures + iid*K + j] -= 2 * deltaArray[i] * x[uid * K + j];
				}
			}

		}

		private double dot(double[] x, int l, int m, int n, int o) {
			if (m - l != o - n)
				throw new IllegalArgumentException(
						"the length is not equal to each other");

			double ret = 0.0;
			for (int i = l, j = n; i < m; i++, j++) {
				ret += x[i] * x[j];
			}

			return ret;
		}
	}
	
	public double[] getParameters() {
		return this.parameters;
	}

}
