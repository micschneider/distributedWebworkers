package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;

public class SenderSession 
{
	private SenderEndpoint senderEndpoint;
	private String sessionId;
	
	public SenderSession(SenderEndpoint sep)
	{
		this.senderEndpoint = sep;
		this.sessionId = this.senderEndpoint.getSession().getId();
	}
	
	public SenderEndpoint getSendWebsocket()
	{
		return this.senderEndpoint;
	}
	
	public String getSessionId()
	{
		return this.sessionId;
	}
}
