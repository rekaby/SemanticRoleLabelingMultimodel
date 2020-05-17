package uni.hamburg.inf.sssa.syntactic;

import uni.hamburg.inf.sssa.util.*;
import uni.hamburg.inf.sssa.util.DictionarySet.DictionaryTypes;
import uni.hamburg.inf.sssa.util.DictionarySet.DictionaryTypes.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.io.*;

import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;


public class DependencyInstanceConll0809 implements Serializable {
	
	
//This class is valid for both conll08/09...some fields will be empty with particular data
	
	private static final long serialVersionUID = 1L;
	
	public int length;
	private int id;
	//private String iterationId;

	// FORM: the forms - usually words, like "thought"
	public String[] forms;

	// LEMMA: the lemmas, or stems, e.g. "think"
	public String[] lemmas;
	
	// PLEMMA: the lemmas, or stems, e.g. "think"
	public String[] plemmas09;
		
	// Gold-POS or POS in 09 case
	public String[] gpostags;

	// predicate-POS in 08 and 09
	public String[] ppostags;

	// FEATURES: some features associated with the elements separated by ":", e.g. "I-E:PERSON"
	public String[][] feats09;
	// FEATURES: some features associated with the elements separated by ":", e.g. "I-E:PERSON"
	public String[][] pfeats09;

	// FORM: the forms - usually words, like "thought"
	public String[] splitForms08;
	// Gold-POS
	public String[] splitLemmas08;
	// predicate-POS
	public String[] splitPpos08;

	// HEAD: the IDs of the heads for each element
	public int[] maltHeads08;

	// DEPREL: the dependency relations, e.g. "SUBJ"
	public String[] maltDeprels08;
	
	// FEATURES: some features associated with the elements separated by ":", e.g. "I-E:PERSON"
	public String[][] featsNE0308;
	// FEATURES: some features associated with the elements separated by ":", e.g. "I-E:PERSON"
	public String[][] featsNEBBN08;
	// FEATURES: some features associated with the elements separated by ":", e.g. "I-E:PERSON"
	public String[][] featsWnss08;

	// HEAD: the IDs of the heads for each element 08, 09
	public int[] heads;
	
	// HEAD: the IDs of the heads for each element
	public int[] pheads09;

	// DEPREL: the dependency relations, e.g. "SUBJ" 08, 09
	public String[] deprels;
	// DEPREL: the dependency relations, e.g. "SUBJ" 
	public String[] pdeprels09;
		
	//a list contains Y if this word is predicate from 09, It has the predicate.sense themselves if it is 08
	public String[] fillPred09;
	//It has the predicate.sense themselves if it is 08
	public String[] predList;
	//these are list of IDS not the actuall words from the input, after he reads the input he looksup in the Dicts to return the IDs and hold them here in these lists
	public int[] formids;
	public int[] lemmaids;
	public int[] plemmaids09;
	public int[] gpostagids;
	public int[] ppostagids;
	public int[] splitFormids08;
	public int[] splitLemmaids08;
	public int[] splitppostagids08;
	public int[] deprelids;
	public int[] pdeprelids09;
	public int[] maltDeprelids08;
	public int[][] featsNE03ids08;//for each word in inst, there is array of features
	public int[][] featsNEBBNids08;//for each word in inst, there is array of features
	public int[][] featsWnssids08;//for each word in inst, there is array of features
	public int[][] featsids09;//for each word in inst, there is array of features
	public int[][] pfeatsids09;//for each word in inst, there is array of features
	
	//public int[] wordVecIds;

	//public int[] deplbids;//label ID

	public ContextInstance contextInstance=new ContextInstance(); 
	public Integer[][] predicateAssociatedRoles;
	public Integer[][] argumentAssociatedRoles;
	public String[][] predicateAssociatedRolesNames;
	public String[][] argumentAssociatedRolesNames;
    
	public DependencyInstanceConll0809() {}
    
    public DependencyInstanceConll0809(int length) { this.length = length; }
    
    public DependencyInstanceConll0809(String[] forms) {
    	length = forms.length;
    	this.forms = forms;
    	this.featsNE0308 = new String[length][];
    	this.featsNEBBN08 = new String[length][];
    	this.featsWnss08 = new String[length][];
    	this.deprels = new String[length];
    }
    
    public DependencyInstanceConll0809(String[] forms, String[] gpostags, String[] ppostags, int[] heads) {
    	this.length = forms.length;
    	this.forms = forms;    	
    	this.heads = heads;
	    this.ppostags = ppostags;
	    this.gpostags = gpostags;
    }
    
    public DependencyInstanceConll0809(String[] forms, String[] gpostags,String[] ppostags, int[] heads, String[] deprels) {
    	this(forms, gpostags,ppostags, heads);
    	this.deprels = deprels;    	
    }

    public DependencyInstanceConll0809(int id,String[] forms, String[] lemmas, String[] gpostags, String[] ppostags,
    		String[] splitforms, String[] splitlemmas, String[] splitppostags,
            String[][] feats03,String[][] featsBBN,String[][] featsWNSS,
            int[] heads, String[] deprels , 
            int[] maltheads, String[] maltdeprels , String []predlist,
            ContextInstance tempContextInstance, String iterationId ) {
    	this(forms, gpostags,ppostags, heads, deprels);
    	this.id=id;
    	//this.iterationId=iterationId;
    	this.lemmas = lemmas;    	
    	this.featsNE0308 = feats03;
    	this.featsNEBBN08 = featsBBN;
    	this.featsWnss08 = featsWNSS;
    	this.contextInstance=tempContextInstance;
    	this.maltHeads08=maltheads;
    	this.maltDeprels08=maltdeprels;
    	this.splitForms08=splitforms;
    	this.splitLemmas08=splitlemmas;
    	this.splitPpos08=splitppostags;
    	this.predList=predlist;
    }
    
    public DependencyInstanceConll0809(int id,String[] forms, String[] lemmas, String [] plemmas09, String[] gpostags, String[] ppostags,
    		String[][] feats09,String[][] pfeats09,
    		int[] heads, int[] pheads09, 
    		String[] deprels , String[] pdeprels09,
            String []fillPred09,String []predlist,
            ContextInstance tempContextInstance, String iterationId ) {
    	this(forms, gpostags,ppostags, heads, deprels);
    	this.id=id;
    	//this.iterationId=iterationId;
    	this.lemmas = lemmas;    	
    	this.plemmas09=plemmas09;
    	
    	this.contextInstance=tempContextInstance;
    	this.feats09=feats09;
    	this.pfeats09=pfeats09;
    	
    	this.pheads09=pheads09;
    	this.pdeprels09=pdeprels09;
    	
    	this.fillPred09=fillPred09;
    	this.predList=predlist;
    }
    
    public DependencyInstanceConll0809(DependencyInstanceConll0809 a) {
    	//this(a.forms, a.lemmas, a.cpostags, a.postags, a.feats, a.heads, a.deprels);
    	length = a.length;
    	heads = a.heads;
    	formids = a.formids;
    	lemmaids = a.lemmaids;
    	plemmaids09 = a.plemmaids09;
    	ppostagids = a.ppostagids;
    	gpostagids = a.gpostagids;
    	deprelids = a.deprelids;
    	pdeprels09=a.pdeprels09;
    	featsNE03ids08= a.featsNE03ids08;
    	featsNEBBNids08= a.featsNEBBNids08;
    	featsWnssids08= a.featsWnssids08;
    	//wordVecIds = a.wordVecIds;
    	contextInstance=a.contextInstance;
    	forms=a.forms;
    }
    
    
    
    
    public void setInstIds(DictionarySet dicts) {
    	    	
    	formids = new int[length];    	
    	lemmaids = new int[length];    	
    	plemmaids09 = new int[length];    	
		//deplbids = new int[length];
		ppostagids = new int[length];
		gpostagids = new int[length];
		splitFormids08= new int[length];
		splitLemmaids08= new int[length];
		splitppostagids08= new int[length];
		maltDeprelids08= new int[length];
		deprelids= new int[length];
		pdeprelids09= new int[length];
		featsNE03ids08 = new int[length][];
		featsNEBBNids08 = new int[length][];
		featsWnssids08 = new int[length][];
		featsids09 = new int[length][];
		pfeatsids09= new int[length][];
		predicateAssociatedRoles= new Integer[length][0];
		argumentAssociatedRoles= new Integer[length][0];
		predicateAssociatedRolesNames= new String[length][0];
		argumentAssociatedRolesNames= new String[length][0];
		
    	for (int i = 0; i < length; ++i) {
    		formids[i] = dicts.lookupIndex(DictionaryTypes.WORD, "form="+normalize(forms[i]));
    		lemmaids[i] = dicts.lookupIndex(DictionaryTypes.LEMMA, "lemma="+normalize(lemmas[i]));
    		if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2009")) 
    		{
    			plemmaids09[i] = dicts.lookupIndex(DictionaryTypes.LEMMA, "lemma="+normalize(plemmas09[i]));	
			}
    		
    		gpostagids[i] = dicts.lookupIndex(DictionaryTypes.POS, "gpos="+gpostags[i]);
			ppostagids[i] = dicts.lookupIndex(DictionaryTypes.POS, "ppos="+ppostags[i]);
			
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2008")) 
			{
	    	splitFormids08[i]= dicts.lookupIndex(DictionaryTypes.WORD, "form="+normalize(splitForms08[i]));
			splitLemmaids08[i]= dicts.lookupIndex(DictionaryTypes.LEMMA, "lemma="+normalize(splitLemmas08[i]));
			splitppostagids08[i]= dicts.lookupIndex(DictionaryTypes.POS, "ppos="+normalize(splitPpos08[i]));
			maltDeprelids08[i] = dicts.lookupIndex(DictionaryTypes.DEPLABEL, "maltlabel="+maltDeprels08[i]) - 1;	// zero-based
			
			if (featsNE0308[i] != null && featsNE0308[i].length>0) 
			{
				featsNE03ids08[i] = new int[featsNE0308[i].length];
				for (int j = 0; j < featsNE0308[i].length; ++j)
				{	featsNE03ids08[i][j] = dicts.lookupIndex(DictionaryTypes.FEAT, "feat="+featsNE0308[i][j]);}
			}
			
			if (featsNEBBN08[i] != null && featsNEBBN08[i].length>0) 
			{
				featsNEBBNids08[i] = new int[featsNEBBN08[i].length];
				for (int j = 0; j < featsNEBBN08[i].length; ++j)
				{featsNEBBNids08[i][j] = dicts.lookupIndex(DictionaryTypes.FEAT, "feat="+featsNEBBN08[i][j]);}
					
			}
			
			if (featsWnss08[i] != null && featsWnss08[i].length>0) 
			{
				featsWnssids08[i] = new int[featsWnss08[i].length];
				for (int j = 0; j < featsWnss08[i].length; ++j)
					featsWnssids08[i][j] = dicts.lookupIndex(DictionaryTypes.FEAT, "feat="+featsWnss08[i][j]);
			}
			}
			deprelids[i] = dicts.lookupIndex(DictionaryTypes.DEPLABEL, "label="+deprels[i]) - 1;	// zero-based
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2009")) 
    		{
				pdeprelids09[i] = dicts.lookupIndex(DictionaryTypes.DEPLABEL, "label="+pdeprels09[i]) - 1;	// zero-based
    		}
			if (SssaProperties.getInstance().getStringProperty(ISssaPropertiesKeys.dataFormat).equalsIgnoreCase("2009")) 
    		{
				if (feats09[i] != null && feats09[i].length>0) {
					featsids09[i] = new int[feats09[i].length];
					for (int j = 0; j < feats09[i].length; ++j)
					{	featsids09[i][j] = dicts.lookupIndex(DictionaryTypes.FEAT, "feat="+feats09[i][j]);}
				}
				if (pfeats09[i] != null && pfeats09[i].length>0) {
					pfeatsids09[i] = new int[pfeats09[i].length];
					for (int j = 0; j < pfeats09[i].length; ++j)
					{	pfeatsids09[i][j] = dicts.lookupIndex(DictionaryTypes.FEAT, "feat="+pfeats09[i][j]);}
				}
			}
		}
    	
    	/*if (lemmas != null) {
    		lemmaids = new int[length];
    		for (int i = 0; i < length; ++i)
    			lemmaids[i] = dicts.lookupIndex(DictionaryTypes.LEMMA, normalize(lemmas[i]));
    	}*/

    	
		
    }

  
    private String normalize(String s) {
		if(s!=null && s.matches("[0-9]+|[0-9]+\\.[0-9]+|[0-9]+[0-9,]+"))
		    return "<num>";
		return s;
    }
    public boolean hasSemanticRelations()
    {
    	return contextInstance==null?false:contextInstance.hasSemanticRelations();
    }

	public ContextInstance getContextInstance() {
		return contextInstance;
	}

	public void setContextInstance(ContextInstance contextInstance) {
		this.contextInstance = contextInstance;
	}
	public int getWordID(String word, int order)
	{
		int id=-1;
		int localOrder=1;
		for (int i = 1; i < forms.length; i++) {//ignore root element
			if(lemmas[i].equals(word))
			{
				if (localOrder==order) 
				{
					id=i;
					break;
				}
				else
				{
					localOrder++;
				}
				
			}
		}
		return id;
	}
	public List<Integer> getWordsIDs(String word,int startIndex)
	{
		int id=-1;
		List<Integer> wordsIds=new ArrayList<Integer>();
		String [] words=word.split("\\s");//white space
		int j=0;
		for (int i = startIndex; i < forms.length; i++) {
			wordsIds.clear();
			for ( j = 0; j < words.length; j++) {
				if(forms.length>i && forms[i].equals(words[j]))
				{
					wordsIds.add(i);
					i++;
				}
				else
				{
					break;
				}
			}
			if(j==words.length) break;
			
		}
		return wordsIds;
	}
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof DependencyInstanceConll0809))
            return false;
        if (obj == this)
            return true;

        DependencyInstanceConll0809 instance = (DependencyInstanceConll0809) obj;
        return Arrays.equals(this.forms,instance.forms)?true:false;
    }
	public int hashCode(){
		String text="";
		for (int i = 1; i < forms.length; i++) {
			text+=forms[i]+" ";
		}
		text.trim();
		   return text!= "" ? text.hashCode() : 0;
		}
	public String toString()
	{
		String results="";
		for (int i = 0; i < forms.length; i++) {
			results+=forms[i]+ " ";
		}
		return results.trim();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	/*
	public String getIterationId() {
		return iterationId;
	}

	public void setIterationId(String iterationId) {
		this.iterationId = iterationId;
	}
*/	
}
