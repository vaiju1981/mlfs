package mlfs.maxent;

import java.util.HashSet;
import java.util.List;

import mlfs.maxent.model.MEEvent;
import mlfs.maxent.model.MEModel;
import mlfs.maxent.model.TrainDataHandler;

public abstract class METrainModel {

	/** 对于未见过的特征的平滑. */
	protected static double SMOOTH_SEEN = 0.1;

	/** 是否使用高斯平滑. */
	protected boolean USE_GAUSSIAN_SMOOTH = false;

	protected TrainDataHandler m_trainData;

	/** labels的总数. */
	protected int m_numLabels;

	/** 谓词的总数. */
	protected int m_numPredicates;

	/** train data中的event列表. */
	protected List<MEEvent> m_events;

	/** 观测期望，训练语料的最大似然估计期望. */
	protected double[][] m_observationExpection;

	/** 模型计算出的期望值. */
	protected double[][] m_modelExpection;

	/** 模型参数. */
	protected double[][] m_parameters;

	/** 谓词集合. */
	protected HashSet<Integer> m_predicates;

	/** label集合. */
	protected HashSet<Integer> m_labels;
	
	public METrainModel(TrainDataHandler handler)
	{
		this.m_trainData = handler;
		
		this.m_numPredicates = handler.getNumPredicates();
		this.m_numLabels = handler.getNumLabels();
		this.m_events = m_trainData.getEvents();
		
		this.m_predicates = m_trainData.getPredicates();
		this.m_labels = m_trainData.getLabels();
		
		this.USE_GAUSSIAN_SMOOTH = false;
	}
	
	/**
	 * Instantiates a new trainmodel.
	 * 使用高斯平滑会导致参数的求解无法使用解析解更新
	 * 只能使用牛顿法更新，进而导致训练速度减慢
	 * 一般来说会提高模型效果，但具体能否提高效果也要看实际应用
	 * 
	 * @param handler the handler
	 * @param useGaussianSmooth  是否使用高斯平滑
	 */
	public METrainModel(TrainDataHandler handler, boolean useGaussianSmooth)
	{
		this.m_trainData = handler;
		
		this.m_numPredicates = handler.getNumPredicates();
		this.m_numLabels = handler.getNumLabels();
		this.m_events = m_trainData.getEvents();
		
		this.m_predicates = m_trainData.getPredicates();
		this.m_labels = m_trainData.getLabels();
		
		this.USE_GAUSSIAN_SMOOTH = useGaussianSmooth;
	}
	
	public abstract MEModel train();
	/**
	 * 计算观测期望
	 */
	protected void calcObservationExpection()
	{
		for (MEEvent event : m_events)
		{
			for (int pid=0; pid<event.m_predicates.length; pid++)
			{
				int predicate = event.m_predicates[pid];
				m_observationExpection[predicate][event.m_label] += event.m_values[pid];
			}
		}
	}
	
	
}
