package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;

import javax.websocket.Session;

public class SessionMonitor
{
	private static ArrayList<WaiterSession> waiterSessionList = new ArrayList<WaiterSession>();
	private static SessionMonitor sessionMonitor = new SessionMonitor();
	
	private SessionMonitor()
	{
	}//end constructor
	
	public static SessionMonitor getSessionMonitor()
	{
		return sessionMonitor;
	}//end method getSessionMonitor
	
	public ArrayList<WaiterSession> getWaiterSessionList()
	{
		return waiterSessionList;
	}//end method getWorkerSessionList
	
	
	public void addWaiterSession(Session session)
	{
		WaiterSession ws = new WaiterSession(session);
		waiterSessionList.add(ws);
	}//end method addSession
	
	public void removeWaiterSession(Session session)
	{
		for(WaiterSession ws : waiterSessionList)
		{
			if(session.getId() == ws.getSessionId())
				waiterSessionList.remove(ws);
		}
	}//end method removeSession
	
	public WaiterSession getFreeSessionForWaiter(String waiterId)
	{
		ArrayList<WaiterSession> freeWaiterSessions = new ArrayList<WaiterSession>();
		WaiterSession waiterHimself = null;
		for(WaiterSession ws : waiterSessionList)
		{
			if(ws.isFree() && !(ws.getSessionId().equals(waiterId)))
				freeWaiterSessions.add(ws);
			else if(ws.getSessionId().equals(waiterId))
				waiterHimself = ws;
		}
		if(freeWaiterSessions.size() > 0)
		{
			int random = (int)(Math.random() * freeWaiterSessions.size());
			return freeWaiterSessions.get(random);
		}
		else
		{
			return waiterHimself;
		}
	}
}//end class SessionMonitor
