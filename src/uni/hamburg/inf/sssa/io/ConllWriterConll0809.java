package uni.hamburg.inf.sssa.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;
import uni.hamburg.inf.sssa.util.architecture.ClassicSingleton;

public class ConllWriterConll0809 extends DependencyWriter{

	private static ConllWriterConll0809 instance = null;
	public static ConllWriterConll0809 getInstance() {
	      if(instance == null) {
	         instance = new ConllWriterConll0809();
	      }
	      return instance;
	   }
	
public void writeInstance(DependencyInstanceConll0809 inst) throws IOException {
		
		//if (first) 
		//	first = false;
		//else
		//	writer.write("\n");
		int startColId=1;
		List<Integer> predicateIdInSentenceOrder=new ArrayList<Integer>();
		String[] forms = inst.forms;
		String[] lemmas = inst.lemmas;
		String[] plemmas = inst.plemmas09;
		
		String[] gpos = inst.gpostags;
		String[] ppos = inst.ppostags;
		int[] heads = inst.heads;
		int[] pheads2009 = inst.pheads09;
		//int[] labelids = inst.deprelids;
		//int[] plabelids2009 = inst.pdeprelids09;
		
		//we need this round to find the predicate list in order to use in real writting out
		for (int i = 1, N = inst.length; i < N; ++i) {
			String predicate=getPredicateOutputFormat(inst,i);
			if(!predicate.equals("-")&&!predicate.equals("_")&&!predicate.equals("O"))
			{
				predicateIdInSentenceOrder.add(i);
			}
		}
		
	    // ID FORM LEMMA COURSE-POS FINE-POS FEATURES HEAD DEPREL Predicate argumentList..
		for (int i = 1, N = inst.length; i < N; ++i) {
			/*
			if(i==1)
			{
				writer.write(""+inst.getId());
			}
			writer.write("\t");//add \t in all cases before start writing the real columns
			if(i==1)
			{
				writer.write(""+inst.getIterationId());
			}
			writer.write("\t");//add \t in all cases before start writing the real columns
			*/
			writer.write(i + "\t");
			writer.write(forms[i] + "\t");
			writer.write((lemmas != null && lemmas[i] != "" ? inst.lemmas[i] : "_") + "\t");
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				writer.write((plemmas != null && plemmas[i] != "" ? inst.plemmas09[i] : "_") + "\t");
			}
			writer.write(gpos[i] + "\t");
			writer.write(ppos[i] + "\t");
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				writer.write(inst.splitForms08[i] + "\t");
				writer.write(inst.splitLemmas08[i] + "\t");
				writer.write(inst.splitPpos08[i] + "\t");
			}
			//write out the features
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				for (int j = 0; inst.feats09[i]!=null && j < inst.feats09[i].length; j++) {
					if(j>0)
						{writer.write("|");}
					writer.write(inst.feats09[i][j]);
				}
				if(inst.feats09[i]==null || inst.feats09[i].length==0)
				{	writer.write("_");}
				writer.write("\t");
				
				for (int j = 0; inst.pfeats09[i]!=null && j < inst.pfeats09[i].length; j++) {
					if(j>0)
						{writer.write("|");}
					writer.write(inst.pfeats09[i][j]);
				}
				if(inst.pfeats09[i]==null || inst.pfeats09[i].length==0)
				{	writer.write("_");}
				writer.write("\t");
					
			}
			
			//end of the features writing 
			writer.write(heads[i] + "\t");
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				writer.write(pheads2009[i] + "\t");
			}
			writer.write( inst.deprels[i]  + "\t");
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				writer.write( inst.pdeprels09[i]  + "\t");
			}
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				writer.write( inst.fillPred09[i] + "\t");
			}
			writer.write( getPredicateOutputFormat(inst,i)  + "\t");
			for (int j = 0; j < predicateIdInSentenceOrder.size(); j++) {
				writer.write(getArgumentOutputFormat(inst,i,predicateIdInSentenceOrder,j)+ "\t");
			}
			
			writer.write("\n");
		}
		
		writer.write("\n");
	}
	private String getPredicateOutputFormat(DependencyInstanceConll0809 inst, int lineID)
	{
		String value="_";
		if(inst.getContextInstance()==null) return value;
		Set<Integer> predicateIdSet=  inst.getContextInstance().getUniquePredicateIdsSet();
		for (Iterator iterator = predicateIdSet.iterator(); iterator.hasNext();) 
		{
			Integer predicateId = (Integer) iterator.next();
			SemanticRelation justARelation=inst.getContextInstance().getFirstRelatedSemanticRelationByPredicateId(predicateId);
			if(predicateId==lineID)
			{
				value=justARelation.getPredicate();
				break;
			}
		}
		return value;
	}
	private String getArgumentOutputFormat(DependencyInstanceConll0809 inst, int lineID,List<Integer> predicateInSentenceOrder, int predicateOrder)
	{
		String value="";
		List<SemanticRelation> relations= inst.getContextInstance().getRelatedSemanticRelationsByPredicateId(predicateInSentenceOrder.get(predicateOrder));
		for (int i = 0; relations!=null&&  i <relations.size(); i++) 
		{
			Integer wordId=relations.get(i).getArgumentHeadID();
			if (wordId==lineID) {
				value=relations.get(i).getType().toString().replace("_","-");
				break; // to avoid any duplicate role assignment
			}
			/*
			
			List<Integer> wordIDs=inst.getWordsIDs(relations.get(i).getArgument(),relations.get(i).getArgumentStartID());
			
			if(wordIDs!=null && wordIDs.contains(lineID))
			{
				if( wordIDs.get(0)==lineID)
				{//Relation starts with this word
					value+="(";
					value+=relations.get(i).getType().toString();
				}
				else
				{
					value+="*";
				}
				if( wordIDs.get(wordIDs.size()-1)==lineID)
				{//Relation ends with this word
					value+=")";
				}
				break;//if you catch the word and relation
			}
			*/
		}
		return value==""?"_":value;
	}

}
