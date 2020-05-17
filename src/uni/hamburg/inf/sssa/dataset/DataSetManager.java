package uni.hamburg.inf.sssa.dataset;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;

public class DataSetManager extends DataSetRepository{

	private static DataSetManager instance = null;
	public static DataSetManager getInstance() {
	      if(instance == null) {
	         instance = new DataSetManager();
	      }
	      return instance;
	   }
	public boolean emptyUnlabeledSet()
	{
		return unlabeledSet.size()==0?true:false;
	}
	public int labelSetSize()
	{
		return labeledSet.size();
	}
	public int unlabelSetSize()
	{
		return unlabeledSet.size();
	}
	public int tempLabeledSetSize()
	{
		return tempLabeledSet.size();
	}
	public int probLabeledSetSize()
	{
		return probLabeledSet.size();
	}
	/*
	public Set<String> getLearningRelatedRoles( String predicate,String pos)
	{
		Set<String> expectedRoles=new HashSet<String>();
		for (int i = 0; i < labeledSet.size(); i++) {
			DependencyInstanceConll0809 instance=labeledSet.get(i);
			//System.out.println("getLearningRelatedRoles:"+instance.getId());
			List<SemanticRelation> semanticRelations=instance.contextInstance.getRelatedSemanticRelationsByRootAndPosStart(predicate,pos.substring(0,1),instance);// just to get any verb or any noun
			if(semanticRelations.size()>0)
			{
				for (int j = 0; j < semanticRelations.size(); j++) {
					SemanticRelation relation=semanticRelations.get(j);
					expectedRoles.add(relation.getType().toString());
				}
			}
		}
		
		return expectedRoles;
	}
	*/
}
