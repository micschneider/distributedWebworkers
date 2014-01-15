package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a code message. A code message is a ClientMessage sent via a sender WebSocket. It will
 * be forwarded to a free waiter client (if existing). The content of this message will be executed
 * by the waiter client in a new worker thread.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class CodeMessage extends ClientMessage
{
	/**
	 * Stores the waiter ID of the sender (different to the sender ID!). This is needed, so the
	 * sender has not to do his own work (because he could be registered as a waiter, too).
	 * Furthermore the waiter client has to know, for which sender ID he is working, because he can
	 * work for more than one sender. If a PostMessage is incoming the waiter has to know, which
	 * worker thread has to receive it.
	 */
	protected String waiterId;

	/**
	 * Constructor for a CodeMessage. Uses the constructor from the super class
	 */
	public CodeMessage()
	{
		super(MessageType.CODE_MESSAGE);
	}// end constructor

	/**
	 * Returns the waiter ID for the message
	 * 
	 * @return The waiter ID
	 */
	public String getWaiterId()
	{
		return this.waiterId;
	}// end method getWaiterId

	/**
	 * Sets the waiter ID for the sender of this message
	 * 
	 * @param waiterId Represents the waiter ID
	 */
	public void setWaiterId(String waiterId)
	{
		this.waiterId = waiterId;
	}// end method setWaiterId
}// end class CodeMessage
