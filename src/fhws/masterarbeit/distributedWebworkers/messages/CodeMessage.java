package fhws.masterarbeit.distributedWebworkers.messages;

public class CodeMessage extends ClientMessage 
{
	protected String waiterId;
	
	public CodeMessage()
	{
		super(MessageType.CODE_MESSAGE);
	}
	
	public String getWaiterId()
	{
		return this.waiterId;
	}
	
	public void setWaiterId(String waiterId)
	{
		this.waiterId = waiterId;
	}
}
