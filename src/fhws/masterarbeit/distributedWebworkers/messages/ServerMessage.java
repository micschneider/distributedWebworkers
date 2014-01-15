package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * The abstract super class for exchanged ServerMessages. ServerMessages are initiated by the
 * server.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public abstract class ServerMessage extends Message
{
	/**
	 * Constructor for all ServerMessages. Uses the constructor from the super class
	 * 
	 * @param Represents the type of the message to be created. Only values defined in enum
	 *            MessageType are allowed
	 */
	public ServerMessage(MessageType type)
	{
		super(type);
	}// end constructor
}// end abstract class ServerMessage
