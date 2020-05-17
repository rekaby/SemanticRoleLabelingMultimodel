package uni.hamburg.inf.sssa.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.VerbNetContextRelationType;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.DictionarySet;
import uni.hamburg.inf.sssa.util.DictionarySet.DictionaryTypes;
import uni.hamburg.inf.sssa.util.Utils;

public class FeatureFactory {

	private int wordDictionaryBits; 
	private int posDictionaryBits; 
	private int lemmaDictionaryBits;
	private int labelDictionaryBits;
	private int featuresDictionaryBits;
	private int contextRelationsBits; 
	private int multiModalRelationsBits;
	private int positionBits;
	 
	
	//DictionarySet dictionarySet;
	/*private int wordDictionaryBits; 
	private int posDictionaryBits; 
	private int lemmaDictionaryBits;
	private int labelDictionaryBits;
	private int positionBits;
	private int contextRelationsBits; 
	private int featuresBits; 
	*/
	public int TOKEN_START = 1;
	public int TOKEN_END = 2;
	
	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(FeatureFactory.class);

	private static FeatureFactory instance = null;
	//private static Map<Integer, String> testMap = new HashMap<Integer, String>();
	//private static int duplicateHashCount=0;
	public static FeatureFactory getInstance() {
	      if(instance == null) {
	         instance = new FeatureFactory(DictionarySet.getInstance());
	      }
	      return instance;
	   }
	public FeatureFactory(DictionarySet dictionarySet) {
		super();
		//this.dictionarySet=dictionarySet;
		wordDictionaryBits=Utils.log2(dictionarySet.size(DictionaryTypes.WORD));
		posDictionaryBits=Utils.log2(dictionarySet.size(DictionaryTypes.POS));
		lemmaDictionaryBits=Utils.log2(dictionarySet.size(DictionaryTypes.LEMMA));
		labelDictionaryBits=Utils.log2(dictionarySet.size(DictionaryTypes.DEPLABEL));
		featuresDictionaryBits=Utils.log2(dictionarySet.size(DictionaryTypes.FEAT));
		
		contextRelationsBits=Utils.log2(ContextRelationType.Context_Relation_Type_End.ordinal());
		multiModalRelationsBits=Utils.log2(VerbNetContextRelationType.Multimodal_Relation_Type_End.ordinal());
		positionBits=8;//maximum statement length =254 + root
		
		//set the shift to max values:
		wordDictionaryBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		posDictionaryBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		lemmaDictionaryBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		labelDictionaryBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		featuresDictionaryBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		positionBits=Math.max(Math.max(Math.max(Math.max(Math.max(wordDictionaryBits,posDictionaryBits),lemmaDictionaryBits),labelDictionaryBits),featuresDictionaryBits),positionBits);
		
		/*
		featuresBits=Utils.log2(FeatureTypes.FEATURE_TYPE_END.ordinal());
		
		*/
		/*log.logInfo(LogCode.GENERAL_CODE.getCode(),"wordDictionaryBits="+wordDictionaryBits);
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"posDictionaryBits="+posDictionaryBits);
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"lemmaDictionaryBits="+lemmaDictionaryBits);
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"labelDictionaryBits="+labelDictionaryBits);
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"contextRelationsBits="+contextRelationsBits);
		*/
		//System.out.println("wordDictionaryBits="+wordDictionaryBits);
		//System.out.println("posDictionaryBits="+posDictionaryBits);
		//System.out.println("lemmaDictionaryBits="+lemmaDictionaryBits);
		//System.out.println("labelDictionaryBits="+labelDictionaryBits);
		//System.out.println("contextRelationsBits="+contextRelationsBits);
	}
	
	public FeatureVector buildFeatureVector(DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{
		FeatureVector fV= new FeatureVector();
		//Call all small methods to create the features then return the fv
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtterance)) {
			addCoreUtteranceFeatures(fV, instance, utterance, predicate, contextRelationType);	
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceHead)) {
			addCoreUtteranceHeadFeatures(fV, instance,  instance.heads[utterance],predicate, contextRelationType);
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceNeighbours)) {
			addCoreUtteranceNeighboursFeatures(fV, instance,  utterance-1, utterance+1,predicate, contextRelationType);
		}
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicate)) {
			addCorePredicateFeatures(fV, instance,  predicate, contextRelationType);
		}	
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateHead)) {
			addCorePredicateHeadFeatures(fV, instance,  instance.heads[predicate], contextRelationType);
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateNeighbours)) {
			addCorePredicateNeighboursFeatures(fV, instance,  predicate-1,predicate+1, contextRelationType);
		}
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeatCore)) {
			addMultiCoreFeatures(fV, instance,  predicate,utterance, contextRelationType);
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeatCoreHead)) {
			addCoreMultiHeadFeatures(fV, instance,  instance.heads[predicate],instance.heads[utterance], contextRelationType);
		}
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeatCoreNeighbours)) {
			addMultiCoreNeighboursFeatures(fV, instance,  predicate-1,predicate+1,utterance-1,utterance+1, contextRelationType);
		}
		
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featUtteranceBigram)){
			addUtteranceBigramFeatures(fV, instance, utterance, utterance-1, utterance+1, predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featUtteranceTrigram)){
			addUtteranceTrigramFeatures(fV, instance, utterance, utterance-1, utterance+1, predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featPredicateBigram)){
			addPredicateBigramFeatures(fV, instance, utterance, predicate-1, predicate+1, predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featPredicateTrigram)){
			addPredicateTrigramFeatures(fV, instance, utterance, predicate-1, predicate+1, predicate, contextRelationType);	
		}
		
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeatBigram)){
			addMultiBigramFeatures(fV, instance, utterance, utterance-1, utterance+1, predicate,predicate-1,predicate+1, contextRelationType);	
		}
		
		
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.feat1OUtterancePredicate)){
			add1OUtterancePredicateFeatures(fV, instance,  utterance,predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.feat1OUtteranceHead)){
			add1OUtteranceHeadFeatures(fV, instance,  utterance,predicate,instance.heads[utterance], contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.feat1OBigramUtterancePredicate)){
			add1OBigramUtterancePredicateFeatures(fV, instance, utterance, predicate-1, predicate+1, predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeat1OUtterancePredicate)){
			addMulti1OUtterancePredicateFeatures(fV, instance,  utterance,predicate, contextRelationType);	
		}
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.multiFeat1OUtteranceHead)){
			addMulti1OUtteranceHeadFeatures(fV, instance,  utterance,instance.heads[utterance], contextRelationType);		
		}
		
		if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featPathPredicateUtterance)){
			addPathPredicateUtteranceFeatures(fV, instance, utterance, predicate, contextRelationType);	
		}
		
		
		
		if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtterance2008)){
				addCoreUtteranceFeatures2008(fV, instance, utterance, predicate, contextRelationType);	
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceHead2008)){
				addCoreUtteranceHeadFeatures2008(fV, instance,  instance.heads[utterance], contextRelationType);	
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceNeighbours2008)){
				addCoreUtteranceNeighboursFeatures2008(fV, instance,  utterance-1, utterance+1,predicate, contextRelationType);	
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicate2008)){
				addCorePredicateFeatures2008(fV, instance, utterance, predicate, contextRelationType);	
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateHead2008)){
				addCorePredicateHeadFeatures2008(fV, instance,  instance.heads[predicate], contextRelationType);	
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateNeighbours2008)){
				addCorePredicateNeighboursFeatures2008(fV, instance,  predicate-1,predicate+1, contextRelationType);	
			}
		}
		
		if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtterance2009)){
				addCoreUtteranceFeatures2009(fV, instance, utterance, predicate, contextRelationType);
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceHead2009)){
				addCoreUtteranceHeadFeatures2009(fV, instance,  instance.heads[utterance], contextRelationType);
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCoreUtteranceNeighbours2009)){
				addCoreUtteranceNeighboursFeatures2009(fV, instance,  utterance-1, utterance+1,predicate, contextRelationType);
			}	
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicate2009)){
				addCorePredicateFeatures2009(fV, instance, utterance, predicate, contextRelationType);
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateHead2009)){
				addCorePredicateHeadFeatures2009(fV, instance,  instance.heads[predicate], contextRelationType);
			}
			if(SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.featCorePredicateNeighbours2009)){
				addCorePredicateNeighboursFeatures2009(fV, instance,  predicate-1,predicate+1, contextRelationType);
			}
		}
		
		return fV;
	}
	private void addMulti1OUtterancePredicateFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int predicate,ContextRelationType contextRelationType)
	{
		long code=0;
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
					
			for (int j = 0; j < instance.predicateAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DC1_R,
						new long[]{instance.predicateAssociatedRoles[utterance][i],instance.predicateAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		} 
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			
			for (int j = 0; j < instance.argumentAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DC2_R,new long[]{instance.predicateAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_DC2_R,new long[]{instance.argumentAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DW_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.formids[predicate]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DP_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.ppostagids[predicate]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DM_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.lemmaids[predicate]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_DL_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.deprelids[predicate]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_DC1_R,
					new long[]{instance.formids[utterance],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_DC1_R,
					new long[]{instance.ppostagids[utterance],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_DC1_R,
					new long[]{instance.lemmaids[utterance],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_DC1_R,
					new long[]{instance.deprelids[utterance],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_DW_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.formids[predicate]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_DP_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.ppostagids[predicate]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_DM_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.lemmaids[predicate]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_DL_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.deprelids[predicate]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_DC2_R,
					new long[]{instance.formids[utterance],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_DC2_R,
					new long[]{instance.ppostagids[utterance],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_DC2_R,
					new long[]{instance.lemmaids[utterance],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_DC2_R,
					new long[]{instance.deprelids[utterance],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		
		
	
	}
	private void add1OUtterancePredicateFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int predicate,ContextRelationType contextRelationType)
	{
		/* UW_DW_R,	UW_DP_R,UW_DM_R,UW_DL_R,	UP_DW_R,UP_DP_R,UP_DM_R,UP_DL_R,	UM_DW_R,UM_DP_R,UM_DM_R,UM_DL_R,	UL_DW_R,UL_DP_R,UL_DM_R,UL_DL_R, */
		long code=0;
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_DW_R,new long[]{instance.formids[utterance],instance.formids[predicate]}, 
				new long[]{wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_DP_R,new long[]{instance.formids[utterance],instance.gpostagids[predicate]},  
				new long[]{wordDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_DM_R,new long[]{instance.formids[utterance],instance.lemmaids[predicate]},  
				new long[]{wordDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_DL_R,new long[]{instance.formids[utterance],instance.deprelids[predicate]}, 
				new long[]{wordDictionaryBits,labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_DW_R,new long[]{instance.gpostagids[utterance],instance.formids[predicate]},  
				new long[]{posDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_DP_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[predicate]}, 
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_DM_R,new long[]{instance.gpostagids[utterance],instance.lemmaids[predicate]}, 
				new long[]{posDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_DL_R,new long[]{instance.gpostagids[utterance],instance.deprelids[predicate]},  
				new long[]{posDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_DW_R,new long[]{instance.lemmaids[utterance],instance.formids[predicate]}, 
				new long[]{lemmaDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_DP_R,new long[]{instance.lemmaids[utterance],instance.gpostagids[predicate]}, 
				new long[]{lemmaDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_DM_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[predicate]},  
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_DL_R,new long[]{instance.lemmaids[utterance],instance.deprelids[predicate]}, 
				new long[]{lemmaDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_DW_R,new long[]{instance.deprelids[utterance],instance.formids[predicate]}, 
				new long[]{labelDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_DP_R,new long[]{instance.deprelids[utterance],instance.gpostagids[predicate]},  
				new long[]{labelDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_DM_R,new long[]{instance.deprelids[utterance],instance.lemmaids[predicate]}, 
				new long[]{labelDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_DL_R,new long[]{instance.deprelids[utterance],instance.deprelids[predicate]}, 
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_DM9_R,new long[]{instance.pdeprelids09[utterance],instance.plemmaids09[predicate]},  
				new long[]{labelDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_DL9_R,new long[]{instance.pdeprelids09[utterance],instance.pdeprelids09[predicate]}, 
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
	}
	private void addMulti1OUtteranceHeadFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int head,ContextRelationType contextRelationType)
	{
		long code=0;
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
					
			for (int j = 0; j < instance.predicateAssociatedRoles[head].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2C1_R,
						new long[]{instance.predicateAssociatedRoles[utterance][i],instance.predicateAssociatedRoles[head][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		} 
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			
			for (int j = 0; j < instance.argumentAssociatedRoles[head].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2C2_R,new long[]{instance.predicateAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[head][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[head].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_H2C2_R,new long[]{instance.argumentAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[head][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2W_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.formids[head]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2P_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.ppostagids[head]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2M_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.lemmaids[head]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_H2L_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.deprelids[head]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[head].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_H2C1_R,
					new long[]{instance.formids[utterance],instance.predicateAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_H2C1_R,
					new long[]{instance.ppostagids[utterance],instance.predicateAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_H2C1_R,
					new long[]{instance.lemmaids[utterance],instance.predicateAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_H2C1_R,
					new long[]{instance.deprelids[utterance],instance.predicateAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_H2W_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.formids[head]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_H2P_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.ppostagids[head]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_H2M_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.lemmaids[head]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_H2L_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.deprelids[head]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[head].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_H2C2_R,
					new long[]{instance.formids[utterance],instance.argumentAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_H2C2_R,
					new long[]{instance.ppostagids[utterance],instance.argumentAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_H2C2_R,
					new long[]{instance.lemmaids[utterance],instance.argumentAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_H2C2_R,
					new long[]{instance.deprelids[utterance],instance.argumentAssociatedRoles[head][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		
		
	
	
	}
	private void add1OUtteranceHeadFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int predicate,int head,ContextRelationType contextRelationType)
	{
		/* UW_HW_R,	UW_HP_R,	UW_HM_R,	UW_HL_R,UP_HW_R,UP_HP_R,UP_HM_R,UP_HL_R,
		UM_HW_R,	UM_HP_R,	UM_HM_R,	UM_HL_R,UL_HW_R,UL_HP_R,UL_HM_R,UL_HL_R,
	//these 6 features only one will have value depends on location of U and Head
	ULL_HLL_R,ULL_HLM_R,	ULH_HLH_R,	ULL_HLH_R,	ULH_HLL_R,ULH_HLM_R, */
		long code=0;
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_H2W_R,new long[]{instance.formids[utterance],instance.formids[head]},  
				new long[]{wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_H2P_R,new long[]{instance.formids[utterance],instance.gpostagids[head]}, 
				new long[]{wordDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_H2M_R,new long[]{instance.formids[utterance],instance.lemmaids[head]}, 
				new long[]{wordDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_H2L_R,new long[]{instance.formids[utterance],instance.deprelids[head]}, 
				new long[]{wordDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_H2W_R,new long[]{instance.gpostagids[utterance],instance.formids[head]},  
				new long[]{posDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_H2P_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[head]}, 
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_H2M_R,new long[]{instance.gpostagids[utterance],instance.lemmaids[head]}, 
				new long[]{posDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_H2L_R,new long[]{instance.gpostagids[utterance],instance.deprelids[head]}, 
				new long[]{posDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_H2W_R,new long[]{instance.lemmaids[utterance],instance.formids[head]}, 
				new long[]{lemmaDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_H2P_R,new long[]{instance.lemmaids[utterance],instance.gpostagids[head]}, 
				new long[]{lemmaDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_H2M_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[head]}, 
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_H2L_R,new long[]{instance.lemmaids[utterance],instance.deprelids[head]}, 
				new long[]{lemmaDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_H2W_R,new long[]{instance.deprelids[utterance],instance.formids[head]}, 
				new long[]{labelDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_H2P_R,new long[]{instance.deprelids[utterance],instance.gpostagids[head]}, 
				new long[]{labelDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_H2M_R,new long[]{instance.deprelids[utterance],instance.lemmaids[head]}, 
				new long[]{labelDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_H2L_R,new long[]{instance.deprelids[utterance],instance.deprelids[head]}, 
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		//ULL_HLL_R,ULL_HLM_R,	ULH_HLH_R,	ULL_HLH_R,	ULH_HLL_R,ULH_HLM_R
		if(utterance<predicate && head<predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULL_HLL_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		if(utterance<predicate && head==predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULL_HLM_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		if(utterance<predicate && head>predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULL_HLH_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		if(utterance>predicate && head>predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULH_HLH_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		if(utterance>predicate && head<predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULH_HLL_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		if(utterance>predicate && head==predicate)
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULH_HLM_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_H2M9_R,new long[]{instance.pdeprelids09[utterance],instance.plemmaids09[head]},  
				new long[]{labelDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_H2L9_R,new long[]{instance.pdeprelids09[utterance],instance.pdeprelids09[head]}, 
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
	}
	private void addPathPredicateUtteranceFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*	*/
		long code=0;
		
		List<List<Integer>> path=getPathString(instance, predicate,utterance);
		
		long [] pathID= new long[path.get(0).size()<5?path.get(0).size():5];
		long [] shiftID= new long[path.get(0).size()<5?path.get(0).size():5];
		for (int i = 0; i < path.get(0).size()&&i<5; i++) {
			pathID[i]=path.get(0).get(i);
			shiftID[i]=labelDictionaryBits;
		}
		code = EncodeCoreFeatureGeneric(FeatureTypes.D_U_PATHU_R,pathID,   
				shiftID
				,contextRelationType);
		fV.addFeature(code);
		
		
		pathID= new long[path.get(1).size()<5?path.get(1).size():5];
		shiftID= new long[path.get(1).size()<5?path.get(1).size():5];
		for (int i = 0; i < path.get(1).size()&&i<5; i++) {
			pathID[i]=path.get(1).get(i);
			shiftID[i]=labelDictionaryBits;
		}
		code = EncodeCoreFeatureGeneric(FeatureTypes.D_U_PATHD_R,pathID,   
				shiftID
				,contextRelationType);
		fV.addFeature(code);
		
		
		for (int i = 0; i < 5; i++) {
			code = EncodeCoreFeatureGeneric(FeatureTypes.valueOf("D_U_PATHU"+(i+1)+"_R"),new long[]{i<path.get(0).size()?path.get(0).get(i):0},   
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < 5; i++) {
			code = EncodeCoreFeatureGeneric(FeatureTypes.valueOf("D_U_PATHD"+(i+1)+"_R"),new long[]{i<path.get(1).size()?path.get(1).get(i):0},   
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		}
	}
	
	private void addCoreUtteranceFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*	UW_R,	UP_R,	UM_R,	UL_R,	ULL_R,	ULH_R,	ULM_R,	*/
		long code=0;
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_R,new long[]{instance.formids[utterance]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_R,new long[]{instance.gpostagids[utterance]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_R,new long[]{instance.lemmaids[utterance]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_R,new long[]{instance.deprelids[utterance]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UDEP_R,new long[]{instance.heads[utterance]}, contextRelationType);
		//fV.addFeature(code);
		
		if(utterance<predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULL_R,new long[]{},  
					new long[]{}
					,contextRelationType);
			
		}
		else if(utterance>predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULH_R,new long[]{},  
					new long[]{}
					,contextRelationType);
		}
		else
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.ULM_R,new long[]{}, 
					new long[]{}
					,contextRelationType);
		}
		
		
		fV.addFeature(code);
		
	}
	private void addCoreUtteranceFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*	UT_R, 	USW_R,	USM_R,	USP_R,	UMATL_R,  UMATDEP_R,*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.UT_R,new long[]{instance.ppostagids[utterance]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.USW_R,new long[]{instance.splitFormids08[utterance]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.USM_R,new long[]{instance.splitLemmaids08[utterance]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.USP_R,new long[]{instance.splitppostagids08[utterance]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) {
			code = EncodeCoreFeatureGeneric(FeatureTypes.UFN_R,Arrays.stream(instance.featsNE03ids08[utterance]!=null?instance.featsNE03ids08[utterance]:new int[]{}).asLongStream().toArray(),  
					instance.featsNE03ids08[utterance]!=null?buildArrayOfValue(instance.featsNE03ids08[utterance].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UFB_R,Arrays.stream(instance.featsNEBBNids08[utterance]!=null?instance.featsNEBBNids08[utterance]:new int[]{}).asLongStream().toArray(),  
					instance.featsNEBBNids08[utterance]!=null?buildArrayOfValue(instance.featsNEBBNids08[utterance].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UFS_R,Arrays.stream(instance.featsWnssids08[utterance]!=null?instance.featsWnssids08[utterance]:new int[]{}).asLongStream().toArray(),  
					instance.featsWnssids08[utterance]!=null?buildArrayOfValue(instance.featsWnssids08[utterance].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UMATL_R,new long[]{instance.maltDeprelids08[utterance]},  
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreFeatureGeneric(FeatureTypes.UMATDEP_R,new long[]{instance.maltHeads08[utterance]},  
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
		}
		
	}

	private void addCoreUtteranceFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*UM9_R,	UL9_R,	UDEP9_R,	UGF9_R,	UF9_R,*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.UM9_R,new long[]{instance.plemmaids09[utterance]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_R,new long[]{instance.pdeprelids09[utterance]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UDEP9_R,new long[]{instance.pheads09[utterance]}, contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UGF9_R,Arrays.stream(instance.featsids09[utterance]!=null?instance.featsids09[utterance]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UF9_R,Arrays.stream(instance.pfeatsids09[utterance]!=null?instance.pfeatsids09[utterance]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
	}
	private void addMultiCoreFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int predicate,int utterance,ContextRelationType contextRelationType)
	{/*	DC_R	*/
		long code=0;
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_R,new long[]{instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_R,new long[]{instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_R,new long[]{instance.predicateAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_R,new long[]{instance.argumentAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		
	}
	
	private void addCorePredicateFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int predicate,ContextRelationType contextRelationType)
	{/*	DW_R,	DP_R,	DM_R,	DL_R,	*/
		long code=0;
		code = EncodeCoreFeatureGeneric(FeatureTypes.DW_R,new long[]{instance.formids[predicate]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DP_R,new long[]{instance.gpostagids[predicate]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DM_R,new long[]{instance.lemmaids[predicate]}, 
				new long[]{lemmaDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DL_R,new long[]{instance.deprelids[predicate]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DDEP_R,new long[]{instance.heads[predicate]}, contextRelationType);
		//fV.addFeature(code);
		
	}
	private void addCorePredicateFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*	UT_R, 	USW_R,	USM_R,	USP_R,	UMATL_R,  UMATDEP_R,*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.DT_R,new long[]{instance.ppostagids[predicate]}, 
				new long[]{posDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.DSW_R,new long[]{instance.splitFormids08[predicate]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DSM_R,new long[]{instance.splitLemmaids08[predicate]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DSP_R,new long[]{instance.splitppostagids08[predicate]}, 
				new long[]{posDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) {
			code = EncodeCoreFeatureGeneric(FeatureTypes.DFN_R,Arrays.stream(instance.featsNE03ids08[predicate]!=null?instance.featsNE03ids08[predicate]:new int[]{}).asLongStream().toArray(),  
					instance.featsNE03ids08[predicate]!=null?buildArrayOfValue(instance.featsNE03ids08[predicate].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DFB_R,Arrays.stream(instance.featsNEBBNids08[predicate]!=null?instance.featsNEBBNids08[predicate]:new int[]{}).asLongStream().toArray(),  
					instance.featsNEBBNids08[predicate]!=null?buildArrayOfValue(instance.featsNEBBNids08[predicate].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DFS_R,Arrays.stream(instance.featsWnssids08[predicate]!=null?instance.featsWnssids08[predicate]:new int[]{}).asLongStream().toArray(),  
					instance.featsWnssids08[predicate]!=null?buildArrayOfValue(instance.featsWnssids08[predicate].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DMATL_R,new long[]{instance.maltDeprelids08[predicate]},  
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.DMATDEP_R,new long[]{instance.maltHeads08[predicate]},  
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
		}
	}

	private void addCorePredicateFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int utterance, int predicate,ContextRelationType contextRelationType)
	{/*	DM9_R,	DL9_R,	DDEP9_R,	DGF9_R,	DF9_R,	*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.DM9_R,new long[]{instance.plemmaids09[predicate]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.DL9_R,new long[]{instance.pdeprelids09[predicate]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DDEP9_R,new long[]{instance.pheads09[predicate]}, contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DGF9_R,Arrays.stream(instance.featsids09[predicate]!=null?instance.featsids09[predicate]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DF9_R,Arrays.stream(instance.pfeatsids09[predicate]!=null?instance.pfeatsids09[predicate]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
	}

	private void addMultiCoreNeighboursFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int previousPre, int nextPre, int previousArg, int nextArg, ContextRelationType contextRelationType)
	{/*	DpW_R,	DpP_R,	DpM_R,	DpL_R,	DnW_R,	DnP_R,	DnM_R,	DnL_R,	*/
		int adaptedPreviousPre = previousPre >= 0 ? previousPre: TOKEN_START;    	
		int adaptedNextPre = nextPre <= instance.length-1 ? nextPre : TOKEN_END;
		int adaptedPreviousArg = previousArg >= 0 ? previousArg: TOKEN_START;    	
		int adaptedNextArg = nextArg <= instance.length-1 ? nextArg : TOKEN_END;
    	
		long code=0;
		
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_R,new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i]},
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_R,new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i]},
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedNextPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DnC1_R,new long[]{instance.predicateAssociatedRoles[adaptedNextPre][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedNextPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DnC2_R,new long[]{instance.argumentAssociatedRoles[adaptedNextPre][i]},
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_R,new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_R,new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedNextArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UnC1_R,new long[]{instance.predicateAssociatedRoles[adaptedNextArg][i]},
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedNextArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UnC2_R,new long[]{instance.argumentAssociatedRoles[adaptedNextArg][i]},
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
	}
	private void addCorePredicateNeighboursFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, ContextRelationType contextRelationType)
	{/*	DpW_R,	DpP_R,	DpM_R,	DpL_R,	DnW_R,	DnP_R,	DnM_R,	DnL_R,	*/
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
    	long code=0;
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpW_R,new long[]{instance.formids[adaptedPrevious]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpP_R,new long[]{instance.gpostagids[adaptedPrevious]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpM_R,new long[]{instance.lemmaids[adaptedPrevious]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpL_R,new long[]{instance.deprelids[adaptedPrevious]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnW_R,new long[]{instance.formids[adaptedNext]},  
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnP_R,new long[]{instance.gpostagids[adaptedNext]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnM_R,new long[]{instance.lemmaids[adaptedNext]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnL_R,new long[]{instance.deprelids[adaptedNext]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
	}
	private void addCoreUtteranceHeadFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int head, int predicate,ContextRelationType contextRelationType)
	{/*	H2W_R,	H2P_R,	H2M_R,	H2L_R,	H2DEP_R,	H2LL_R,	H2LH_R,	H2LM_R,	*/
		long code=0;
		//try {
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2W_R,new long[]{instance.formids[head]},  
					new long[]{wordDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);	
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	code = EncodeCoreFeatureGeneric(FeatureTypes.H2W_R,new long[]{instance.formids[head]}, contextRelationType);
		//}
		
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H2P_R,new long[]{instance.gpostagids[head]},  
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H2M_R,new long[]{instance.lemmaids[head]},  
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H2L_R,new long[]{instance.deprelids[head]},  
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H2DEP_R,new long[]{instance.heads[head]},  
		//new long[]{positionBits}
		//,contextRelationType);
		//fV.addFeature(code);
		
		if(head<predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2LL_R,new long[]{},  
					new long[]{}
					,contextRelationType);
			
		}
		else if(head>predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2LH_R,new long[]{},   
					new long[]{}
					,contextRelationType);
		}
		else//parent is predicate
		{	code = EncodeCoreFeatureGeneric(FeatureTypes.H2LM_R,new long[]{},   
				new long[]{}
				,contextRelationType);}	
		fV.addFeature(code);
	}
	
	private void addCoreMultiHeadFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int headPre, int headArg,ContextRelationType contextRelationType)
	{
		long code=0;
		for (int i = 0; i < instance.predicateAssociatedRoles[headPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.H1C1_R,new long[]{instance.predicateAssociatedRoles[headPre][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[headPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.H1C2_R,new long[]{instance.argumentAssociatedRoles[headPre][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[headArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.H2C1_R,new long[]{instance.predicateAssociatedRoles[headArg][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		} 
		for (int i = 0; i < instance.argumentAssociatedRoles[headArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.H2C2_R,new long[]{instance.argumentAssociatedRoles[headArg][i]},	
					new long[]{multiModalRelationsBits},contextRelationType);
			fV.addFeature(code);	
		}

	}
	private void addCoreUtteranceHeadFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int head,ContextRelationType contextRelationType)
	{/*	UT_R, 	USW_R,	USM_R,	USP_R,	UMATL_R,  UMATDEP_R,*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.H2T_R,new long[]{instance.ppostagids[head]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.H2SW_R,new long[]{instance.splitFormids08[head]},   
				new long[]{wordDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H2SM_R,new long[]{instance.splitLemmaids08[head]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H2SP_R,new long[]{instance.splitppostagids08[head]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) 
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2FN_R,Arrays.stream(instance.featsNE03ids08[head]!=null?instance.featsNE03ids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[head]!=null?buildArrayOfValue(instance.featsNE03ids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2FB_R,Arrays.stream(instance.featsNEBBNids08[head]!=null?instance.featsNEBBNids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsNEBBNids08[head]!=null?buildArrayOfValue(instance.featsNEBBNids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2FS_R,Arrays.stream(instance.featsWnssids08[head]!=null?instance.featsWnssids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsWnssids08[head]!=null?buildArrayOfValue(instance.featsWnssids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2MATL_R,new long[]{instance.maltDeprelids08[head]},    
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.H2MATDEP_R,new long[]{instance.maltHeads08[head]},    
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
		}
	}

	private void addCoreUtteranceHeadFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int head,ContextRelationType contextRelationType)
	{/*	H2M9_R,	H2L9_R,	H2DEP9_R,	H2GF9_R,	H2F9_R,	*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.H2M9_R,new long[]{instance.plemmaids09[head]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.H2L9_R,new long[]{instance.pdeprelids09[head]},    
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H2DEP9_R,new long[]{instance.pheads09[head]}, contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H2GF9_R,Arrays.stream(instance.featsids09[head]!=null?instance.featsids09[head]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H2F9_R,Arrays.stream(instance.pfeatsids09[head]!=null?instance.pfeatsids09[head]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		
	}

	private void addCorePredicateHeadFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int head,ContextRelationType contextRelationType)
	{/*	H2W_R,	H2P_R,	H2M_R,	H2L_R,	H2DEP_R,	H2LL_R,	H2LH_R,	H2LM_R,	*/
		long code=0;
		//try {
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1W_R,new long[]{instance.formids[head]},    
					new long[]{wordDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);	
		//} catch (Exception e) {
		//	e.printStackTrace();
		//	code = EncodeCoreFeatureGeneric(FeatureTypes.H1W_R,new long[]{instance.formids[head]}, contextRelationType);
		//}
		
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H1P_R,new long[]{instance.gpostagids[head]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H1M_R,new long[]{instance.lemmaids[head]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H1L_R,new long[]{instance.deprelids[head]},    
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H1DEP_R,new long[]{instance.heads[head]}, contextRelationType);
		//fV.addFeature(code);
	}
	
	private void addCorePredicateHeadFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int head,ContextRelationType contextRelationType)
	{/*	UT_R, 	USW_R,	USM_R,	USP_R,	UMATL_R,  UMATDEP_R,*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.H1T_R,new long[]{instance.ppostagids[head]},   
				new long[]{posDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.H1SW_R,new long[]{instance.splitFormids08[head]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H1SM_R,new long[]{instance.splitLemmaids08[head]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.H1SP_R,new long[]{instance.splitppostagids08[head]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) 
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1FN_R,Arrays.stream(instance.featsNE03ids08[head]!=null?instance.featsNE03ids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[head]!=null?buildArrayOfValue(instance.featsNE03ids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1FB_R,Arrays.stream(instance.featsNEBBNids08[head]!=null?instance.featsNEBBNids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsNEBBNids08[head]!=null?buildArrayOfValue(instance.featsNEBBNids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1FS_R,Arrays.stream(instance.featsWnssids08[head]!=null?instance.featsWnssids08[head]:new int[]{}).asLongStream().toArray(),    
					instance.featsWnssids08[head]!=null?buildArrayOfValue(instance.featsWnssids08[head].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1MATL_R,new long[]{instance.maltDeprelids08[head]},    
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.H1MATDEP_R,new long[]{instance.maltHeads08[head]},    
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
		}
	}

	private void addCorePredicateHeadFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int head,ContextRelationType contextRelationType)
	{/*	H1M9_R,		H1L9_R,		H1DEP9_R,		H1GF9_R,		H1F9_R,		*/
		long code=0;

		code = EncodeCoreFeatureGeneric(FeatureTypes.H1M9_R,new long[]{instance.plemmaids09[head]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);

		code = EncodeCoreFeatureGeneric(FeatureTypes.H1L9_R,new long[]{instance.pdeprelids09[head]},    
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H1DEP9_R,new long[]{instance.pheads09[head]}, contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H1GF9_R,Arrays.stream(instance.featsids09[head]!=null?instance.featsids09[head]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.H1F9_R,Arrays.stream(instance.pfeatsids09[head]!=null?instance.pfeatsids09[head]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
	}
	private void addCoreUtteranceNeighboursFeatures(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{/*	UpW_R,	UpP_R,	UpM_R,	UpL_R,	UpLL_R,	UpLH_R,	UpLM_R,	UnW_R,	UnP_R,	UnM_R,	UnL_R,	UnLL_R,	UnLH_R,	UnLM_R,*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
		
    	code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_R,new long[]{instance.formids[adaptedPrevious]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_R,new long[]{instance.gpostagids[adaptedPrevious]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_R,new long[]{instance.lemmaids[adaptedPrevious]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpDEP_R,new long[]{instance.heads[adaptedPrevious]},    
		//new long[]{positionBits}
		//,contextRelationType);
		//fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_R,new long[]{instance.deprelids[adaptedPrevious]},    
				new long[]{labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		if(previous<predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpLL_R,new long[]{},    
					new long[]{}
					,contextRelationType);
		}
		else if(previous>predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpLH_R,new long[]{},    
					new long[]{}
					,contextRelationType);
		}
		else//parent is predicate
		{	code = EncodeCoreFeatureGeneric(FeatureTypes.UpLM_R,new long[]{},    
				new long[]{}
				,contextRelationType);}	
		fV.addFeature(code);
		///////////////////////////////////////////////////////////////////////////////////////	
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnW_R,new long[]{instance.formids[adaptedNext]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnP_R,new long[]{instance.gpostagids[adaptedNext]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnM_R,new long[]{instance.lemmaids[adaptedNext]},   
				new long[]{lemmaDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnL_R,new long[]{instance.deprelids[adaptedNext]},   
				new long[]{labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UnDEP_R,new long[]{instance.heads[adaptedNext]}, contextRelationType);
		//fV.addFeature(code);
		
		if(next<predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnLL_R,new long[]{},    
					new long[]{}
					,contextRelationType);
		}
		else if(next>predicate)
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnLH_R,new long[]{},   
					new long[]{}
					, contextRelationType);
		}
		else//parent is predicate
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnLM_R,new long[]{},   
					new long[]{}
					, contextRelationType);	
		fV.addFeature(code);
	}
	private void addCoreUtteranceNeighboursFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{/*	UpT_R,	UpSW_R,	UpSM_R,	UpSP_R,	UpFN_R,	UpFB_R,	UpFS_R,	UpMATL_R,	UpMATDEP_R,	
		UnT_R,	UnSW_R,	UnSM_R,	UnSP_R,	UnFN_R,	UnFB_R,	UnFS_R,	UnMATL_R,	UnMATDEP_R,	*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpT_R,new long[]{instance.ppostagids[adaptedPrevious]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpSW_R,new long[]{instance.splitFormids08[adaptedPrevious]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpSM_R,new long[]{instance.splitLemmaids08[adaptedPrevious]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpSP_R,new long[]{instance.splitppostagids08[adaptedPrevious]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnT_R,new long[]{instance.ppostagids[adaptedNext]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnSW_R,new long[]{instance.splitFormids08[adaptedNext]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnSM_R,new long[]{instance.splitLemmaids08[adaptedNext]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnSP_R,new long[]{instance.splitppostagids08[adaptedNext]},   
				new long[]{posDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) 
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpFN_R,Arrays.stream(instance.featsNE03ids08[adaptedPrevious]!=null?instance.featsNE03ids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsNE03ids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpFB_R,Arrays.stream(instance.featsNEBBNids08[adaptedPrevious]!=null?instance.featsNEBBNids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),   
					instance.featsNEBBNids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsNEBBNids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					, contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpFS_R,Arrays.stream(instance.featsWnssids08[adaptedPrevious]!=null?instance.featsWnssids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),   
					instance.featsWnssids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsWnssids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					, contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpMATL_R,new long[]{instance.maltDeprelids08[adaptedPrevious]},   
					new long[]{labelDictionaryBits}
					, contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.UpMATDEP_R,new long[]{instance.maltHeads08[adaptedPrevious]},   
					new long[]{positionBits}
					, contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnFN_R,Arrays.stream(instance.featsNE03ids08[adaptedNext]!=null?instance.featsNE03ids08[adaptedNext]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[adaptedNext]!=null?buildArrayOfValue(instance.featsNE03ids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnFB_R,Arrays.stream(instance.featsNEBBNids08[adaptedNext]!=null?instance.featsNEBBNids08[adaptedNext]:new int[]{}).asLongStream().toArray(),    
					instance.featsNEBBNids08[adaptedNext]!=null?buildArrayOfValue(instance.featsNEBBNids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnFS_R,Arrays.stream(instance.featsWnssids08[adaptedNext]!=null?instance.featsWnssids08[adaptedNext]:new int[]{}).asLongStream().toArray(),   
					instance.featsWnssids08[adaptedNext]!=null?buildArrayOfValue(instance.featsWnssids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					, contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnMATL_R,new long[]{instance.maltDeprelids08[adaptedNext]},    
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.UnMATDEP_R,new long[]{instance.maltHeads08[adaptedNext]},    
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
		}
	}
	
	private void addCoreUtteranceNeighboursFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{/*	UpM9_R,	UpL9_R,	UpDEP9_R,	UpGF9_R,	UpF9_R,
     	UnM9_R,	UnL9_R,	UnDEP9_R,	UnGF9_R,	UnF9_R,	*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL9_R,new long[]{instance.plemmaids09[adaptedPrevious]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL9_R,new long[]{instance.pdeprelids09[adaptedPrevious]},   
				new long[]{labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpDEP9_R,new long[]{instance.pheads09[adaptedPrevious]}, contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpGF9_R,Arrays.stream(instance.featsids09[adaptedPrevious]!=null?instance.featsids09[adaptedPrevious]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpF9_R,Arrays.stream(instance.pfeatsids09[adaptedPrevious]!=null?instance.pfeatsids09[adaptedPrevious]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnL9_R,new long[]{instance.plemmaids09[adaptedNext]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UnL9_R,new long[]{instance.pdeprelids09[adaptedNext]},   
				new long[]{labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UnDEP9_R,new long[]{instance.pheads09[adaptedNext]}, contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UnGF9_R,Arrays.stream(instance.featsids09[adaptedNext]!=null?instance.featsids09[adaptedNext]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UnF9_R,Arrays.stream(instance.pfeatsids09[adaptedNext]!=null?instance.pfeatsids09[adaptedNext]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
	}
	
	private void addCorePredicateNeighboursFeatures2008(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, ContextRelationType contextRelationType)
	{/*	DpT_R,	DpSW_R,	DpSM_R,	DpSP_R,	DpFN_R,	DpFB_R,	DpFS_R,	DpMATL_R,	DpMATDEP_R,	
		DnT_R,	DnSW_R,	DnSM_R,	DnSP_R,	DnFN_R,	DnFB_R,	DnFS_R,	DnMATL_R,	DnMATDEP_R,	*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpT_R,new long[]{instance.ppostagids[adaptedPrevious]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpSW_R,new long[]{instance.splitFormids08[adaptedPrevious]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpSM_R,new long[]{instance.splitLemmaids08[adaptedPrevious]},   
				new long[]{lemmaDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpSP_R,new long[]{instance.splitppostagids08[adaptedPrevious]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnT_R,new long[]{instance.ppostagids[adaptedNext]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnSW_R,new long[]{instance.splitFormids08[adaptedNext]},    
				new long[]{wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnSM_R,new long[]{instance.splitLemmaids08[adaptedNext]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnSP_R,new long[]{instance.splitppostagids08[adaptedNext]},    
				new long[]{posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.data2008Open)) 
		{
			code = EncodeCoreFeatureGeneric(FeatureTypes.DpFN_R,Arrays.stream(instance.featsNE03ids08[adaptedPrevious]!=null?instance.featsNE03ids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsNE03ids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DpFB_R,Arrays.stream(instance.featsNEBBNids08[adaptedPrevious]!=null?instance.featsNEBBNids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),    
					instance.featsNEBBNids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsNEBBNids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DpFS_R,Arrays.stream(instance.featsWnssids08[adaptedPrevious]!=null?instance.featsWnssids08[adaptedPrevious]:new int[]{}).asLongStream().toArray(),   
					instance.featsWnssids08[adaptedPrevious]!=null?buildArrayOfValue(instance.featsWnssids08[adaptedPrevious].length,featuresDictionaryBits):new long[]{}
					, contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DpMATL_R,new long[]{instance.maltDeprelids08[adaptedPrevious]},    
					new long[]{labelDictionaryBits}
					,contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.DpMATDEP_R,new long[]{instance.maltHeads08[adaptedPrevious]},    
					new long[]{positionBits}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DnFN_R,Arrays.stream(instance.featsNE03ids08[adaptedNext]!=null?instance.featsNE03ids08[adaptedNext]:new int[]{}).asLongStream().toArray(),    
					instance.featsNE03ids08[adaptedNext]!=null?buildArrayOfValue(instance.featsNE03ids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DnFB_R,Arrays.stream(instance.featsNEBBNids08[adaptedNext]!=null?instance.featsNEBBNids08[adaptedNext]:new int[]{}).asLongStream().toArray(),    
					instance.featsNEBBNids08[adaptedNext]!=null?buildArrayOfValue(instance.featsNEBBNids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DnFS_R,Arrays.stream(instance.featsWnssids08[adaptedNext]!=null?instance.featsWnssids08[adaptedNext]:new int[]{}).asLongStream().toArray(),    
					instance.featsWnssids08[adaptedNext]!=null?buildArrayOfValue(instance.featsWnssids08[adaptedNext].length,featuresDictionaryBits):new long[]{}
					,contextRelationType);
			fV.addFeature(code);
			
			code = EncodeCoreFeatureGeneric(FeatureTypes.DnMATL_R,new long[]{instance.maltDeprelids08[adaptedNext]},   
					new long[]{labelDictionaryBits}
					, contextRelationType);
			fV.addFeature(code);
		
			code = EncodeCoreFeatureGeneric(FeatureTypes.DnMATDEP_R,new long[]{instance.maltHeads08[adaptedNext]},   
					new long[]{positionBits}
					, contextRelationType);
			fV.addFeature(code);
		}
	}

	private void addCorePredicateNeighboursFeatures2009(FeatureVector fV,DependencyInstanceConll0809 instance, int previous, int next, ContextRelationType contextRelationType)
	{/*DpM9_R,		DpL9_R,		DpDEP9_R,		DpGF9_R,		DpF9_R,			
		DnM9_R,		DnL9_R,		DnDEP9_R,		DnGF9_R,		DnF9_R,*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpL9_R,new long[]{instance.plemmaids09[adaptedPrevious]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpL9_R,new long[]{instance.pdeprelids09[adaptedPrevious]},   
				new long[]{labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DpDEP9_R,new long[]{instance.pheads09[adaptedPrevious]},   
		//new long[]{positionBits}
		//, contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DpGF9_R,Arrays.stream(instance.featsids09[adaptedPrevious]!=null?instance.featsids09[adaptedPrevious]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DpF9_R,Arrays.stream(instance.pfeatsids09[adaptedPrevious]!=null?instance.pfeatsids09[adaptedPrevious]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnL9_R,new long[]{instance.plemmaids09[adaptedNext]},    
				new long[]{lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.DnL9_R,new long[]{instance.pdeprelids09[adaptedNext]},   
				new long[]{labelDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DnDEP9_R,new long[]{instance.pheads09[adaptedNext]}, contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DnGF9_R,Arrays.stream(instance.featsids09[adaptedNext]!=null?instance.featsids09[adaptedNext]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
		//code = EncodeCoreFeatureGeneric(FeatureTypes.DnF9_R,Arrays.stream(instance.pfeatsids09[adaptedNext]!=null?instance.pfeatsids09[adaptedNext]:new int[]{}).asLongStream().toArray(), contextRelationType);
		//fV.addFeature(code);
	}
	private void addMultiBigramFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previousUtterance, int nextUtterance, int predicate, int previousPredicate,int nextPredicate,ContextRelationType contextRelationType)
	{/*	UpW_UW_R,	UpP_UP_R,	UpM_UM_R,	UpL_UL_R,	UW_UnW_R,	UP_UnP_R,	UM_UnM_R,	UL_UnL_R,*/
		long code=0;
		int adaptedPreviousPre = previousPredicate >= 0 ? previousPredicate: TOKEN_START;    	
		int adaptedNextPre = nextPredicate <= instance.length-1 ? nextPredicate: TOKEN_END;
		int adaptedPreviousArg = previousUtterance >= 0 ? previousUtterance: TOKEN_START;    	
		int adaptedNextArg = nextUtterance <= instance.length-1 ? nextUtterance: TOKEN_END;
		
		
		
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousPre].length; i++) {
					
			for (int j = 0; j < instance.predicateAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DC1_R,
						new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.predicateAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		} 
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousPre].length; i++) {
			
			for (int j = 0; j < instance.argumentAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DC2_R,new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.argumentAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousPre].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[predicate].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_DC2_R,new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i],instance.argumentAssociatedRoles[predicate][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DW_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.formids[predicate]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DP_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.ppostagids[predicate]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DM_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.lemmaids[predicate]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC1_DL_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousPre][i],instance.deprelids[predicate]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpW_DC1_R,
					new long[]{instance.formids[adaptedPreviousPre],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpP_DC1_R,
					new long[]{instance.ppostagids[adaptedPreviousPre],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpM_DC1_R,
					new long[]{instance.lemmaids[adaptedPreviousPre],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpL_DC1_R,
					new long[]{instance.deprelids[adaptedPreviousPre],instance.predicateAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_DW_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i],instance.formids[predicate]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_DP_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i],instance.ppostagids[predicate]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_DM_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i],instance.lemmaids[predicate]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpC2_DL_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousPre][i],instance.deprelids[predicate]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpW_DC2_R,
					new long[]{instance.formids[adaptedPreviousPre],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpP_DC2_R,
					new long[]{instance.ppostagids[adaptedPreviousPre],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpM_DC2_R,
					new long[]{instance.lemmaids[adaptedPreviousPre],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DpL_DC2_R,
					new long[]{instance.deprelids[adaptedPreviousPre],instance.argumentAssociatedRoles[predicate][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		//////////////////////////////////////////////////////////////////////////////
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousArg].length; i++) {
			for (int j = 0; j < instance.predicateAssociatedRoles[utterance].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UC1_R,new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.predicateAssociatedRoles[utterance][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		} 
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousArg].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[utterance].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UC2_R,new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.argumentAssociatedRoles[utterance][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousArg].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[utterance].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_UC2_R,new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i],instance.argumentAssociatedRoles[utterance][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedPreviousArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UW_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.formids[utterance]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UP_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.ppostagids[utterance]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UM_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.lemmaids[utterance]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC1_UL_R,
					new long[]{instance.predicateAssociatedRoles[adaptedPreviousArg][i],instance.deprelids[utterance]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpW_UC1_R,
					new long[]{instance.formids[adaptedPreviousArg],instance.predicateAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpP_UC1_R,
					new long[]{instance.ppostagids[adaptedPreviousArg],instance.predicateAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpM_UC1_R,
					new long[]{instance.lemmaids[adaptedPreviousArg],instance.predicateAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpL_UC1_R,
					new long[]{instance.deprelids[adaptedPreviousArg],instance.predicateAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedPreviousArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_UW_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i],instance.formids[utterance]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_UP_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i],instance.ppostagids[utterance]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_UM_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i],instance.lemmaids[utterance]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpC2_UL_R,
					new long[]{instance.argumentAssociatedRoles[adaptedPreviousArg][i],instance.deprelids[utterance]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpW_UC2_R,
					new long[]{instance.formids[adaptedPreviousArg],instance.argumentAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpP_UC2_R,
					new long[]{instance.ppostagids[adaptedPreviousArg],instance.argumentAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpM_UC2_R,
					new long[]{instance.lemmaids[adaptedPreviousArg],instance.argumentAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UpL_UC2_R,
					new long[]{instance.deprelids[adaptedPreviousArg],instance.argumentAssociatedRoles[utterance][i]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		/////////////////////NEXT////////////////////////////////////////////////
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			for (int j = 0; j < instance.predicateAssociatedRoles[adaptedNextPre].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnC1_R,new long[]{instance.predicateAssociatedRoles[predicate][i],instance.predicateAssociatedRoles[adaptedNextPre][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[adaptedNextPre].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnC2_R,new long[]{instance.predicateAssociatedRoles[predicate][i],instance.argumentAssociatedRoles[adaptedNextPre][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[predicate].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[adaptedNextPre].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_DnC2_R,new long[]{instance.argumentAssociatedRoles[predicate][i],instance.argumentAssociatedRoles[adaptedNextPre][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedNextPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DW_DnC1_R,
					new long[]{instance.formids[predicate],instance.predicateAssociatedRoles[adaptedNextPre][i]},	
					new long[]{wordDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DP_DnC1_R,
					new long[]{instance.ppostagids[predicate],instance.predicateAssociatedRoles[adaptedNextPre][i]},	
					new long[]{posDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DM_DnC1_R,
					new long[]{instance.lemmaids[predicate],instance.predicateAssociatedRoles[adaptedNextPre][i]},	
					new long[]{lemmaDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DL_DnC1_R,
					new long[]{instance.deprelids[predicate],instance.predicateAssociatedRoles[adaptedNextPre][i]},	
					new long[]{labelDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnW_R,
					new long[]{instance.predicateAssociatedRoles[predicate][i],instance.formids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnP_R,
					new long[]{instance.predicateAssociatedRoles[predicate][i],instance.ppostagids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnM_R,
					new long[]{instance.predicateAssociatedRoles[predicate][i],instance.lemmaids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC1_DnL_R,
					new long[]{instance.predicateAssociatedRoles[predicate][i],instance.deprelids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedNextPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DW_DnC2_R,
					new long[]{instance.formids[predicate],instance.argumentAssociatedRoles[adaptedNextPre][i]},	
					new long[]{wordDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DP_DnC2_R,
					new long[]{instance.ppostagids[predicate],instance.argumentAssociatedRoles[adaptedNextPre][i]},	
					new long[]{posDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DM_DnC2_R,
					new long[]{instance.lemmaids[predicate],instance.argumentAssociatedRoles[adaptedNextPre][i]},	
					new long[]{lemmaDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DL_DnC2_R,
					new long[]{instance.deprelids[predicate],instance.argumentAssociatedRoles[adaptedNextPre][i]},	
					new long[]{labelDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[predicate].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_DnW_R,
					new long[]{instance.argumentAssociatedRoles[predicate][i],instance.formids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_DnP_R,
					new long[]{instance.argumentAssociatedRoles[predicate][i],instance.ppostagids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_DnM_R,
					new long[]{instance.argumentAssociatedRoles[predicate][i],instance.lemmaids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.DC2_DnL_R,
					new long[]{instance.argumentAssociatedRoles[predicate][i],instance.deprelids[adaptedNextPre]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		
		
		
		
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			for (int j = 0; j < instance.predicateAssociatedRoles[adaptedNextArg].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnC1_R,new long[]{instance.predicateAssociatedRoles[utterance][i],instance.predicateAssociatedRoles[adaptedNextArg][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[adaptedNextArg].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnC2_R,new long[]{instance.predicateAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[adaptedNextArg][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			for (int j = 0; j < instance.argumentAssociatedRoles[adaptedNextArg].length; j++) {
				
				code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_UnC2_R,new long[]{instance.argumentAssociatedRoles[utterance][i],instance.argumentAssociatedRoles[adaptedNextArg][j]},	
						new long[]{multiModalRelationsBits,multiModalRelationsBits},
						contextRelationType);
			fV.addFeature(code);
			}
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[adaptedNextArg].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_UnC1_R,
					new long[]{instance.formids[utterance],instance.predicateAssociatedRoles[adaptedNextArg][i]},	
					new long[]{wordDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_UnC1_R,
					new long[]{instance.ppostagids[utterance],instance.predicateAssociatedRoles[adaptedNextArg][i]},	
					new long[]{posDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_UnC1_R,
					new long[]{instance.lemmaids[utterance],instance.predicateAssociatedRoles[adaptedNextArg][i]},	
					new long[]{lemmaDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_UnC1_R,
					new long[]{instance.deprelids[utterance],instance.predicateAssociatedRoles[adaptedNextArg][i]},	
					new long[]{labelDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.predicateAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnW_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.formids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnP_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.ppostagids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnM_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.lemmaids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC1_UnL_R,
					new long[]{instance.predicateAssociatedRoles[utterance][i],instance.deprelids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[adaptedNextPre].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UW_UnC2_R,
					new long[]{instance.formids[utterance],instance.argumentAssociatedRoles[adaptedNextArg][i]},	
					new long[]{wordDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UP_UnC2_R,
					new long[]{instance.ppostagids[utterance],instance.argumentAssociatedRoles[adaptedNextArg][i]},	
					new long[]{posDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UM_UnC2_R,
					new long[]{instance.lemmaids[utterance],instance.argumentAssociatedRoles[adaptedNextArg][i]},	
					new long[]{lemmaDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UL_UnC2_R,
					new long[]{instance.deprelids[utterance],instance.argumentAssociatedRoles[adaptedNextArg][i]},	
					new long[]{labelDictionaryBits,multiModalRelationsBits},
					contextRelationType);
			fV.addFeature(code);
		}
		for (int i = 0; i < instance.argumentAssociatedRoles[utterance].length; i++) {
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_UnW_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.formids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,wordDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_UnP_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.ppostagids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,posDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_UnM_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.lemmaids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,lemmaDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
			code = EncodeCoreMultiFeatureGeneric(FeatureTypes.UC2_UnL_R,
					new long[]{instance.argumentAssociatedRoles[utterance][i],instance.deprelids[adaptedNextArg]},	
					new long[]{multiModalRelationsBits,labelDictionaryBits},
					contextRelationType);
			fV.addFeature(code);
		}
		
	}
	private void addUtteranceBigramFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previous, int next, int predicate, ContextRelationType contextRelationType)
	{/*	UpW_UW_R,	UpP_UP_R,	UpM_UM_R,	UpL_UL_R,	UW_UnW_R,	UP_UnP_R,	UM_UnM_R,	UL_UnL_R,*/
		long code=0;
		int adaptedPrevious = previous>= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next<= instance.length-1 ? next: TOKEN_END;
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance]},    
				new long[]{wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance]},    
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance]},    
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance]},    
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_UnW_R,new long[]{instance.formids[utterance],instance.formids[adaptedNext]},    
				new long[]{wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_UnP_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[adaptedNext]},   
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_UnM_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[adaptedNext]},    
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_UnL_R,new long[]{instance.deprelids[utterance],instance.deprelids[adaptedNext]},    
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
	}
	private void add1OBigramUtterancePredicateFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{/*	UpW_UW_DW_R,	UpW_UW_DP_R,	UpW_UW_DM_R,	UpW_UW_DL_R,
	UpP_UP_DW_R,	UpP_UP_DP_R,	UpP_UP_DM_R,	UpP_UP_DL_R,	
	UpM_UM_DW_R,	UpM_UM_DP_R,	UpM_UM_DM_R,	UpM_UM_DL_R,
	UpL_UL_DW_R,	UpL_UL_DP_R,	UpL_UL_DM_R,	UpL_UL_DL_R,
	UW_UnW_DW_R,	UW_UnW_DP_R,	UW_UnW_DM_R,	UW_UnW_DL_R,
	UP_UnP_DW_R,	UP_UnP_DP_R,	UP_UnP_DM_R,	UP_UnP_DL_R,
	UM_UnM_DW_R,	UM_UnM_DP_R,	UM_UnM_DM_R,	UM_UnM_DL_R,
	UL_UnL_DW_R,	UL_UnL_DP_R,	UL_UnL_DM_R,	UL_UnL_DL_R,
*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
    	
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_DW_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance],instance.formids[predicate]},   
				new long[]{wordDictionaryBits,wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_DP_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance],instance.gpostagids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_DM_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance],instance.lemmaids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_DL_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance],instance.deprelids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_DW_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance],instance.formids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_DP_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance],instance.gpostagids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_DM_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance],instance.lemmaids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_DL_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance],instance.deprelids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_DW_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance],instance.formids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_DP_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance],instance.gpostagids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_DM_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance],instance.lemmaids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_DL_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance],instance.deprelids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_DW_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance],instance.formids[predicate]},
    	//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_DP_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance],instance.gpostagids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_DM_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance],instance.lemmaids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_DL_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance],instance.deprelids[predicate]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL9_UL9_DL9_R,new long[]{instance.pdeprelids09[adaptedPrevious],instance.pdeprelids09[utterance],instance.pdeprelids09[predicate]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		/////////////////////////////////////////////////////////////////
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UW_UnW_DW_R,new long[]{instance.formids[utterance],instance.formids[adaptedNext],instance.formids[predicate]},   
				new long[]{wordDictionaryBits,wordDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UW_UnW_DP_R,new long[]{instance.formids[utterance],instance.formids[adaptedNext],instance.gpostagids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UW_UnW_DM_R,new long[]{instance.formids[utterance],instance.formids[adaptedNext],instance.lemmaids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UW_UnW_DL_R,new long[]{instance.formids[utterance],instance.formids[adaptedNext],instance.deprelids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
				
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_UnP_DW_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[adaptedNext],instance.formids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_UnP_DP_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[adaptedNext],instance.gpostagids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_UnP_DM_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[adaptedNext],instance.lemmaids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UP_UnP_DL_R,new long[]{instance.gpostagids[utterance],instance.gpostagids[adaptedNext],instance.deprelids[predicate]},   
				new long[]{posDictionaryBits,posDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_UnM_DW_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[adaptedNext],instance.formids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_UnM_DP_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[adaptedNext],instance.gpostagids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_UnM_DM_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[adaptedNext],instance.lemmaids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UM_UnM_DL_R,new long[]{instance.lemmaids[utterance],instance.lemmaids[adaptedNext],instance.deprelids[predicate]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UL_UnL_DW_R,new long[]{instance.deprelids[utterance],instance.deprelids[adaptedNext],instance.formids[predicate]},
    	//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UL_UnL_DP_R,new long[]{instance.deprelids[utterance],instance.deprelids[adaptedNext],instance.gpostagids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		//code = EncodeCoreFeatureGeneric(FeatureTypes.UL_UnL_DM_R,new long[]{instance.deprelids[utterance],instance.deprelids[adaptedNext],instance.lemmaids[predicate]},
		//		 contextRelationType);
		//fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL_UnL_DL_R,new long[]{instance.deprelids[utterance],instance.deprelids[adaptedNext],instance.deprelids[predicate]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		code = EncodeCoreFeatureGeneric(FeatureTypes.UL9_UnL9_DL9_R,new long[]{instance.pdeprelids09[utterance],instance.pdeprelids09[adaptedNext],instance.pdeprelids09[predicate]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
	}
	
	private void addUtteranceTrigramFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{	/*  UpW_UW_UnW_R,		UpP_UP_UnP_R,		UpM_UM_UnM_R,		UpL_UL_UnL_R,*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
    	code = EncodeCoreFeatureGeneric(FeatureTypes.UpW_UW_UnW_R,new long[]{instance.formids[adaptedPrevious],instance.formids[utterance],instance.formids[adaptedNext]},   
				new long[]{wordDictionaryBits,wordDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpP_UP_UnP_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[utterance],instance.gpostagids[adaptedNext]},   
				new long[]{posDictionaryBits,posDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpM_UM_UnM_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[utterance],instance.lemmaids[adaptedNext]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.UpL_UL_UnL_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[utterance],instance.deprelids[adaptedNext]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
	}
	private void addPredicateBigramFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{/*	DpW_DW_R,	DpP_DP_R,	DpM_DM_R,	DpL_DL_R,			DW_DnW_R,	DP_DnP_R,	DM_DnM_R,	DL_DnL_R,*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
    	code = EncodeCoreFeatureGeneric(FeatureTypes.DpW_DW_R,new long[]{instance.formids[adaptedPrevious],instance.formids[predicate]},   
				new long[]{wordDictionaryBits,wordDictionaryBits}
				, contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpP_DP_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[predicate]},    
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpM_DM_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[predicate]},    
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpL_DL_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[predicate]},    
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DW_DnW_R,new long[]{instance.formids[predicate],instance.formids[adaptedNext]},    
				new long[]{wordDictionaryBits,wordDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DP_DnP_R,new long[]{instance.gpostagids[predicate],instance.gpostagids[adaptedNext]},    
				new long[]{posDictionaryBits,posDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DM_DnM_R,new long[]{instance.lemmaids[predicate],instance.lemmaids[adaptedNext]},    
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DL_DnL_R,new long[]{instance.deprelids[predicate],instance.deprelids[adaptedNext]},    
				new long[]{labelDictionaryBits,labelDictionaryBits}
				,contextRelationType);
		fV.addFeature(code);
		
	}
	
	private void addPredicateTrigramFeatures(FeatureVector fV,DependencyInstanceConll0809 instance,int utterance, int previous, int next, int predicate,ContextRelationType contextRelationType)
	{	/*  DpW_DW_DnW_R,	DpP_DP_DnP_R,	DpM_DM_DnM_R,	DpL_DL_DnL_R,*/
		long code=0;
		int adaptedPrevious = previous >= 0 ? previous: TOKEN_START;    	
		int adaptedNext = next <= instance.length-1 ? next : TOKEN_END;
    	code = EncodeCoreFeatureGeneric(FeatureTypes.DpW_DW_DnW_R,new long[]{instance.formids[adaptedPrevious],instance.formids[predicate],instance.formids[adaptedNext]},   
				new long[]{wordDictionaryBits,wordDictionaryBits,wordDictionaryBits}
				,
    			 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpP_DP_DnP_R,new long[]{instance.gpostagids[adaptedPrevious],instance.gpostagids[predicate],instance.gpostagids[adaptedNext]},   
				new long[]{posDictionaryBits,posDictionaryBits,posDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpM_DM_DnM_R,new long[]{instance.lemmaids[adaptedPrevious],instance.lemmaids[predicate],instance.lemmaids[adaptedNext]},   
				new long[]{lemmaDictionaryBits,lemmaDictionaryBits,lemmaDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
		
		code = EncodeCoreFeatureGeneric(FeatureTypes.DpL_DL_DnL_R,new long[]{instance.deprelids[adaptedPrevious],instance.deprelids[predicate],instance.deprelids[adaptedNext]},   
				new long[]{labelDictionaryBits,labelDictionaryBits,labelDictionaryBits}
				,
				 contextRelationType);
		fV.addFeature(code);
	}
	private int EncodeCoreFeatureGenericOld(FeatureTypes feature, long[] elementIds,ContextRelationType contextRelationType)
	{
		StringBuilder code=new StringBuilder(feature.name()).append("_");
		for (int i = 0; i < elementIds.length; i++) {
			code.append(elementIds[i]+"*");//
		}
		code.append(contextRelationType.ordinal());
		
	
		return code.toString().hashCode();
	}
	
	private long EncodeCoreFeatureGeneric(FeatureTypes feature, long[] elementIds,long[] shiftValues,ContextRelationType contextRelationType)
	{
		assert elementIds.length==shiftValues.length;
		//return EncodeCoreFeatureGenericOld( feature, elementIds,contextRelationType);
		
		long result=feature.ordinal();
		//StringBuilder code=new StringBuilder(feature.name()).append("_");
		for (int i = 0; i < elementIds.length; i++) {
			result=(result<<shiftValues[i])|elementIds[i];
		}
		result=(result<<contextRelationsBits)|contextRelationType.ordinal();
		return result;
		
//		return code.toString().hashCode();
	}
	private long EncodeCoreMultiFeatureGeneric(FeatureTypes feature, long[] elementIds,long[] shiftValues,
		//int verbnetRelationType,	
		ContextRelationType contextRelationType)
	{
		assert elementIds.length==shiftValues.length;
		//return EncodeCoreFeatureGenericOld( feature, elementIds,contextRelationType);
		
		long result=feature.ordinal();
		//StringBuilder code=new StringBuilder(feature.name()).append("_");
		for (int i = 0; i < elementIds.length; i++) {
			result=(result<<shiftValues[i])|elementIds[i];
		}
		//result=(result<<multiModalRelationsBits)|verbnetRelationType;//.ordinal();
		result=(result<<contextRelationsBits)|contextRelationType.ordinal();
		return result;
		
//		return code.toString().hashCode();
	}
	private List<List<Integer>> getPathString(DependencyInstanceConll0809 instance,int predicateId, int argumentId)//0 is up, 1 is down
	{
		List<Integer> upPath=new ArrayList<Integer>();
		List<Integer> downPath=new ArrayList<Integer>();
		if (isChildNode(instance, predicateId,argumentId)) {
			downPath =getDirectedDependencyString(instance, predicateId, argumentId);
		}
		else if(isChildNode(instance, argumentId,predicateId)) 
		{
			upPath =getDirectedDependencyString(instance, argumentId, predicateId);
		}
		else
		{
			int nodeID=predicateId;
			
			while (nodeID!=0) {
				upPath.add(instance.deprelids[nodeID]);
				nodeID=instance.heads[nodeID];
				if (isChildNode(instance, nodeID, argumentId)) {
					downPath =getDirectedDependencyString(instance, nodeID, argumentId);
					break;
				}
			}
		}
		
		List<List<Integer>>  result=  new ArrayList<List<Integer>>();
		result.add(upPath);
		result.add(downPath);
		 return result;
	}
	private long[] buildArrayOfValue(int size, long value)
	{
		long[] result= new long[size];
		for (int i = 0; i < result.length; i++) {
			result[i]=value;
		}
		return result; 
	}
	private List<Integer> getDirectedDependencyString(DependencyInstanceConll0809 instance,int headId, int childId)
	{
		List<Integer> results=new ArrayList<Integer>();
		while(childId!=0 && childId!=headId)
		{
			results.add(instance.deprelids[childId]);
			childId=instance.heads[childId];
		}
		return results;
	}
	private boolean isChildNode(DependencyInstanceConll0809 instance,int headId, int childId)
	{
		int nodeId=childId;
		while(nodeId!=0)
		{
			if (headId== nodeId ) {
				return true;
			}
			nodeId=instance.heads[nodeId];
		}
		return false;
	}

	
	
}
