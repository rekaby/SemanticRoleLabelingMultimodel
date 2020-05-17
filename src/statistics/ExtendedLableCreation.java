package statistics;

import java.nio.channels.Pipe;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.print.attribute.HashAttributeSet;

import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;

public class ExtendedLableCreation {
public static String Mode="Syn_Sem";//or Syn
	public static DependencyInstanceExtLable createDependencyInstExtLable(DependencyInstanceConll0809 originalInst)
	{
		DependencyInstanceExtLable result=new DependencyInstanceExtLable( originalInst);
		
		result.extendedLable=getExtLable(originalInst);	
		
			
		return result;
	}
	private static String[] getExtLable( DependencyInstanceConll0809 originalInst)
	{
		String[] result=new String[originalInst.length];
		Map<Integer, Map<String, Integer>> sentencemap=getSemanticRelationMaps(originalInst);
		for (int i = 0; i < result.length; i++) {
			String syn=originalInst.deprels[i];//pipe.types[originalInst.deplbids[i]];////
			//if (syn.contains("#")) {
			//	System.out.println("KOKO");
			//}
		
			String sem=getDominantSemanticLabel(sentencemap.get(i)).replaceAll("_", "");
			
			
			if (Mode.equals("Syn")) {
				result[i]=syn;
			}
			else{
				result[i]=syn+"_"+sem;	
			}

			//result[i]=syn+"_"+sem;
		}
		return result;
	}
	public static Map<Integer, Map<String, Integer>> getSemanticRelationMaps( DependencyInstanceConll0809 originalInst)
	{
		
		Map<Integer, Map<String, Integer>> sentencemap=new HashMap();//first int is word Id, then map of roles and count of each one
		if (originalInst.contextInstance==null ||originalInst.contextInstance.getSemanticRelations()==null) {
			return sentencemap;
		}
		for (int j = 0; j < originalInst.contextInstance.getSemanticRelations().size(); j++) {
			SemanticRelation relation=originalInst.contextInstance.getSemanticRelations().get(j);
			if (!sentencemap.keySet().contains(relation.getArgumentHeadID())) {
				sentencemap.put(relation.getArgumentHeadID(), new HashMap<String, Integer>());
			}
			if (sentencemap.get(relation.getArgumentHeadID()).get(relation.getType().toString())==null ) {
				sentencemap.get(relation.getArgumentHeadID()).put(relation.getType().toString(),1);
			}
			else
			{
				sentencemap.get(relation.getArgumentHeadID()).put(relation.getType().toString(),
						sentencemap.get(relation.getArgumentHeadID()).get(relation.getType().toString())+1);
			}
			
		}
		
		return sentencemap;
	}
	private static String getDominantSemanticLabel(  Map<String, Integer> map)
	{
		String label="";
		if (map==null || map.keySet()==null || map.keySet().size()==0) {
			return label;
		}
		int max=0;
		for (Iterator iterator = map.keySet().iterator(); iterator.hasNext();) {
			String key = (String) iterator.next();
			if (map.get(key)>max) {
				max=map.get(key);
				label=key;
			}
			
		}
				
		return label;
	}
}
