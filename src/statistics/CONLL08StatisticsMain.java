package statistics;
/*
 * This class calculate some statistics of Conll08 shared task file, 
 * It needs the dependency 
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import uni.hamburg.inf.sssa.io.DependencyReader;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;

public class CONLL08StatisticsMain {

	public static void main(String[] args) {
		String filePath="C:\\Users\\Amr_Local\\Desktop\\Joint Parsing Papers and Data\\Joint Shared Tasks 08-09\\2008\\conll08st\\data\\devel\\devel.composed";
		SssaProperties.getInstance().loadProperties(null);
		java.util.List<DependencyInstanceConll0809> data= new ArrayList<DependencyInstanceConll0809>(); 
		
		try {
			uni.hamburg.inf.sssa.io.DependencyReader reader = DependencyReader.createDependencyReader();
	    	reader.startReading(filePath);

	    	DependencyInstanceConll0809 inst = reader.nextInstance(true);//pipe.createInstance(reader);  
			while (inst != null) {
	    		data.add(inst);
	    		inst = reader.nextInstance(true);//pipe.createInstance(reader);
	    		
			}	
			
			//DependencyInstanceExtLable[] newList= new DependencyInstanceExtLable[data.size()];
			//for (int i = 0; i < data.size(); i++) {
			//	newList[i]=ExtendedLableCreation.createDependencyInstExtLable(data.get(i));
			//	}
			
			} catch (Exception e) {
		e.printStackTrace();
			}
		printSentenceLength(data);
		printTokensCount(data);
		printVocabSize(data);
		printPredicateTypes(data);
		printPredicateTypesCoarse(data);
		printArgumentPerPredicateTypesCoarse(data);
		printArgumentypesDistinctRolesCount(data);
		printArgumentypesRolesCount(data);
	}
private static void printSentenceLength(java.util.List<DependencyInstanceConll0809> data)	
{
	System.out.println("# of sentences: "+data.size());
}
private static void printTokensCount(java.util.List<DependencyInstanceConll0809> data)	
{
	int count=0;
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		count+=element.length-1;//to remove root
	}
	System.out.println("printTokensCount: "+count);
}
private static void printVocabSize(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		for (int i = 1; i < element.splitForms08.length; i++) {
			if (!counterMap.keySet().contains(element.splitForms08[i])) {
				counterMap.put(element.splitForms08[i], 1);
			}
			else
			{
				counterMap.put(element.splitForms08[i], counterMap.get(element.splitForms08[i])+1);
			}
		}
		
	}
	System.out.println("VocabSize: "+(counterMap.size() -1));//to remove root
}
	
private static void printPredicateTypes(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		
		for (int i = 1; i < element.splitForms08.length; i++) {
			SemanticRelation relation=element.contextInstance.getFirstRelatedSemanticRelationByPredicateId(i);
			if (relation!=null) {
				if (!counterMap.keySet().contains(element.splitPpos08[i])) {
					counterMap.put(element.splitPpos08[i], 1);
				}
				else
				{
					counterMap.put(element.splitPpos08[i], counterMap.get(element.splitPpos08[i])+1);
				}	
			}
			
		}
		
	}
	System.out.println("Predicates Types: "+counterMap);
}

private static void printPredicateTypesCoarse(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		 
		for (int i = 1; i < element.splitForms08.length; i++) {
			SemanticRelation relation=element.contextInstance.getFirstRelatedSemanticRelationByPredicateId(i);
			if (relation!=null) {
				
				if (!counterMap.keySet().contains(element.splitPpos08[i].substring(0,1))) {
					counterMap.put(element.splitPpos08[i].substring(0,1), 1);
				}
				else
				{
					counterMap.put(element.splitPpos08[i].substring(0,1), counterMap.get(element.splitPpos08[i].substring(0,1))+1);
				}	
			}
		}
	}
	System.out.println("Predicates Types Coarse: "+counterMap);
}

private static void printArgumentPerPredicateTypesCoarse(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		 
		for (int i = 1; i < element.splitForms08.length; i++) {
			List<SemanticRelation> relations=element.contextInstance.getRelatedSemanticRelationsByPredicateId(i);
			if (relations!=null && relations.size()>0) {
				
				if (!counterMap.keySet().contains(element.splitPpos08[i].substring(0,1))) {
					counterMap.put(element.splitPpos08[i].substring(0,1), relations.size());
				}
				else
				{
					counterMap.put(element.splitPpos08[i].substring(0,1), counterMap.get(element.splitPpos08[i].substring(0,1))+relations.size());
				}	
			}
		}
	}
	System.out.println("Argument per Predicates Types Coarse: "+counterMap);
}
private static void printArgumentypesDistinctRolesCount(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		
		Map<Integer, Map<String, Integer>> semanticMap =ExtendedLableCreation.getSemanticRelationMaps(element);
		for (int i = 1; i < element.splitForms08.length; i++) {

			 Map<String, Integer> elementMap= semanticMap.get(i);
			 if (!counterMap.keySet().contains(elementMap==null?"0":""+elementMap.size())) 
			{
				counterMap.put(elementMap==null?"0":""+elementMap.size(), 1);
			}
			else
			{
				counterMap.put(elementMap==null?"0":""+elementMap.size(), counterMap.get(elementMap==null?"0":""+elementMap.size())+1);
			}
		
		}
		
	}
	System.out.println("Argument-Role distinct Counts: "+counterMap);
}
private static void printArgumentypesRolesCount(java.util.List<DependencyInstanceConll0809> data)	
{
	Map<String, Integer> counterMap=new HashMap<String, Integer>();
	for (Iterator iterator = data.iterator(); iterator.hasNext();) {
		DependencyInstanceConll0809 element = (DependencyInstanceConll0809) iterator.next();
		
		Map<Integer, Map<String, Integer>> semanticMap =ExtendedLableCreation.getSemanticRelationMaps(element);
		for (int i = 1; i < element.splitForms08.length; i++) {
			int count=0;
			 Map<String, Integer> elementMap= semanticMap.get(i);
			 if (elementMap==null) {
				count=0;
			}
			 else
			 {
				 for (Iterator iterator2 = elementMap.keySet().iterator(); iterator2.hasNext();) {
					 String roleKey = (String) iterator2.next();
					count+=elementMap.get(roleKey);
				}
			 }
			 
			 if (!counterMap.keySet().contains(""+count)) 
			{
				counterMap.put(""+count, 1);
			}
			else
			{
				counterMap.put(""+count, counterMap.get(""+count)+1);
			}
		
		}
		
	}
	System.out.println("Argument-Role Counts: "+counterMap);
}

}