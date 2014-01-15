package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a message which has to sent to a certain web worker. Its recipient must have received
 * a code message from the same sender client before, so the recipient can be looked up in the task
 * table. It inherits from Client Message because a sender client sends a post message to the
 * server. The server only forwards the message.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class PostMessage extends ClientMessage
{
	/**
	 * Constructor for a post message
	 */
	public PostMessage()
	{
		super(MessageType.POST_MESSAGE);
	}// end constructor
}// end class PostMessage
