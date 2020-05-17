package statistics;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.io.ConllReaderConll09;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class StatisticsMain {

	public static void main(String[] args) {
		
		DependencyInstanceConll0809 element=null;
		String inputFilePath="C:\\Users\\Amr_Local\\Desktop\\Flickr annotation project\\10k.txt";
		int minSentenceId=0;
		int maxSentenceId=5000;
		
		int sentencesCount=0;
		int labeledSentencesCount=0;
		int unlabeledSentencesCount=0;
		
		int wordsCount=0;
		
		
		
		HashSet<String> distinctWords= new HashSet<String>();
		HashSet<String> distinctLemmas= new HashSet<String>();
		HashMap<String, Integer> posMap= new HashMap<String, Integer>();
		HashMap<ContextRelationType, Integer[]> semanticRelationsMap= new HashMap<ContextRelationType, Integer[]>();
		
		try {
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				ConllReaderConll08.getInstance().startReading(inputFilePath);
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				ConllReaderConll09.getInstance().startReading(inputFilePath);
			}
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				element=ConllReaderConll08.getInstance().nextInstance(true);
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				element=ConllReaderConll09.getInstance().nextInstance(true);
			}
			while(element!=null )
			{
				if(!(element.getId()>=minSentenceId && element.getId()<=maxSentenceId))
				{
					if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
						element=ConllReaderConll08.getInstance().nextInstance(true);
					}
					else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
						element=ConllReaderConll09.getInstance().nextInstance(true);
					}
					continue;
				}
				sentencesCount++;
			//	System.out.println(element.getId());
				wordsCount+=element.forms.length-1;//to neglect the root
				if(element.hasSemanticRelations())
				{
					labeledSentencesCount++;
					for (int i = 0; i < element.getContextInstance().getSemanticRelations().size(); i++) {
						SemanticRelation semanticRelation= element.getContextInstance().getSemanticRelations().get(i);
						if (semanticRelationsMap.keySet().contains(semanticRelation.getType())) {
							int relationsCount=semanticRelationsMap.get(semanticRelation.getType())[0];
							int argumentsCount=semanticRelationsMap.get(semanticRelation.getType())[1];
							int thisArgumentLength=semanticRelation.getArgumentEndID()-semanticRelation.getArgumentStartID()+1;
							
							semanticRelationsMap.replace(semanticRelation.getType(), new Integer[] {relationsCount+1,argumentsCount+thisArgumentLength});
						}
						else
						{
							int argumentLength=semanticRelation.getArgumentEndID()-semanticRelation.getArgumentStartID()+1;
							semanticRelationsMap.put(semanticRelation.getType(), new Integer[] {1,argumentLength});//first # is the count of relations, second is count of arguments
						}
					}
				}
				else
				{
					unlabeledSentencesCount++;
				}
				for (int i = 1; i < element.length; i++) {//ignore the root
					if (!distinctWords.contains(element.forms[i])) {
						distinctWords.add(element.forms[i]);
					}
					/*else
					{
						System.out.println("Repeated word:"+element.forms[i]);
					}*/
					if (!distinctLemmas.contains(element.lemmas[i])) {
						distinctLemmas.add(element.lemmas[i]);
					}
					/*else
					{
						System.out.println("Repeated Lemma:"+element.lemmas[i]);
					}*/
					if (!posMap.keySet().contains(element.gpostags[i])) {
						posMap.put(element.gpostags[i], 1);
					}
					else
					{
						posMap.replace(element.gpostags[i],posMap.get(element.gpostags[i])+1);
					}
				}
				
				
				
				if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
					element=ConllReaderConll08.getInstance().nextInstance(true);
				}
				else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
					element=ConllReaderConll09.getInstance().nextInstance(true);
				}
				
				
			}
			
			//Statistics OUTPUT
			System.out.println("Sentences Count: "+sentencesCount);
			System.out.println("Labeled Sentences Count: "+labeledSentencesCount);
			System.out.println("Unlabeled Sentences Count: "+unlabeledSentencesCount);
			System.out.println("Words Count: "+wordsCount);
			System.out.println("Distinct Words Count: "+distinctWords.size());
			System.out.println("Distinct Words: "+distinctWords);
			System.out.println("Distinct Lemmas Count: "+distinctLemmas.size());
			System.out.println("Distinct Lemmas: "+distinctLemmas);
			
			for (String pos : posMap.keySet()) {
				System.out.println(pos+":"+posMap.get(pos));
			}
			for (ContextRelationType key : semanticRelationsMap.keySet()) {
				System.out.println(key+":"+semanticRelationsMap.get(key)[0]+ " "+semanticRelationsMap.get(key)[1] );
			}
			
			//END Statistics OUTPUT			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
				ConllReaderConll08.getInstance().close();
			}
			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
				ConllReaderConll09.getInstance().close();
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
