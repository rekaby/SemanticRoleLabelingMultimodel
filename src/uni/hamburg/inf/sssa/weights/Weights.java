package uni.hamburg.inf.sssa.weights;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.util.PatriciaST;
import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TLongFloatHashMap;

public  class Weights {

	private static Weights instance = null;
	public static Weights getInstance() {
	      if(instance == null) {
	    	  if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useDiskStorage)) {
	    		  instance = new WeightMongoDB();	
	    	  }
	    	  else
	    	  {
	    		  instance = new WeightsMemory();
	    	  }
	         
	      }
	      return instance;
	   }
	//PatriciaST<Float> weightsTree=new PatriciaST<Float>();
	//org.mapdb.LongMap<Float> mapDbWeights=new LongHashMap<Float>();
	
	//TIntFloatMap publicWeights2=new TIntFloatHashMap();
	
	//Map<Long, Float> publicWeights= new HashMap<Long, Float>();
	public void addWeight(long featureKey, float value)
	{}
	public void addWeight(long featureKey)
	{}
	public void updateWeight(long featureKey, float value)
	{}
	public void updatePublicWeights(FeatureVector allFeatures,int hammingDistance, float lose)
	{
		float gamma=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.gamma) ;
		float c=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.C) ;
		
		//double loss = - allFeatures.dotProduct(allFeatures,publicWeights)*gamma + hammingDistance;
		float loss =  Math.abs(lose+ hammingDistance);//*gamma + hammingDistance;
        float l2norm = allFeatures.Squaredl2NormUnsafe() * gamma * gamma;
        //System.out.println("Lose="+loss);
        //System.out.println("l2norm="+l2norm);
        float alpha = loss/l2norm;
    	alpha = Math.min(c, alpha);
    	//System.out.println("In Update weights...Features:"+allFeatures.getFeatureMap().toString());
    	//System.out.println("alpha:"+alpha);
    	//System.out.println("gamma:"+gamma);
    	//System.out.println("B4 Pub Weights:"+Weights.getInstance().getPublicWeights().toString());
    	if (alpha > 0) 
    	{
    		// update theta
    		float coeff = alpha * gamma;
    		TLongFloatMap featureMap=allFeatures.getFeatureMap();
    		long [] keys=allFeatures.getFeatureMap().keys();
	    	for (int i = 0; i< allFeatures.size(); i++) 
	    	{
		   		long key = keys[i];
		   		float value =Weights.getInstance().getPublicWeights().get(key);//: Weights.getInstance().getPublicWeights2().get(key);//allFeatures.getFeatureMap().get(key);
		   		float newValue= value;
		   		newValue = newValue+(coeff * value*featureMap.get(key));
		   		Weights.getInstance().updateWeight(key, newValue);
	    	}
    	}
    	//System.out.println("Public Weights:"+Weights.getInstance().getPublicWeights().toString());
	}
	public TLongFloatMap getPublicWeights() {
		return null;
	}
	public TLongFloatMap getPublicWeights(FeatureVector fv) {
		return null;
	}
	/*public TIntFloatMap getPublicWeights2() {
		return publicWeights2;
	}*/
	
	/*public org.mapdb.LongMap<Float> getMapDbWeights() {
		return mapDbWeights;
	}
	public void setMapDbWeights(org.mapdb.LongMap<Float> mapDbWeights) {
		this.mapDbWeights = mapDbWeights;
	}
	*/
	/*public PatriciaST<Float> getWeightsTree() {
		return weightsTree;
	}
	public void setWeightsTree(PatriciaST<Float> weightsTree) {
		this.weightsTree = weightsTree;
	}*/
	
	
}
