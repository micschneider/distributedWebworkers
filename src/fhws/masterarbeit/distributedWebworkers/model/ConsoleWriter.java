package fhws.masterarbeit.distributedWebworkers.model;

/**
 * Represents the Logging screen. All output, which should be printed on the console is made via
 * this class. There is only one instance of this class.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class ConsoleWriter
{
	/**
	 * Stores the only reference to the ConsoleWriter
	 */
	private static ConsoleWriter writer = new ConsoleWriter();

	/**
	 * Constructor of the ConsoleWriter class, which can only be accessed internally
	 */
	private ConsoleWriter()
	{
	}// end constructor

	/**
	 * Returns a reference to the ConsoleWriter
	 * 
	 * @return A reference to the ConsoleWriter instance
	 */
	public static ConsoleWriter getConsoleWriter()
	{
		return writer;
	}// end method getConsoleWriter

	/**
	 * Writes a message to the console
	 * 
	 * @param message Represents the message to be logged
	 */
	public void writeMessageToConsole(String message)
	{
		System.out.println(message);
	}// end method writeMessageToConsole

	/**
	 * Writes an error/exception to the console
	 * 
	 * @param throwable Represents the throwable object
	 */
	public void writeErrorToConsole(Throwable throwable)
	{
		System.out.println("ERROR: " + throwable.getMessage() + ", CAUSE: " + throwable.getCause());
	}// end method writeErrorToConsole
}// end class ConsoleWriter
