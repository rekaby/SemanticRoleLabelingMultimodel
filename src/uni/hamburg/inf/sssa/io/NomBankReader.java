package uni.hamburg.inf.sssa.io;

import uni.hamburg.inf.sssa.logging.SssaWrapperLogging;
import uni.hamburg.inf.sssa.properties.ISssaPropertiesKeys;

public class NomBankReader extends PropBankReader {

	private static SssaWrapperLogging log = SssaWrapperLogging.getInstance(NomBankReader.class);
	private static NomBankReader instance = null;
	public static NomBankReader getInstance() {
	      if(instance == null) {
	         instance = new NomBankReader();
	         instance.repositoryPath=ISssaPropertiesKeys.nomBankFolderPath;
	      }
	      return instance;
	   }
	
}
