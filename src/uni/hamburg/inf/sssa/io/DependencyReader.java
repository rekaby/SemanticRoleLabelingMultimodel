package uni.hamburg.inf.sssa.io;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.util.architecture.ClassicSingleton;


public abstract class DependencyReader {
	
	BufferedReader reader;
	boolean isLabeled;
	
	public static DependencyReader createDependencyReader() {
		
		if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
			return new ConllReaderConll08();
		}
		else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
			return new ConllReaderConll09();
		}
		return null;	
		
	}
	
	public abstract uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809 nextInstance(boolean trainData) throws IOException;
	public abstract boolean IsLabeledDependencyFile(String file) throws IOException;
	
	public boolean startReading(String file) throws IOException {
		isLabeled = IsLabeledDependencyFile(file);
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF8"));
		return isLabeled;
	}
	
	protected ContextInstance readSemanticRelations(ArrayList<String[]> lstLines,String[] predicates,String[] splitPpos08,int predicateCol)
	{
		ContextInstance contextInstance= new ContextInstance();
		int predicateId=1;
		int length = lstLines.size();
		for (int i = 1; i < predicates.length; i++) 
		{//loop on mentioned predicate
			
			if(predicates[i].equals("_") || predicates[i].equals("-")||predicates[i].equals("") )//predicate is not mentioned
	    	{
				continue;
	    	}
			else
			{//it is not empty one
				if ( (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("V")&&splitPpos08[i].startsWith("V"))
				    	   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("N")&&splitPpos08[i].startsWith("N"))
				    	   ||(SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.predicateType).equalsIgnoreCase("B"))
				    		) 
				{
							//we do nothing in this case, we check it
				}
				else
				{
					predicateId++;// we neglect this predicate, consequently neglect one column , but we continue
					continue;
				}
				if ( predicates[i].endsWith(".SU")) {
					predicateId++;// we neglect this predicate, consequently neglect one column , but we continue
					continue;	
				}
				
			}
			for (int j = 1; j < length + 1; ++j) {//loop on lines of input
		    	String[] parts = lstLines.get(j-1);
		    	if(parts.length> predicateId+predicateCol &&!parts[predicateId+predicateCol].equals("_")
		    			&&!parts[predicateId+predicateCol].equals("-")&&!parts[predicateId+predicateCol].equals("") 
		    			&& !parts[predicateId+predicateCol].equals("SU"))// the arguments start from 9
		    	{//TODO: Rekaby change this and add function take string and return object or null
		    		
		    	try {
					String relationType=parts[predicateId+predicateCol];
		    		relationType=relationType.replace("-", "_");//for technical reason
		    		if(Arrays.asList(ContextRelationType.values()).contains(ContextRelationType.valueOf(relationType)))
		    		{
		    			SemanticRelation semanticRelation=null;
		    			ContextRelationType contextRelationType=ContextRelationType.valueOf(relationType);
			    		//if(parts[startColId+9+i].endsWith(")"))//Argument is only one word
			    		//{
		    			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2008")) {
		    				semanticRelation=new SemanticRelation(predicates[i], parts[6],i,j,j,j,contextRelationType);//only one word argument
						}
		    			else if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equals("2009")) {
		    				semanticRelation=new SemanticRelation(predicates[i], parts[2],i,j,j,j,contextRelationType);//only one word argument
						}
			    			
				    		
			    		//}
			    		/*else
			    		{
			    			int k=0;
			    			int originalJ=j;
			    			String argument=parts[startColId+1];
				    		do {
								
								j++;
								parts = lstLines.get(j-1);
								argument+=" "+parts[startColId+1];
							}while (!parts[startColId+9+i].endsWith(")"));
							semanticRelation=new SemanticRelation(predicates.get(i), argument,originalJ,j, contextRelationType);
			    		}*/
			    		contextInstance.addSemanticRelations(semanticRelation);
		    		}
		    	} catch (IllegalArgumentException e) {
						// TODO: handle exception
				}
		    		
		    		
		    }
		    	
			}
			predicateId++;
		}
		
		
		
		
		return contextInstance;
	}

	public void close() throws IOException { if (reader != null) reader.close(); }

    
}
