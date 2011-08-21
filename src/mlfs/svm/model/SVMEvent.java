package mlfs.svm.model;

public class SVMEvent {

	public final int m_label;
	public final int[] m_index;
	public final double[] m_values;
	
	public SVMEvent(int label, int[] index, double[] values)
	{
		this.m_label = label;
		this.m_index = index;
		this.m_values = values;
	}
}
