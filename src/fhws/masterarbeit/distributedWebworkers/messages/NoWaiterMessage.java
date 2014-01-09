package fhws.masterarbeit.distributedWebworkers.messages;

public class NoWaiterMessage extends ServerMessage
{
	public NoWaiterMessage() 
	{
		super(MessageType.NO_WAITER_MESSAGE);
	}
}
