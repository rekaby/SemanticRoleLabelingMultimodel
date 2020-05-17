package uni.hamburg.inf.sssa.logging;



/**
 * In this class , i added Log cods ,this log codes depends on numerical
 * formula. The first number at the formula dedicated to the layer that the
 * logging happened at.
 * 
 * We have "3" layers until now , layers are
 * 
 * 1- Interface. 2- Business. 3- Persistence layer.
 * 
 * Each layer communicate with many subLayers
 * 
 * 1- Interface ----- don't have subLayer 
 * 2- Business ----- layer communicate with application configuration XML file.
 * 			 |_____ Layer communicate with Data
 * Extraction configuration XML file.
 * 
 * 3-Persistence ---- layer communicate with out put txt file. 
 * 				|___ layer communicate with AO Database.
 * 
 * The first digit at the log code formula indicates to subLayer we have.
 * 
 * Interface ------------------------------------------------------- 1 
 * layer communicate with application configuration XML file --------2 
 * Layer communicate with Data Extraction configuration XML file ----3 
 * layer communicate with out put txt file---------------------------4 
 * layer communicate with AO Database -------------------------------5
 * For general exception  -------------------------------------------6
 * 
 * The second digit is determined by the following schema: 1 Info Message 2
 * Warning 3 Validation-/Format problem 4 Internal System problem 5 External
 * system problem 6 Programming-/system error 7 Unknown error
 * 
 * The other digits are made up by counting. Counting starts at 10 and is done
 * in steps of 10.
 * 
 * 
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 * If you get attention that we need more levels ,or you want to change some
 * level names, don't hesitate to change it, just cascade with others.
 * $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
 * 
 * @version $Revision:
 */
public class LogCode {

	
	/****************** This is log codes used with Exceptions ******************/
	/** Exception in reading file resources. */
	public static final ILogMsg GENERAL_CODE = new LogMsg(1);

	
}
