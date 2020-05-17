
package uni.hamburg.inf.sssa.logging;

/**
 * Interface representing a logging message given by a code, message string and optional paramter, inserted into the
 * message. For using parameter, the message should have variable fields in form '{x}' where x is the number of the
 * paramter.
 *
 * @version $Revision: $
 */
public interface ILogMsg
{

   /**
    * @return Returns the code.
    */
   int getCode();

   String getMessage();


}
