package fhws.masterarbeit.distributedWebworkers.messages;

public class WorkerDownMessage extends ServerMessage
{
	public WorkerDownMessage() 
	{
		super(MessageType.WORKER_DOWN_MESSAGE);
	}
}
