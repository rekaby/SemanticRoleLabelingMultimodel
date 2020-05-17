package uni.hamburg.inf.sssa.verbnet_manager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import uni.hamburg.inf.sssa.annotater.Main;
import uni.hamburg.inf.sssa.annotater.SssaFunctions;
import uni.hamburg.inf.sssa.io.VerbNetReader;
import uni.hamburg.inf.sssa.logging.LogCode;
import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;

public class Test {
	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(Test.class);
	public static void main(String[] args){
		SssaFunctions.getInstance().LoadPropertyFile(args);
		VerbNetReader.getInstance().loadVerbNet();
		HashMap<String, Set<String>> map=VerbNetReader.getInstance().getVerbRolesMap();
		log.logInfo(LogCode.GENERAL_CODE.getCode(),"---------------Calculate Conf Scores After Elementry---------------------");
		log.logInfo(LogCode.GENERAL_CODE.getCode(),map.keySet().toString());
		Set<String> roles=new HashSet<>();
		for (String key : map.keySet()) {
			roles.addAll(map.get(key));
		}
		log.logInfo(LogCode.GENERAL_CODE.getCode(),roles.toString());
		System.out.println(map.get("try"));
	}
	
}
