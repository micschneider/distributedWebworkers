package fhws.masterarbeit.distributedWebworkers.messages;

public class TerminateMessage extends ClientMessage
{
	public TerminateMessage() 
	{
		super(MessageType.TERMINATE_MESSAGE);
	}
}
