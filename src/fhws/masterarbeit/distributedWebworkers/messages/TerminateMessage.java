package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a message which will terminate the regarding worker. It is sent from the sender
 * client, whose worker should be ended. It will be forwarded to the waiter client, which executes
 * the regarding worker. It inherits from ClientMessage because the termination is initiated by a
 * sender client.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class TerminateMessage extends ClientMessage
{
	/**
	 * Constructor for a TerminateMessage
	 */
	public TerminateMessage()
	{
		super(MessageType.TERMINATE_MESSAGE);
	}// end constructor
}// end class TerminateMessage
