/*
 * GraphCacher.java 
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
package mlfs.crf.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class GraphCacher.
 * 图的边和节点的缓存类，保存若干node和edge对象，
 * 避免频繁调用node和edge构造函数和回收垃圾对象
 * 
 * 注意：此类为单例模式，如果改为多线程，应该让
 * 每个线程拥有一个cacher，而不是使用单例模式
 */
public class GraphCacher {

	/** The m_nodes. */
	private List<Node> m_nodes;
	
	private List<Edge> m_edges;
	
	private int m_nodePos;
	
	private int m_edgePos;
	
	private static GraphCacher m_cacher;
	
	private GraphCacher()
	{
		this.m_nodes = new ArrayList<Node>();
		this.m_edges = new ArrayList<Edge>();
		this.m_nodePos = 0;
		this.m_edgePos = 0;
	}
	
	public static GraphCacher getInstance()
	{
		if (m_cacher == null)
			m_cacher = new GraphCacher();

		m_cacher.clear();
		return m_cacher;
	}
	
	public void clear()
	{
		this.m_nodePos = this.m_edgePos = 0;
	}

	public Node getNode()
	{
		if (m_nodePos == m_nodes.size()) 
			m_nodes.add(new Node(-1, -1, -1));
		
		Node node = m_nodes.get(m_nodePos++);
		return node;
	}

	public Edge getEdge()
	{
		if (m_edgePos == m_edges.size())
			m_edges.add(new Edge(null, null));
		
		Edge e = m_edges.get(m_edgePos++);
		return e;
	}
}
