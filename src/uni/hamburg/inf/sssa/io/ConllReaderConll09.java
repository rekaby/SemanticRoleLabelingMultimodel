package uni.hamburg.inf.sssa.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.naming.PartialResultException;

import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.ContextInstance;
import uni.hamburg.inf.sssa.semantic.ContextRelationType;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.architecture.ClassicSingleton;



public class ConllReaderConll09 extends DependencyReader {

	private static ConllReaderConll09 instance = null;
	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(ConllReaderConll09.class);

	public static ConllReaderConll09 getInstance() {
	      if(instance == null) {
	         instance = new ConllReaderConll09();
	      }
	      return instance;
	   }
	
	@Override
	public DependencyInstanceConll0809 nextInstance(boolean traindata) throws IOException {
		
	    ArrayList<String[]> lstLines = new ArrayList<String[]>();
	    
	    int sentenceId=0;
	    int startColId=0;
	    
	   
	    String line = reader.readLine();
	    while (line != null && !line.equals("") && !line.startsWith("*")) {
	    	//if(!line.split("\t")[startColId+1].equals("."))
	    	//{//to ignore the ending "."
	    		lstLines.add(line.split("\t"));
	    	//}
	    	line = reader.readLine();
	    }
	    
	    if (lstLines.size() == 0) return null;
	    
	    int length = lstLines.size();
	    String[] forms = new String[length + 1];
	    String[] lemmas = new String[length + 1];
	    String[] plemmas = new String[length + 1];
	    String[] gpos = new String[length + 1];
	    String[] ppos = new String[length + 1];
	    String[][] feats09 = new String[length + 1][];
	    String[][] pfeats09 = new String[length + 1][];
	    
	    
	    String[] deprels = new String[length + 1];
	    int[] heads = new int[length + 1];
	    String[] pdeprels = new String[length + 1];
	    int[] pheads = new int[length + 1];
	    String[] fillPred09=new String [length + 1];
	    String[] predicates=new String [length + 1];//to keep predicates here 
	    
	    ContextInstance tempContextInstance=null;
	    forms[0] = "<root>";
	    lemmas[0] = "<root-LEMMA>";
	    gpos[0] = "<root-CPOS>";
	    ppos[0] = "<root-POS>";
	    plemmas[0]="<root-PLEMMA>";
	    
	    
	    feats09[0]=new String[1];
	    feats09[0][0]="root-feature";
	    pfeats09[0]=new String[1];
	    pfeats09[0][0]="root-feature";
	    
	    deprels[0] = "<no-type>";
	    heads[0] = -1;
	    pdeprels[0] = "<no-type>";
	    pheads[0] = -1;
	    fillPred09[0]="_";
	    predicates[0]="_";
	    //boolean hasLemma = false;
	    
	    // 3 eles ele pron pron-pers M|3P|NOM 4 SUBJ _ _
	    // ID FORM LEMMA gold-POS p-POS splitform  splitlemma   splitppos   feat03   featBBN  featWNN  malthead  maltdeprel  HEAD DEPREL Pred Arg
	    for (int i = 1; i < length + 1; ++i) {
	    	String[] parts = lstLines.get(i-1);
	    	
	    	//if (i==1) {//get sentence ID from first row ID
	    	//	sentenceId=startColId-1>=0?  Integer.parseInt(parts[startColId-1]):0;
		    //}
	    	
	    	forms[i] = parts[startColId+1];//we ignore the word ID

	    	//if (!parts[startColId+2].equals("_")) { 
	    	lemmas[i] = parts[startColId+2];
	    	plemmas[i] = parts[startColId+3];
	    	//	hasLemma = true;
	    	//} //else lemmas[i] = forms[i];
	    	gpos[i] = parts[startColId+4];
	    	ppos[i] = parts[startColId+5];
	    	
	    	
	    	
	    	if (!parts[startColId+6].equals("0")&& !parts[startColId+6].equals("_")) feats09[i] = parts[startColId+6].split("\\|:");
	    	if (!parts[startColId+7].equals("0")&&!parts[startColId+7].equals("_")) pfeats09[i] = parts[startColId+7].split("\\|:");
	    	
	    	
	    	try {
	    		heads[i] = Integer.parseInt(parts[startColId+8]);	
			} catch (Exception e) {
				e.printStackTrace();
				log.logFatal(LogCode.GENERAL_CODE.getCode(),"Missing dependency info in statement:"+ forms[1]+" "+ forms[2]+" "+ forms[3]+" "+ forms[4]+" "+ forms[5]+" "+ forms[6]+" with length:"+forms.length);
			}
	    	try {
	    		pheads[i] = Integer.parseInt(parts[startColId+9]);	
			} catch (Exception e) {
				e.printStackTrace();
				log.logFatal(LogCode.GENERAL_CODE.getCode(),"Missing dependency info in statement:"+ forms[1]+" "+ forms[2]+" "+ forms[3]+" "+ forms[4]+" "+ forms[5]+" "+ forms[6]+" with length:"+forms.length);
			}
	    	
	    	deprels[i] = (/*options.learnLabel &&*/ isLabeled) ? parts[startColId+10] : "<no-type>";
	    	pdeprels[i] = (/*options.learnLabel &&*/ isLabeled) ? parts[startColId+11] : "<no-type>";
	    	
	    	//if(parts.length>=startColId+13 && !parts[startColId+13].equals("_") &&!parts[startColId+13].equals("-")&&!parts[startColId+13].equals("") )//predicate is mentioned
	    	//{
	    	//if ( (ISssaPropertiesKeys.predicateType.equalsIgnoreCase("V")&&gpos[i].startsWith("V"))
	    	//   ||(ISssaPropertiesKeys.predicateType.equalsIgnoreCase("N")&&gpos[i].startsWith("N"))
	    	//   ||(ISssaPropertiesKeys.predicateType.equalsIgnoreCase("B"))
	    	//	) {
	    	fillPred09[i]=parts[startColId+12].trim();
	    	predicates[i]=parts[startColId+13].trim();
			//}
	    		
	    	//}
	    }
	    
	    if (traindata) {
	    	tempContextInstance=readSemanticRelations(lstLines,predicates,ppos,startColId+13);	
		}
	    
	    return new DependencyInstanceConll0809(sentenceId,forms, lemmas, plemmas, gpos, ppos, feats09,pfeats09, heads,pheads, deprels,pdeprels,fillPred09,predicates,tempContextInstance,traindata?"0":"");//training data
	    //}
		
	}

		@Override
	public boolean IsLabeledDependencyFile(String file) throws IOException {
		return true;
	}

}
