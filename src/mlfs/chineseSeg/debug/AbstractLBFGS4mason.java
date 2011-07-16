/*
 * AbstractLBFGS.java 
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
 * Last Update:Jun 26, 2011
 * 
 */
package mlfs.chineseSeg.debug;

import riso.numerical.LBFGS;

/**
 * The Class AbstractLBFGS.
 * 
 * 包装L-BFGS的细节，调用者只须指定给定x如何计算优化函数的值以及导数即可
 * 注意：L-BFGS用于求解优化函数的最小值！使用这个类前，确定自己要做的是最小化目标函数
 * 
 * 注意：AbstractLBFGS保证在getsolution方法中每次迭代先调用calFunctionVal(x)方法，后调用calGradientVal(x, g)
 */
public abstract class AbstractLBFGS4mason {

	/** 维度. */
	protected int m_dimension;
	
	/** 用于逼近的向量个数. */
	private int m_m;
	
	private int m_numIter = 200;
	
	/**
	 * Instantiates a new abstract lbfgs.
	 *
	 * @param dimetion the dimetion
	 */
	public AbstractLBFGS4mason(int dimetion)
	{
		this.m_dimension = dimetion;
		this.m_m = 5;
	}
	
	
	/**
	 * Instantiates a new abstract lbfgs.
	 *
	 * @param dimetion the dimetion
	 * @param m the m
	 */
	public AbstractLBFGS4mason(int dimetion, int m, int numIter)
	{
		this.m_dimension = dimetion;
		this.m_m = m;
		this.m_numIter = numIter;
	}
	
	/**
	 * Gets the solution.
	 *
	 * @param x 保存解向量
	 * @return 求解结束最后一轮的函数值
	 */
	public double getSolution(double[] x)
	{
		if (x==null || x.length != m_dimension)
			throw new IllegalArgumentException("向量x为null或长度不等于给定维度");
		
		double[] g = new double[m_dimension];
		double[] diag= new double[m_dimension];
		double[] w = null;
		
		int n = m_dimension;
		int m = m_m;
		
		int[] iprint = new int[2];
		iprint[0] = 1;
		iprint[1] = 0;
		
		boolean diagco= false;
		double eps= 1.0e-7;
		double xtol= 1.0e-16;
		int icall=0;
		int[] iflag = new int[1];
		iflag[0]=0;
		
		//init x[]
		for (int i=0; i<n; i++)
			x[i] = 0.000;
		
		double f = 0.0;
		do
		{
			//calc function f given vector x
			f = calFunctionVal(x);
			
			//calc gradient given vector x
			calGradientVal(x, g);
			
			try
			{
				LBFGS.lbfgs ( n , m , x , f , g , diagco , diag , iprint , eps , xtol , iflag );
			}
			catch (LBFGS.ExceptionWithIflag e)
			{
				System.err.println( "Sdrive: lbfgs failed.\n"+e );
				System.exit(-1);
			}

			icall += 1;
		}
		while ( iflag[0] != 0 && icall <= m_numIter );
		return f;
	}
	
	/**
	 * 给定向量x，求解目标函数值
	 *
	 * @param x the x
	 * @return the double
	 */
	public abstract double  calFunctionVal(double[] x) ;
	
	/**
	 * 给定向量x，求解对应梯度值
	 *
	 * @param x the x
	 * @param g the g
	 */
	public abstract void  calGradientVal(double[] x, double[] g) ;
	
}
