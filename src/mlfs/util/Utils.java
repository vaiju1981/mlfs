/*
 * Utils.java 
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
 * Last Update:Aug 21, 2011
 * 
 */
package mlfs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static double docProduct(double[] v1, double[] v2)
	{
		if (v1.length != v2.length)
			throw new IllegalArgumentException("the len of v1 is different from len of v2 !");
		
		double res = 0.0;
		for (int i=0; i<v1.length; i++)
			res += v1[i] * v2[i];
		
		return res;
	}
	
	public static List<String> getAllLines(String path) throws IOException
	{
		List<String> ret = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		String line = null;
		while ((line = in.readLine()) != null)
			ret.add(line);
		
		in.close();
		return ret;
	}
	
	/**
	 * log求和
	 * 假设a和b分别是x和y的log值，即a=log(x)且b=log(y)
	 * 返回结果是log(x+y)即log(exp(a) + exp(b)).
	 *
	 * @param a the a
	 * @param b the b
	 * @param flg 如果flag为true，则直接返回b的值，否则返回相应计算值
	 * @return the double
	 */
	public static double logSum(double a, double b, boolean flg)
	{
		if (flg)
			return b;
		double max, min;
		if (a > b)
		{
			max = a;
			min = b;
		}
		else
		{
			max = b;
			min = a;
		}
		
		if (max > min+50)
			return max;
		
		return max + Math.log(1.0 + Math.exp(min-max));
	}
}

