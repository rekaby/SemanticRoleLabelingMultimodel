package uni.hamburg.inf.sssa.properties;

public interface ISssaPropertiesKeys {

	public static String dataFormat="Data_Format";
	public static String verbnetFolderPath="Verbnet_Folder_Path";
	public static String probBankFolderPath="ProbBank_Folder_Path";
	public static String nomBankFolderPath="NomBank_Folder_Path";
	public static String semLinkFilePath="Semlink_File_Path";
	public static String trainFilePath="Train_File_Path";
	public static String testFilePath="Test_File_Path";
	public static String outFilePath="Out_File_Path";
	public static String threshold_Level4="Threshold_Level4";
	public static String threshold_Level3="Threshold_Level3";
	public static String threshold_Level2="Threshold_Level2";
	public static String threshold_Level1="Threshold_Level1";
	public static String thresholdSpeedup="Threshold_Calc_Speedup";
	
	public static String useLowThreshold="Use_Just_Trusted_Threshold";
	public static String gamma="Gamma";
	public static String C="C";
	public static String elementaryIterationCount="Elementary_Iteration_Count";
	public static String trainingIterationCount="Training_Iteration_Count";
	public static String randomWeights="Random_Weights";
	//public static String maxBootstrappingIterations="Max_Bootstrapping_Iterations";
	public static String useRatioComparison="Use_Normalized_Comparison";
	public static String minTrainID="Min_Train_ID";
	public static String maxTrainID="Max_Train_ID";
	public static String minTestID="Min_Test_ID";
	public static String maxTestID="Max_Test_ID";
	
	public static String coarseGrainAverage="Coarse_Grain_Average";
	public static String predicateType="Predicate_Type";
	public static String data2008Open="Data2008_Open";
	public static String parameterPruning="Parameter_Pruning";
	public static String parameterPruningSecondLevel="Parameter_Pruning_Second_Level";
	public static String readThesaurusRoles="Read_Thesaurus_Roles";
	public static String useThesaurusRoles="Use_Thesaurus_Roles";
	public static String loadAllSemanticRoles="Load_All_Semantic_Roles";
	public static String useAssociationConstraints="Use_Association_Constraints";
	public static String parameterPhraseIdentification="Parameter_Phrase_Identification";
	public static String predicatePreIdentification="Predicate_PreIdentification";
	
	public static String featCoreUtterance="Feat_Core_Utterance";
	public static String featCoreUtteranceHead="Feat_Core_UtteranceHead";
	public static String featCoreUtteranceNeighbours="Feat_Core_UtteranceNeighbours";
	public static String featCorePredicate="Feat_Core_Predicate";
	public static String featCorePredicateHead="Feat_Core_PredicateHead";
	public static String featCorePredicateNeighbours="Feat_Core_PredicateNeighbours";
	public static String featUtteranceBigram="Feat_Utterance_Bigram";
	public static String featUtteranceTrigram="Feat_Utterance_Trigram";
	public static String featPredicateBigram="Feat_Predicate_Bigram";
	public static String featPredicateTrigram="Feat_Predicate_Trigram";
	public static String feat1OUtterancePredicate="Feat_1OUtterancePredicate";
	public static String feat1OUtteranceHead="Feat_1OUtteranceHead";
	public static String feat1OBigramUtterancePredicate="Feat_1OBigram_UtterancePredicate";
	public static String featPathPredicateUtterance="Feat_PathPredicateUtterance";
	public static String featCoreUtterance2009="Feat_Core_Utterance2009";
	public static String featCoreUtteranceHead2009="Feat_Core_UtteranceHead2009";
	public static String featCoreUtteranceNeighbours2009="Feat_Core_UtteranceNeighbours2009";
	public static String featCorePredicate2009="Feat_Core_Predicate2009";
	public static String featCorePredicateHead2009="Feat_Core_PredicateHead2009";
	public static String featCorePredicateNeighbours2009="Feat_Core_PredicateNeighbours2009";
	public static String featCoreUtterance2008="Feat_Core_Utterance2008";
	public static String featCoreUtteranceHead2008="Feat_Core_UtteranceHead2008";
	public static String featCoreUtteranceNeighbours2008="Feat_Core_UtteranceNeighbours2008";
	public static String featCorePredicate2008="Feat_Core_Predicate2008";
	public static String featCorePredicateHead2008="Feat_Core_PredicateHead2008";
	public static String featCorePredicateNeighbours2008="Feat_Core_PredicateNeighbours2008";

	public static String useDiskStorage ="Use_Disk_Storage";
	public static String averagePerceptron ="Average_Perceptron";
	public static String minimumOneArgumentAssignment ="Minimum_One_Argument_Assignment";
	public static String thresholdDiscount ="Threshold_Discount";
	public static String learningPace="Learning_Pace";
	
	public static String multiFeat="Multimodal_Features";
	public static String multiFeatPredAssocRoles="Multimodal_Predicate_Associated_Roles_Features";
	public static String multiFeatPredAssocRolesApproach="Multimodal_Predicate_Associated_Roles_Approach";
	public static String multiFeatArgAssocRoles="Multimodal_Argument_Associated_Roles_Features";

	public static String multiFeatCore="Multi_Feat_Core";
	public static String multiFeatCoreHead="Multi_Feat_Core_Head";
	public static String multiFeatCoreNeighbours="Multi_Feat_Core_Neighbours";
	public static String multiFeatBigram="Multi_Feat_Bigram";
	public static String multiFeat1OUtterancePredicate="Multi_Feat_1OUtterancePredicate";
	public static String multiFeat1OUtteranceHead="Multi_Feat_1OUtteranceHead";
					
	//public static String multiFeat1OBiagramUtterancePredicate="Multi_Feat_1OBigram_UtterancePredicate";
	

	//public static String multiFeatCore2009="Multi_Feat_Core_2009";
	//public static String multiFeatHead2009="Multi_Feat_Core_Head2009";
	//public static String multiFeatNeighbours2009="Multi_Feat_Core_Neighbours2009";

}
