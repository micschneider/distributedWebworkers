package fhws.masterarbeit.distributedWebworkers.messages;

public class NoRecipientPostMessage extends ClientMessage
{
	public NoRecipientPostMessage() 
	{
		super(MessageType.NO_RECIPIENT_POST_MESSAGE);
	}
}
