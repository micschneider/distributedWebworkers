package fhws.masterarbeit.distributedWebworkers.controller;

import java.util.ArrayList;
import java.util.Iterator;

import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.IdMessage;
import fhws.masterarbeit.distributedWebworkers.messages.ResultMessage;
import fhws.masterarbeit.distributedWebworkers.messages.WorkerDownMessage;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
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
		ArrayList<TableEntry> toRemoveList = this.taskTable.getTableEntriesByWorkerId(sessionId);
		Iterator<TableEntry> it = toRemoveList.iterator();
		
		while(it.hasNext())
		{
			TableEntry te = it.next();
			WorkerDownMessage wdm = new WorkerDownMessage();
			wdm.setContent("Worker is down");
			SenderSession ss = this.sessionMonitor.getSenderSessionById(te.getSender());
			ss.getSendWebsocket().sendMessage(wdm);
			this.consoleWriter.writeMessageToConsole("Tabelleneintrag " + te + " gelöscht");
			this.taskTable.removeTableEntry(te);
		}
		this.sessionMonitor.removeWaiterSession(sessionId);
	}//end method sessionRemoved

	public void handleError(Throwable throwable) 
	{
		this.consoleWriter.writeErrorToConsole(throwable);
	}//end method handleError

	public void handleMessage(ClientMessage message, WaiterEndpoint wep) 
	{
		if(message instanceof ResultMessage)
		{
			ResultMessage rm = (ResultMessage)message;
			this.consoleWriter.writeMessageToConsole("Ergebnis von Waiter mit der ID " + wep.getSession().getId() + " erhalten");
			
			TableEntry te = this.taskTable.getTableEntryBySenderId(rm.getRecipientId());
			if(te != null)
			{
				this.consoleWriter.writeMessageToConsole("Empfänger der Ergebnisnachricht gefunden. ID: " + te.getSender());
				SenderSession recipientSession = sessionMonitor.getSenderSessionById(te.getSender());
				recipientSession.getSendWebsocket().sendMessage(rm);
				//taskTable.removeTableEntry(te);
			}
		}		
	}//end method handleTextMessage
}//end class WorkerController

