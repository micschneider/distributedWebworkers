package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;

public class SenderSession extends MySession
{
	private SenderEndpoint senderEndpoint;
	
	public SenderSession(SenderEndpoint sep)
	{
		this.senderEndpoint = sep;
		this.sessionId = this.senderEndpoint.getSession().getId();
	}
	
	public SenderEndpoint getSendWebsocket()
	{
		return this.senderEndpoint;
	}
}
