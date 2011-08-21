package mlfs.svm;

public enum SVM_TYPE {

	C_SVC(1),
	NU_SVC(2),
	One_Class_SVM(3),
	Epsilon_SVR(4),
	Nu_SVR(5);
	
	int value;
	private SVM_TYPE(int v)
	{
		value = v;
	}
	
	public int getValue()
	{
		return value;
	}
}
