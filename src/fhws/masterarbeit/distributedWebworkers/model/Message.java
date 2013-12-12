package fhws.masterarbeit.distributedWebworkers.model;

public abstract class Message 
{
	protected MessageType type;
	protected String senderId;
	protected String content;
	
	public Message(MessageType type)
	{
		this.type = type;
	}
	
	public MessageType getType() 
	{
		return this.type;
	}
	public String getSenderId() 
	{
		return this.senderId;
	}
	public String getContent() 
	{
		return this.content;
	}
	
	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}
	public void setContent(String content)
	{
		this.content = content;
	}
}
