package mlfs.kernel;

public enum KERNEL_TYPE {

	Linear(1),
	RBF(2);
	
	int value;
	private KERNEL_TYPE(int v)
	{
		value = v;
	}
	
	public int getValue()
	{
		return value;
	}
}
