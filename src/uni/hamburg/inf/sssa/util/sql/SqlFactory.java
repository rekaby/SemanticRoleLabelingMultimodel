package uni.hamburg.inf.sssa.util.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;





import uni.hamburg.inf.sssa.io.ConllReaderConll08;
import uni.hamburg.inf.sssa.io.PlanFileWriter;
import uni.hamburg.inf.sssa.semantic.SemanticRelation;
import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class SqlFactory {

	public static void main(String[] args) {
		
		DependencyInstanceConll0809 element=null;
		//String insertSentence="INSERT INTO Sentences (sentence_id, sentence, sentence_type)VALUES (";
		//String insertWords="INSERT INTO Words ( sentence_id,word_ID ,word,pos)VALUES";
		//String insertTriples="";
		int[] trainingData= {};
		try {
			ConllReaderConll08.getInstance().startReading("C:\\Users\\Amr_Local\\Desktop\\Flickr annotation project\\2000 sentences for sql.txt");
			element=ConllReaderConll08.getInstance().nextInstance(true);
			PlanFileWriter.getInstance().startWriting("C:\\Users\\Amr_Local\\Desktop\\Flickr annotation project\\2000 sentences for sql_Out.txt");
			while(element!=null )
			{
				/*if(element.getId()%100==1)
				{
					
					
				}*/
				String insertSentence="INSERT INTO Sentences (sentence_id, sentence, sentence_type)VALUES (";
				String insertWords="INSERT INTO Words ( sentence_id,word_ID ,word,pos)VALUES ";
				String insertTriples="";
				
				insertSentence+=element.getId()+", \'";
				
				for (int i = 1; i < element.forms.length; i++) {
					if(i != 1)
					{
						insertSentence+=" ";
						insertWords+=", ";
					}
					
					insertSentence+=element.forms[i].replace("\'", "\\'");
					insertWords+="("+element.getId()+", "+i+", "+"\'"+element.forms[i].replace("\'", "\\'")+"\', "+"\'"+element.gpostags[i]+"\')";
				}
				if (Arrays.asList(trainingData).contains(element.getId())) {
					insertSentence+="\', \'sample\');\n";
				}
				else
				{
					insertSentence+="\', \'auto\');\n";
				}
				//insertSentence+="\', \'auto\');\n";
				insertWords+=";\n";
				
				if (element.getContextInstance()!=null && element.getContextInstance().getSemanticRelations()!=null && element.getContextInstance().getSemanticRelations().size()>0) 
				{
					int j=0;
					insertTriples="INSERT INTO triples (sentence_id, predicate_id,relation,arg_start_id,arg_end_id,triple_type)VALUES ";
					for (Iterator<SemanticRelation> iterator = element.getContextInstance().getSemanticRelations().iterator(); iterator.hasNext();) 
					{
						if (j!=0) {
							insertTriples+=", ";
						}
						SemanticRelation semanticRelation = (SemanticRelation) iterator.next();
						//List<Integer> argumentIDs=element.getWordsIDs(semanticRelation.getArgument(),semanticRelation.getArgumentID());
						insertTriples+="("+element.getId()+", "+
						element.getWordID(Utils.extractStringFromAlphaNum(semanticRelation.getPredicate()),Utils.extractIntFromAlphaNum(semanticRelation.getPredicate()))+
						", \'"+semanticRelation.getType().name()+"\',"+
						semanticRelation.getArgumentStartID()+", "+semanticRelation.getArgumentEndID()+", "+
						"\'auto\')";
						j=1;
					}
					insertTriples+=";\n";
				}
				PlanFileWriter.getInstance().writeInstance(insertSentence);
				PlanFileWriter.getInstance().writeInstance(insertWords);
				PlanFileWriter.getInstance().writeInstance(insertTriples);
				
				System.out.println(insertSentence);
				System.out.println(insertWords);
				System.out.println(insertTriples);
				
				/*if(element.getId()%228==0)
				{
					PlanFileWriter.getInstance().close();
					
				}*/
				element=ConllReaderConll08.getInstance().nextInstance(true);
				/*if(element==null)
				{
					element=ConllReader.getInstance().nextInstance();
				}*/
			}
			
			
			PlanFileWriter.getInstance().close();
			
			ConllReaderConll08.getInstance().close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

}
