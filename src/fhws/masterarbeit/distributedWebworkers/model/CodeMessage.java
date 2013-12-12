package fhws.masterarbeit.distributedWebworkers.model;

public class CodeMessage extends Message 
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
