package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

/**
 * Represents a WebSocket session with a waiter client
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class WaiterSession extends MySession
{
	/**
	 * Stores a reference to the regarding WaiterEndpoint
	 */
	private WaiterEndpoint waiterEndpoint;
	/**
	 * Indicates if the WaiterSession can handle more work or not
	 */
	private boolean free;

	/**
	 * Stores the workers currently executed on the waiter client
	 */
	private int currentWorkers = 0;

	/**
	 * Final constant, which sets the maximum of concurrently executable workers on one waiter
	 * client
	 */
	private static final int MAX_WORKERS = 3;

	/**
	 * Constructor of a WaiterSession
	 * 
	 * @param wep Reference to the WaiterEndpoint object which should be stored
	 */
	public WaiterSession(WaiterEndpoint wep)
	{
		this.waiterEndpoint = wep;
		this.sessionId = this.waiterEndpoint.getSession().getId();
		this.free = true;
	}// end constructor

	/**
	 * Returns a reference to the regarding WaiterEndpoint
	 * 
	 * @return A Reference to the stored WaiterEndpoint
	 */
	public WaiterEndpoint getWaitWebsocket()
	{
		return this.waiterEndpoint;
	}// end method getWaitWebsocket()

	/**
	 * Returns, if the regarding WaiterSession is able to handle more workers or not
	 * 
	 * @return 'True' if more workers can be executed, 'False' if not
	 */
	public boolean isFree()
	{
		return this.free;
	}// end method isFree()

	/**
	 * Returns the current count of workers executed on this waiter client
	 * 
	 * @return The number of currently executed workers on the waiter client
	 */
	public int getCurrentWorkers()
	{
		return this.currentWorkers;
	}// end method getCurrentWorkers

	/**
	 * Increases the worker counter by 1 an checks the worker state
	 */
	public void addWorker()
	{
		this.currentWorkers++;
		if (this.currentWorkers >= MAX_WORKERS)
			this.free = false;
		else
			this.free = true;
	}// end method addWorker()

	/**
	 * Reduces the worker counter by 1 and checks the worker state
	 */
	public void removeWorker()
	{
		if (this.currentWorkers > 0)
			this.currentWorkers--;
		if (this.currentWorkers >= MAX_WORKERS)
			this.free = false;
		else
			this.free = true;
	}// end method removeWorker()
}// end class WaiterSession
