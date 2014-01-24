package fhws.masterarbeit.distributedWebworkers.controller;

import java.util.ArrayList;
import java.util.Iterator;

import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.IdMessage;
import fhws.masterarbeit.distributedWebworkers.messages.ResultMessage;
import fhws.masterarbeit.distributedWebworkers.messages.WorkerDownMessage;
import fhws.masterarbeit.distributedWebworkers.messages.WorkerErrorMessage;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.model.SenderSession;
import fhws.masterarbeit.distributedWebworkers.model.SessionMonitor;
import fhws.masterarbeit.distributedWebworkers.model.TableEntry;
import fhws.masterarbeit.distributedWebworkers.model.TaskTable;
import fhws.masterarbeit.distributedWebworkers.server.WaiterEndpoint;

/**
 * Deals with new connected or disconnected waiter clients and handles the messages incoming from
 * waiter/worker clients.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class WaiterController
{
	/**
	 * Stores a static reference to the only existing WaiterController
	 */
	private static WaiterController controller = new WaiterController();

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
	 * Private constructor for the only existing WaiterController
	 */
	private WaiterController()
	{
		// Get references to the other singletons
		this.sessionMonitor = SessionMonitor.getSessionMonitor();
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.taskTable = TaskTable.getTaskTable();
	}// end constructor

	/**
	 * Returns a reference to the only existing WaiterController
	 * 
	 * @return A reference to the waiter controller
	 */
	public static WaiterController getController()
	{
		return controller;
	}// end method getController()

	/**
	 * Adds the new waiter session to the session monitors list and sends a new IdMessage back to
	 * the new waiter client. The content of the ID message is the session ID of the WebSocket
	 * connection.
	 * 
	 * @param wep A reference to the new WaiterEndpoint created by the new WebSocket connection
	 */
	public synchronized void waiterEndpointAdded(WaiterEndpoint wep)
	{
		// Add the new waiter session to the session monitor
		this.sessionMonitor.addWaiterSession(wep);

		// Create a new IdMessage an send it back to the waiter client
		IdMessage idm = new IdMessage();
		idm.setContent(wep.getSession().getId());
		wep.sendMessage(idm);
	}// end method waiterEndpointAdded()

	/**
	 * Removes a waiter session from the session monitors list. Before it does so, it checks if the
	 * disconnected waiter client is currently executing tasks for any sender clients. If it does,
	 * those sender clients will get a WorkerDown message, so they know about it. The task table is
	 * updated, too.
	 * 
	 * @param sessionId The session ID of the waiter client to be removed
	 */
	public synchronized void waiterEndpointRemoved(String sessionId)
	{
		// Get the Table entries where the waiter client is registered as worker
		ArrayList<TableEntry> toRemoveList = this.taskTable.getTableEntriesByWorkerId(sessionId);

		// Iterate over this list
		Iterator<TableEntry> it = toRemoveList.iterator();
		while (it.hasNext())
		{
			TableEntry te = it.next();

			// Send a WorkerDown message to all regarding sender clients
			WorkerDownMessage wdm = new WorkerDownMessage();
			wdm.setContent("Worker is down");
			SenderSession ss = this.sessionMonitor.getSenderSessionById(te.getSender());
			ss.getSendWebsocket().sendMessage(wdm);
			this.consoleWriter.writeMessageToConsole("Tabelleneintrag " + te + " gelöscht");

			// Remove the entry from the task table
			this.taskTable.removeTableEntry(te);
		}// end while

		// Remove the waiter session from session monitors waiter session list
		this.sessionMonitor.removeWaiterSession(sessionId);
	}// end method waiterEndpointRemoved()

	/**
	 * Is called if any error occurs in a WaiterEndpoint
	 * 
	 * @param throwable A reference to the throwable object
	 */
	public synchronized void handleError(Throwable throwable)
	{
		this.consoleWriter.writeErrorToConsole(throwable);
	}// end method handleError()

	/**
	 * Is called when a new message is incoming from a worker client
	 * 
	 * @param message The message incoming from the client
	 * @param wep The WaiterEndpoint, which received the message
	 */
	public synchronized void handleMessage(ClientMessage message, WaiterEndpoint wep)
	{
		// Check if the message contains a result
		if (message instanceof ResultMessage)
		{
			// Cast the message
			ResultMessage rm = (ResultMessage) message;
			this.consoleWriter.writeMessageToConsole("Ergebnis von Waiter mit der ID "
					+ wep.getSession().getId() + " erhalten");

			// Find out the recipient of the message and send him the result
			TableEntry te = this.taskTable.getTableEntryBySenderId(rm.getRecipientId());
			if (te != null)
			{
				this.consoleWriter
						.writeMessageToConsole("Empfänger der Ergebnisnachricht gefunden. ID: "
								+ te.getSender());
				SenderSession recipientSession = sessionMonitor
						.getSenderSessionById(te.getSender());
				recipientSession.getSendWebsocket().sendMessage(rm);
			}// end if
		}// end if
		// Check if the message is an error message
		else if (message instanceof WorkerErrorMessage)
		{
			// Cast the message
			WorkerErrorMessage wem = (WorkerErrorMessage) message;
			this.consoleWriter.writeMessageToConsole("Fehlernachricht von Waiter mit der ID "
					+ wep.getSession().getId() + " erhalten");

			// Find out the recipient of the message and send him the error and close the session
			// with this sender client
			TableEntry te = this.taskTable.getTableEntryBySenderId(wem.getRecipientId());
			if (te != null)
			{
				this.consoleWriter
						.writeMessageToConsole("Empfänger der Fehlernachricht gefunden. ID: "
								+ te.getSender());
				SenderSession recipientSession = sessionMonitor
						.getSenderSessionById(te.getSender());
				recipientSession.getSendWebsocket().sendMessage(wem);
				recipientSession.getSendWebsocket().closeSession();
			}// end if
		}// end if
	}// end method handleMessage()
}// end class WorkerController

