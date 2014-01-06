package fhws.masterarbeit.distributedWebworkers.controller;

import fhws.masterarbeit.distributedWebworkers.model.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.Message;
import fhws.masterarbeit.distributedWebworkers.model.NoWaiterMessage;
import fhws.masterarbeit.distributedWebworkers.model.SessionMonitor;
import fhws.masterarbeit.distributedWebworkers.model.TableEntry;
import fhws.masterarbeit.distributedWebworkers.model.TaskTable;
import fhws.masterarbeit.distributedWebworkers.model.WaiterSession;
import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;

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

	public void senderEndpointAdded(SenderEndpoint sep) 
	{
		this.sessionMonitor.addSenderSession(sep);
	}//end method sessionAdded

	public void senderEndpointRemoved(String sessionId) 
	{
		this.sessionMonitor.removeSenderSession(sessionId);
	}//end method sessionRemoved

	public void handleError(Throwable throwable) 
	{
		consoleWriter.writeErrorToConsole(throwable);
	}//end method handleError

	public void handleMessage(Message message, SenderEndpoint sep) throws Exception 
	{
		if(message instanceof CodeMessage)
		{
			CodeMessage cm = (CodeMessage)(message);
			WaiterSession ws = this.sessionMonitor.getFreeSessionForWaiter(cm.getWaiterId());
			if (ws != null)
			{
				ws.getWaitWebsocket().sendMessage(message);
				TableEntry te = new TableEntry(cm.getSenderId(), ws.getSessionId());
				System.out.println("Neuer Eintrag in der TaskTable: " + te);
				this.taskTable.addTableEntry(te);
			}
			else
			{
				NoWaiterMessage nwm = new NoWaiterMessage();
				nwm.setContent("Kein freier Waiter");
				sep.sendMessage(nwm);
			}
		}
	}//end method handleTextMessage
}//end class ManagerController
