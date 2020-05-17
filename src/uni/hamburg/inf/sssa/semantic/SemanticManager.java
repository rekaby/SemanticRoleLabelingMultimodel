package uni.hamburg.inf.sssa.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.rmi.CORBA.Util;

import uni.hamburg.inf.sssa.dataset.DataSetManager;
import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.io.NomBankReader;
import uni.hamburg.inf.sssa.io.PropBankReader;
import uni.hamburg.inf.sssa.io.VerbNetReader;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class SemanticManager {

	private static SemanticManager instance = null;
	private Map<String, Set<String>> trainingDataExpectedRolesMap=new HashMap<String, Set<String>>();
	private static final String  ROLE_POS_SPLITTER="##";
	public static SemanticManager getInstance() {
	      if(instance == null) {
	         instance = new SemanticManager();
	      }
	      return instance;
	   }
	
	public Set<String> getVerbNetExpectedRoles(String predicate)
	{
		HashMap<String, Set<String>> verbnetMap=VerbNetReader.getInstance().getVerbRolesMap();
		return verbnetMap.get(predicate)==null? new HashSet<String>():verbnetMap.get(predicate);
	}
	
	public Set<String> getProbBankExpectedRoles(String predicate)
	{
		Set<String> allRoles=new HashSet<String>();
		HashMap<String, Map<String,Set<String>>> probBankMap=PropBankReader.getInstance().getBankRolesMap();
		if (probBankMap.get(predicate)==null) {
			return new HashSet<String>();
		}
		else
		{
			Map<String,Set<String>> roles=probBankMap.get(predicate);
			for (String iterable_element : roles.keySet()) {
				allRoles.addAll(roles.get(iterable_element));
			}
			return allRoles;
		}
		
	}
	public Set<String> getNounBankExpectedRoles(String predicate)
	{
		Set<String> allRoles=new HashSet<String>();
		HashMap<String, Map<String,Set<String>>> probBankMap=NomBankReader.getInstance().getBankRolesMap();
		if (probBankMap.get(predicate)==null) {
			return new HashSet<String>();
		}
		else
		{
			Map<String,Set<String>> roles=probBankMap.get(predicate);
			for (String iterable_element : roles.keySet()) {
				allRoles.addAll(roles.get(iterable_element));
			}
			return allRoles;
		}
		
	}
	
	public int getHammingDistance(List<SemanticRelation> gold, List<SemanticRelation> actual)
	{
		int distance = 0;
		for (SemanticRelation goldRelation : gold) {
			for (SemanticRelation actualRelation : actual) {
				if (goldRelation.getType().equals(actualRelation.getType())&& goldRelation.getPredicateid()==actualRelation.getPredicateid()) {
					distance++;
					break;//go to next gold
				}
			}
		}
			
			
		return distance;
	}
	public void loadTrainingDataExpectedRoles(List<DependencyInstanceConll0809> labeledSet)
	{
		for (DependencyInstanceConll0809 dependencyInstanceConll0809 : labeledSet) {
			for (SemanticRelation semanticRelation : dependencyInstanceConll0809.contextInstance.semanticRelations) {
				String pos="";
				if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2008")) 
				{
					pos=dependencyInstanceConll0809.splitPpos08[semanticRelation.predicateid];
				}
				else
				{
					pos=dependencyInstanceConll0809.ppostags[semanticRelation.predicateid];
				}
				String mapKey=semanticRelation.predicateRoot+ ROLE_POS_SPLITTER+pos.substring(0,1);
				Set<String> roles=new HashSet<String>();;
				if (trainingDataExpectedRolesMap.keySet().contains(mapKey)) 
				{
					roles=trainingDataExpectedRolesMap.get(mapKey);
				}
				roles.add(semanticRelation.getType().toString());
				trainingDataExpectedRolesMap.put(mapKey, roles);
			}
			
		}
		
	}
	public List<String> getExpectedRoles(String predicate,String pos)
	{
		String predicateRoot=Utils.extractStringFromAlphaNum(predicate);
		Set<String> expectedRoles=new HashSet<String>();
		//get roles from Probbank and training data
		
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useThesaurusRoles)) 
		{
			if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("V") && pos.startsWith("V"))
			{
				expectedRoles.addAll(SemanticManager.getInstance().getProbBankExpectedRoles(predicateRoot));
			}
			else if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("N") && pos.startsWith("N"))
			{
				expectedRoles.addAll(SemanticManager.getInstance().getNounBankExpectedRoles(predicateRoot));
			}
			else if(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("B"))
			{
				if ( pos.startsWith("V")) {
					expectedRoles.addAll(SemanticManager.getInstance().getProbBankExpectedRoles(predicateRoot));
				}
				else if(pos.startsWith("N"))
				{
					expectedRoles.addAll(SemanticManager.getInstance().getNounBankExpectedRoles(predicateRoot));
				}
			}
		}
		//System.out.println(predicate+expectedRoles);
		//System.out.println(predicateRoot+ ROLE_POS_SPLITTER+pos.subSequence(0,1));
		Set<String> rolesFromData=trainingDataExpectedRolesMap.get(predicateRoot+ ROLE_POS_SPLITTER+pos.subSequence(0,1));
		if (rolesFromData!=null) {
			expectedRoles.addAll(rolesFromData);	
		}
		//System.out.println(predicate+rolesFromData);
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.loadAllSemanticRoles)) 
		{
			for (int i = 0; i < ContextRelationType.Context_Relation_Type_End.ordinal(); i++) {
				expectedRoles.add(ContextRelationType.values()[i].toString());
			}
		}
		//System.out.println(predicate+rolesFromData);
		//System.out.println(predicate+expectedRoles);
		
		
//		if (expectedRoles==null||expectedRoles.size()==0) {
//			System.out.println(predicate+pos);
//			expectedRoles=getSpecialCaseRoles(predicate, pos);
//		}
		return new ArrayList<String>(expectedRoles);
	}
	public Set<String> getSpecialCaseRoles(String predicate,String pos)
	{
		Set<String> roles=new HashSet<String>();
		if (predicate.equalsIgnoreCase("sales") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("sale"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
					
		}
		if (predicate.equalsIgnoreCase("feelings") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("feeling"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("works") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("work"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("parts") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("part"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("statistics") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("statistic"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("dealings") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("dealing"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("hands") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("hand"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("aides") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("aide"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("numbers") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("number"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("services") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("service"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("laws") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("law"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("organs") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("organ"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("funds") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("fund"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("losses") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("loss"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("stakes") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("stake"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("minutes") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("minute"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("elements") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("element"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("earnings") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("earning"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("thanks") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("thank"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("names") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("name"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("transactions") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("transaction"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("tactics") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("tactic"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("days") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("day"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("years") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("year"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("operations") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("operation"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("rates") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("rate"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("scores") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("score"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("letters") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("letter"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("knowns") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("known"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("circumstances") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("circumstance"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("profits") && pos.equalsIgnoreCase("NNS")) {
			roles.addAll(trainingDataExpectedRolesMap.get("profit"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("%") && pos.equalsIgnoreCase("NN")) {
			roles.addAll(trainingDataExpectedRolesMap.get("0"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		
		
		if (predicate.equalsIgnoreCase("felt") && pos.equalsIgnoreCase("VBD")) {
			roles.addAll(trainingDataExpectedRolesMap.get("feel"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("felt") && pos.equalsIgnoreCase("VBN")) {
			roles.addAll(trainingDataExpectedRolesMap.get("feel"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("fell") && pos.equalsIgnoreCase("VBD")) {
			roles.addAll(trainingDataExpectedRolesMap.get("fall"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("clad") && pos.equalsIgnoreCase("VBD")) {
			roles.addAll(trainingDataExpectedRolesMap.get("clothe"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("clad") && pos.equalsIgnoreCase("VBN")) {
			roles.addAll(trainingDataExpectedRolesMap.get("clothe"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("banking") && pos.equalsIgnoreCase("VBG")) {
			roles.addAll(trainingDataExpectedRolesMap.get("bank"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		if (predicate.equalsIgnoreCase("answering") && pos.equalsIgnoreCase("VBG")) {
			roles.addAll(trainingDataExpectedRolesMap.get("answer"+ ROLE_POS_SPLITTER+pos.subSequence(0,1)));
		}
		return roles;
	}
	public int countUniqueSemanticArgument(List<SemanticRelation> relations)
	{//count the arguments used in these relations
		int result=0;
		for (int i = 0; i < relations.size(); i++) {
			SemanticRelation relation=relations.get(i);
			result+=relation.getArgument().split("\\s").length;
		}
		return result;
	}
	public int getSuitablePredicateID(DependencyInstanceConll0809 inst, int wordId)
	{
		int count=1;
		for (int i = 1; i < wordId; i++) {
			if(inst.lemmas[i].equals(inst.lemmas[wordId]))
			{
				count++;
			}
		}
		return count;
	}
	public VerbNetContextRelationType getCorelatedSemanticRelationType(VerbNetContextRelationType verbNetRelation)
	{
		return verbNetRelation;
		/*
		ContextRelationType result=null;
		switch (verbNetRelation)
		{
		case Agent:
		case Co_Agent:
		case Experiencer:
		case Cause:
			result=ContextRelationType.Agent;
			break;
		case Patient:
		case Co_Patient:
		case Theme:
		case Topic:
		case Product:
			result=ContextRelationType.Patient;
			break;	
		case Attribute:
			result=ContextRelationType.Attribute;
			break;
		case Material:
			result=ContextRelationType.Material;
			break;
		case Beneficiary:
			result=ContextRelationType.Beneficiary;
			break;
		case Recipient:
			result=ContextRelationType.Recipient;
			break;
		case Location:
			result=ContextRelationType.Location;
			break;
		case Source:
			result=ContextRelationType.Source;
			break;
		case Destination:
			result=ContextRelationType.Destination;
			break;
		//case Asset:
		//	result=ContextRelationType.Asset;
		//	break;
		//case Extent:
		//	result=ContextRelationType.Extent;
		//	break;
		case Instrument:
			result=ContextRelationType.Instrument;
			break;
		case Property:
			result=ContextRelationType.Property;
			break;
		case Time:
			result=ContextRelationType.Time;
			break;
		}
		return result;
		*/
	}
}
