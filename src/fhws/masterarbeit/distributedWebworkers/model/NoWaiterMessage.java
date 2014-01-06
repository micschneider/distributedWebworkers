package fhws.masterarbeit.distributedWebworkers.model;

public class NoWaiterMessage extends Message
{
	public NoWaiterMessage() 
	{
		super(MessageType.NOWAITER_MESSAGE);
	}
}
