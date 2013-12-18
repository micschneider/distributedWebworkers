package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

public class WaiterSession 
{
	private WaiterEndpoint waiterEndpoint;
	private String sessionId;
	private boolean free;
	
	public WaiterSession(WaiterEndpoint wep)
	{
		this.waiterEndpoint = wep;
		this.sessionId = this.waiterEndpoint.getSession().getId();
		this.free = true;
	}
	
	public WaiterEndpoint getWaitWebsocket()
	{
		return this.waiterEndpoint;
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
