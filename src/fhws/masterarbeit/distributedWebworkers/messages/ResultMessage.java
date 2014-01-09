package fhws.masterarbeit.distributedWebworkers.messages;

public class ResultMessage extends ClientMessage
{
	private String recipientId;
	
	public ResultMessage() 
	{
		super(MessageType.RESULT_MESSAGE);
	}
	
	public String getRecipientId()
	{
		return this.recipientId;
	}
	
	public void setRecipientId(String recipientId)
	{
		this.recipientId = recipientId;
	}
}
