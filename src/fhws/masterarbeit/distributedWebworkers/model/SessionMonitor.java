package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;
import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

public class SessionMonitor
{
	private static HashMap<String, WaiterSession> waiterSessionList = new HashMap<String, WaiterSession>();
	private static HashMap<String, SenderSession> senderSessionList = new HashMap<String, SenderSession>();
	private static SessionMonitor sessionMonitor = new SessionMonitor();
	
	private SessionMonitor()
	{
	}//end constructor
	
	public static SessionMonitor getSessionMonitor()
	{
		return sessionMonitor;
	}//end method getSessionMonitor
	
	public HashMap<String, WaiterSession> getWaiterSessionList()
	{
		return waiterSessionList;
	}//end method getWorkerSessionList
	
	public HashMap<String, SenderSession> getSenderSessionList()
	{
		return senderSessionList;
	}	
	
	public void addWaiterSession(WaiterEndpoint socket)
	{
		WaiterSession ws = new WaiterSession(socket);
		waiterSessionList.put(ws.getSessionId(), ws);
	}//end method addSession
	
	public void addSenderSession(SenderEndpoint socket)
	{
		SenderSession ss = new SenderSession(socket);
		senderSessionList.put(ss.getSessionId(), ss);
	}//end method addSession
	
	public void removeWaiterSession(String sessionId)
	{
		waiterSessionList.remove(sessionId);
	}//end method removeSession
	
	public void removeSenderSession(String sessionId)
	{
		senderSessionList.remove(sessionId);
	}//end method removeSession
	
	public SenderSession getSenderSessionById(String id)
	{
		Iterator<Entry<String, SenderSession>> it = senderSessionList.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<String, SenderSession> pairs = (Map.Entry<String, SenderSession>)it.next();
	        if(pairs.getKey().equals(id))
	        	return pairs.getValue();
	    }
	    return null;
	}
	
	public WaiterSession getWaiterSessionById(String id)
	{
		Iterator<Entry<String, WaiterSession>> it = waiterSessionList.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<String, WaiterSession> pairs = (Map.Entry<String, WaiterSession>)it.next();
	        if(pairs.getKey().equals(id))
	        	return pairs.getValue();
	    }
	    return null;
	}
	
	public WaiterSession getFreeSessionForWaiter(String waiterId) throws Exception
	{
		System.out.println("Suche freie WaiterSession...");
		ArrayList<WaiterSession> freeWaiterSessions = new ArrayList<WaiterSession>();
		WaiterSession waiterHimself = null;
		
		Iterator<Entry<String, WaiterSession>> it = waiterSessionList.entrySet().iterator();
	    while (it.hasNext()) 
	    {
	        Map.Entry<String, WaiterSession> pairs = (Map.Entry<String, WaiterSession>)it.next();
	        if(pairs.getValue().isFree() && !pairs.getValue().getSessionId().equals(waiterId))
	        	freeWaiterSessions.add(pairs.getValue());
			else if(pairs.getValue().getSessionId().equals(waiterId))
				waiterHimself = pairs.getValue();
			else
				throw new Exception("NO SESSION FOUND");
	    }
	    
		if(freeWaiterSessions.size() > 0)
		{
			int random = (int)(Math.random() * freeWaiterSessions.size());
			System.out.println("Fremder Waiter mit ID " + freeWaiterSessions.get(random).getSessionId() + " erledigt die Aufgabe");
			return freeWaiterSessions.get(random);
		}
		else
		{
			System.out.println("Kein anderer freier Waiter! Arbeit muss von Worker ID " + waiterHimself.getSessionId() + " selbst erledigt werden");
			return waiterHimself;
		}
	}
}//end class SessionMonitor
