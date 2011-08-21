package mlfs.svm;

import java.util.List;

import mlfs.kernel.KERNEL_TYPE;
import mlfs.svm.model.SVMEvent;
import mlfs.svm.model.SVMModel;

public class C_SVC_Solver {

	private List<SVMEvent> m_events;
	private KERNEL_TYPE m_kernelType;
	
	public C_SVC_Solver(List<SVMEvent> events, KERNEL_TYPE kernelType)
	{
		this.m_events = events;
		this.m_kernelType = kernelType;
	}
	
	public SVMModel solve()
	{
		return null;
	}
}
