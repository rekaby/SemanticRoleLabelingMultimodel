package uni.hamburg.inf.sssa.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {
	
	public static Random rnd = new Random(System.currentTimeMillis());
	
	public static void Assert(boolean assertion) 
	{
		if (!assertion) {
			(new Exception()).printStackTrace();
			System.exit(1);
		}
	}
		
	public static int log2(long x) 
	{
		long y = 1;
		int i = 0;
		while (y < x) {
			y = y << 1;
			++i;
		}
		return i;
	}
	
	public static double logSumExp(double x, double y) 
	{
		if (x == Double.NEGATIVE_INFINITY && x == y)
			return Double.NEGATIVE_INFINITY;
		else if (x < y)
			return y + Math.log1p(Math.exp(x-y));
		else 
			return x + Math.log1p(Math.exp(y-x));
	}
	
	public static double[] getRandomUnitVector(int length) 
	{
		double[] vec = new double[length];
		double sum = 0;
		for (int i = 0; i < length; ++i) {
			vec[i] = rnd.nextDouble() - 0.5;
			sum += vec[i] * vec[i];
		}
		double invSqrt = 1.0 / Math.sqrt(sum);
		for (int i = 0; i < length; ++i) 
			vec[i] *= invSqrt;
		return vec;
	}
	
	public static double squaredSum(double[] vec) 
	{
		double sum = 0;
		for (int i = 0, N = vec.length; i < N; ++i)
			sum += vec[i] * vec[i];
		return sum;
	}
	
	public static void normalize(double[] vec) 
	{
		double coeff = 1.0 / Math.sqrt(squaredSum(vec));
		for (int i = 0, N = vec.length; i < N; ++i)
			vec[i] *= coeff;
	}
	
	public static double max(double[] vec) 
	{
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0, N = vec.length; i < N; ++i)
			max = Math.max(max, vec[i]);
		return max;
	}
	
	public static double min(double[] vec) 
	{
		double min = Double.POSITIVE_INFINITY;
		for (int i = 0, N = vec.length; i < N; ++i)
			min = Math.min(min, vec[i]);
		return min;
	}

	public static String extractStringFromAlphaNum(String stringWithNum)
	{
		return stringWithNum.split(".\\d")[0];
	}
	public static String extractIntFromAlphaNumInString(String stringWithNum)
	{
		try {
			return stringWithNum.split(".\\d")[1];	
		} catch (Exception e) {
			return "00";
		}
	}
	public static int extractIntFromAlphaNum(String stringWithNum)
	{
		try {
			return Integer.parseInt(stringWithNum.split(".\\d")[1]);	
		} catch (Exception e) {
			return 0;
		}
	}
	public static boolean isContinousList(int [] Ids)
	{
		boolean isContinous=true;;
		int []copyIds=new int[Ids.length];
		copyIds=Ids.clone();
		Arrays.sort(copyIds);
		if(copyIds.length>0)
		{
			for (int i = 1; i < copyIds.length; i++) {
				
					if(!(Math.abs((int)copyIds[i]-copyIds[i-1])==1))//check if elements are sequential 
					{
						isContinous=false;
					}
				}
				
		}
		return isContinous;
	}

	
}
