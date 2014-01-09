package fhws.masterarbeit.distributedWebworkers.messages;

public abstract class Message 
{
	protected MessageType type;
	protected String content;
	
	public Message(MessageType type)
	{
		this.type = type;
	}
	
	public MessageType getType() 
	{
		return this.type;
	}
	
	public String getContent() 
	{
		return this.content;
	}
	
	public void setContent(String content)
	{
		this.content = content;
	}
}
