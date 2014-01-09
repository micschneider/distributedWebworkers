package fhws.masterarbeit.distributedWebworkers.messages;

public abstract class ClientMessage extends Message
{
	protected String senderId;
	
	public ClientMessage(MessageType type) 
	{
		super(type);
	}
	
	public String getSenderId()
	{
		return this.senderId;
	}
	
	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}
}
