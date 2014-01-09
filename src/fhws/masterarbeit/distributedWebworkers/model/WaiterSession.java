package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

public class WaiterSession extends MySession
{
	private WaiterEndpoint waiterEndpoint;
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
	
	public void setBusy()
	{
		this.free = false;
	}
	
	public void setFree()
	{
		this.free = true;
	}
	
	public boolean isFree()
	{
		return this.free;
	}
}
