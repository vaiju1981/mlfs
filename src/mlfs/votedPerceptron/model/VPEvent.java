package mlfs.votedPerceptron.model;

public class VPEvent {
	
	public final int[] predicates;
	public final double[] values;
	
	public final int label;
	
	public VPEvent(int[] preds, double[] v, int l)
	{
		predicates = preds;
		values = v;
		label = l;
	}

}
