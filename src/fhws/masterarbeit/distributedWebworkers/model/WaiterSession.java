package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

public class WaiterSession extends MySession
{
	private WaiterEndpoint waiterEndpoint;
	private boolean free;
	private int currentWorkers = 0;
	private static final int MAX_WORKERS = 3;
	
	public WaiterSession(WaiterEndpoint wep)
	{
		this.waiterEndpoint = wep;
		this.sessionId = this.waiterEndpoint.getSession().getId();
		this.free = true;
	}
	
	private void setBusy()
	{
		this.free = false;
	}
	
	private void setFree()
	{
		this.free = true;
	}
	
	private void checkIfBusy()
	{
		if(this.currentWorkers >= MAX_WORKERS)
			this.setBusy();
		else
			this.setFree();
	}
	
	public WaiterEndpoint getWaitWebsocket()
	{
		return this.waiterEndpoint;
	}
	
	public boolean isFree()
	{
		return this.free;
	}
	
	public int addWorker()
	{
		this.currentWorkers++;
		checkIfBusy();
		return this.currentWorkers;
	}
	
	public int removeWorker()
	{
		this.currentWorkers--;
		checkIfBusy();
		return this.currentWorkers;
	}
}
