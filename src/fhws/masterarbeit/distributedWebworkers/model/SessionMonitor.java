package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;
import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

/**
 * Represents a monitor where all waiter and sender session can be watched and managed. It also
 * chooses free waiter sessions for a task, which has to be executed.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class SessionMonitor
{
	/**
	 * Stores a static reference to the only existing list of connected waiter sessions
	 */
	private static HashMap<String, WaiterSession> waiterSessionList = new HashMap<String, WaiterSession>();

	/**
	 * Stores a static reference to the only existing list of connected sender sessions
	 */
	private static HashMap<String, SenderSession> senderSessionList = new HashMap<String, SenderSession>();
	private static SessionMonitor sessionMonitor = new SessionMonitor();

	/**
	 * Private constructor of the session monitor
	 */
	private SessionMonitor()
	{
	}// end constructor

	/**
	 * Returns a reference to the only exising session monitor
	 * 
	 * @return The reference to the session monitor
	 */
	public static SessionMonitor getSessionMonitor()
	{
		return sessionMonitor;
	}// end method getSessionMonitor()

	/**
	 * Returns the list of connected waiter sessions
	 * 
	 * @return The list of connected waiter sessions
	 */
	public HashMap<String, WaiterSession> getWaiterSessionList()
	{
		return waiterSessionList;
	}// end method getWaiterSessionList()

	/**
	 * Returns the list of connected sender sessions
	 * 
	 * @return The list of connected sender sessions
	 */
	public HashMap<String, SenderSession> getSenderSessionList()
	{
		return senderSessionList;
	}// end method getSenderSessionList()

	/**
	 * Adds a new waiter session to the waiter session list
	 * 
	 * @param socket The WaiterEndpoint, which should be added as a WaiterSession
	 */
	public void addWaiterSession(WaiterEndpoint socket)
	{
		WaiterSession ws = new WaiterSession(socket);
		waiterSessionList.put(ws.getSessionId(), ws);
	}// end method addWaiterSession()

	/**
	 * Adds a new sender session to the sender session list
	 * 
	 * @param socket The SenderEndpoint, which should be added as a SenderSession
	 */
	public void addSenderSession(SenderEndpoint socket)
	{
		SenderSession ss = new SenderSession(socket);
		senderSessionList.put(ss.getSessionId(), ss);
	}// end method addSenderSession()

	/**
	 * Removes a waiter session from the waiter session list
	 * 
	 * @param sessionId The ID of the waiter session to be removed
	 */
	public void removeWaiterSession(String sessionId)
	{
		waiterSessionList.remove(sessionId);
	}// end method removeWaiterSession()

	/**
	 * Removes a sender session from the sender session list
	 * 
	 * @param sessionId The ID of the sender session to be removed
	 */
	public void removeSenderSession(String sessionId)
	{
		senderSessionList.remove(sessionId);
	}// end method removeSenderSession()

	/**
	 * Returns a reference to a certain sender session by a given ID
	 * 
	 * @param id The ID of the sender session, whose reference is needed
	 * @return A reference to the regarding sender session
	 */
	public SenderSession getSenderSessionById(String id)
	{
		// Iterate over the sender session list and find the regarding session
		Iterator<Entry<String, SenderSession>> it = senderSessionList.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, SenderSession> pairs = (Map.Entry<String, SenderSession>) it.next();
			if (pairs.getKey().equals(id))
				return pairs.getValue();
		}// end while
		return null;
	}// end method getSenderSessionById()

	/**
	 * Returns a reference to a certain waiter session by a given ID
	 * 
	 * @param id The ID of the waiter session, whose reference is needed
	 * @return A reference to the regarding waiter session
	 */
	public WaiterSession getWaiterSessionById(String id)
	{
		// Iterate over the waiter session list and find the regarding session
		Iterator<Entry<String, WaiterSession>> it = waiterSessionList.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, WaiterSession> pairs = (Map.Entry<String, WaiterSession>) it.next();
			if (pairs.getKey().equals(id))
				return pairs.getValue();
		}// end while
		return null;
	}// end method getWaiterSessionById()

	/**
	 * Finds a free waiter session for a new sender client, if possible. The sender sends his own
	 * waiter ID with the work package, so he can be identified and does not get his own wok package
	 * back, if there is any other free waiter session.
	 * 
	 * @param waiterId The own waiter ID of the sender session
	 * @return A reference to a free waiter session, which is not the sender, if possible
	 */
	public WaiterSession getFreeSessionForWaiter(String waiterId)
	{
		// Create a new array where all free waiter sessions a stored
		ArrayList<WaiterSession> freeWaiterSessions = new ArrayList<WaiterSession>();

		// Iterate over the waiter session list and find a free session, which is not the sender
		// All found sessions are added to the array
		Iterator<Entry<String, WaiterSession>> it = waiterSessionList.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<String, WaiterSession> pairs = (Map.Entry<String, WaiterSession>) it.next();
			if (pairs.getValue().isFree() && !pairs.getValue().getSessionId().equals(waiterId))
				freeWaiterSessions.add(pairs.getValue());
		}// end while

		// Check if any free waiter sessions were found and return a random session
		if (freeWaiterSessions.size() > 0)
		{
			int random = (int) (Math.random() * freeWaiterSessions.size());
			return freeWaiterSessions.get(random);
		}// end if
		else
		{
			// Return null if there is no free foreign waiter session
			return null;
		}// end else
	}// end method getFreeSessionForWaiter()
}// end class SessionMonitor
