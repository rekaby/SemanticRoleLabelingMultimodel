package uni.hamburg.inf.sssa.feature.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Set;

import uni.hamburg.inf.sssa.feature.FeatureFactory;
import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticManager;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class FeatureMatrixFactory {

	private static FeatureMatrixFactory instance = null;
	public static FeatureMatrixFactory getInstance() {
	      if(instance == null) {
	         instance = new FeatureMatrixFactory();
	      }
	      return instance;
	   }
	public FeatureMatrix buildFeatureMatrix(DependencyInstanceConll0809 instance, int predicateId, String predicate)
	{
		FeatureMatrix matrix= new FeatureMatrix();
		FeatureVector vector= new FeatureVector();

		List<String> expectedRoles=new ArrayList<String>();
		if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008"))
		{
			expectedRoles=SemanticManager.getInstance().getExpectedRoles(  predicate,instance.splitPpos08[predicateId]);
		}
		if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009"))
		{
			expectedRoles=SemanticManager.getInstance().getExpectedRoles(  predicate,instance.ppostags[predicateId]);
		}			
		matrix.initMatrix(expectedRoles.size(), instance.length, predicateId);
		//Create the matrx of features
		for (int i = 0; i < expectedRoles.size(); i++) {
			vector=new FeatureVector();
			for (int j = 1; j < instance.length; j++) {
				if(j==predicateId) 
				{
					vector=new FeatureVector();
					matrix.matrix[j][i]=vector;
					continue;//ignore self relation
				}
				if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruning)) {
					if(!isPotentialArgument(instance, predicateId, j))
					{
						//System.out.println(getPathString(instance, predicateId, j));
						vector=new FeatureVector();
						matrix.matrix[j][i]=vector;
						continue;//ignore this predicate if doesn't match prunning
					}
				}
				//System.out.println("Vector:"+predicateId+ " Utt:"+j+ " Role:"+i);
				vector=FeatureFactory.getInstance().buildFeatureVector(instance,j , predicateId, ContextRelationType.valueOf(expectedRoles.get(i)));
				matrix.matrix[j][i]=vector;
			}
			
		}
		matrix.rolesNames=expectedRoles;
		//instance.contextInstance.getFeatureMatrix().put(predicateId,matrix);
		return matrix;
	}
	public FeatureMatrix buildEmptyFeatureMatrix(DependencyInstanceConll0809 instance, int predicateId, String predicate)
	{
		FeatureMatrix matrix= new FeatureMatrix();
		FeatureVector vector= new FeatureVector();

		List<String> expectedRoles=new ArrayList<String>();
		if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008"))
		{
			expectedRoles=SemanticManager.getInstance().getExpectedRoles(  predicate,instance.splitPpos08[predicateId]);
		}
		if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009"))
		{
			expectedRoles=SemanticManager.getInstance().getExpectedRoles(  predicate,instance.ppostags[predicateId]);
		}			
		matrix.initMatrix(expectedRoles.size(), instance.length, predicateId);
		//Create the matrx of features
		for (int i = 0; i < expectedRoles.size(); i++) {
			vector=new FeatureVector();
			for (int j = 1; j < instance.length; j++) {
				
					vector=new FeatureVector();
					matrix.matrix[j][i]=vector;
					continue;//ignore self relation
			}
			
		}
		matrix.rolesNames=expectedRoles;
		//instance.contextInstance.getFeatureMatrix().put(predicateId,matrix);
		return matrix;
	}

	/**
	 * document from PHD thesis of Xavier Liuiss
	 * "This rule constraints the search of
argument candidates to direct descendants of the predicate or direct descendants
of the predicate ancestors."
I found cases like MOD needs to consider argument itself as one of the predicate ancestors, not descendant of them
	 * @param instance
	 * @param predicateId
	 * @param argumentId
	 * @return
	 */
	private boolean isPotentialArgument(DependencyInstanceConll0809 instance,int predicateId, int argumentId)
	{
		if (instance.heads[argumentId]==predicateId ||instance.pheads09[argumentId]==predicateId) {//Direct descendants of the predicate
			return true;
		}
		/*
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel))
		{
			int nodeId=instance.heads[argumentId];
			while(nodeId!=0)
			{
				if(nodeId==predicateId)
				{
					return true;
				}
				nodeId=instance.heads[nodeId];
			}
		}
		*/
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel)&&instance.heads[argumentId]!=0 && instance.heads[instance.heads[argumentId]]==predicateId ) {
			return true;
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel)&&instance.pheads09[argumentId]!=0 && instance.pheads09[instance.pheads09[argumentId]]==predicateId ) {
			return true;
		}
		
		int nodeId=instance.heads[predicateId];
		while(nodeId!=0)
		{
			if (argumentId== nodeId || instance.heads[argumentId]==nodeId ||instance.pheads09[argumentId]==nodeId) {
				return true;
			}
			/*
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel))
			{
				int argId=instance.heads[argumentId];
				while(argId!=0)
				{
					if(argId==nodeId)
					{
						return true;
					}
					argId=instance.heads[argId];
				}	
			}
			*/
			
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel)&&instance.heads[argumentId]!=0 && instance.heads[instance.heads[argumentId]]==nodeId ) {
				return true;
			}
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPruningSecondLevel)&&instance.pheads09[argumentId]!=0 && instance.pheads09[instance.pheads09[argumentId]]==nodeId ) {
				return true;
			}
			nodeId=instance.heads[nodeId];
		}
		return false;
	}
	
}
