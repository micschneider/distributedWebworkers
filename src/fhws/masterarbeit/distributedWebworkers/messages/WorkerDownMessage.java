package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Represents a message, which is sent to a sender client when its worker goes done. It inherits
 * from ServerMessage because this message is created by the server.
 * 
 * @author Michael
 * @version 1.0
 */
public class WorkerDownMessage extends ServerMessage
{
	/**
	 * Constructor for a WorkerDownMessage
	 */
	public WorkerDownMessage()
	{
		super(MessageType.WORKER_DOWN_MESSAGE);
	}// end constructor
}// end class WorkerDownMessage
