package mlfs.votedPerceptron.model;

import java.util.Arrays;

public class Perceptron {

	public double[] vector;
	public int correct;
	
	public Perceptron(double[] v, int c)
	{
		vector = Arrays.copyOf(v, v.length);
		correct = c;
	}
}
