package mlfs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static double docProduct(double[] v1, double[] v2)
	{
		if (v1.length != v2.length)
			throw new IllegalArgumentException("the len of v1 is different from len of v2 !");
		
		double res = 0.0;
		for (int i=0; i<v1.length; i++)
			res += v1[i] * v2[i];
		
		return res;
	}
	
	public static List<String> getAllLines(String path) throws IOException
	{
		List<String> ret = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(new File(path)));
		String line = null;
		while ((line = in.readLine()) != null)
			ret.add(line);
		
		in.close();
		return ret;
	}
}
