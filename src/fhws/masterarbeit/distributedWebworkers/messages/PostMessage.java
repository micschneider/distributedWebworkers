package fhws.masterarbeit.distributedWebworkers.messages;

public class PostMessage extends ClientMessage
{
	public PostMessage() 
	{
		super(MessageType.POST_MESSAGE);
	}
}
