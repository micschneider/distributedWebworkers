package fhws.masterarbeit.distributedWebworkers.controller;

import fhws.masterarbeit.distributedWebworkers.messages.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.messages.Message;
import fhws.masterarbeit.distributedWebworkers.messages.NoRecipientPostMessage;
import fhws.masterarbeit.distributedWebworkers.messages.NoRecipientTerminateMessage;
import fhws.masterarbeit.distributedWebworkers.messages.NoWaiterMessage;
import fhws.masterarbeit.distributedWebworkers.messages.PostMessage;
import fhws.masterarbeit.distributedWebworkers.messages.TerminateMessage;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.SenderSession;
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
		TableEntry toRemove = this.taskTable.getTableEntryBySenderId(sessionId);
		
		if(toRemove != null)
		{
			TerminateMessage tm = new TerminateMessage();
			tm.setContent("Sender ist down");
			WaiterSession ws = this.sessionMonitor.getWaiterSessionById(toRemove.getWorker());
			ws.getWaitWebsocket().sendMessage(tm);
			this.consoleWriter.writeMessageToConsole("Tabelleneintrag " + toRemove + " gelöscht");
			this.consoleWriter.writeMessageToConsole("Worker " + ws + " bearbeitet nun " + ws.removeWorker() + " Aufgaben");
			this.taskTable.removeTableEntry(toRemove);
		}
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
				ws.getWaitWebsocket().sendMessage(cm);
				this.consoleWriter.writeMessageToConsole("Worker " + ws + "bearbeitet nun " + ws.addWorker() + " fremde Aufgaben");
				TableEntry te = new TableEntry(cm.getSenderId(), ws.getSessionId());
				this.taskTable.addTableEntry(te);
				this.consoleWriter.writeMessageToConsole("Neuer Eintrag in der TaskTable: " + te);		
			}
			else
			{
				NoWaiterMessage nwm = new NoWaiterMessage();
				nwm.setContent(cm.getSenderId());
				sep.sendMessage(nwm);
			}
		}
		else if(message instanceof PostMessage)
		{
			PostMessage pm = (PostMessage)(message);
			TableEntry te = this.taskTable.getTableEntryBySenderId(pm.getSenderId());
			
			if(te != null)
			{
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(te.getWorker());
				ws.getWaitWebsocket().sendMessage(pm);
			}
			else
			{
				NoRecipientPostMessage nrpm = new NoRecipientPostMessage();
				nrpm.setContent(pm.getContent());
				nrpm.setSenderId(pm.getSenderId());
				sep.sendMessage(nrpm);
			}
		}
		else if(message instanceof TerminateMessage)
		{
			TerminateMessage tm = (TerminateMessage)(message);
			TableEntry te = this.taskTable.getTableEntryBySenderId(tm.getSenderId());
		
			if(te != null)
			{
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(te.getWorker());
				ws.getWaitWebsocket().sendMessage(tm);
				this.consoleWriter.writeMessageToConsole("Worker " + ws + " bearbeitet nun "+ ws.removeWorker() + " fremde Aufgaben");
				this.taskTable.removeTableEntry(te);
				SenderSession ss = this.sessionMonitor.getSenderSessionById(te.getSender());
				ss.getSendWebsocket().closeSession();
				this.consoleWriter.writeMessageToConsole("Tabelleneintrag "+ te + " gelöscht");
			}
			
			else
			{
				NoRecipientTerminateMessage nrtm = new NoRecipientTerminateMessage();
				nrtm.setContent(tm.getSenderId());
				sep.sendMessage(nrtm);
				SenderSession ss = this.sessionMonitor.getSenderSessionById(tm.getSenderId());
				ss.getSendWebsocket().closeSession();
			}
		}
	}//end method handleTextMessage
}//end class ManagerController
