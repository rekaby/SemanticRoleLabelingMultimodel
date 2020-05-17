package uni.hamburg.inf.sssa.feature;


import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.iterator.TLongFloatIterator;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TLongDoubleMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TLongDoubleHashMap;
import gnu.trove.map.hash.TLongFloatHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.util.PatriciaST;
import uni.hamburg.inf.sssa.weights.Weights;

public class FeatureVector {
	
	TLongFloatMap featureMap= new TLongFloatHashMap();
		
	
	public void addFeature(long key, float value) {
		if (value == 0) return;
		
		featureMap.put(key, value);
		Weights.getInstance().addWeight(key);
	}
	public void addFeature(long key) {
		
		featureMap.put(key, 1.0f);
		Weights.getInstance().addWeight(key);
	}	
	public void addEntries(FeatureVector m) {
		addEntries(m, 1.0f);
	}
	
	public void addEntries(FeatureVector m, float coeff) {
		
		
		if (coeff == 0 ) return;
		long [] keys=m.featureMap.keys();
		for (int i = 0; i < keys.length; i++) {
			featureMap.put( keys[i], m.featureMap.get(keys[i])*coeff);
			Weights.getInstance().addWeight(keys[i]);
		}
		
	}
	public void addEntriesIfAbsentOrRemove(FeatureVector m, float coeff) 
	{//this method is used when combine negative and positive features, so we add the positive only if no previous negative are there, else we remove it
		
		
		if (coeff == 0 ) return;
		long []keys=m.featureMap.keys();
		for (int i = 0; i < m.featureMap.keys().length; i++) {
			if(featureMap.containsKey(keys[i]))
			{//remove in case of existance and similarity
				if(featureMap.get(keys[i])!=m.featureMap.get(keys[i]))
				{
					featureMap.remove(keys[i]);	
				}
				
			}
			else
			{//add in case of absent
				featureMap.put( keys[i], m.featureMap.get(keys[i])*coeff);	
				Weights.getInstance().addWeight(keys[i]);
			}
		}
	}
	
	
	public double listToNorm() {
		double sum = 0;
		for (int i = 0; i <featureMap.values().length; ++i)
			sum += featureMap.values()[i]*featureMap.values()[i];
		return Math.sqrt(sum);
	}
	

	
    public double min() {
        double m = Double.POSITIVE_INFINITY;
        for (int i = 0; i < featureMap.values().length; ++i)
            if (m > featureMap.values()[i]) m = featureMap.values()[i];
        return m;
    }
    
    public double max() {
        double m = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < featureMap.values().length; ++i)
            if (m < featureMap.values()[i]) m = featureMap.values()[i];
        return m;
    }

	public int size() {
		return featureMap.size();
	}
    
   
    public double dotProduct(FeatureVector y) {
        return dotProduct(this, y);
    }
        
	/*public double dotProduct(double[] y) {
		return dotProduct(this, y);
	}
	
	public double dotProduct(double[] y, int offset) {
		return dotProduct(this, y, offset); 
	}*/
	
	public TLongFloatMap getFeatureMap() {
		return featureMap;
	}

	public void setFeatureMap(TLongFloatMap featureMap) {
		this.featureMap = featureMap;
	}

	private static double[] dpVec;			 //non-sparse vector repr for vector dot product
	/*public static double dotProduct(FeatureVector x, FeatureVector y) {
		
		
		
		if (dpVec == null || dpVec.length < y.size()) dpVec = new double[y.size()];
		
		for (int i = 0; i < y.size(); ++i)
			dpVec[y.getFeatureMap().keys()[i]] += _y.va[i];
		
		double sum = 0;
		for (int i = 0; i < _x.size; ++i)
			sum += _x.va[i] * dpVec[_x.x[i]];

		for (int i = 0; i < _y.size; ++i)
			dpVec[_y.x[i]] = 0;
		
		return sum;
	}*/
	
	public static double dotProduct(FeatureVector x, FeatureVector y) {
		
		double sum = 0;
		TLongFloatMap tempMap=x.getFeatureMap();
		for (TLongFloatIterator iterator = tempMap.iterator(); iterator.hasNext();)
			sum += iterator.value() * y.featureMap.get((int)iterator.key());
		return sum;
	}
	public static float dotProduct(FeatureVector x, TLongFloatMap publicWeights1) {
		
		float sum = 0;
		TLongFloatMap tempMap=x.getFeatureMap();
		long[] keys=tempMap.keys();
		
		//System.out.println("key's Length"+keys.length);
		for (int i = 0; i < keys.length; i++) {
			float weight=publicWeights1.get(keys[i]);
			sum += tempMap.get(keys[i]) * weight;
		}
		//System.out.println("score Befor:"+sum);
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.averagePerceptron) && keys.length!=0)
		{
			sum=sum/keys.length;//here we return average of perceptron instead of just sum
			//System.out.println("score After:"+sum);
		}
		return sum;
	}
	public static float absDotProduct(FeatureVector x,TLongFloatMap publicWeights1) {
		
		float sum = 0;
		TLongFloatMap tempMap=x.getFeatureMap();
		long[] keys=tempMap.keys();
		for (int i = 0; i <keys.length; i++) {
			float weight=publicWeights1.get(keys[i]);
			sum += Math.abs(tempMap.get(keys[i]) * weight);
		}
		
		return sum;
	}
	
	public float Squaredl2NormUnsafe() {
		
		float sum = 0;
		long[] keys=featureMap.keys();
		for (int i = 0; i < keys.length; i++) {
			float value=Weights.getInstance().getPublicWeights().get(keys[i]);//featureMap.get(keys[i]);
			sum += value*value;
		}
		return (float) Math.sqrt(sum);
		//return featureMap.keys().length;
		
	}/*public static double dotProduct(FeatureVector x, double[] y, int offset) {
		
		double sum = 0;
		TLongDoubleMap tempMap=x.getFeatureMap();
		for (TLongDoubleIterator iterator = tempMap.iterator(); iterator.hasNext();)
			sum += iterator.value() * y[offset +(int)iterator.key()];
		return sum;
	}*/
	
	
}

/*class Entry {
	int x;
	double value;
	
	public Entry(int x, double value) 
	{
		this.x = x;
		this.value = value;
	}
}
*/
