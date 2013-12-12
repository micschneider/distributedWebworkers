package fhws.masterarbeit.distributedWebworkers.controller;

import javax.websocket.Session;

import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.IdMessage;
import fhws.masterarbeit.distributedWebworkers.model.SessionMonitor;
import fhws.masterarbeit.distributedWebworkers.model.TaskTable;
import fhws.masterarbeit.distributedWebworkers.model.WaiterSession;
import fhws.masterarbeit.distributedWebworkers.server.WaitWebsocket;

public class WaiterController 
{
	private static WaiterController controller = new WaiterController();
	private SessionMonitor sessionMonitor;
	private ConsoleWriter consoleWriter;
	private TaskTable taskTable;
	
	private WaiterController()
	{
		this.sessionMonitor = SessionMonitor.getSessionMonitor();
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.taskTable = TaskTable.getTaskTable();
	}//end constructor
	
	public static WaiterController getController()
	{
		return controller;
	}//end method getController

	public void sessionAdded(Session session) 
	{
		this.sessionMonitor.addWaiterSession(session);
		IdMessage idm = new IdMessage();
		idm.setContent(session.getId());
		WaitWebsocket.sendMessage(session, idm);
	}//end method sessionAdded

	public void sessionRemoved(Session session) 
	{
		this.sessionMonitor.removeWaiterSession(session);
				
		try 
		{
			WaitWebsocket.closeSession(session);
		}//end try
		catch (Throwable throwable)
		{
			this.consoleWriter.writeErrorToConsole(throwable);
		}//end catch
	}//end method sessionRemoved

	public void handleError(Throwable throwable) 
	{
		consoleWriter.writeErrorToConsole(throwable);
	}//end method handleError

	public void handleTextMessage(String message) 
	{
		consoleWriter.writeMessageToConsole(message);
	}//end method handleTextMessage
}//end class WorkerController

