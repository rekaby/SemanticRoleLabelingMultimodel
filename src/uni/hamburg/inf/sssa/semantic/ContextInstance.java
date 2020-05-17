package uni.hamburg.inf.sssa.semantic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uni.hamburg.inf.sssa.feature.matrix.FeatureMatrix;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class ContextInstance implements Serializable {

	private static final long serialVersionUID = 1L;
	List<SemanticRelation> semanticRelations= new ArrayList<SemanticRelation>();
	Map<Integer, String> uniquePredicateSetAndIds=new HashMap<Integer, String>();
	//Map<Integer, FeatureMatrix> featureMatrix= new HashMap<Integer, FeatureMatrix>();//feature matrix per predicate ID
	
	/*public Map<Integer, FeatureMatrix> getFeatureMatrix() {
		return featureMatrix;
	}

	public void setFeatureMatrix(Map<Integer, FeatureMatrix> featureMatrix) {
		this.featureMatrix = featureMatrix;
	}
*/
	public void setUniquePredicateSetAndIds(
			Map<Integer, String> uniquePredicateSetAndIds) {
		this.uniquePredicateSetAndIds = uniquePredicateSetAndIds;
	}

	public List<SemanticRelation> getSemanticRelations() {
		return semanticRelations;
	}

	public void setSemanticRelations(List<SemanticRelation> semanticRelations) {
		this.semanticRelations = semanticRelations;
	}
	public void addSemanticRelations(SemanticRelation semanticRelation) {
		this.semanticRelations.add(semanticRelation) ;
	}
	public void addSemanticRelations(List<SemanticRelation> semanticRelations) {
		this.semanticRelations.addAll(semanticRelations) ;
	}
	public void removeSemanticRelations(SemanticRelation semanticRelations) {
		this.semanticRelations.remove(semanticRelations) ;
	}
	
	
	public boolean hasSemanticRelations()
    {
		return semanticRelations==null||semanticRelations.size()==0? false:true;
    }
	public Set<String> getUniquePredicateSetold()
	{
		Set<String> predicatesSet=new HashSet<String>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			predicatesSet.add(semanticRelation.getPredicate());
		}
		return predicatesSet;
	}
	public Set<Integer> getUniquePredicateIdsSet()
	{
		Set<Integer> predicatesSet=new HashSet<Integer>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			predicatesSet.add(semanticRelation.getPredicateid());
		}
		return predicatesSet;
	}
	public Map<Integer, String> getUniquePredicateSetAndIds()
	{
		if (uniquePredicateSetAndIds.size()>0) {
			return uniquePredicateSetAndIds;
		}
		
		Map<Integer, String> results=new HashMap<Integer, String>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			results.put(semanticRelation.getPredicateid(),semanticRelation.getPredicate());
		}
		return results;
	}
	public List<SemanticRelation> getRelatedSemanticRelationsByRootAndPosStart(String root,String posStart,DependencyInstanceConll0809 instance)
	{
		List<SemanticRelation> subSemanticRelations=new ArrayList<SemanticRelation>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
		//	System.out.println(semanticRelation);
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2008")) 
			{
				if(semanticRelation.getPredicateRoot().equalsIgnoreCase(root) &&instance.splitPpos08[semanticRelation.predicateid].startsWith(posStart))
				{subSemanticRelations.add(semanticRelation);}	
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2009")) 
			{
				if(semanticRelation.getPredicateRoot().equalsIgnoreCase(root) &&instance.ppostags[semanticRelation.predicateid].startsWith(posStart))
				{subSemanticRelations.add(semanticRelation);}	
			}
			
		}
		return subSemanticRelations;
	}
	public List<SemanticRelation> getRelatedSemanticRelationsByPredicateold(String predicate)
	{
		List<SemanticRelation> subSemanticRelations=new ArrayList<SemanticRelation>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			if(semanticRelation.getPredicate().equals(predicate))
				subSemanticRelations.add(semanticRelation);
		}
		return subSemanticRelations;
	}
	public List<SemanticRelation> getRelatedSemanticRelationsByPredicateId(Integer predicateId)
	{
		List<SemanticRelation> subSemanticRelations=new ArrayList<SemanticRelation>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			if(semanticRelation.getPredicateid()==  predicateId)
				subSemanticRelations.add(semanticRelation);
		}
		return subSemanticRelations;
	}
	public List<SemanticRelation> getRelatedSemanticRelationsByArgumentId(Integer argumentId)
	{
		List<SemanticRelation> subSemanticRelations=new ArrayList<SemanticRelation>();
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			if(semanticRelation.getArgumentHeadID()==  argumentId)
				subSemanticRelations.add(semanticRelation);
		}
		return subSemanticRelations;
	}
	public SemanticRelation getFirstRelatedSemanticRelationByPredicateId(Integer predicateId)
	{
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation semanticRelation=semanticRelations.get(i);
			if(semanticRelation.getPredicateid()==  predicateId)
				return semanticRelation;
		}
		return null;
	}
	public void mergeSemanticRelations(List<SemanticRelation> newSemanticRelations)
	{
		//Set<SemanticRelation> set=new HashSet<SemanticRelation>(newSemanticRelations);
		Set<SemanticRelation> filteredSet=new HashSet<SemanticRelation>();
		for (int i = 0; i < newSemanticRelations.size(); i++) {//here we loop on all new relations, check if not contradict another old relation so we add it
			if(!contradictingRelation(newSemanticRelations.get(i)))
			{
				filteredSet.add(newSemanticRelations.get(i));
			}
		}
		
		filteredSet.addAll(semanticRelations);//we add the old relations, if same predicate/type was one, its ignored and we use the new one. if new relation is filtered out so we keep the old version
		semanticRelations.clear();
		semanticRelations.addAll(filteredSet);
	}
	private boolean contradictingRelation(SemanticRelation newSemanticRelation)
	{
		for (Iterator iterator = semanticRelations.iterator(); iterator.hasNext();) {
			SemanticRelation semanticRelation = (SemanticRelation) iterator.next();
			if(semanticRelation.predicate.equals(newSemanticRelation.predicate) && !semanticRelation.type.equals(newSemanticRelation.type))//so this is not the old similar relation
			{
				if(semanticRelation.argumentStartID<=newSemanticRelation.argumentEndID && newSemanticRelation.argumentStartID<=semanticRelation.argumentEndID)
				{
					return true;//overlap is there between the new relation and one of the old relations on same predicate w diff type
				}
			}
			
		}
		return false;
	}
}
