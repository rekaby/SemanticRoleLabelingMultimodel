package uni.hamburg.inf.sssa.logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;

/**
 * 
 * This class is the wrapper class that wrap Logging.
 * 
 * It has methods for Fatal ,error ,warn ,info ,and debug logging levels.
 * 
 * */
public class SssaWrapperLogging {
		
	/*a Qualified name of the class that logging happened at */
	private static final String FQCN = SssaWrapperLogging.class.getName() + ".";
	
	private static SssaWrapperLogging sssaWrapperLogging = new SssaWrapperLogging() ;
	/*used to check if debug level Enabled */
	private static boolean debugEnabled =false;
	
	/*Create logger */
	private static Logger logger;
	
	private SssaWrapperLogging(){
	}
	
	/*constructor receives class name that logging
	public AOWrapperLogging(Class clazz){
	      this(clazz.getName());
	}
	
	
	/*
	 * constructor receives class  that logging ,
	 * and send the name of class the the overloaded constructor 
	public AOWrapperLogging(String argName){
		 this.logger = Logger.getLogger(argName);
		 this.debugEnabled = this.logger.isDebugEnabled();	 
	 }
	 * */
	
	public static SssaWrapperLogging getInstance(Class clazz){
		return getInstance(clazz.getName());
	}
	 private static SssaWrapperLogging  getInstance(String name) {
		 logger = Logger.getLogger(name);
		 debugEnabled = logger.isDebugEnabled();
		return sssaWrapperLogging;	
	}

	/*Log Fatal level ,don't receive throwable */
	public void logFatal(int logCode,String message){
		MDC.put("logCode", logCode);
		this.logger.log(FQCN, Level.FATAL, message,null);
	}
	
	/*Log Fatal level */
	public void logFatal(int logCode,String message,Throwable ex){
		this.logger.fatal(message, ex);
	}
	
	/*Log error level ,don't receive throwable */
	public void logError(int logCode,String message){
		MDC.put("logCode", logCode);
		this.logger.log(FQCN, Level.ERROR, message,null);
	}
	
	/*Log error level */
	public void logError(int logCode ,String message,Throwable ex){
		MDC.put("logCode", logCode);
		this.logger.error(message, ex);
	}
	/*Log warning level */
	public void warning(int logCode,String message){
		MDC.put("logCode", logCode);
		this.logger.log(FQCN, Level.WARN, message,null);
	}
	/*Log Info level */
	public void logInfo(int logCode,String message){
		MDC.put("logCode", logCode);
		this.logger.log(FQCN, Level.INFO, message, null);
	}
	/*Log Debug level */
	public void logDebug(int logCode,String message){
		if (isDebugEnabled())
	      {
			MDC.put("logCode", logCode);
			this.logger.log(FQCN, Level.DEBUG, message, null);
	      }
	}
	
	/*Logging message that is wrapped at logeMsg object */
	public void log(LogMsg logeMsg){
		if (logeMsg.getLevel().toString().equalsIgnoreCase("debug") ){
			logDebug(logeMsg.getCode(),logeMsg.getMessage());
		}
		else{
			MDC.put("logCode", logeMsg.getCode());
			this.logger.log(FQCN, logeMsg.getLevel(), logeMsg.getMessage(), logeMsg.getThrowable());
	      }
	}
	
	
	/**
	    * Returns true, if logger is in debug mode.
	    *
	    * @return true, if logger is in debug mode.
	    */
	   private boolean isDebugEnabled()
	   {
	      return this.debugEnabled;
	   }
}
