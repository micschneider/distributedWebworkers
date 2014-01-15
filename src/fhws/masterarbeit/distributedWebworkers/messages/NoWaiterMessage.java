package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a message, which is sent to a sender client as an answer to his code message, when no
 * waiter/worker is free to overtake the code message. It inherits from ServerMessage because this
 * message will be created by the server
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class NoWaiterMessage extends ServerMessage
{
	/**
	 * Constructor for a NoWaiterMessage
	 */
	public NoWaiterMessage()
	{
		super(MessageType.NO_WAITER_MESSAGE);
	}// end constructor
}// end class NoWaiterMessage
