package uni.hamburg.inf.sssa.feature.matrix;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import uni.hamburg.inf.sssa.feature.FeatureVector;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.weights.Weights;

public class FeatureMatrix {

	
	FeatureVector [][] matrix;
	double [][]nodeTotalScore;
	List<String> rolesNames;
	public void initMatrix(int rolesCount, int wordsCount, int predicateID)
	{
		matrix=new FeatureVector[wordsCount][rolesCount];
		nodeTotalScore=new double[wordsCount][rolesCount];
		rolesNames=new ArrayList<String>();
		for (int i=0; i<wordsCount;i++)
		{
			for (int j = 0; j < rolesCount; j++) {
				nodeTotalScore[i][j]=0.0;
			}
			
		}
	}
	public void calculateMatrixScore( int predicateID)
	{
		if(matrix==null) return;
		int wordsCount=matrix.length;
		int rolesCount=matrix[0].length;
		
		//calculate the double matrix by dot product
		
		for (int i=1; i<wordsCount;i++)//start from 1 and ignore element 0
		{
			for (int j = 0; j < rolesCount; j++) {
				//if(i!=predicateID)
				//{
				if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.useDiskStorage)) {
					nodeTotalScore[i][j]=matrix[i][j].dotProduct(matrix[i][j],Weights.getInstance().getPublicWeights(matrix[i][j]));//,Weights.getInstance().getPublicWeights2()) ;//real calculation should be here
				}
				else
				{
					nodeTotalScore[i][j]=matrix[i][j].dotProduct(matrix[i][j],Weights.getInstance().getPublicWeights());
				}
				//}
				//else
				//{
				//	nodeTotalScore[predicateID][j]=Double.NEGATIVE_INFINITY;
				//}
			}
		}
	}
	public FeatureVector[][] getMatrix() {
		return matrix;
	}
	public void setMatrix(FeatureVector[][] matrix) {
		this.matrix = matrix;
	}
	public double[][] getNodeTotalScore() {
		return nodeTotalScore;
	}
	public void setNodeTotalScore(double[][] nodeTotalScore) {
		this.nodeTotalScore = nodeTotalScore;
	}
	public List<String> getRolesNames() {
		return rolesNames;
	}
	public void setRolesNames(List<String> rolesNames) {
		this.rolesNames = rolesNames;
	}
	public String toString()
	{
		String result="";

		if(matrix==null) return"";
		int wordsCount=matrix.length;
		int rolesCount=matrix[0].length;
		
		//calculate the double matrix by dot product
		for (int i=0; i<rolesCount;i++)
		{
			result+=String.format("%1$"+10+ "s", rolesNames.get(i));
		}
		result+="\n";
		for (int i=1; i<wordsCount;i++)//start from 1 and ignore element 0
		{
			for (int j = 0; j < rolesCount; j++) {
				
				result+=nodeTotalScore[i][j]+" ";
				
				
			}
			result+="\n";
		}
	
		return result;
	}
	
}
