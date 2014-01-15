package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * The abstract super class for exchanged ClientMessages. ClientMessages are initiated by the
 * client.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public abstract class ClientMessage extends Message
{
	/**
	 * Stores the sender ID of the client
	 */
	protected String senderId;

	/**
	 * Constructor for all ClientMessages. Uses the constructor from the super class
	 * 
	 * @param Represents the type of the message to be created. Only values defined in enum
	 *            MessageType are allowed
	 */
	public ClientMessage(MessageType type)
	{
		super(type);
	}// end constructor

	/**
	 * Returns the sender ID of the message
	 * 
	 * @return The sender ID
	 */
	public String getSenderId()
	{
		return this.senderId;
	}// end method getSenderId

	/**
	 * Sets the sender ID for the message
	 * 
	 * @param senderId The sender ID to be set
	 */
	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}// end method setSenderId
}// end abstract class ClientMessage
