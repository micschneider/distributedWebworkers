package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a post message, whose recipient can not be found in the task table. It inherits from
 * ClientMessage, because the post message is sent from a sender client. The message will be sent
 * back to the sender client it comes from, so the sender client has to deal with it.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class NoRecipientPostMessage extends ClientMessage
{
	/**
	 * Constructor for a NoRecipientPostMessage
	 */
	public NoRecipientPostMessage()
	{
		super(MessageType.NO_RECIPIENT_POST_MESSAGE);
	}// end constructor
}// end class NoRecipientPostMessage
