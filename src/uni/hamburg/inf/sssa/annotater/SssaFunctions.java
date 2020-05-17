package uni.hamburg.inf.sssa.annotater;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.SerializationUtils;










import java.util.Set;

import javax.management.relation.RoleStatus;

import uni.hamburg.inf.sssa.dataset.DataSetManager;
import uni.hamburg.inf.sssa.feature.FeatureFactory;
import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.feature.matrix.FeatureMatrix;
import uni.hamburg.inf.sssa.feature.matrix.FeatureMatrixEncoder;
import uni.hamburg.inf.sssa.feature.matrix.FeatureMatrixFactory;
import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.io.ConllReaderConll09;
import uni.hamburg.inf.sssa.io.ConllWriterConll0809;
import uni.hamburg.inf.sssa.io.NomBankReader;
import uni.hamburg.inf.sssa.io.SemlinkReader;
import uni.hamburg.inf.sssa.io.VerbNetReader;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticManager;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.semantic.VerbNetContextRelationType;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.DictionarySet;
import uni.hamburg.inf.sssa.util.GlobalVariables;
import uni.hamburg.inf.sssa.util.DictionarySet.DictionaryTypes;
import uni.hamburg.inf.sssa.util.Utils;
import uni.hamburg.inf.sssa.weights.Weights;

public class SssaFunctions {

	public DictionarySet dicts;
	public uni.hamburg.inf.sssa.util.Dictionary posDict;
	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(SssaFunctions.class);
	//public static double[] trustedScores;
	Date timeStamp= new Date();
	private static SssaFunctions instance = null;
	public static SssaFunctions getInstance() {
	      if(instance == null) {
	         instance = new SssaFunctions();
	      }
	      return instance;
	   }

	
	public void resetDictionarySet()
	{
		posDict=dicts.get(DictionaryTypes.POS);
		//System.out.println(posDict.toString());
		dicts=null;
	}

	private SssaFunctions() {
		super();
		this.dicts = DictionarySet.getInstance();
	}



	public void LoadPropertyFile(String[] args)
	{
		if (args.length>0) {
			SssaProperties.getInstance().loadProperties(args[0]);
		}
		else
		{
			SssaProperties.getInstance().loadProperties(null);
		}
	}
	public void ReadInputData(boolean train)
	{
		//int minTrainID=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.minTrainID);
		//int maxTrainID=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTrainID);
		//int minTestID=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.minTestID);
		//int maxTestID=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTestID);
		Map<String, Integer> verbNetRolesMap= new HashMap<String, Integer>();
		Map<String, Integer> semlinkVerbnetRolesMap= new HashMap<String, Integer>();
		try {
			DependencyInstanceConll0809 element=null;
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				ConllReaderConll08.getInstance().startReading(SssaProperties.getInstance().getStringProperty(train?ISssaPropertiesKeys.trainFilePath:ISssaPropertiesKeys.testFilePath));
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				ConllReaderConll09.getInstance().startReading(SssaProperties.getInstance().getStringProperty(train?ISssaPropertiesKeys.trainFilePath:ISssaPropertiesKeys.testFilePath));
			}
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				element=ConllReaderConll08.getInstance().nextInstance(train);
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				element=ConllReaderConll09.getInstance().nextInstance(train);
			}
			
			
			while(element!=null )
			{
				element.setInstIds(dicts);
				//System.out.println(element.getId()+"----------------------------------------------------------------");//TODO remove this debug line
				//load Multimodal
				if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeat))
			    {
			    	if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeatPredAssocRoles))
				    {
			    		if(SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.multiFeatPredAssocRolesApproach)==1)
					    {
			    			this.enrichPredicateAssVerbNetRoles(element);
			    		}
			    		if(SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.multiFeatPredAssocRolesApproach)==2)
					    {
			    			this.enrichPredicateAssSemlinkVerbnetRoles(element);
					    }
			    		if(SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.multiFeatPredAssocRolesApproach)==100)
					    {
			    			DependencyInstanceConll0809 temp1=(DependencyInstanceConll0809)SerializationUtils.clone(element);
			    			DependencyInstanceConll0809 temp2=(DependencyInstanceConll0809)SerializationUtils.clone(element);
			    			this.enrichPredicateAssVerbNetRoles(temp1);
			    			this.enrichPredicateAssSemlinkVerbnetRoles(temp2);
			    			for (int i = 0; i < temp1.length; i++) {
			    				System.out.println(i+":"+element.lemmas[i]+":"+element.getContextInstance().getRelatedSemanticRelationsByPredicateId(i)+":"+Arrays.toString(temp1.predicateAssociatedRolesNames[i])+Arrays.toString(temp2.predicateAssociatedRolesNames[i])+":"+temp1.predicateAssociatedRoles[i].length+":"+temp2.predicateAssociatedRoles[i].length);
			    				for (String role : temp1.predicateAssociatedRolesNames[i]) {
			    					if (verbNetRolesMap.keySet().contains(role))
			    						{verbNetRolesMap.put(role, verbNetRolesMap.get(role)+1);}
			    					else
			    						{verbNetRolesMap.put(role, 1);}	
			    				}
			    				for (String role : temp2.predicateAssociatedRolesNames[i]) {
			    					if (semlinkVerbnetRolesMap.keySet().contains(role))
			    						{semlinkVerbnetRolesMap.put(role, semlinkVerbnetRolesMap.get(role)+1);}
			    					else
			    						{semlinkVerbnetRolesMap.put(role, 1);}
			    				}
			    				
			    			}
			    			
					    }
				    }
			    	
			    }
				
				if(train)
				{
					DataSetManager.getInstance().addLabeledElement(element);
				}
				else
				{
					DataSetManager.getInstance().addUnLabeledElement(element);
				}
				DataSetManager.getInstance().addElementToAllDataset(element);
				
				if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
					element=ConllReaderConll08.getInstance().nextInstance(train);
				}
				else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
					element=ConllReaderConll09.getInstance().nextInstance(train);
				}
				
			}
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				ConllReaderConll08.getInstance().close();
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				ConllReaderConll09.getInstance().close();
			}
			if(SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.multiFeatPredAssocRolesApproach)==100)
		    {
				System.out.println(verbNetRolesMap);
				System.out.println(semlinkVerbnetRolesMap);
				
				System.exit(0);
		    }
		} catch (Exception e) {
			log.logFatal(LogCode.GENERAL_CODE.getCode(),"Reading input data is failed:",e);	
			e.printStackTrace();
		}
	}
	private static void enrichPredicateAssVerbNetRoles(DependencyInstanceConll0809 instance)
	{
		for (int i = 0; i < instance.lemmas.length; i++) {
			if (instance.ppostags[i].startsWith("V")) {
				Set<String> roles= VerbNetReader.getInstance().getVerbRolesMap().get(instance.lemmas[i]);
				roles=(roles==null?new HashSet<String>():roles);
				instance.predicateAssociatedRoles[i]=	getVerbNetRolesIds(roles==null?new HashSet<String>():roles);
				instance.predicateAssociatedRolesNames[i] = roles.toArray(new String[roles.size()]);
			}
			//else if(instance.ppostags[i].startsWith("N"))
			//{
			//	Set<String> roles= NomBankReader.getInstance().getBankRolesMap().get(instance.lemmas[i]);
			//	instance.predicateAssociatedRoles[i]=	readMultiModal(roles==null?new HashSet<String>():roles);}
			//}
			
		}
			
	}
	private static void enrichPredicateAssSemlinkVerbnetRoles(DependencyInstanceConll0809 instance)
	{
		if (instance.hasSemanticRelations())
		{
			for (int i = 0; i < instance.lemmas.length; i++) {
				if (instance.ppostags[i].startsWith("V")) {
					List<SemanticRelation> existingSemanticRelations=instance.getContextInstance().getRelatedSemanticRelationsByPredicateId(i);
					if (existingSemanticRelations== null ||existingSemanticRelations.size()==0)
						{continue;}
					Set<String> semanticRelationsTypes=new HashSet<>();
					for (SemanticRelation semanticRelation:existingSemanticRelations)
					{
						semanticRelationsTypes.add(semanticRelation.getType().name());
					}
					Set<String> roles= SemlinkReader.getInstance().getRelatedVerbnetSemlinkRoles(instance.lemmas[i],semanticRelationsTypes);
					roles=(roles==null?new HashSet<String>():roles);
					instance.predicateAssociatedRoles[i]=	getVerbNetRolesIds(roles==null?new HashSet<String>():roles);
					instance.predicateAssociatedRolesNames[i] = roles.toArray(new String[roles.size()]);
					}
				
			}
		}
	}
	private static Integer[] getVerbNetRolesIds(Set<String> roles)
	{
		Set<Integer> roleIds=new HashSet<Integer>();
		
		for (String role : roles) {
			try {
			roleIds.add(VerbNetContextRelationType.valueOf(role).ordinal());
			} catch (IllegalArgumentException e) {
			//	e.printStackTrace();//Comment it in the future
			}
		}
		return roleIds.toArray(new Integer[roleIds.size()]);
	}
	public void writeOutputData(List<DependencyInstanceConll0809> elements,int iterationId)
	{
		try {
			SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");//dd/MM/yyyy
		    
		    String strDate = sdfDate.format(timeStamp);
		    if (iterationId==0) {
		    	savePrperties(strDate,iterationId);	
			}
			ConllWriterConll0809.getInstance().startWriting(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.outFilePath).replace(".txt","-"+strDate+"-"+iterationId+".txt"));
			for (int i = 0; i < elements.size()&& i<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTestID); i++) {
				ConllWriterConll0809.getInstance().writeInstance(elements.get(i));
			}
			ConllWriterConll0809.getInstance().close();
			
			
			Process process =Runtime.getRuntime().exec("perl \"C:/Users/Amr_Local/Desktop/Joint Parsing Papers and Data/Joint Shared Tasks 08-09/2009/Evaluation2/eval09.pl\" "
					//+ "-g \"C:/Users/Amr_Local/Desktop/Joint Parsing Papers and Data/Joint Shared Tasks 08-09/2009/Evaluation/CoNLL2009-ST-evaluation-English.txt\""+
					+" -g \""+SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.testFilePath)+"\""
					+" -s \""+SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.outFilePath).replace(".txt","-"+strDate+"-"+iterationId+".txt")+"\""
					+" -o \""+SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.outFilePath).replace(".txt","-"+strDate+"-"+iterationId+"score.txt")+"\"");
			/*process.waitFor();
			if(process.exitValue() == 0)
			{
			System.out.println("Command Successful");
			}
			else
			{
				BufferedReader errinput = new BufferedReader(new InputStreamReader(
						process.getErrorStream()));
				System.out.println(errinput.readLine());
			System.out.println("Command Failure");
			}
			*/
		} catch (Exception e) {
			//System.out.println("Writing output data is failed:");
			log.logFatal(LogCode.GENERAL_CODE.getCode(),"Writing output data is failed:",e);	

			e.printStackTrace();
		}
		
	}
	public void savePrperties(String strDate,int iterationId) {
	    try {
	        Properties props = SssaProperties.getInstance().getProperties();
	        File f = new File(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.outFilePath).replace(".txt","-"+strDate+"-"+iterationId+".prop"));
	        OutputStream out = new FileOutputStream( f );
	        props.store(out, "This is a Property file");
	        out.close();
	    }
	    catch (Exception e ) {
	        e.printStackTrace();
	    }
	}
	public double[][] calculateTrustedConfidenceScores(int iterationID)
	{
		float[][] sumScore= new float [ContextRelationType.Context_Relation_Type_End.ordinal()][posDict.size()];//Double.POSITIVE_INFINITY;
		float[][] countScore=new float [ContextRelationType.Context_Relation_Type_End.ordinal()][posDict.size()];//Double.NEGATIVE_INFINITY;
		ArrayList<Float>[][] actualScores= new ArrayList[ContextRelationType.Context_Relation_Type_End.ordinal()][posDict.size()];
		
		double[][] averageScore= new double [ContextRelationType.Context_Relation_Type_End.ordinal()][posDict.size()];
		double[] coarseGrainAverageScore= new double [ContextRelationType.Context_Relation_Type_End.ordinal()];
		FeatureMatrix featureMatrix=null;
		List<SemanticRelation> goldSemanticRelations=null;
		float[][][] tempSumCountScore;//[Role][pos][sum and count]
		for (int i = 0; i < sumScore.length; i++) {
			for (int j = 0; j < sumScore[i].length; j++) {
				sumScore[i][j]=0;
				countScore[i][j]=0;	
				actualScores[i][j]=new ArrayList<Float>();
			}
		}
		
		for (int j = 0; j < DataSetManager.getInstance().getLabeledSet().size()&& j<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTrainID); j++) {
			DependencyInstanceConll0809 instance = DataSetManager.getInstance().getLabeledSet().get(j);
			Map<Integer, String> predicateSetAndId= instance.getContextInstance().getUniquePredicateSetAndIds();
			for (Iterator iterator = predicateSetAndId.keySet().iterator(); iterator.hasNext();) 
			{
				Integer predicateId = (Integer) iterator.next();
				String  predicate = (String) predicateSetAndId.get(predicateId);
				
				//if you speedup then it builds empty matrices then calculate on the fly, but don't consider the # of missing vectors in the log
				if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.thresholdSpeedup)) {
					featureMatrix=FeatureMatrixFactory.getInstance().buildEmptyFeatureMatrix(instance, predicateId,predicate);
				}
				else
				{
					featureMatrix=FeatureMatrixFactory.getInstance().buildFeatureMatrix(instance, predicateId,predicate);					
				}

				
				featureMatrix.calculateMatrixScore(predicateId);
				goldSemanticRelations=instance.contextInstance.getRelatedSemanticRelationsByPredicateId(predicateId);
				tempSumCountScore=FeatureMatrixEncoder.getInstance().getRelationScoresPerSemanticTypeAndPOS(featureMatrix, instance, goldSemanticRelations);
				for (int i = 0; i < tempSumCountScore.length; i++) {
					for (int k = 0; k < tempSumCountScore[i].length; k++) {
						sumScore[i][k]+=tempSumCountScore[i][k][0];
						countScore[i][k]+=tempSumCountScore[i][k][1];
						if (tempSumCountScore[i][k][0]!=0||tempSumCountScore[i][k][1]!=0) {
						//	if (i==4) {
						//		System.out.println("DEBUG");
						//	}
							actualScores[i][k].add(tempSumCountScore[i][k][0]/tempSumCountScore[i][k][1]);
			//				System.out.println(tempSumCountScore[i][k][0]);
							//System.out.println(tempSumCountScore[i][k][1]);	
						}
					}
				}
				featureMatrix=null;
			}
		}
		for (int i = 0; i < averageScore.length; i++) {
			for (int j = 0; j < averageScore[i].length; j++) {
				averageScore[i][j]=(sumScore[i][j]!=0 && countScore[i][j]!=0)?sumScore[i][j]/countScore[i][j]:  Double.POSITIVE_INFINITY;	
				if (actualScores[i][j].size()>0) {
					log.logInfo(LogCode.GENERAL_CODE.getCode(),ContextRelationType.values()[i].name()+ " with "+posDict.writeFirstkeyForAvalue(j)+ " variance\t"+
							calculateVariance(actualScores[i][j], averageScore[i][j])+"\t"+averageScore[i][j]+"\t"+actualScores[i][j].size());
				}
			}
		}
		for (int i = 0; i < averageScore.length; i++) {
			float count=0f;
			float sum=0f;
			ArrayList<Float> CoarseActualValues= new ArrayList<Float>();
			for (int j = 0; j < averageScore[i].length; j++) {
				sum+=(sumScore[i][j]!=0 && countScore[i][j]!=0)?sumScore[i][j]:0;
				count+=(sumScore[i][j]!=0 && countScore[i][j]!=0)?countScore[i][j]:0;
				CoarseActualValues.addAll(actualScores[i][j]);
			}
			coarseGrainAverageScore[i]=(sum!=0 && count!=0)?sum/count:  Double.POSITIVE_INFINITY;
			if (CoarseActualValues.size()>0) {
				log.logInfo(LogCode.GENERAL_CODE.getCode(),ContextRelationType.values()[i].name()+ " with - "+ " variance\t"+
						calculateVariance(CoarseActualValues, coarseGrainAverageScore[i])+"\t"+coarseGrainAverageScore[i]+"\t"+CoarseActualValues.size());
			}
		}
		//System.out.println(Arrays.toString(averageScore));
		FeatureMatrixEncoder.getInstance().updateAcceptedConfidenceScores(averageScore,iterationID);//update with the average
		FeatureMatrixEncoder.getInstance().updateCoarseGrainAcceptedConfidenceScores(coarseGrainAverageScore,iterationID);//update with the average
		return averageScore;
	}
	private float calculateVariance(ArrayList<Float> values, double average)
	{
		if (values==null || values.size()==0) {
			return 0;
		}
		float temp = 0;
		for(float a :values)
	        temp += (a-average)*(a-average);
	    return temp/(values.size());
	}
	
	/*
	public void calculateTrustedConfidenceScoresBackup()
	{
		double[] minAcceptedScore= new double [ContextRelationType.Context_Relation_Type_End.ordinal()];//Double.POSITIVE_INFINITY;
		double[] maxAcceptedScore=new double [ContextRelationType.Context_Relation_Type_End.ordinal()];//Double.NEGATIVE_INFINITY;
		double[] averageScore= new double [ContextRelationType.Context_Relation_Type_End.ordinal()];
		for (int i = 0; i < maxAcceptedScore.length; i++) {
			minAcceptedScore[i]=Double.POSITIVE_INFINITY;
			maxAcceptedScore[i]=Double.NEGATIVE_INFINITY;
		}
				
		for (int j = 0; j < DataSetManager.getInstance().getLabeledSet().size()&& j<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTrainID); j++) {
			DependencyInstanceConll0809 instance = DataSetManager.getInstance().getLabeledSet().get(j);
			Map<Integer, String> predicateSetAndId= instance.getContextInstance().getUniquePredicateSetAndIds();
			for (Iterator iterator = predicateSetAndId.keySet().iterator(); iterator.hasNext();) 
			{
				Integer predicateId = (Integer) iterator.next();
				String  predicate = (String) predicateSetAndId.get(predicateId);
				FeatureMatrix featureMatrix=FeatureMatrixFactory.getInstance().buildFeatureMatrix
						(instance, predicateId,predicate);
				featureMatrix.calculateMatrixScore(predicateId);
				List<SemanticRelation> goldSemanticRelations=instance.contextInstance.getRelatedSemanticRelationsByPredicateId(predicateId);
				double[][] tempMinMaxScore=FeatureMatrixEncoder.getInstance().getMinMaxRelationScoreBackup(featureMatrix, instance, goldSemanticRelations);
				for (int i = 0; i < tempMinMaxScore.length; i++) {
					if(minAcceptedScore[i]>tempMinMaxScore[i][0])
					{
						minAcceptedScore[i]=tempMinMaxScore[i][0];
					}
					if(maxAcceptedScore[i]<tempMinMaxScore[i][1])
					{
						maxAcceptedScore[i]=tempMinMaxScore[i][1];
					}
				}
				
			}
			
		}
		for (int i = 0; i < averageScore.length; i++) {
			averageScore[i]=(minAcceptedScore[i]+maxAcceptedScore[i])/2;
		}
		FeatureMatrixEncoder.getInstance().updateAcceptedConfidenceScores(averageScore);//update with the average
		//return averageScore;
	}
	*/
	public void runElementryTrainCycle()
	{
		for(int i=0;i<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.elementaryIterationCount);i++)
		{
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"Elementry phase starts!!");
			float totalCycleSum=0;
			FeatureMatrix featureMatrix;
			for (int j = 0; j < DataSetManager.getInstance().getLabeledSet().size()&& j<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTrainID); j++) 
			{
				if (j%8000==0) {
					log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...Record:"+j+ " # of weights:"+ Weights.getInstance().getPublicWeights().size());
				}
				DependencyInstanceConll0809 instance = DataSetManager.getInstance().getLabeledSet().get(j);
				Map<Integer, String> predicateSetAndId= instance.getContextInstance().getUniquePredicateSetAndIds();
				for (Iterator iterator = predicateSetAndId.keySet().iterator(); iterator.hasNext();) 
				{
					Integer predicateId = (Integer) iterator.next();
					String  predicate = (String) predicateSetAndId.get(predicateId);
					
					featureMatrix=FeatureMatrixFactory.getInstance().buildFeatureMatrix(instance, predicateId,predicate);
					//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...calculateMatrixScore:");
					featureMatrix.calculateMatrixScore(predicateId);
					
					//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...getMaxMatrixRelations:");
					List<SemanticRelation> semanticRelations=FeatureMatrixEncoder.getInstance().getMaxMatrixRelations(featureMatrix,instance,predicateId,true);
					List<SemanticRelation> goldSemanticRelations=instance.contextInstance.getRelatedSemanticRelationsByPredicateId(predicateId);
					
					log.logDebug(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...Expected Roles:"+semanticRelations);
					log.logDebug(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...Gold Roles:"+goldSemanticRelations);
					//System.out.println("In Elementary Phase...Expected Roles:"+semanticRelations);
					//System.out.println("In Elementary Phase...Gold Roles:"+goldSemanticRelations);
					
					//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...combineAllRelatedFeatures:");
					for (String roleName : featureMatrix.getRolesNames()) {
						ContextRelationType relation=ContextRelationType.valueOf(roleName);
						FeatureVector allFeatures=combineAllRelatedFeatures(instance,featureMatrix,goldSemanticRelations,semanticRelations,relation);
						//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...getNormalizedDotProducts:");
						float loseSum= - getNormalizedDotProducts(featureMatrix,instance ,allFeatures);
						loseSum=Math.abs(loseSum);
						// loseSum+=getNormalizedDotProducts(featureMatrix,instance ,goldSemanticRelations);//no need for it now after combining the features
						//log.logInfo(LogCode.GENERAL_CODE.getCode(),"Elementry LOSS "+j+":\t"+loseSum);
						log.logDebug(LogCode.GENERAL_CODE.getCode(),"Weights "+j+":\t"+Weights.getInstance().getPublicWeights().size());
					
						totalCycleSum+=loseSum;
						//System.out.println("LOSS: "+loseSum);
					 
						int hammingDistance=0;//SemanticManager.getInstance().getHammingDistance(goldSemanticRelations, semanticRelations);
					
						//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Elementary Phase...updatePublicWeights:");
						Weights.getInstance().updatePublicWeights(allFeatures,hammingDistance, loseSum);
						allFeatures=null;
					}
					//featureMatrix=null;
					
				}
				if (j%1000==999) {
				//	log.logInfo(LogCode.GENERAL_CODE.getCode(),"Count of weights"+":\t"+Weights.getInstance().getPublicWeights1().size()+" "+Weights.getInstance().getPublicWeights2().size());
					System.gc();
				}
			}
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"------Iteration LOSS: "+i+" "+totalCycleSum);
		}
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Elementry, weights Vector size :"+Weights.getInstance().getPublicWeights().size());
		
	}
	private double getNormalizedDotProducts(FeatureMatrix featureMatrix,DependencyInstanceConll0809 instance,List<SemanticRelation> semanticRelations)
	{
		FeatureVector fv= new FeatureVector();
		double value=0;
		for (int i = 0; i < semanticRelations.size(); i++) {
			SemanticRelation relation = semanticRelations.get(i);
			fv.addEntries(getRelatedFeatures(instance,featureMatrix,relation),1);
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useDiskStorage)) {
			value=fv.dotProduct(fv,Weights.getInstance().getPublicWeights(fv));//,Weights.getInstance().getPublicWeights2());
		}
		else
		{
			value=fv.dotProduct(fv,Weights.getInstance().getPublicWeights());//,Weights.getInstance().getPublicWeights2());
		}
		value=value/SemanticManager.getInstance().countUniqueSemanticArgument(semanticRelations);
		return value;
	}
	private float getNormalizedDotProducts(FeatureMatrix featureMatrix,DependencyInstanceConll0809 instance,FeatureVector combinedFv)
	{
		
		float value=0;
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useDiskStorage)) {
			value=combinedFv.dotProduct(combinedFv,Weights.getInstance().getPublicWeights(combinedFv));//,Weights.getInstance().getPublicWeights2());
		}
		else
		{
			value=combinedFv.dotProduct(combinedFv,Weights.getInstance().getPublicWeights());//,Weights.getInstance().getPublicWeights2());
		}
		
		if (combinedFv!=null && combinedFv.size()!=0&& value==0f) {//we do that to get out of initial equal weights trap
			System.out.println("we do that to get out of initial equal weights trap");
			value=-SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.C);
			return value;
		}
		//if (hasPositiveAndNegativeFeatures(combinedFv) && value>0) {//here is passive form, when already Gold is higher than retrieved, and also its not only Gold
			//value=0f;
		//	return value;
		//}
		//value=value/SemanticManager.getInstance().countUniqueSemanticArgument(semanticRelations);
		return value;
	}
	private boolean hasPositiveAndNegativeFeatures(FeatureVector combinedFv)
	{
		boolean negative=false, positive=false;
		for (long key : combinedFv.getFeatureMap().keys()) 
		{
			if (combinedFv.getFeatureMap().get(key)>0) {
				positive=true;
			}
			if (combinedFv.getFeatureMap().get(key)<0) {
				negative=true;
			}
			if (positive&&negative) {
				return true;
			}
		}
		return negative&& positive;
	}
	public void runTrainCycle()
	{
		//timeStamp = new Date();
		int trainingIterationCount=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.trainingIterationCount);
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------TRAIN LABEL DATA"+"---------------------");
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Calculate Conf Scores"+"---------------------");
		//trustedScores=
		GlobalVariables.missingVectorsCount=0;
		//TODO open this if you closed output after elementry
		//calculateTrustedConfidenceScores(0);
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Missing Vectors count:"+GlobalVariables.missingVectorsCount);
		/*for (int i = 0; i < FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore().length; i++) {
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Elementry Phase acceptance scores:"+Arrays.toString(FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[i]));
		}*/
		for (int i = 1; i <=trainingIterationCount ; i++) {
			float learningSpeed= SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.learningPace);//*i
			SssaProperties.getInstance().setStringProperty(ISssaPropertiesKeys.C, ""+(SssaProperties.getInstance().getfloatProperty(ISssaPropertiesKeys.C)+learningSpeed));
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------TRAIN LABEL DATA "+i+" ---------------------");
			trainLabeledData(i);
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Training, weights Vector size :"+Weights.getInstance().getPublicWeights().size());
		
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------VERIFY UNLABEL DATA "+i+" ---------------------");
			//System.out.println("---------------VERIFY UNLABEL DATA"+"---------------------");
			
			//trustedScores=
			GlobalVariables.missingVectorsCount=0;
			calculateTrustedConfidenceScores(i);
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Missing Vectors count:"+GlobalVariables.missingVectorsCount);
			
			verifyUnlabelData(1+"");//TODO Rekaby
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Testing, weights Vector size :"+Weights.getInstance().getPublicWeights().size());
		
		
			SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getUnlabeledSet(),i);
		}
	}
	private void trainLabeledData(int iterationId)
	{
		//read all labeled data, train it and update the weights
		float totalLoss=0;
		GlobalVariables.fetchedRelationsCount=0;
		GlobalVariables.goldRelationsCount=0;
		for (int i = 0; i < DataSetManager.getInstance().getLabeledSet().size()&& i<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTrainID); i++) {
			DependencyInstanceConll0809 instance = DataSetManager.getInstance().getLabeledSet().get(i);
		//	System.out.println("---------------TRAIN LABEL DATA: INSTANCE "+i+"  ID:"+instance.getId()+"---------------------");
			totalLoss+=trainLabeledInstance(instance);
			if (i%8000==0) {
				log.logInfo(LogCode.GENERAL_CODE.getCode(),"In training Phase...Record:"+i+ " "+ Weights.getInstance().getPublicWeights().size());
			}
			//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------TRAIN LABEL DATA: INSTANCE "+i+" totalLoss:\t"+totalLoss+" "+Weights.getInstance().getPublicWeights().size());
			//log.logInfo(LogCode.GENERAL_CODE.getCode(),"Weights "+i+":\t"+Weights.getInstance().getPublicWeights().size());
		}
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Complete TRAIN Cycle Loss "+iterationId+":\t"+totalLoss+"\t---------------------");
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Complete TRAIN Cycle Loss "+iterationId+":\t fetched:"+GlobalVariables.fetchedRelationsCount+"\t Gold:"+GlobalVariables.goldRelationsCount+"---------------------");
	}
	private float trainLabeledInstance(DependencyInstanceConll0809 instance)
	{
		Map<Integer, String> predicateMap=instance.getContextInstance().getUniquePredicateSetAndIds();
		float totalLoss=0;
		for (Iterator iterator = predicateMap.keySet().iterator(); iterator.hasNext();) 
		{
			Integer predicateId=(Integer)iterator.next();
			String predicate = (String) predicateMap.get(predicateId);
			
			FeatureMatrix featureMatrix=FeatureMatrixFactory.getInstance().buildFeatureMatrix(instance, predicateId,predicate);
			
			featureMatrix.calculateMatrixScore(predicateId);
			//System.out.println("Instance "+instance.length+" "+featureMatrix.getNodeTotalScore().toString());
			
			//log.logDebug(LogCode.GENERAL_CODE.getCode(),"Train Matrix For Instance:"+instance.toString()+" Predicate:"+predicate);
			//System.out.println("Train Matrix For Instance:"+instance.toString()+" Predicate:"+predicate);
			//log.logDebug(LogCode.GENERAL_CODE.getCode(),featureMatrix.toString());
			
			//System.out.println(featureMatrix);
			
			List<SemanticRelation> semanticRelations;
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPhraseIdentification)) {
				semanticRelations=FeatureMatrixEncoder.getInstance().getTrustedMatrixRelations(featureMatrix,instance,predicateId);
			}
			else
			{
				semanticRelations=FeatureMatrixEncoder.getInstance().getMaxMatrixRelations(featureMatrix,instance,predicateId,false);
			}
			
			List<SemanticRelation> goldSemanticRelations=instance.contextInstance.getRelatedSemanticRelationsByPredicateId(predicateId);
			
			//for debug code
			GlobalVariables.fetchedRelationsCount+=semanticRelations.size();
			GlobalVariables.goldRelationsCount+=goldSemanticRelations.size();	
			//end of debug code
			log.logDebug(LogCode.GENERAL_CODE.getCode(),"In Train Label Phase ...Expected Relation:"+semanticRelations);
			// System.out.println("In Train Label Phase ...Expected Relation:"+semanticRelations);
			log.logDebug(LogCode.GENERAL_CODE.getCode(),"In Train Label Phase ...Gold Relation:"+goldSemanticRelations);	
			//	System.out.println("In Train Label Phase ...Gold Relation:"+goldSemanticRelations);	
			
			for (String roleName : featureMatrix.getRolesNames()) {
				ContextRelationType relation=ContextRelationType.valueOf(roleName);
				FeatureVector allFeatures=combineAllRelatedFeatures(instance,featureMatrix,goldSemanticRelations,semanticRelations,relation);
				float loseSum=- getNormalizedDotProducts(featureMatrix,instance ,allFeatures);
				loseSum=Math.abs(loseSum);
				 //loseSum+=getNormalizedDotProducts(featureMatrix,instance ,goldSemanticRelations);
				//log.logDebug(LogCode.GENERAL_CODE.getCode(),"Train LOSS: "+instance.getId()+" "+loseSum);
				//System.out.println("LOSS: "+loseSum);
				totalLoss+=loseSum;
				int hammingDistance=0;//SemanticManager.getInstance().getHammingDistance(goldSemanticRelations, semanticRelations);
				Weights.getInstance().updatePublicWeights(allFeatures,hammingDistance,loseSum);
			}
		}
		return totalLoss;
	}
	public void verifyUnlabelData(String iterationId)
	{
		for (int i = 0; i < DataSetManager.getInstance().getUnlabeledSet().size()&& i<SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxTestID); i++) {
			DependencyInstanceConll0809 instance = DataSetManager.getInstance().getUnlabeledSet().get(i);
			//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------VERIFY UNLABEL DATA: INSTANCE "+i+"  ID");
			//System.out.println("---------------VERIFY UNLABEL DATA: INSTANCE "+i+"  ID:"+instance.getId()+"---------------------");
			
			verifyUnlabelInstance(instance,iterationId);
		}
	}
	private void verifyUnlabelInstance(DependencyInstanceConll0809 instance, String iterationId)//iterationId not needed at the moment
	{
		List<SemanticRelation> trustedSemanticRelations=new ArrayList<SemanticRelation>();
		List<SemanticRelation> justTrustedSemanticRelations=new ArrayList<SemanticRelation>();
		for (int i=1;i< instance.lemmas.length;i++) {
			String predicate =  "";
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				predicate=instance.splitLemmas08[i];
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009"))//2009 case
			{
				predicate=instance.lemmas[i];
			}
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.predicatePreIdentification)&&
			  (
			   (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")&&instance.predList[i].equals("_"))||
			   (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")&&instance.fillPred09[i].equals("_"))
			  ))
			{
				continue;//ignore if we have already the list of predicate
			}
			//if(instance.deprels[i].contains("aux")){continue;}//handle special case of aux verbs, we should not annotate it
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")&&
				! ((SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("V")&&instance.splitPpos08[i].startsWith("V"))
			   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("N")&&instance.splitPpos08[i].startsWith("N"))
			   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("B"))))
			{
				continue;//ignore false predicates
			}
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")&&
					! ((SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("V")&&instance.ppostags[i].startsWith("V"))
				   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("N")&&instance.ppostags[i].startsWith("N"))
				   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("B"))))
				{
					continue;//ignore false predicates
				}
			if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")&&
					SemanticManager.getInstance().getExpectedRoles(predicate,instance.splitPpos08[i]).size()==0)continue;//no roles for this predicate
			if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")&&
					SemanticManager.getInstance().getExpectedRoles(predicate,instance.ppostags[i]).size()==0)continue;//no roles for this predicate
			FeatureMatrix featureMatrix=FeatureMatrixFactory.getInstance().buildFeatureMatrix(instance,i,predicate);//just build matrix with word ID
			featureMatrix.calculateMatrixScore(i);//this logic not needed  instance.getWordID(Utils.extractStringFromAlphaNum(predicate),Utils.extractIntFromAlphaNum(predicate)));
			
			//log.logDebug(LogCode.GENERAL_CODE.getCode(),"UnLabeled Matrix For Instance:"+instance.toString()+" Predicate:"+i);
			//log.logDebug(LogCode.GENERAL_CODE.getCode(),featureMatrix.toString());
			//System.out.println("UnLabeled Matrix For Instance:"+instance.toString()+" Predicate:"+i);
			//System.out.println(featureMatrix);
			
			if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.parameterPhraseIdentification)) {
				trustedSemanticRelations.addAll(FeatureMatrixEncoder.getInstance().getTrustedMatrixRelations(featureMatrix,instance,i));
			}
			else
			{
				trustedSemanticRelations.addAll(FeatureMatrixEncoder.getInstance().getMaxMatrixRelations(featureMatrix,instance,i,false));
			}
			/*
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useLowThreshold))
			{
				justTrustedSemanticRelations.addAll(FeatureMatrixEncoder.getInstance().getJustTrustedMatrixRelations
						(featureMatrix,instance,predicate+"."+String.format("%02d",SemanticManager.getInstance().getSuitablePredicateID(instance, i))));	
			}
			*/
		}
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"In Testing Phase...TRUSTED RELATIONS FOUND:"+trustedSemanticRelations);
		
		//if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useLowThreshold))
		//{
		//	log.logDebug(LogCode.GENERAL_CODE.getCode(),"Just TRUSTED RELATIONS FOUND:"+justTrustedSemanticRelations);
		//}
		// System.out.println("TRUSTED RELATIONS FOUND:"+trustedSemanticRelations);
		// System.out.println("Just TRUSTED RELATIONS FOUND:"+justTrustedSemanticRelations);
		 
		instance.setContextInstance(new ContextInstance());
		if(trustedSemanticRelations!=null && trustedSemanticRelations.size()>0)
		{
			//if(instance.getContextInstance()==null)
			//{//TODO always set it to empty object
			//instance.setContextInstance(new ContextInstance());
			//}
			//instance.setIterationId(iterationId);
			instance.getContextInstance().addSemanticRelations(trustedSemanticRelations);
			//instance.getContextInstance().mergeSemanticRelations(trustedSemanticRelations);
			//DataSetManager.getInstance().removeUnLabeledElement(instance);
			//DataSetManager.getInstance().mergeIntoList(instance, DataSetManager.getInstance().getLabeledSet());
			//DataSetManager.getInstance().mergeIntoList(instance, DataSetManager.getInstance().getProbLabeledSet());
		}
		/*
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useLowThreshold)&& justTrustedSemanticRelations!=null && justTrustedSemanticRelations.size()>0)
		{
			
			//move to Temp list
		}
		*/
	}
	private void verifyTemplabelData()
	{
//		DataSetManager.getInstance().mergeIntoList(element, list);
	}
	/*
	private void verifyProplabelData()
	{
		for (int i = 0; i < DataSetManager.getInstance().getProbLabeledSet().size(); i++) {
			DependencyInstanceConll0809 instance = DataSetManager.getInstance().getProbLabeledSet().get(i);
			log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------VERIFY PROP DATA: INSTANCE "+i+"  ID:"+instance.getId()+"---------------------");
			
			//System.out.println("---------------VERIFY PROP DATA: INSTANCE "+i+"  ID:"+instance.getId()+"---------------------");
			verifyUnlabelInstance(instance,instance.getIterationId());//same logic
		}
	}*/
	private FeatureVector combineAllRelatedFeatures(DependencyInstanceConll0809 instance,FeatureMatrix featureMatrix,List<SemanticRelation> goldSemanticRelation,List<SemanticRelation> actualSemanticRelation, ContextRelationType relationType)
	{//This doesn't really combine, it add negative then override it with positive feature in case of overlapping 
		FeatureVector fv= new FeatureVector();
		
		for (int i = 0; i < actualSemanticRelation.size(); i++) {
			SemanticRelation relation = actualSemanticRelation.get(i);
			if (relationType.name().equals(relation.getType().name())) {
				fv.addEntries(getRelatedFeatures(instance,featureMatrix,relation),-1);
			}
		}
		for (int i = 0; i < goldSemanticRelation.size(); i++) {
			SemanticRelation relation = goldSemanticRelation.get(i);
			//fv.addEntries(getRelatedFeatures(instance,featureMatrix,relation),1);
			if (relationType.name().equals(relation.getType().name())) {
				fv.addEntriesIfAbsentOrRemove(getRelatedFeatures(instance,featureMatrix,relation),1);
			}
		}
		
		return fv;
	}
	public FeatureVector getRelatedFeatures(DependencyInstanceConll0809 instance,FeatureMatrix featureMatrix,SemanticRelation semanticRelation)
	{
		FeatureVector fv= new FeatureVector();
		FeatureVector fetchedVector= new FeatureVector();
		//List<Integer> argumentIDs=instance.getWordsIDs(semanticRelation.getArgument(),semanticRelation.getArgumentID());
		String roleName=semanticRelation.getType().name();
		int roleID=featureMatrix.getRolesNames().indexOf(roleName);
		int predicateId=semanticRelation.getPredicateid();
		if (roleID==-1) {
			System.out.println(roleName+instance);
		}
		for (int i = semanticRelation.getArgumentStartID(); i <= semanticRelation.getArgumentEndID(); i++) {
			try {
				if(featureMatrix.getMatrix()[i][roleID]!=null)
					fetchedVector=  featureMatrix.getMatrix()[i][roleID];
					if (fetchedVector==null || fetchedVector.size()==0) {//in this case means the word was missed in matrix creation coz of pruning
						fetchedVector=   FeatureFactory.getInstance().buildFeatureVector(instance,i , predicateId, ContextRelationType.valueOf(roleName));
					}
					fv.addEntries(fetchedVector);	
			} catch (Exception e) {
				e.printStackTrace();
				log.logError(LogCode.GENERAL_CODE.getCode(), "argumentIDs.get(i)"+i+" roleID"+roleID,e);
				//System.out.println("argumentIDs.get(i)"+argumentIDs.get(i));
				//System.out.println("roleID"+roleID);
				//System.out.println("."+featureMatrix.getMatrix()[argumentIDs.get(i)][roleID]);
				//System.out.println("."+featureMatrix.getMatrix()[argumentIDs.get(i)][roleID].getFeatureMap());
				//System.out.println("."+featureMatrix.getMatrix()[argumentIDs.get(i)][roleID].getFeatureMap().keySet());
				
				
			}
				
		}
		
		
		return fv;
	}
}
