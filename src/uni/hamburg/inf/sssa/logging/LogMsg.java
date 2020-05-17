package uni.hamburg.inf.sssa.logging;

import java.text.MessageFormat;

import org.apache.log4j.Priority;

/**
 * Implementation of logging messages . Attributs for maintaining purposes are added.
 * this class looks like "TO",becasue in it we put the message, code level ...etc ,then we 
 * use this information mainly at AOWrapperlogging class.
 *
 * @version $Revision: $
 */
public class LogMsg implements ILogMsg
{
   /** Unique error code. */
   private int code;

   /** Log message. */
   private String message;


   /** Level of message (ERROR, INFO, WARNING, DEBUG). */
   private Priority level ;

   private Throwable ex;

   /**
    * Constructor for the log message manager only.
    */
   protected LogMsg()
   {
      // nop
   }

   /**
    * Constructor.
    *
    * @param code error code.
    */
   public LogMsg(int code)
   {
      setCode(code);
      setLevel(Priority.INFO);
      setMessage("");
   }

   /**
    * Constructor.
    *
    * @param code error code.
    * @param level the log level.
    * @param argMessageTemplate message template string.
    */
   public LogMsg(int code, Priority level, String argMessage)
   {
      setCode(code);
      setLevel(level);
      setMessage(argMessage);
   }

   /**
    * Constructor.
    *
    * @param Throwable.
    * @param argMessageTemplate message template string.
    */
   public LogMsg(String argMessage ,Throwable ex)
   {
      setThrowable(ex);
      setMessage(argMessage);
      setLevel(level.ERROR);
   }
   /**
    * Constructor.
    *
    * @param Throwable.
    * @param level the log level.
    * @param argMessageTemplate message template string.
    */
   public LogMsg(String argMessage,Priority level,Throwable ex)
   {
      setThrowable(ex);
      setMessage(argMessage);
      setLevel(level);
   }
   
/**
    * Constructor.
    *
    * @param code error code.
    * @param argMessageTemplate message template string.
    */
   public LogMsg(int code, String argMessage)
   {
      setCode(code);
      setMessage(argMessage);
      setLevel(Priority.INFO);
   }

   /**
    * Constructor.
    *
    * @param msg log message to copy
    */
   public LogMsg(ILogMsg msg)
   {
      this(msg.getCode(), msg.getMessage());
   }

   /**
    * {@inheritDoc}
    */
   public int getCode()
   {
      return this.code;
   }

   /**
    * Sets the logging code.
    *
    * @param code code to set
    */
   public void setCode(int code)
   {
      this.code = code;
   }
   
   /**
    * Sets the Throwable.
    *
    * @param Throwable to set
    */
   public void setThrowable(Throwable ex) {
		
	   this.ex = ex;
	   
   }
   /**
    * Gets the Throwable code.
    *
    * 
    */
   public Throwable getThrowable(){
	   return ex;
   }
  

  
  

  

   

  
   /**
    * Returns the LogLevel of this message.
    *
    * @return level.
    */
   public Priority getLevel()
   {
      return this.level;
   }

   /**
    * Sets the LogLevel.
    *
    * @param level LogLevel
    */
   public void setLevel(Priority level)
   {
      this.level = level;
   }

   /**
    * Returns the message without modification of parameter.
    *
    * @return Returns the message.
    */
   public String getMessage()
   {
      return this.message;
   }

 
   
  

   /**
    * @param argMessageFormat The messageFormat to set.
    */
   private void setMessage(String argMessage)
   {
      this.message = argMessage;
   }


}



