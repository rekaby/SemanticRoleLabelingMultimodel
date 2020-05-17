package uni.hamburg.inf.sssa.semantic;

import java.io.Serializable;
import java.util.ArrayList;

import uni.hamburg.inf.sssa.syntactic.DependencyInstanceConll0809;
import uni.hamburg.inf.sssa.util.Utils;

public class SemanticRelation implements Serializable{
	private static final long serialVersionUID = 1L;
	String predicate=""; //action of Agent, Theme, INST and Owner in Own relation 
	String predicateRoot="";//predicate without sense 01 and so on
	String argument="";//object in Agent, Theme, Instrument, and Child (Owned)in owner relation
	ContextRelationType type;
	String sense="00";
	int trustScore=0;
	int predicateid=0;
	int argumentStartID=0;
	int argumentEndID=0;
	int argumentHeadID=0;
	
	
	public ContextRelationType getType() {
		return type;
	}
	public void setType(ContextRelationType type) {
		this.type = type;
	}
	public SemanticRelation(String predicate, String argument,int predicateId,int argumentStartID,int argumentEndID,int argumentHeadID,ContextRelationType type) {
		super();
		this.predicate = predicate;
		this.sense=Utils.extractIntFromAlphaNumInString(predicate);
		this.predicateRoot=Utils.extractStringFromAlphaNum(predicate);
		this.argument = argument;
		this.predicateid=predicateId;
		this.argumentStartID=argumentStartID;
		this.argumentEndID=argumentEndID;
		this.argumentHeadID=argumentHeadID;
		this.type=type;
		//System.out.println("New Relation:"+argument+ " "+ predicate+" "+ type.name());
	}
	public SemanticRelation(DependencyInstanceConll0809 instance,String predicate, String argument,int argumentStartID,int argumentEndID,int argumentHeadID,ContextRelationType type) {
		super();
		this.predicate = predicate;
		this.predicateRoot=Utils.extractStringFromAlphaNum(predicate);
		this.argument = argument;
		for (int i = 1; i < instance.length; i++) {
			if (instance.lemmas[i].equalsIgnoreCase(this.predicateRoot)) {
				this.predicateid=i;
				break;
			}
		}
		this.argumentStartID=argumentStartID;
		this.argumentEndID=argumentEndID;
		this.argumentHeadID=argumentHeadID;
		this.type=type;
		//System.out.println("New Relation:"+argument+ " "+ predicate+" "+ type.name());
	}
	public String getPredicate() {
		return predicate;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getPredicateRoot() {
		return predicateRoot;
	}
	public void setPredicateRoot(String predicateRoot) {
		this.predicateRoot = predicateRoot;
	}
	public String getArgument() {
		return argument;
	}
	public void setArgument(String argument) {
		this.argument = argument;
	}
	@Override
    public boolean equals(Object obj) {
       if (!(obj instanceof SemanticRelation))
            return false;
        if (obj == this)
            return true;

        SemanticRelation relation = (SemanticRelation) obj;
        return //this.argument.equals(relation.argument)&&
        		this.predicate.equals(relation.predicate)&&
        		//this.predicateRoot.equals(relation.predicateRoot)&&
        		this.type.ordinal()==relation.type.ordinal()
                //this.argumentID==relation.argumentID
        		?true:false;
    }
	@Override
	public int hashCode(){
		   return predicateAndAtype().trim()!= "" ? predicateAndAtype().trim().hashCode() : 0;
		}
	public String predicateAndAtype() {
		return  predicate+" "+ type.name();
	}
	@Override
	public String toString() 
	{
		return argument+ " "+argumentHeadID+" "+ predicate+" "+ type.name()+" "+argumentStartID+"-"+argumentEndID;
	}
	public int getTrustScore() {
		return trustScore;
	}
	public void setTrustScore(int trustScore) {
		this.trustScore = trustScore;
	}
	public int getArgumentStartID() {
		return argumentStartID;
	}
	public void setArgumentStartID(int argumentStartID) {
		this.argumentStartID = argumentStartID;
	}
	public int getArgumentEndID() {
		return argumentEndID;
	}
	public void setArgumentEndID(int argumentEndID) {
		this.argumentEndID = argumentEndID;
	}
	public int getPredicateid() {
		return predicateid;
	}
	public void setPredicateid(int predicateid) {
		this.predicateid = predicateid;
	}
	public int getArgumentHeadID() {
		return argumentHeadID;
	}
	public void setArgumentHeadID(int argumentHeadID) {
		this.argumentHeadID = argumentHeadID;
	}
	
	/*public boolean hasAction(String[] statement, int index) {//check that the verb which has one or more tokens is same verb there in the statement
		
		String[] lstTokens ;
		int count=0;
		
		try {
			lstTokens=predicate.trim().split(" ");
			for (int i = 0,j=index; i < lstTokens.length && j<statement.length; i++, j++) {
				if (lstTokens[i].equalsIgnoreCase(statement[j])) {
					count++;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (count== lstTokens.length) {
			System.out.println("Action Match "+verb +" "+index+" "+type);	
		}
	  return count== lstTokens.length? true:false;  
	}*/
	/*public boolean hasEntity(String[] statement, int index) {
		
		String[] lstTokens ;
		int count=0;
		
		try {
			lstTokens=entity.trim().split(" ");
			for (int i = 0,j=index; i < lstTokens.length && j<statement.length; i++, j++) {
				if (lstTokens[i].equalsIgnoreCase(statement[j])) {
					count++;
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (count== lstTokens.length) {
			System.out.println("Object Match "+entity +" "+index+" "+type);	
		}
	  return count== lstTokens.length? true:false;  
	}*/
	/*public boolean hasOwner(String[] statement, int index) {
		return hasAction(statement, index);
	}
	public boolean hasOwned(String[] statement, int index) {
		return hasEntity(statement, index);
	}*/
	
}
