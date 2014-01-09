package fhws.masterarbeit.distributedWebworkers.controller;

import java.util.Iterator;

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
				nwm.setContent(cm.getSenderId());
				sep.sendMessage(nwm);
				/*TableEntry te = new TableEntry(cm.getSenderId(), cm.getSenderId());
				System.out.println("Neuer Eintrag in der TaskTable: " + te);
				this.taskTable.addTableEntry(te);*/
			}
		}
		else if(message instanceof PostMessage)
		{
			PostMessage pm = (PostMessage)(message);
			String workerId = this.taskTable.getWorkerIdBySenderId(pm.getSenderId());
			if(workerId != null)
			{
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(workerId);
				if(ws!=null)
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
			String workerId = this.taskTable.getWorkerIdBySenderId(tm.getSenderId());
			if(workerId != null)
			{
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(workerId);
				if(ws!=null)
					ws.getWaitWebsocket().sendMessage(tm);
				
				Iterator<TableEntry> it = this.taskTable.iterator();
				TableEntry toRemove = null;
				while(it.hasNext())
				{
					TableEntry te = it.next();
					if(te.getSender().equals(tm.getSenderId()))
					{
						toRemove = te;
						String sender = te.getSender();
						SenderSession ss = this.sessionMonitor.getSenderSessionById(sender);
						ss.getSendWebsocket().closeSession();
						System.out.println("Tabelleneintrag "+ te + " gelöscht");
					}
				}
				if(toRemove != null)
					this.taskTable.removeTableEntry(toRemove);
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
