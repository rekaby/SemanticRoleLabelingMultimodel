package uni.hamburg.inf.sssa.feature;

public enum FeatureTypes {
//TODO to extend each family of the following
	/*
	 * Here we describe the letters used in features naming
	 *
	 * 
	 * D Predicate
	 * U utterance
	 * H1 Head of Predicate
	 * H2 Head of utterance 
	 * 
	 * 
	 * P pos
	 * W word
	 * M lemma
	 * L label (syntactic) (in case of direct dependency)
	 * DEP deprel head ID
	 * C1  Context Multimodal Predicate associated 
	 * C2  Context Multimodal Argument associated
	 * 
	 * 2008
	 * T predicted pos Tag//2008
	 * S split 2008
	 * MAT Malt parser
	 * FN feature name entity Conl2003 
	 * FB feature name entity BBN
	 * FS feature word net Sense
	 * 
	 * * 2009
	 * M9 predicted Lemma//2009
	 * L9 predicted label 2009
	 * DEP9 predicted deprel head ID 2009
	 * GF9 features 2009
	 * F9 predicated features 2009
	 * 
	 * 
	 * LL location Low
	 * LH location High
	 * LM location match (predicate position)
	 * 
	 * Think about word position
	 * 
	 * p previous
	 * n next
	 *
	 * R Relation (semantic) type
	 */
	FEATURE_TYPE_START,
	//Core utterance features    //Done
	UW_R,
	UP_R,
	UM_R,
	UL_R,
	UDEP_R,
	ULL_R,
	ULH_R,
	ULM_R,
	//Core utterance features 2008   
	UT_R,
	USW_R,
	USM_R,
	USP_R,
	UFN_R,
	UFB_R,
	UFS_R,
	UMATL_R,
	UMATDEP_R,
	//Core utterance features 2009   
	UM9_R,
	UL9_R,
	UDEP9_R,
	UGF9_R,
	UF9_R,
	
	//Core Predicate features     //Done
	DW_R,
	DP_R,
	DM_R,
	DL_R,
	DDEP_R,
	//core Multi features
	DC1_R,
	DC2_R,
	UC1_R,
	UC2_R,
	//Core Predicate features 2008   
	DT_R,
	DSW_R,
	DSM_R,
	DSP_R,
	DFN_R,
	DFB_R,
	DFS_R,
	DMATL_R,
	DMATDEP_R,
	//Core Predicate features 2009   
	DM9_R,
	DL9_R,
	DDEP9_R,
	DGF9_R,
	DF9_R,	
	
	//Core utterance Head features    //Done
	H2W_R,
	H2P_R,
	H2M_R,
	H2L_R,
	H2DEP_R,
	H2LL_R,
	H2LH_R,
	H2LM_R,
	//Core utterance Head features 2008   
	H2T_R,
	H2SW_R,
	H2SM_R,
	H2SP_R,
	H2FN_R,
	H2FB_R,
	H2FS_R,
	H2MATL_R,
	H2MATDEP_R,
	//Core utterance Head features 2009
	H2M9_R,
	H2L9_R,
	H2DEP9_R,
	H2GF9_R,
	H2F9_R,	
	
	//Core Multi Head features    
	H1C1_R,
	H1C2_R,
	H2C1_R,
	H2C2_R,
		
	//Core predicate Head features    
	H1W_R,
	H1P_R,
	H1M_R,
	H1L_R,
	H1DEP_R,
	H1LL_R,
	H1LH_R,
	H1LM_R,
	//Core predicate Head features 2008   
	H1T_R,
	H1SW_R,
	H1SM_R,
	H1SP_R,
	H1FN_R,
	H1FB_R,
	H1FS_R,
	H1MATL_R,
	H1MATDEP_R,	
	//Core predicate Head features 2009
		H1M9_R,
		H1L9_R,
		H1DEP9_R,
		H1GF9_R,
		H1F9_R,	
		
	//Core utterance neighbours features    //Done
	UpW_R,
	UpP_R,
	UpM_R,
	UpL_R,
	UpDEP_R,
	UpLL_R,
	UpLH_R,
	UpLM_R,
	
	UnW_R,
	UnP_R,
	UnM_R,
	UnL_R,
	UnDEP_R,
	UnLL_R,
	UnLH_R,
	UnLM_R,
	
	//Core utterance neighbours features 2008
	UpT_R,
	UpSW_R,
	UpSM_R,
	UpSP_R,
	UpFN_R,
	UpFB_R,
	UpFS_R,
	UpMATL_R,
	UpMATDEP_R,	
	
	UnT_R,
	UnSW_R,
	UnSM_R,
	UnSP_R,
	UnFN_R,
	UnFB_R,
	UnFS_R,
	UnMATL_R,
	UnMATDEP_R,	
	//Core utterance neighbours features 2009
	UpM9_R,
	UpL9_R,
	UpDEP9_R,
	UpGF9_R,
	UpF9_R,
		
	UnM9_R,
	UnL9_R,
	UnDEP9_R,
	UnGF9_R,
	UnF9_R,
	
	//Core Multi neighbours features    //Done
	UpC1_R,
	UpC2_R,
	UnC1_R,
	UnC2_R,
	DpC1_R,
	DpC2_R,
	DnC1_R,
	DnC2_R,
			
	//Core Predicate neighbours features    //Done
	DpW_R,
	DpP_R,
	DpM_R,
	DpL_R,
	DpDEP_R,
	
	DnW_R,
	DnP_R,
	DnM_R,
	DnL_R,
	DnDEP_R,
	
	//Core Predicate neighbours features 2008
		DpT_R,
		DpSW_R,
		DpSM_R,
		DpSP_R,
		DpFN_R,
		DpFB_R,
		DpFS_R,
		DpMATL_R,
		DpMATDEP_R,	
		
		DnT_R,
		DnSW_R,
		DnSM_R,
		DnSP_R,
		DnFN_R,
		DnFB_R,
		DnFS_R,
		DnMATL_R,
		DnMATDEP_R,	
		//Core Predicate neighbours features 2009
		DpM9_R,
		DpL9_R,
		DpDEP9_R,
		DpGF9_R,
		DpF9_R,
			
		DnM9_R,
		DnL9_R,
		DnDEP9_R,
		DnGF9_R,
		DnF9_R,
	//Bigram Utterance		//Done
	UpW_UW_R,
	UpP_UP_R,
	UpM_UM_R,
	UpL_UL_R,
	
	UW_UnW_R,
	UP_UnP_R,
	UM_UnM_R,
	UL_UnL_R,

	//Trigram Utterance
	UpW_UW_UnW_R,
	UpP_UP_UnP_R,
	UpM_UM_UnM_R,
	UpL_UL_UnL_R,
	//Bigram Multi
	DpC1_DC1_R,
	DpC1_DC2_R,
	DpC2_DC2_R,
	DC1_DnC1_R,
	DC1_DnC2_R,
	DC2_DnC2_R,
	UpC1_UC1_R,
	UpC1_UC2_R,
	UpC2_UC2_R,
	UC1_UnC1_R,
	UC1_UnC2_R,
	UC2_UnC2_R,
	
	DpC1_DW_R,
	DpC1_DP_R,
	DpC1_DM_R,
	DpC1_DL_R,
	DpC2_DW_R,
	DpC2_DP_R,
	DpC2_DM_R,
	DpC2_DL_R,
	
	DpW_DC1_R,
	DpP_DC1_R,
	DpM_DC1_R,
	DpL_DC1_R,
	DpW_DC2_R,
	DpP_DC2_R,
	DpM_DC2_R,
	DpL_DC2_R,
	
	UpC1_UW_R,
	UpC1_UP_R,
	UpC1_UM_R,
	UpC1_UL_R,
	UpC2_UW_R,
	UpC2_UP_R,
	UpC2_UM_R,
	UpC2_UL_R,
	
	UpW_UC1_R,
	UpP_UC1_R,
	UpM_UC1_R,
	UpL_UC1_R,
	UpW_UC2_R,
	UpP_UC2_R,
	UpM_UC2_R,
	UpL_UC2_R,

	DW_DnC1_R,
	DP_DnC1_R,
	DM_DnC1_R,
	DL_DnC1_R,
	DW_DnC2_R,
	DP_DnC2_R,
	DM_DnC2_R,
	DL_DnC2_R,
	
	DC1_DnW_R,
	DC1_DnP_R,
	DC1_DnM_R,
	DC1_DnL_R,
	DC2_DnW_R,
	DC2_DnP_R,
	DC2_DnM_R,
	DC2_DnL_R,
	
	UW_UnC1_R,
	UP_UnC1_R,
	UM_UnC1_R,
	UL_UnC1_R,
	UW_UnC2_R,
	UP_UnC2_R,
	UM_UnC2_R,
	UL_UnC2_R,
	
	UC1_UnW_R,
	UC1_UnP_R,
	UC1_UnM_R,
	UC1_UnL_R,
	UC2_UnW_R,
	UC2_UnP_R,
	UC2_UnM_R,
	UC2_UnL_R,
	
	//Bigram Predicate
	DpW_DW_R,
	DpP_DP_R,
	DpM_DM_R,
	DpL_DL_R,
		
	DW_DnW_R,
	DP_DnP_R,
	DM_DnM_R,
	DL_DnL_R,

	//Trigram Predicate
	DpW_DW_DnW_R,
	DpP_DP_DnP_R,
	DpM_DM_DnM_R,
	DpL_DL_DnL_R,
	
	//1st Order features Utterance-Predicate	//Done
	UW_DW_R,
	UW_DP_R,
	UW_DM_R,
	UW_DL_R,
	
	UP_DW_R,
	UP_DP_R,
	UP_DM_R,
	UP_DL_R,
	
	UM_DW_R,
	UM_DP_R,
	UM_DM_R,
	UM_DL_R,
	
	UL_DW_R,
	UL_DP_R,
	UL_DM_R,
	UL_DL_R,//utterance label to its parent , predicate label to its parent 
	
	
	UL9_DL9_R,
	UL9_DM9_R,
	
	//1st Multi Order features Utterance-Predicate
	UC1_DC1_R,
	UC1_DC2_R,
	UC2_DC2_R,
	UC1_DW_R,
	UC1_DP_R,
	UC1_DM_R,
	UC1_DL_R,
	UC2_DW_R,
	UC2_DP_R,
	UC2_DM_R,
	UC2_DL_R,
	
	UW_DC1_R,
	UP_DC1_R,
	UM_DC1_R,
	UL_DC1_R,
	UW_DC2_R,
	UP_DC2_R,
	UM_DC2_R,
	UL_DC2_R,
	//1st Order Feature Utterance Bigram - Predicate 
	UpW_UW_DW_R,
	UpW_UW_DP_R,
	UpW_UW_DM_R,
	UpW_UW_DL_R,
	
	UpP_UP_DW_R,
	UpP_UP_DP_R,
	UpP_UP_DM_R,
	UpP_UP_DL_R,
	
	UpM_UM_DW_R,
	UpM_UM_DP_R,
	UpM_UM_DM_R,
	UpM_UM_DL_R,
	
	UpL_UL_DW_R,
	UpL_UL_DP_R,
	UpL_UL_DM_R,
	UpL_UL_DL_R,
	UpL9_UL9_DL9_R,
	
	UW_UnW_DW_R,
	UW_UnW_DP_R,
	UW_UnW_DM_R,
	UW_UnW_DL_R,
	
	UP_UnP_DW_R,
	UP_UnP_DP_R,
	UP_UnP_DM_R,
	UP_UnP_DL_R,
	
	UM_UnM_DW_R,
	UM_UnM_DP_R,
	UM_UnM_DM_R,
	UM_UnM_DL_R,
	
	UL_UnL_DW_R,
	UL_UnL_DP_R,
	UL_UnL_DM_R,
	UL_UnL_DL_R,
	UL9_UnL9_DL9_R,
	//1st Order features Utterance-Parent
	UW_H2W_R,
	UW_H2P_R,
	UW_H2M_R,
	UW_H2L_R,
	
	UP_H2W_R,
	UP_H2P_R,
	UP_H2M_R,
	UP_H2L_R,
	
	UM_H2W_R,
	UM_H2P_R,
	UM_H2M_R,
	UM_H2L_R,
	
	UL_H2W_R,
	UL_H2P_R,
	UL_H2M_R,
	UL_H2L_R,//utterance label to its parent , Head label to its parent 
	UL9_H2L9_R,
	UL9_H2M9_R,
	
	ULL_HLL_R,//these 6 features only one will have value depends on location of U and Head
	ULL_HLM_R,
	ULH_HLH_R,
	ULL_HLH_R,
	ULH_HLL_R,
	ULH_HLM_R,
	
	//Multi 1st Order features Utterance-Parent
	UC1_H2C1_R,
	UC1_H2C2_R,
	UC2_H2C2_R,
	UC1_H2W_R,
	UC1_H2P_R,
	UC1_H2M_R,
	UC1_H2L_R,
	UC2_H2W_R,
	UC2_H2P_R,
	UC2_H2M_R,
	UC2_H2L_R,
	
	UW_H2C1_R,
	UP_H2C1_R,
	UM_H2C1_R,
	UL_H2C1_R,
	UW_H2C2_R,
	UP_H2C2_R,
	UM_H2C2_R,
	UL_H2C2_R,
	
	//Predicate Utterance Path
	D_U_PATHU_R,
	D_U_PATHD_R,
	
	D_U_PATHU1_R,
	D_U_PATHU2_R,
	D_U_PATHU3_R,
	D_U_PATHU4_R,
	D_U_PATHU5_R,
	D_U_PATHU6_R,
	D_U_PATHU7_R,
	D_U_PATHU8_R,
	D_U_PATHU9_R,
	D_U_PATHU10_R,
	
	D_U_PATHD1_R,
	D_U_PATHD2_R,
	D_U_PATHD3_R,
	D_U_PATHD4_R,
	D_U_PATHD5_R,
	D_U_PATHD6_R,
	D_U_PATHD7_R,
	D_U_PATHD8_R,
	D_U_PATHD9_R,
	D_U_PATHD10_R,
	
	/*
	//1st Order features Parent-Predicate
	HW_DW_R,
	HP_DP_R,
	HM_DM_R,
	HL_DL_R,//utterance label to its parent , predicate label to its parent
	*/
	
	FEATURE_TYPE_END;
}
