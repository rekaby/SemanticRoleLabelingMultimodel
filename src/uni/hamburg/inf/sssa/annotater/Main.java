/**
 * 
 */
package uni.hamburg.inf.sssa.annotater;

import java.io.IOException;
import java.util.IntSummaryStatistics;

import uni.hamburg.inf.sssa.dataset.DataSetManager;
import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.io.NomBankReader;
import uni.hamburg.inf.sssa.io.PropBankReader;
import uni.hamburg.inf.sssa.io.SemlinkReader;
import uni.hamburg.inf.sssa.io.VerbNetReader;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticManager;
import uni.hamburg.inf.sssa.util.GlobalVariables;
import uni.hamburg.inf.sssa.weights.Weights;
import uni.hamburg.inf.sssa.weights.WeightsMemory;


/**
 * @author Rekaby
 *
 */
public class Main {

	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(Main.class);

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args)  {
		
		try {
			//testElementaryTraining(args);
			testFullCycleTraining(args);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void testElementaryTraining(String[] args)
	{
		SssaFunctions.getInstance().LoadPropertyFile(args);
		//VerbNetReader.getInstance().loadVerbNet();
		//PropBankReader.getInstance().loadProbBank();
		SssaFunctions.getInstance().ReadInputData(true);
		SssaFunctions.getInstance().ReadInputData(false);
		
		SssaFunctions.getInstance().runElementryTrainCycle();
		//debug code
		double[][] scores=	SssaFunctions.getInstance().calculateTrustedConfidenceScores(0);//calculateTrustedConfidenceScores();
		for (int j = 0; j < scores.length; j++) {
			log.logInfo(LogCode.GENERAL_CODE.getCode(),""+ContextRelationType.values()[j].toString()+scores[j]);
		}
		//end of debug code		
	 
	}
	private static void testFullCycleTraining(String[] args)
	{
		SssaFunctions.getInstance().LoadPropertyFile(args);
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.readThesaurusRoles)) {
			SemlinkReader.getInstance().loadSemlink();
			VerbNetReader.getInstance().loadVerbNet();	
			PropBankReader.getInstance().loadBank();
			NomBankReader.getInstance().loadBank();
			
		}
		SssaFunctions.getInstance().ReadInputData(true);//read train
		SssaFunctions.getInstance().ReadInputData(false);//read test data
		SssaFunctions.getInstance().resetDictionarySet();
		SemanticManager.getInstance().loadTrainingDataExpectedRoles(DataSetManager.getInstance().getLabeledSet());
		((WeightsMemory)WeightsMemory.getInstance()).extendPublicWeights=true;
		SssaFunctions.getInstance().runElementryTrainCycle();
		
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Missing Vectors count:"+GlobalVariables.missingVectorsCount);
		//debug code
		//for (int l = 0; l < FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore().length; l++) {
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"B4 Unlabel Verification Accepted Score:"+Arrays.toString(FeatureMatrixEncoder.getInstance().getTrustedAcceptedScore()[l]));
		//}
		
		//for (int j = 0; j < scores.length; j++) {
		//	log.logInfo(LogCode.GENERAL_CODE.getCode(),""+scores[j]);
		//}
		//end of debug code
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Calculate Conf Scores After Elementry---------------------");
		SssaFunctions.getInstance().calculateTrustedConfidenceScores(0);
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Create Output---------------------");
		SssaFunctions.getInstance().verifyUnlabelData(1+"");//TODO Rekaby
		SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getUnlabeledSet(),0);
		((WeightsMemory)WeightsMemory.getInstance()).extendPublicWeights=false;
		
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"B4 Train Cycle...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
		//log.logInfo(LogCode.GENERAL_CODE.getCode(),"B4 Train Cycle...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());

		//System.out.println("B4 Train Cycle...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
	//	System.out.println("B4 Train Cycle...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());
		
		//for (int i = 1; i <=SssaProperties.getInstance().getIntProperty(ISssaPropertiesKeys.maxBootstrappingIterations) ; i++) {
			
		SssaFunctions.getInstance().runTrainCycle();
			
			//log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Train Cycle:"+i+" ...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
			//log.logInfo(LogCode.GENERAL_CODE.getCode(),"After Train Cycle:"+i+" ...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());

			//System.out.println("After Train Cycle:"+i+" ...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
			//System.out.println("After Train Cycle:"+i+" ...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());
		//}
			
		
		//SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getUnlabeledSet());
		
	}
	private static void testReadFullTrainingData(String[] args)
	{
		SssaFunctions.getInstance().LoadPropertyFile(args);
		VerbNetReader.getInstance().loadVerbNet();
		SssaFunctions.getInstance().ReadInputData(true);
		
		//SssaFunctions.getInstance().runElementryTrainCycle();
		//SssaFunctions.getInstance().calculateTrustedConfidenceScores();
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"B4 Train Cycle...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"B4 Train Cycle...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());

		//System.out.println("B4 Train Cycle...Label Data Size:"+DataSetManager.getInstance().labelSetSize());
		//System.out.println("B4 Train Cycle...UNLabel Data Size:"+DataSetManager.getInstance().unlabelSetSize());
		//SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getAllDataSet());
		
	}
	private static void testWriteScenarioLabelData(String[] args)
	{
		SssaFunctions.getInstance().LoadPropertyFile(args);
		VerbNetReader.getInstance().loadVerbNet();
		SssaFunctions.getInstance().ReadInputData(true);
		//SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getLabeledSet());
	}
	private static void testWriteScenarioUnLabelData(String[] args)
	{
		SssaFunctions.getInstance().LoadPropertyFile(args);
		VerbNetReader.getInstance().loadVerbNet();
		SssaFunctions.getInstance().ReadInputData(true);
		//SssaFunctions.getInstance().writeOutputData(DataSetManager.getInstance().getUnlabeledSet());
	}

}
