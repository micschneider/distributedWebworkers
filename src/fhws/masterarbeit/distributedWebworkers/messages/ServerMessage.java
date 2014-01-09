package fhws.masterarbeit.distributedWebworkers.messages;

public abstract class ServerMessage extends Message
{
	public ServerMessage(MessageType type) 
	{
		super(type);
	}
}
