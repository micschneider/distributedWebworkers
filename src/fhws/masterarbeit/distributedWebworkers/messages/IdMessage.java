package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents an ID Message, which is sent to a waiter client as soon as it connects to the server.
 * The ID is the WebSocket ID of the WaiterEndpoint. The message inherits from ServerMessage,
 * because this message is created by the server.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class IdMessage extends ServerMessage
{
	/**
	 * Constructor for an IdMessage
	 */
	public IdMessage()
	{
		super(MessageType.ID_MESSAGE);
	}// end constructor
}// end class IdMessage
