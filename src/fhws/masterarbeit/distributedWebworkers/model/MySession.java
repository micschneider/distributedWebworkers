package fhws.masterarbeit.distributedWebworkers.model;

public abstract class MySession 
{
	protected String sessionId;
	
	public String getSessionId()
	{
		return this.sessionId;
	}
	
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}
}
