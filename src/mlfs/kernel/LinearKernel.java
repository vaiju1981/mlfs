/*
 * LinearKernel.java 
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
package mlfs.kernel;

public class LinearKernel implements Kernel{

	@Override
	public double func(double[] x1, double[] x2) {
		
		if (x1.length != x2.length)
			throw new IllegalArgumentException("len of x1 is not equal to len of x2");
		
		double res = 0.0;
		for (int i=x1.length-1; i>=0; i--)
			res += x1[i] * x2[i];
		return res;
	}

	@Override
	public double func(double[] x1, int[] idx1, double[] x2, int[] idx2) {
		
		double res = 0.0;
		int pos1 = 0, pos2 = 0;
		while ((pos1<idx1.length) && (pos2<idx2.length))
		{
			if (idx1[pos1] == idx2[pos2])
			{
				res += x1[pos1] * x2[pos2];
				pos1++;
				pos2++;
			}
			else if (idx1[pos1] < idx2[pos2])
			{
				pos1++;
			}
			else
			{
				pos2++;
			}
		}
		
		return res;
	}
	
}
