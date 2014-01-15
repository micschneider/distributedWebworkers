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

/**
 * Deals with new connected or disconnected sender clients and handles the messages incoming from
 * sender clients.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class SenderController
{
	/**
	 * Stores a reference to the only existing SenderController
	 */
	private static SenderController controller = new SenderController();

	/**
	 * Stores a reference to the SessionMonitor
	 */
	private SessionMonitor sessionMonitor;

	/**
	 * Stores a reference to the ConsoleWriter
	 */
	private ConsoleWriter consoleWriter;

	/**
	 * Stores a reference to the TaskTable
	 */
	private TaskTable taskTable;

	/**
	 * Private constructor for the only existing SenderController
	 */
	private SenderController()
	{
		// Get references to the other singletons
		this.sessionMonitor = SessionMonitor.getSessionMonitor();
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.taskTable = TaskTable.getTaskTable();
	}// end constructor

	/**
	 * Returns a reference to the WaiterController
	 * 
	 * @return A reference to the waiter controller
	 */
	public static SenderController getController()
	{
		return controller;
	}// end method getController()

	/**
	 * Adds a new sender session to the session monitors sender session list.
	 * 
	 * @param sep The SenderEndpoint to be added to the session monitors list
	 */
	public void senderEndpointAdded(SenderEndpoint sep)
	{
		this.sessionMonitor.addSenderSession(sep);
	}// end method senderEndpointAdded()

	/**
	 * Removes a sender session from the session monitors list. Before it does so, it checks if the
	 * disconnected sender client had distributed a task to a waiter client. It will send a
	 * terminate message to this worker client, because his work is not needed anymore. The task
	 * table is updated, too.
	 * 
	 * @param sessionId The session ID of the disconnecting sender client
	 */
	public void senderEndpointRemoved(String sessionId)
	{
		// Get the TableEntry where the sender client is registered as sender
		TableEntry toRemove = this.taskTable.getTableEntryBySenderId(sessionId);

		// If this table entry exists
		if (toRemove != null)
		{
			// Send a TerminateMessage to this worker
			TerminateMessage tm = new TerminateMessage();
			tm.setContent("Sender ist down");
			WaiterSession ws = this.sessionMonitor.getWaiterSessionById(toRemove.getWorker());
			ws.getWaitWebsocket().sendMessage(tm);

			// Reduce the number of workers the regarding worker is working on
			ws.removeWorker();
			this.consoleWriter.writeMessageToConsole("Tabelleneintrag " + toRemove + " gelöscht");
			this.consoleWriter.writeMessageToConsole("Worker " + ws + " bearbeitet nun "
					+ ws.getCurrentWorkers() + " Aufgaben");

			// Remove the entry from the task table
			this.taskTable.removeTableEntry(toRemove);
		}// end if

		// Remove the session from the session monitors list
		this.sessionMonitor.removeSenderSession(sessionId);
	}// end method senderEndpointRemoved()

	/**
	 * Is called if any error occurs in a SenderEndpoint
	 * 
	 * @param throwable A reference to the throwable object
	 */
	public void handleError(Throwable throwable)
	{
		consoleWriter.writeErrorToConsole(throwable);
	}// end method handleError()

	/**
	 * Is called when a new message is incoming from a sender client
	 * 
	 * @param message The message, which is incoming
	 * @param sep The SenderEndpoint which receives the message
	 */
	public void handleMessage(Message message, SenderEndpoint sep)
	{
		// Check if the message is a code message
		if (message instanceof CodeMessage)
		{
			// Cast the message
			CodeMessage cm = (CodeMessage) (message);

			// Try to find a free waiter for the new task
			WaiterSession ws = this.sessionMonitor.getFreeSessionForWaiter(cm.getWaiterId());
			this.consoleWriter.writeMessageToConsole("Suche freie WaiterSession...");

			// If a free waiter was found
			if (ws != null)
			{
				// Send the code to the worker
				this.consoleWriter.writeMessageToConsole("Fremder Waiter mit ID "
						+ ws.getSessionId() + " erledigt die Aufgabe");
				ws.getWaitWebsocket().sendMessage(cm);

				// Increase the number of tasks this worker client is executing
				ws.addWorker();
				this.consoleWriter.writeMessageToConsole("Worker " + ws + "bearbeitet nun "
						+ ws.getCurrentWorkers() + " fremde Aufgaben");

				// Create and add a new table entry to the task table
				TableEntry te = new TableEntry(cm.getSenderId(), ws.getSessionId());
				this.taskTable.addTableEntry(te);
				this.consoleWriter.writeMessageToConsole("Neuer Eintrag in der TaskTable: " + te);
			}// end if
				// If no free worker client exists
			else
			{
				// Send a NoWaiterMessage back to the SenderClient
				this.consoleWriter
						.writeMessageToConsole("Kein anderer freier Worker. Arbeit muss vom Sender selbst erledigt werden");
				NoWaiterMessage nwm = new NoWaiterMessage();
				nwm.setContent(cm.getSenderId());
				sep.sendMessage(nwm);
			}// end else
		}// end if
			// If the incoming message is a PostMessage
		else if (message instanceof PostMessage)
		{
			// Cast the message
			PostMessage pm = (PostMessage) (message);

			// Try to find the corresponding table entry for the sender client
			TableEntry te = this.taskTable.getTableEntryBySenderId(pm.getSenderId());

			// If the entry was found
			if (te != null)
			{
				// Send the PostMessage to the registered worker client
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(te.getWorker());
				ws.getWaitWebsocket().sendMessage(pm);
			}// end if
				// If no entry was found (Sender does the work by himself)
			else
			{
				// Send a new NoRecipientPostMessage back to the sender client
				NoRecipientPostMessage nrpm = new NoRecipientPostMessage();
				nrpm.setContent(pm.getContent());
				nrpm.setSenderId(pm.getSenderId());
				sep.sendMessage(nrpm);
			}// end else
		}// end else if
			// If the incoming message is a TerminateMessage
		else if (message instanceof TerminateMessage)
		{
			// Cast the message
			TerminateMessage tm = (TerminateMessage) (message);

			// Try to find the corresponding table entry for the sender client
			TableEntry te = this.taskTable.getTableEntryBySenderId(tm.getSenderId());

			// If the entry was found
			if (te != null)
			{
				// Send the TerminateMessage to the registered worker client
				WaiterSession ws = this.sessionMonitor.getWaiterSessionById(te.getWorker());
				ws.getWaitWebsocket().sendMessage(tm);

				// Reduce the number of tasks the worker client is currently working on
				ws.removeWorker();
				this.consoleWriter.writeMessageToConsole("Worker " + ws + " bearbeitet nun "
						+ ws.getCurrentWorkers() + " fremde Aufgaben");

				// Remove the table entry
				this.taskTable.removeTableEntry(te);
				this.consoleWriter.writeMessageToConsole("Tabelleneintrag " + te + " gelöscht");

				// Close the session with the sender client
				SenderSession ss = this.sessionMonitor.getSenderSessionById(te.getSender());
				ss.getSendWebsocket().closeSession();
			}// end if
				// If no table entry was found (Sender does the work by himself)
			else
			{
				// Send a new NoRecipientTerminateMessage back to the sender client
				NoRecipientTerminateMessage nrtm = new NoRecipientTerminateMessage();
				nrtm.setContent(tm.getSenderId());
				sep.sendMessage(nrtm);
				SenderSession ss = this.sessionMonitor.getSenderSessionById(tm.getSenderId());

				// Close the session with the sender client
				ss.getSendWebsocket().closeSession();
			}// end else
		}// end else if
	}// end method handleMessage()
}// end class ManagerController
