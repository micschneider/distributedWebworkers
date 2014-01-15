package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * The abstract super class for all exchanged messages.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public abstract class Message
{
	/**
	 * Stores the type of the message. Every message has a type represented by an enum value.
	 */
	protected MessageType type;

	/**
	 * Stores the content of the message in a String. Every message has a content.
	 */
	protected String content;

	/**
	 * Constructor for all message types
	 * 
	 * @param type Represents the type of the message to be created. Only values defined in enum
	 *            MessageType are allowed
	 */
	public Message(MessageType type)
	{
		this.type = type;
	}// end constructor

	/**
	 * Returns the MessageType of the message.
	 * 
	 * @return The MessageType of the message
	 */
	public MessageType getType()
	{
		return this.type;
	}// end method getType

	/**
	 * Returns the content of the message.
	 * 
	 * @return The content of the message
	 */
	public String getContent()
	{
		return this.content;
	}// end method getContent

	/**
	 * Sets the content of the message.
	 * 
	 * @param content Contains the content to be set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}// end method setContent
}// end abstract class Message
