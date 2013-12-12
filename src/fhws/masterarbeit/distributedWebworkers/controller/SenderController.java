package fhws.masterarbeit.distributedWebworkers.controller;

import javax.websocket.Session;

import fhws.masterarbeit.distributedWebworkers.model.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.Message;
import fhws.masterarbeit.distributedWebworkers.model.SessionMonitor;
import fhws.masterarbeit.distributedWebworkers.model.TableEntry;
import fhws.masterarbeit.distributedWebworkers.model.TaskTable;
import fhws.masterarbeit.distributedWebworkers.model.WaiterSession;
import fhws.masterarbeit.distributedWebworkers.server.SendWebsocket;
import fhws.masterarbeit.distributedWebworkers.server.WaitWebsocket;

public class SenderController
{
	private static SenderController controller = new SenderController();
	private SessionMonitor sessionMonitor;
	private ConsoleWriter consoleWriter;
	private TaskTable taskTable;
	
	private SenderController()
	{
		this.sessionMonitor = SessionMonitor.getSessionMonitor();
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.taskTable = TaskTable.getTaskTable();
	}//end constructor
	
	public static SenderController getController()
	{
		return controller;
	}//end method getController

	public void sessionAdded(Session session) 
	{
		;
	}//end method sessionAdded

	public void sessionRemoved(Session session) 
	{
		try 
		{
			SendWebsocket.closeSession(session);
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

	public void handleMessage(Message message, Session session) 
	{
		if(message instanceof CodeMessage)
		{
			CodeMessage cm = (CodeMessage)(message);
			cm.setSenderId(session.getId());
			this.consoleWriter.writeMessageToConsole("Message from Sender: " + cm.getSenderId() + "with WaiterID: " + cm.getWaiterId());

			WaiterSession ws = this.sessionMonitor.getFreeSessionForWaiter(cm.getWaiterId());
			WaitWebsocket.sendMessage(ws.getSession(), cm);
			TableEntry te = new TableEntry(cm.getSenderId(), ws.getSessionId());
			this.taskTable.addTableEntry(te);
		}
	}//end method handleTextMessage
}//end class ManagerController
