package fhws.masterarbeit.distributedWebworkers.server;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import fhws.masterarbeit.distributedWebworkers.controller.WaiterController;
import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.Message;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.util.MessageDecoder;
import fhws.masterarbeit.distributedWebworkers.util.MessageEncoder;

/**
 * Represents the WebSocket connection between a waiter and the application server. This connection
 * is established automatically when the client includes the framework in his HTML file. 
 * The ServerEndpoint annotation includes the following attributes: 
 * 'value': The relative path, where this WebSocket can be addressed from the client 
 * 'encoders': An array where all encoders for outgoing messages are listed 
 * 'decoders': An array where all decoders for incoming messages are listed.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
@ServerEndpoint(value = "/waitWebsocket", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WaiterEndpoint
{
	/**
	 * Stores a reference to the session object.
	 */
	private Session session;
	/**
	 * Stores a reference to the static WaiterController.
	 */
	private WaiterController controller;
	/**
	 * Stores a reference to the static ConsoleWriter.
	 */
	private ConsoleWriter consoleWriter;

	/**
	 * Returns the session representing the WebSocket connection.
	 * 
	 * @return The session object
	 */
	public Session getSession()
	{
		return this.session;
	}// end method getSession

	/**
	 * Is called automatically when a new waiter client connects to the server and notifies the
	 * controller.
	 * 
	 * @param session Represents the new session between the server and the waiter client
	 */
	@OnOpen
	public void onOpen(Session session)
	{
		// set Variables and print out the new connection
		this.session = session;
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.consoleWriter.writeMessageToConsole("Neuer WaiterClient mit der ID " + session.getId()
				+ " connected");
		this.controller = WaiterController.getController();

		// inform the controller
		this.controller.waiterEndpointAdded(this);
	}// end method onOpen

	/**
	 * Is called automatically when a new message from a waiter client is incoming. Stores the
	 * sender of the incoming message and informs the controller about the new message.
	 * 
	 * @param message Represents the incoming message. Its type is determined by the decoder
	 */
	@OnMessage
	public void onMessage(ClientMessage message)
	{
		consoleWriter.writeMessageToConsole("Neue " + message.getClass().getSimpleName()
				+ " vom WaiterClient " + this.session.getId() + " erhalten");
		message.setSenderId(this.session.getId());
		this.controller.handleMessage(message, this);
	}// end method onMessage

	/**
	 * Informs the controller it has to deal with an occurred error.
	 * 
	 * @param throwable Represents the error object
	 */
	@OnError
	public void onError(Throwable throwable)
	{
		this.controller.handleError(throwable);
	}// end method onError

	/**
	 * Is called when the connection to a waiter client is closed and notifies the controller.
	 */
	@OnClose
	public void onClose()
	{
		this.consoleWriter.writeMessageToConsole("Session mit WaiterClient " + this.session.getId()
				+ " wurde geschlossen");
		this.controller.waiterEndpointRemoved(this.session.getId());
	}// end method onClose

	/**
	 * Sends a message to a waiter client. If an error occurs during sending, the controller
	 * is notified.
	 * 
	 * @param message Represents the message to be sent
	 */
	public void sendMessage(Message message)
	{
		try
		{
			this.consoleWriter.writeMessageToConsole("Sende " + message.getClass().getSimpleName()
					+ " an WaiterClient mit der ID " + this.session.getId());

			// sending the message via BasicRemote
			// the message encoder encodes the message
			this.session.getBasicRemote().sendObject(message);
		}// end try
		catch (IOException | EncodeException e)
		{
			controller.handleError(e);
		}// end catch IOException | EncodeException
	}// end method sendMessage

	/**
	 * Closes the session with the waiter client and therefore the WebSocket connection. If an error occurs, the controller
	 * is notified.
	 */
	public void closeSession()
	{
		try
		{
			this.consoleWriter.writeMessageToConsole("Session mit WaiterClient "
					+ this.session.getId() + "  wird vom Server geschlossen");
			this.session.close();
		}// end try
		catch (IOException e)
		{
			controller.handleError(e);;
		}// end catch
	}// end method closeSession
}// end class WaitWebsocket
