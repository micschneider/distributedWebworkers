package fhws.masterarbeit.distributedWebworkers.messages;

public class NoRecipientTerminateMessage extends ServerMessage
{
	public NoRecipientTerminateMessage() 
	{
		super(MessageType.NO_RECIPIENT_TERMINATE_MESSAGE);
	}
}
