/*
 * Kernel.java 
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

/**
 * The Interface Kernel.
 * 核方法的抽象接口
 */
public interface Kernel {

	/**
	 * 对两个相同维度的向量进行计算.
	 *
	 * @param x1 x1向量
	 * @param x2 x2向量
	 * @return 经过核运算后的结果
	 */
	public double func(double[] x1, double[] x2);
	
	/**
	 * 对两个稀疏向量进行核方法计算.
	 * 调用者负责保证idx1和idx2两个数组都是按照生序排列
	 *
	 * @param x1 向量1的非零值
	 * @param idx1 这些非零值对应的维度
	 * @param x2 向量2的非零值
	 * @param idx2 这些非零值对应的维度
	 * @return 经过核运算后的结果
	 */
	public double func(double[] x1, int[] idx1, double[] x2, int[] idx2);
}
