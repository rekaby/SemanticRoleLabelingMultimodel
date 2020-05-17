package uni.hamburg.inf.sssa.feature.matrix;

import gnu.trove.map.TMap;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uni.hamburg.inf.sssa.annotater.SssaFunctions;
import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticManager;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.DictionarySet.DictionaryTypes;
import uni.hamburg.inf.sssa.util.GlobalVariables;
import uni.hamburg.inf.sssa.util.Utils;
import uni.hamburg.inf.sssa.weights.Weights;

public class FeatureMatrixEncoder {
//This class is responsible for extracting the relations from the matix
	private double threshold_Level4=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level4);
	private double threshold_Level3=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level3);
	private double threshold_Level2=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level2);
	private double threshold_Level1=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level1);
	//private double[][] acceptedConfidenceScore=new double [ContextRelationType.Context_Relation_Type_End.ordinal()][SssaFunctions.getInstance().posDict.size()];
	private double[][] trustedAcceptedScore=new double [ContextRelationType.Context_Relation_Type_End.ordinal()][SssaFunctions.getInstance().posDict.size()];
	private double[] coarseGraintrustedAcceptedScore=new double [ContextRelationType.Context_Relation_Type_End.ordinal()];
//	private double[] justTrustedAcceptedScore=new double [(ContextRelationType.Context_Relation_Type_End.ordinal()*SssaFunctions.getInstance().posDict.size())];
	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(FeatureMatrixEncoder.class);
	
	private static FeatureMatrixEncoder instance = null;
	public static FeatureMatrixEncoder getInstance() {
	      if(instance == null) {
	         instance = new FeatureMatrixEncoder();
	      }
	      return instance;
	   }
	
	public double[][] getTrustedAcceptedScore() {
		return trustedAcceptedScore;
	}

	public void setTrustedAcceptedScore(double[][] trustedAcceptedScore) {
		this.trustedAcceptedScore = trustedAcceptedScore;
	}

	public double[] getCoarseGraintrustedAcceptedScore() {
		return coarseGraintrustedAcceptedScore;
	}

	public void setCoarseGraintrustedAcceptedScore(
			double[] coarseGraintrustedAcceptedScore) {
		this.coarseGraintrustedAcceptedScore = coarseGraintrustedAcceptedScore;
	}

	public List<SemanticRelation> getMaxMatrixRelations(FeatureMatrix matrix, DependencyInstanceConll0809 instance, Integer predicateid,boolean elementryPhase)
	{

		//Here we implement the relation extract algorithm, later we can update it
		boolean coarseGrainCheck=SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.coarseGrainAverage);
		List<SemanticRelation> semanticRelations= new ArrayList<SemanticRelation>();
		int posID=instance.ppostagids[predicateid];
		int wordsCount=matrix.nodeTotalScore.length;
		int rolesCount=matrix.nodeTotalScore[0].length;
		double [][] localMatrixOriginalScores=copyMatrix(matrix.nodeTotalScore,wordsCount,rolesCount);
		double [][] localMatrixRatio=copyMatrix(matrix.nodeTotalScore,wordsCount,rolesCount);
		if (!elementryPhase && SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useRatioComparison)) {//we transform matrix to ratios
			for (int i = 0; i < wordsCount; i++) {
				for (int j = 0; j < rolesCount; j++) {
					String roleName=matrix.getRolesNames().get(j);
					if (coarseGrainCheck) {
						localMatrixRatio[i][j]=localMatrixRatio[i][j]/FeatureMatrixEncoder.getInstance().getCoarseGraintrustedAcceptedScore()[ContextRelationType.valueOf(roleName).ordinal()];	
					}
					else
					{
						localMatrixRatio[i][j]=localMatrixRatio[i][j]/FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[ContextRelationType.valueOf(roleName).ordinal()][posID];
					}
					
				}
			}
		}
		if (elementryPhase ||! SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useAssociationConstraints)) {
			for (int i = 0; i < rolesCount; i++) {
				//temp code:
				int tempi=-1;
				int tempj=-1;
				double max=Double.NEGATIVE_INFINITY;
				double maxOriginal=Double.NEGATIVE_INFINITY;
				for (int j = 1; j < wordsCount; j++) {//start from 1
					if (Double.isInfinite(localMatrixRatio[j][i])|| Double.isNaN(localMatrixRatio[j][i])) {
						continue;
					}
					if(max<localMatrixRatio[j][i])//temp code
					{
						max=localMatrixRatio[j][i];
						maxOriginal=localMatrixOriginalScores[j][i];
						tempi=i;
						tempj=j;
					}
				}
				//temp code
				//here we set start and end of argument as same word...this is for Elementry phase only
				if (!elementryPhase)
				{// in not elementry phase we have to compare with the max
					if ((!coarseGrainCheck && maxOriginal!= Double.NEGATIVE_INFINITY && maxOriginal>=FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[ContextRelationType.valueOf(matrix.rolesNames.get(tempi)).ordinal()][posID] )
					||
					(coarseGrainCheck && maxOriginal!= Double.NEGATIVE_INFINITY && maxOriginal>=FeatureMatrixEncoder.getInstance().getCoarseGraintrustedAcceptedScore()[ContextRelationType.valueOf(matrix.rolesNames.get(tempi)).ordinal()] )){
						addSemanticRelationFromMaxMatrix(semanticRelations,matrix,instance,predicateid, tempi,tempj);
					}
				}
				else
				{
					addSemanticRelationFromMaxMatrix(semanticRelations,matrix,instance,predicateid, tempi,tempj);
				}
			}	
			
			if (semanticRelations.size()==0 && SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.minimumOneArgumentAssignment) && !elementryPhase) {//post processing to catch minimum one arg per predicate
				//get just max word for role of 0
				int tempi=-1;
				int tempj=-1;
				double max=Double.NEGATIVE_INFINITY;
				double maxOriginal=Double.NEGATIVE_INFINITY;
				for (int j = 1; j < wordsCount; j++) {//start from 1
					
					if(max<localMatrixRatio[j][0] && Double.isInfinite(localMatrixRatio[j][0])&& Double.isNaN(localMatrixRatio[j][0]))//temp code
					{
						max=localMatrixRatio[j][0];
						maxOriginal=localMatrixOriginalScores[j][0];
						tempi=0;
						tempj=j;
					}
				}
				if (tempi!=-1&&tempj!=-1) {
					addSemanticRelationFromMaxMatrix(semanticRelations,matrix,instance,predicateid, tempi,tempj);	
				}
				
			}
		}
		else//means not elementry phase and we use association constraint
		{
			List<Double> allScores=new ArrayList();
			List<Integer> assignedWords=new ArrayList();
			List<Integer> assignedroles=new ArrayList();
			for (int i = 0; i < rolesCount; i++) {
				for (int j = 1; j < wordsCount; j++) {
					allScores.add(localMatrixRatio[j][i]);
				}
			}
			Collections.sort(allScores);
			for (int k = allScores.size()-1; k >=0 && allScores.get(k)!=0 && assignedroles.size()<rolesCount; k--) {//score >0 means its already considered in parameter pruning
				if (Double.isNaN(allScores.get(k))|| Double.isInfinite(allScores.get(k))) {
					continue;
				}
				double currentMaxScore=allScores.get(k);
				for (int i = 0; i < rolesCount; i++) {
					for (int j = 1; j < wordsCount; j++) {
						if (localMatrixRatio[j][i]!=currentMaxScore) {
							continue;
						}
						//means this is a top score now
						if (!assignedWords.contains(j)&& !assignedroles.contains(i)) {
							
							if ((!coarseGrainCheck &&localMatrixOriginalScores[j][i]>=FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[ContextRelationType.valueOf(matrix.rolesNames.get(i)).ordinal()][posID]) 
								||	(coarseGrainCheck &&localMatrixOriginalScores[j][i]>=FeatureMatrixEncoder.getInstance().getCoarseGraintrustedAcceptedScore()[ContextRelationType.valueOf(matrix.rolesNames.get(i)).ordinal()])) {
							//if (localMatrixOriginalScores[j][i]>=averageAnArray(FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[ContextRelationType.valueOf(matrix.rolesNames.get(i)).ordinal()]) ) {
								addSemanticRelationFromMaxMatrix(semanticRelations,matrix,instance,predicateid, i,j);
								assignedWords.add(j);
								assignedroles.add(i);
								break;
							}
						}
					}
				}
			}
			
			
			if (semanticRelations.size()==0 && SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.minimumOneArgumentAssignment)) {//post processing to catch minimum one arg per predicate
				//get just max word-role
				double currentMaxScore=0;
				for (int k = allScores.size()-1; k >=0 && allScores.get(k)!=0 ; k--) {
					if (Double.isNaN(allScores.get(k))|| Double.isInfinite(allScores.get(k))) {
						continue;
					}
					 currentMaxScore=allScores.get(k);
				}
				
				for (int i = 0; i < rolesCount; i++) {
					for (int j = 1; j < wordsCount; j++) {
						
						if (localMatrixRatio[j][i]!=currentMaxScore) {
							continue;
						}
							addSemanticRelationFromMaxMatrix(semanticRelations,matrix,instance,predicateid, i,j);
							return semanticRelations;
					}
				}
			}
		}
		
		//if (semanticRelations.size()==0) {
		//	log.logInfo(LogCode.GENERAL_CODE.getCode(), "NIX");
		//}
		return semanticRelations;
	
	}

	private void addSemanticRelationFromMaxMatrix(List<SemanticRelation> semanticRelations,FeatureMatrix matrix,DependencyInstanceConll0809 instance,int predicateid,int tempi, int tempj)
	{
		if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2008")) 
		{
			semanticRelations.add(new SemanticRelation(instance.splitLemmas08[predicateid]+".01", instance.splitLemmas08[tempj],predicateid,tempj,tempj,tempj,ContextRelationType.valueOf(matrix.rolesNames.get(tempi))));	
		}
		else if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2009"))//2009
		{
			semanticRelations.add(new SemanticRelation(instance.lemmas[predicateid]+".01", instance.lemmas[tempj],predicateid,tempj,tempj,tempj,ContextRelationType.valueOf(matrix.rolesNames.get(tempi))));
		}	
	}
	public float [][][] getRelationScoresPerSemanticTypeAndPOS(FeatureMatrix matrix, DependencyInstanceConll0809 instance,List<SemanticRelation> goldSemanticRelations)
	{//D0 Role, D1 POS, D3 0:sum, 1:count
		float [][][]sumCount=new float[ContextRelationType.Context_Relation_Type_End.ordinal()][SssaFunctions.getInstance().posDict.size()][2];//{Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY};
		for (int i = 0; i < sumCount.length; i++) {
			for (int j = 0; j < sumCount[i].length; j++) {
				sumCount[i][j]=new float[]{0f,0f};//Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY};	
			}
		}
		for (int i = 0; i < goldSemanticRelations.size(); i++) {
			SemanticRelation relation=goldSemanticRelations.get(i);
		//	List<Integer> argumentIDs=instance.getWordsIDs(relation.getArgument(),relation.getArgumentID());
			String roleName=relation.getType().name();
			int posID=instance.ppostagids[relation.getPredicateid()];
			int roleID=matrix.getRolesNames().indexOf(roleName);
			for (int j = relation.getArgumentStartID(); j <= relation.getArgumentEndID(); j++) {
				try {
					double nodeScore=matrix.nodeTotalScore[j][roleID];
					if (nodeScore==0.0) {
						GlobalVariables.missingVectorsCount++;
						FeatureVector fV= SssaFunctions.getInstance().getRelatedFeatures(instance,matrix,relation);
						if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useDiskStorage)) {
							nodeScore=fV.dotProduct(fV,Weights.getInstance().getPublicWeights(fV));//,Weights.getInstance().getPublicWeights2()) ;//real calculation should be here
						}
						else
						{
							nodeScore=fV.dotProduct(fV,Weights.getInstance().getPublicWeights());
						}
						//nodeScore=fV.dotProduct(fV,Weights.getInstance().getPublicWeights());;
					}
					sumCount[ContextRelationType.valueOf(roleName).ordinal()][posID][0]+=nodeScore;
					sumCount[ContextRelationType.valueOf(roleName).ordinal()][posID][1]++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sumCount;
	}
	public double [][] getMinMaxRelationScoreBackup(FeatureMatrix matrix, DependencyInstanceConll0809 instance,List<SemanticRelation> goldSemanticRelations)
	{//0:min, 1:Max
		double [][]minMax=new double[ContextRelationType.Context_Relation_Type_End.ordinal()][];//{Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY};
		for (int i = 0; i < minMax.length; i++) {
			minMax[i]=new double[]{Double.POSITIVE_INFINITY,Double.NEGATIVE_INFINITY};
		}
		for (int i = 0; i < goldSemanticRelations.size(); i++) {
			SemanticRelation relation=goldSemanticRelations.get(i);
			
		//	List<Integer> argumentIDs=instance.getWordsIDs(relation.getArgument(),relation.getArgumentID());
			String roleName=relation.getType().name();
			int roleID=matrix.getRolesNames().indexOf(roleName);
			for (int j = relation.getArgumentStartID(); j <= relation.getArgumentEndID(); j++) {
				try {
					if(minMax[ContextRelationType.valueOf(roleName).ordinal()][0]>matrix.nodeTotalScore[j][roleID])
					{
						minMax[ContextRelationType.valueOf(roleName).ordinal()][0]=matrix.nodeTotalScore[j][roleID];
					}
					if(minMax[ContextRelationType.valueOf(roleName).ordinal()][1]<matrix.nodeTotalScore[j][roleID])
					{
						minMax[ContextRelationType.valueOf(roleName).ordinal()][1]=matrix.nodeTotalScore[j][roleID];
					}	
				} catch (Exception e) {
					e.printStackTrace();
				}
				
					
			}
		}
		return minMax;
	}
	
	public List<SemanticRelation> getTrustedMatrixRelations(FeatureMatrix matrix, DependencyInstanceConll0809 instance, int predicateId)
	{
		if (!SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.coarseGrainAverage)) {
			return getMatrixRelations(matrix, trustedAcceptedScore, Double.POSITIVE_INFINITY,instance, predicateId);	
		}
		else
		{//lets duplicate the coarse grain into temp 2D array
			double[][] tempTrustedAcceptedScore=new double [ContextRelationType.Context_Relation_Type_End.ordinal()][SssaFunctions.getInstance().posDict.size()];
			for (int i = 0; i < tempTrustedAcceptedScore.length; i++) {
				for (int j = 0; j < tempTrustedAcceptedScore[i].length; j++) {
					tempTrustedAcceptedScore[i][j]=coarseGraintrustedAcceptedScore[i];
				}
			}
			return getMatrixRelations(matrix, tempTrustedAcceptedScore, Double.POSITIVE_INFINITY,instance, predicateId);
		}
		
	}
	/*
	public List<SemanticRelation> getJustTrustedMatrixRelations(FeatureMatrix matrix, DependencyInstanceConll0809 instance, String predicate)
	{
		return getMatrixRelations(matrix,justTrustedAcceptedScore, Double.MAX_VALUE,instance, predicate);
	}
	*/
	public List<SemanticRelation> getMatrixRelations(FeatureMatrix matrix, double[][] lowScores,double highScore, DependencyInstanceConll0809 instance, int predicateId)
	{//just a wrapper method to fill a list with Max values as high scores
		 double[][] highScores=new double[lowScores.length][lowScores[0].length];
		 for(int i=0;i<lowScores.length;i++)
		 {
			 for (int j = 0; j < lowScores[i].length; j++) 
			 {
				highScores[i][j]=highScore;
				if(Double.isNaN(lowScores[i][j]))//handle if the role has NAN accepted score
				{lowScores[i][j]=highScore;}
			 }
		 }
		 return getMatrixRelations( matrix, lowScores, highScores,  instance,  predicateId);
	}
	public List<SemanticRelation> getMatrixRelations(FeatureMatrix matrix, double[][] lowScore,double[][] highScore, DependencyInstanceConll0809 instance, int predicateId)
	{
		//Here we implement the relation extract algorithm, later we can update it
		List<SemanticRelation> semanticRelations= new ArrayList<SemanticRelation>();
		int tempWordID;
		int tempRoleID;
		Map<Integer, List<Integer>> semanticRelationsBinaries= new HashMap<Integer, List<Integer>>();//this map has roleID, and per each one a list of word IDs. used in semantic extraction logic
		int wordsCount=matrix.nodeTotalScore.length;
		int rolesCount=matrix.nodeTotalScore[0].length;
		double [][] localMatrix=copyMatrix(matrix.nodeTotalScore,wordsCount,rolesCount);
		//cleanWeekNodesMatrix(localMatrix, wordsCount,rolesCount,lowScore);
		int posID=instance.ppostagids[predicateId]; 
		cleanWeekAndTooStrongNodesMatrix(localMatrix,matrix.rolesNames, wordsCount,rolesCount,lowScore,highScore,posID);
		
		
		double sortedmatrix[][]=sortMatrixScoreAscending(localMatrix,wordsCount,rolesCount,matrix.rolesNames,lowScore,posID);
		log.logDebug(LogCode.GENERAL_CODE.getCode(),"Sorted Matrix:");
		//System.out.println("Sorted Matrix:");
		String temp="";
		/*
		for (int t = 0; t < sortedmatrix.length; t++) {
			for (int u = 0; u < sortedmatrix[t].length; u++) {
				
				temp+=sortedmatrix[t][u]+" ";
			}
			log.logDebug(LogCode.GENERAL_CODE.getCode(),temp);
			temp="";
		//	System.out.println();
		}
		*/
		for (int role = 0; role < rolesCount; role++)// we wanna deal with roles one by one to delete candidate words from our check is word used with other roles 
		{
			for (int i = sortedmatrix.length-1; i >=0 && sortedmatrix[i][0]>Double.NEGATIVE_INFINITY; i--) {//loop from max to min on the scores to select the roles
				if (sortedmatrix[i][2]!=role) {
					continue;
				}
				if (!SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useAssociationConstraints))
				{
					semanticRelationsBinaries=mergeSemanticBinaries(semanticRelationsBinaries, (int) sortedmatrix[i][1],(int) sortedmatrix[i][2] );
				}
				else if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useAssociationConstraints)&&
						!isWordUsedwithOtherRole(semanticRelations,instance,(int) sortedmatrix[i][1],(int) sortedmatrix[i][2]))
				{
					semanticRelationsBinaries=mergeSemanticBinaries(semanticRelationsBinaries, (int) sortedmatrix[i][1],(int) sortedmatrix[i][2] );
				}
			}
			if (semanticRelationsBinaries.get(role)!=null) {
				semanticRelations.addAll(convertBinariesToSemanticRelations(semanticRelationsBinaries,instance,matrix,predicateId,role));	
			}
			
		}
		/*
		double sortedScores[]=sortScoreAscending(localMatrix,wordsCount,rolesCount);
		for (int i = sortedScores.length-1; i >=0 && sortedScores[i]>Double.NEGATIVE_INFINITY; i--) {//loop from max to min on the scores to select the roles
			tempWordID=-1;
			tempRoleID=-1;
			for (int u = 0; u < rolesCount; u++) {
				for (int v = 1; v < wordsCount; v++) {//start from 1
					if(localMatrix[v][u]==sortedScores[i])
					{
						tempWordID=v;
						tempRoleID=u;
						
					}
				}
			}
			if(tempWordID==-1 ||tempRoleID==-1) 
			{
				System.out.println("ERRRROOOOOOORR in score matrix encoding");// in case of error
				break;
			}
			if (!isWordUsedwithOtherRole(semanticRelationsBinaries,instance, tempWordID, tempRoleID))
			{
				//add (merge) to semanticRelationsBinaries
				semanticRelationsBinaries=mergeSemanticBinaries(semanticRelationsBinaries, tempWordID,  tempRoleID );
			}
			
			
		}
		*///end of old code for matrix encoding
		
		// at the end convert semanticRelationsBinaries to semanticRelations
	//	coz its already added in the last phrase but not in final version
		log.logDebug(LogCode.GENERAL_CODE.getCode(),"Semantic Binaries: "+semanticRelationsBinaries);
		//System.out.println("Semantic Binaries: "+semanticRelationsBinaries);
		//semanticRelations=convertBinariesToSemanticRelations(semanticRelationsBinaries,instance,matrix,predicateId);
		
	
			

			//temp code
			//semanticRelations.add(new SemanticRelation(predicate, instance.forms[tempj],ContextRelationType.valueOf(matrix.rolesNames.get(tempi))));

		return semanticRelations;
	}
	private List<SemanticRelation> convertBinariesToSemanticRelations(Map<Integer, List<Integer>> semanticRelationsBinaries,DependencyInstanceConll0809 instance,FeatureMatrix matrix,int predicateId, int roleKey)
	{
		List<SemanticRelation> semanticRelations= new ArrayList<SemanticRelation>();
		
		//for (Integer key : semanticRelationsBinaries.keySet()) {
			
			List<Integer>consideredId= new ArrayList<Integer>();
			String argument="";
			Object [] argumentsIds=semanticRelationsBinaries.get(roleKey).toArray();
			//Arrays.sort(argumentsIds);//no need to sort, lets start with same order which is from strong to week and keep maximum continous length
			
				if(argumentsIds.length>0)
				{
					//argument+=instance.forms[(int)argumentsIds[0]];
					consideredId.add((int)argumentsIds[0]);
					for (int i = 1; i < argumentsIds.length; i++) {
						boolean continous=false;
						for (int j = 0; j < consideredId.size(); j++) {
							if(Math.abs((int)argumentsIds[i]-consideredId.get(j))==1)
							{
								continous=true;
								break;
							}
						}
						if(continous&& !consideredId.contains((int)argumentsIds[i]))
						{
							//argument+=" "+instance.forms[(int)argumentsIds[i]];
							consideredId.add((int)argumentsIds[i]);
							i=0;//reset to restart again from begin to catch anymissing
						}
					}
				}
			
			
			Object[] consideredIds=consideredId.toArray();
			Arrays.sort(consideredIds);//this sort to have the string in correct order
			for (int i = 0; i < consideredIds.length; i++) {
				if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
					argument+=instance.splitLemmas08[(int)consideredIds[i]]+" ";
					
				}
				else
				{
					argument+=instance.lemmas[(int)consideredIds[i]]+" ";	
				}
				
			}
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				
						
				semanticRelations.add(
						new SemanticRelation(instance.splitLemmas08[predicateId]+".01", argument,predicateId,
								(int)consideredIds[0],(int)consideredIds[consideredIds.length-1],
								getSimpleHeadOfPhrase(instance,(int)consideredIds[0],(int)consideredIds[consideredIds.length-1]),
								ContextRelationType.valueOf(matrix.rolesNames.get(roleKey)))
						);
			}
			else
			{
				semanticRelations.add(
						new SemanticRelation(instance.lemmas[predicateId]+".01",argument,predicateId,
								(int)consideredIds[0],(int)consideredIds[consideredIds.length-1],
								getSimpleHeadOfPhrase(instance,(int)consideredIds[0],(int)consideredIds[consideredIds.length-1]),
								ContextRelationType.valueOf(matrix.rolesNames.get(roleKey)))
						);
				
			}
			
		//}
		return semanticRelations;
	}
	/*
	 * @ ensure result!=-1;
	 */
	private int getSimpleHeadOfPhrase(DependencyInstanceConll0809 instance, int startId, int endId)
	{
		int result=-1;
		for (int i = startId; i <= endId; i++) {
			if (instance.heads[i]<startId || instance.heads[i]>endId) //get first element points to a head out of the phrase
				return i;
		}
		
		return result;
	}
	private Map<Integer, List<Integer>> mergeSemanticBinaries(Map<Integer, List<Integer>> semanticRelationsBinaries,int wordId, int roleId )
	{
		List<Integer> wordIds=new ArrayList<Integer>();
		if(!semanticRelationsBinaries.keySet().contains(roleId))
		{ 
			wordIds.add(wordId);
			semanticRelationsBinaries.put(roleId, wordIds);
		}
		else
		{
			//for (Integer key : semanticRelationsBinaries.keySet()) {
			Integer key =roleId;
				//if(semanticRelationsBinaries.get(key)!=null && semanticRelationsBinaries.get(key).size()>0)
				//{
			 	if(semanticRelationsBinaries.get(key)==null)
			 	{
			 		wordIds.add(wordId);
			 		semanticRelationsBinaries.put(roleId, wordIds);
				}
			 	else if( !semanticRelationsBinaries.get(key).contains(wordId))//role is there, we add the word to arguments
				{
					wordIds.addAll(semanticRelationsBinaries.get(key));
					wordIds.add(wordId);
					semanticRelationsBinaries.put(roleId, wordIds);
					//break;
				}
				//}
			//}
		}
		return semanticRelationsBinaries;
		
	}
	private boolean isWordUsedwithOtherRole(List<SemanticRelation> semanticRelations,DependencyInstanceConll0809 instance,int wordId, int roleId)
	{
		boolean value=false;
		for (SemanticRelation semanticRelation : semanticRelations) {
			if (semanticRelation.getArgumentStartID()<=wordId && semanticRelation.getArgumentEndID()>=wordId) {
				value=true;
				break;
			}
		}
		/*
		for (Integer key : semanticRelationsBinaries.keySet()) {
			if(semanticRelationsBinaries.get(key)!=null && semanticRelationsBinaries.get(key).size()>0)
			{
				if(key!= roleId && semanticRelationsBinaries.get(key).contains(wordId))//another role
				{
					value=true;
					break;
				}
			}
		}
		*/
		return value;
	}
	
	private double[] sortScoreAscending(double [][]nodeTotalScore,int wordsCount,int rolesCount)
	{
		double scores[] = new double[(wordsCount-1)*rolesCount];
		for (int i = 0; i < rolesCount; i++) {
			for (int j = 1; j < wordsCount; j++) {
				scores[(i*(wordsCount-1))+j-1]=nodeTotalScore[j][i];
			}
		}
		 Arrays.sort(scores);
		 return scores;
	}
	private double[][] sortMatrixScoreAscending(double [][]nodeTotalScore,int wordsCount,int rolesCount, List<String> roleNames, double [][]lScore,int posID)
	{//it returns matrix, each row has 3 elements: score, rowID, Column ID
		double ratioScores[][] = new double[(wordsCount-1)*rolesCount][3];
		for (int i = 0; i < rolesCount; i++) {
			for (int j = 1; j < wordsCount; j++) {
				if(nodeTotalScore[j][i]>Double.NEGATIVE_INFINITY && SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useRatioComparison))
				{
					ratioScores[(i*(wordsCount-1))+j-1][0]=nodeTotalScore[j][i]/lScore[ContextRelationType.valueOf(roleNames.get(i)).ordinal()][posID];
				}
				else
				{
					ratioScores[(i*(wordsCount-1))+j-1][0]=nodeTotalScore[j][i];
				}
				ratioScores[(i*(wordsCount-1))+j-1][1]=j;
				ratioScores[(i*(wordsCount-1))+j-1][2]=i;
			}
		}
		java.util.Arrays.sort(ratioScores, new java.util.Comparator<double[]>() {
		    public int compare(double[] a, double[] b) {
		        return Double.compare(a[0], b[0]);
		    }
		});
		 
		 return ratioScores;
	}
	private double [][] copyMatrix(double [][] matrix, int rowCounts, int coulumnsCount)
	{
		double [][] localMatrix=new double[rowCounts][coulumnsCount];
		for (int i = 0; i < rowCounts; i++) {
			for (int j = 0; j < coulumnsCount; j++) {
				localMatrix[i][j]=matrix[i][j];
			}
		}
		return localMatrix;
	}
	private double [][] cleanWeekNodesMatrix(double [][] matrix, int rowCounts, int coulumnsCount, double score)
	{
		for (int i = 0; i < rowCounts; i++) {
			for (int j = 0; j < coulumnsCount; j++) {
				if(matrix[i][j]<score)
				{
					matrix[i][j]=Double.NEGATIVE_INFINITY;
				}
				
			}
		}
		return matrix;
	}
	private double [][] cleanWeekAndTooStrongNodesMatrix(double [][] matrix,List<String> roleNames, int rowCounts, int coulumnsCount, double [][]lScore, double [][]hScore,int posID)
	{
		for (int i = 0; i < rowCounts; i++) {
			for (int j = 0; j < coulumnsCount; j++) {
				if(matrix[i][j]<lScore[ContextRelationType.valueOf(roleNames.get(j)).ordinal()][posID] ||
				   matrix[i][j]>hScore[ContextRelationType.valueOf(roleNames.get(j)).ordinal()][posID])
				{
					matrix[i][j]=Double.NEGATIVE_INFINITY;
				}
			}
		}
		return matrix;
	}
	
	public void updateAcceptedConfidenceScores(double[][] acceptedConfidenceScore,int iterationID) {
		float discount=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.thresholdDiscount)*iterationID;
		threshold_Level4=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level4)-discount;
		threshold_Level3=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level3)-discount;
		threshold_Level2=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level2)-discount;
		threshold_Level1=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level1)-discount;
		for (int i = 0; i < acceptedConfidenceScore.length; i++) {
			for (int j = 0; j < acceptedConfidenceScore[i].length; j++) {
				if (i<=ContextRelationType.A2.ordinal())//A0 and A1 
				{
					trustedAcceptedScore[i][j]=acceptedConfidenceScore[i][j]*threshold_Level2;	
				}
				else if(i==ContextRelationType.AM_TMP.ordinal()||i==ContextRelationType.AM_MOD.ordinal()||i==ContextRelationType.AM_NEG.ordinal())
				{
					trustedAcceptedScore[i][j]=acceptedConfidenceScore[i][j]*threshold_Level1;
				}
				else if(i==ContextRelationType.AM_ADV.ordinal()||i==ContextRelationType.AM_DIS.ordinal()||i==ContextRelationType.AM_LOC.ordinal()||
						i==ContextRelationType.A3.ordinal()||i==ContextRelationType.A4.ordinal()||i==ContextRelationType.AM_MNR.ordinal())
				{
					trustedAcceptedScore[i][j]=acceptedConfidenceScore[i][j]*threshold_Level3;
				}
				else
				{
					trustedAcceptedScore[i][j]=acceptedConfidenceScore[i][j]*threshold_Level4;
				}
			
			}
		}
	}
	public void updateCoarseGrainAcceptedConfidenceScores(double[] acceptedConfidenceScore,int iterationID) {
		float discount=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.thresholdDiscount)*iterationID;
		threshold_Level4=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level4)-discount;
		threshold_Level3=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level3)-discount;
		threshold_Level2=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level2)-discount;
		threshold_Level1=SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.threshold_Level1)-discount;
		for (int i = 0; i < acceptedConfidenceScore.length; i++) {
				if (i<=ContextRelationType.A2.ordinal())//A0 and A1 
				{
					coarseGraintrustedAcceptedScore[i]=acceptedConfidenceScore[i]*threshold_Level2;	
				}
				else if(i==ContextRelationType.AM_TMP.ordinal()||i==ContextRelationType.AM_MOD.ordinal()||i==ContextRelationType.AM_NEG.ordinal())
				{
					coarseGraintrustedAcceptedScore[i]=acceptedConfidenceScore[i]*threshold_Level1;
				}
				else if(i==ContextRelationType.AM_ADV.ordinal()||i==ContextRelationType.AM_DIS.ordinal()||i==ContextRelationType.AM_LOC.ordinal()||
						i==ContextRelationType.A3.ordinal()||i==ContextRelationType.A4.ordinal()||i==ContextRelationType.AM_MNR.ordinal())
				{
					coarseGraintrustedAcceptedScore[i]=acceptedConfidenceScore[i]*threshold_Level3;
				}
				else
				{
					coarseGraintrustedAcceptedScore[i]=acceptedConfidenceScore[i]*threshold_Level4;
				}
		}
	}
	
}
