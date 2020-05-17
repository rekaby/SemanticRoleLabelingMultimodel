package uni.hamburg.inf.sssa.test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import uni.hamburg.inf.sssa.annotater.SssaFunctions;
import uni.hamburg.inf.sssa.dataset.DataSetManager;
import uni.hamburg.inf.sssa.io.NomBankReader;
import uni.hamburg.inf.sssa.io.PropBankReader;
import uni.hamburg.inf.sssa.io.VerbNetReader;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;
import uni.hamburg.inf.sssa.properties.SssaProperties;
import uni.hamburg.inf.sssa.semantic.SemanticManager;

public class TestThesasures {
	private static Map<String, Set<String>> trainingDataExpectedRolesMap=new HashMap<String, Set<String>>();
	public static void main(String[] args) {
		SssaFunctions.getInstance().LoadPropertyFile(args);
		if (SssaProperties.getInstance().getBooleanProperty(ISssaPropertiesKeys.readThesaurusRoles)) {
			//VerbNetReader.getInstance().loadVerbNet();	
			PropBankReader.getInstance().loadBank();
			//NomBankReader.getInstance().loadBank();
		}
		String str="revitalize";
		SssaFunctions.getInstance().ReadInputData(true);//read train
		SssaFunctions.getInstance().ReadInputData(false);//read test data
		SssaFunctions.getInstance().resetDictionarySet();
		SemanticManager.getInstance().loadTrainingDataExpectedRoles(DataSetManager.getInstance().getLabeledSet());
		
			
		System.out.println(PropBankReader.getInstance().getBankRolesMap().get(str));
		Set<String> rolesFromData=SemanticManager.getInstance().getProbBankExpectedRoles(str+ "##"+"V");
		if (rolesFromData!=null) {
			System.out.println(rolesFromData);	
		}
		
	}
}
