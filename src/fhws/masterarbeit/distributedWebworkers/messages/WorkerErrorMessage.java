package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a error message, which is sent from a worker client, which executes a web worker for a
 * sender client. IF this web worker gets an error, the worker client will send a WorkerErrorMessage
 * to the server. The message has to be forwarded to the regarding sender client. Therefore the
 * worker error message has the recipient ID stored in it, because a waiter client can work for
 * multiple senders.WorkerErrorMessage inherits from ClientMessage because it is sent by a waiter
 * client.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class WorkerErrorMessage extends ClientMessage
{
	public WorkerErrorMessage()
	{
		super(MessageType.WORKER_ERROR_MESSAGE);
	}// end constructor

	/**
	 * Represents the recipient of this message
	 */
	private String recipientId;

	/**
	 * Returns the ID of the sender client the message has to be sent to
	 * 
	 * @return The ID of the recipient
	 */
	public String getRecipientId()
	{
		return this.recipientId;
	}// end method getRecipientId

	/**
	 * Sets the ID of the sender client the message has to be sent to
	 * 
	 * @param recipientId Represents the ID of the receiving sender client
	 */
	public void setRecipientId(String recipientId)
	{
		this.recipientId = recipientId;
	}// end method setRecipientId
}// end class WorkerErrorMessage
