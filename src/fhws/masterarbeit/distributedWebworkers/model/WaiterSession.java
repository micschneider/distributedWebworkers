package fhws.masterarbeit.distributedWebworkers.model;

import javax.websocket.Session;

public class WaiterSession 
{
	private Session session;
	private String sessionId;
	private boolean free;
	
	public WaiterSession(Session session)
	{
		this.session = session;
		this.sessionId = session.getId();
		this.free = true;
	}
	
	public Session getSession()
	{
		return this.session;
	}
	
	public String getSessionId()
	{
		return this.sessionId;
	}
	
	public boolean isFree()
	{
		return this.free;
	}
}
