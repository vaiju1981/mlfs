package mlfs.svm;

import java.util.List;

import mlfs.kernel.KERNEL_TYPE;
import mlfs.svm.model.SVMEvent;
import mlfs.svm.model.SVMModel;

public class SVMTrainer {

	private int m_numClz;
	private List<SVMEvent> m_events;
	private SVM_TYPE m_svmType;
	private KERNEL_TYPE m_kernelType;
	
	public SVMTrainer(List<SVMEvent> events, int numClz, SVM_TYPE svmType, KERNEL_TYPE kernelType)
	{
		this.m_events = events;
		this.m_numClz = numClz;
		this.m_svmType = svmType;
		this.m_kernelType = kernelType;
		
		if (m_numClz != 2)
			throw new IllegalArgumentException("For now, MLFS SVM support 2 classes classify only!");
	}
	
	public SVMModel train()
	{
		SVMModel model = null;
		if (m_svmType == SVM_TYPE.C_SVC)
			;
		
		return model;
	}
	
}
