package fhws.masterarbeit.distributedWebworkers.controller;

import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.IdMessage;
import fhws.masterarbeit.distributedWebworkers.model.Message;
import fhws.masterarbeit.distributedWebworkers.model.ResultMessage;
import fhws.masterarbeit.distributedWebworkers.model.SenderSession;
import fhws.masterarbeit.distributedWebworkers.model.SessionMonitor;
import fhws.masterarbeit.distributedWebworkers.model.TableEntry;
import fhws.masterarbeit.distributedWebworkers.model.TaskTable;
import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

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

	public void waiterEndpointAdded(WaiterEndpoint wep) 
	{
		this.sessionMonitor.addWaiterSession(wep);
		IdMessage idm = new IdMessage();
		idm.setContent(wep.getSession().getId());
		wep.sendMessage(idm);
	}//end method sessionAdded

	public void waiterEndpointRemoved(String sessionId) 
	{
		this.sessionMonitor.removeWaiterSession(sessionId);
	}//end method sessionRemoved

	public void handleError(Throwable throwable) 
	{
		this.consoleWriter.writeErrorToConsole(throwable);
	}//end method handleError

	public void handleMessage(Message message, WaiterEndpoint wep) 
	{
		if(message instanceof ResultMessage)
		{
			System.out.println("Ergebnis von Waiter mit der ID " + wep.getSession().getId() + " erhalten");
			String receiverId = "";
			for(TableEntry te : taskTable)
			{
				if(te.getWorker().equals(message.getSenderId()))
				{
					receiverId = te.getSender();
					System.out.println("Empfänger der Ergebnisnachricht gefunden. ID: " + receiverId);
					taskTable.removeTableEntry(te);
					break;
				}
			}
			if(!receiverId.equals(""))
			{
				SenderSession receiverSession = sessionMonitor.getSenderSessionById(receiverId);
				receiverSession.getSendWebsocket().sendMessage(message);
			}
		}
	}//end method handleTextMessage
}//end class WorkerController

