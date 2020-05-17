package uni.hamburg.inf.sssa.weights;

import gnu.trove.map.TIntFloatMap;
import gnu.trove.map.TLongFloatMap;
import gnu.trove.map.hash.TIntFloatHashMap;
import gnu.trove.map.hash.TLongFloatHashMap;
import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;

public class WeightsMemory extends Weights{
	TLongFloatMap publicWeights=new TLongFloatHashMap();
	public boolean extendPublicWeights=false;
@Override
public void addWeight(long featureKey, float value)
{
	if (!extendPublicWeights) {//here to speedup, we need to add weights only in elementry and first testing data handling
		return;
	}
	//mapDbWeights.put(featureKey, value);
	//weightsTree.put(featureKey+"", value);
	//if (featureKey<0) {
		publicWeights.putIfAbsent(featureKey, value);
	//}
	//else
	//{
	 // publicWeights2.putIfAbsent(featureKey, value);
	//}
	
	//publicWeights.putIfAbsent(featureKey, value);
}
@Override
public void addWeight(long featureKey)
{
	if (!extendPublicWeights) {//here to speedup, we need to add weights only in elementry and first testing data handling
		return;
	}
	if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.randomWeights))
	{
	//	mapDbWeights.put(featureKey, (float)(Math.random()*0.5));
		//weightsTree.put(featureKey+"", (float)(Math.random()*0.5));
		//if (featureKey<0) {
			publicWeights.putIfAbsent(featureKey, 0.4f+(float)(Math.random()*0.1));//make scale from 0 to 0.5
		//}
		//else
		//{
		//	publicWeights2.putIfAbsent(featureKey, (float)(Math.random()*0.5));//make scale from 0 to 0.5
		//}
		
	}
	else
	{
	//	mapDbWeights.put(featureKey, 0.5f);
		//weightsTree.put(featureKey+"", 0.5f);
		//if (featureKey<0) {
			publicWeights.putIfAbsent(featureKey, 0.5f);
		//}
		//else
		//{
		//	publicWeights2.putIfAbsent(featureKey, 0.5f);
		//}
			
	}
	
}
@Override
public void updateWeight(long featureKey, float value)
{
	publicWeights.put(featureKey, value);
	//if (featureKey<0) {
	//	publicWeights1.put(featureKey, value);
	//}
	//else
	//{
	//	publicWeights2.put(featureKey, value);
	//}
	
}
@Override
public TLongFloatMap getPublicWeights() {
	return publicWeights;
}
@Override
public TLongFloatMap getPublicWeights(FeatureVector fv) {
	return publicWeights;
}

}
