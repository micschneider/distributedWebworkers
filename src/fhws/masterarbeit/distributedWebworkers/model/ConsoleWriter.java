package fhws.masterarbeit.distributedWebworkers.model;

public class ConsoleWriter 
{
	private static ConsoleWriter writer = new ConsoleWriter();
	
	private ConsoleWriter()
	{	
	}//end constructor
	
	public static ConsoleWriter getConsoleWriter()
	{
		return writer;
	}//end method getConsoleWriter
	
	public void writeMessageToConsole(String message)
	{
		System.out.println(message);
	}//end method writeMessageToConsole
	
	public void writeErrorToConsole(Throwable throwable)
	{
		System.out.println("ERROR: " + throwable.getStackTrace() + ", CAUSE: " + throwable.getCause());
	}//end method writeErrorToConsole
}//end class ConsoleWriter
