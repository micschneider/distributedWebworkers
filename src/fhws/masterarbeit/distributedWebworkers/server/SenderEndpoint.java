package fhws.masterarbeit.distributedWebworkers.server;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import fhws.masterarbeit.distributedWebworkers.controller.SenderController;
import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.Message;
import fhws.masterarbeit.distributedWebworkers.model.ConsoleWriter;
import fhws.masterarbeit.distributedWebworkers.util.MessageDecoder;
import fhws.masterarbeit.distributedWebworkers.util.MessageEncoder;

/**
 * Represents the WebSocket connection between a sender and the application server. This connection
 * is established when the client creates a DistributedWebworker object in his JavaScript code. 
 * The ServerEndpoint annotation includes the following attributes: 
 * 'value': The relative path, where this WebSocket can be addressed from the client 
 * 'encoders': An array where all encoders for outgoing messages are listed 
 * 'decoders': An array where all decoders for incoming messages are listed.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
@ServerEndpoint(value = "/sendWebsocket", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class SenderEndpoint
{
	/**
	 * Stores a reference to the session object.
	 */
	private Session session;
	/**
	 * Stores a reference to the static SenderController.
	 */
	private SenderController controller;
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
	 * Is called automatically when a new sender client connects to the server and notifies the
	 * controller.
	 * 
	 * @param session Represents the new session between the server and the sender client
	 */
	@OnOpen
	public void onOpen(Session session)
	{
		// set Variables and print out the new connection

		this.session = session;
		this.consoleWriter = ConsoleWriter.getConsoleWriter();
		this.consoleWriter.writeMessageToConsole("Neuer SenderClient mit der ID " + session.getId()
				+ " connected");
		this.controller = SenderController.getController();
		
		// inform the controller
		this.controller.senderEndpointAdded(this);
	}// end method onOpen

	/**
	 * Is called automatically when a new message from a sender client is incoming. Stores the
	 * sender of the incoming message and informs the controller about the new message.
	 * 
	 * @param message Represents the incoming message. Its type is determined by the decoder
	 */
	@OnMessage
	public void onMessage(ClientMessage message)
	{
		this.consoleWriter.writeMessageToConsole("Neue " + message.getClass().getSimpleName()
				+ " vom SenderClient " + this.session.getId() + " erhalten");
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
		this.consoleWriter.writeMessageToConsole("SenWebsocket - onError");
		this.controller.handleError(throwable);
	}// end method onError
	
	/**
	 * Is called when the connection to a sender client is closed and notifies the controller.
	 */
	@OnClose
	public void onClose()
	{
		this.consoleWriter.writeMessageToConsole("Session mit SenderClient " + this.session.getId()
				+ " wurde geschlossen");
		this.controller.senderEndpointRemoved(this.session.getId());
	}// end method onClose

	/**
	 * Sends a message to a sender client. If an error occurs during sending, the controller
	 * is notified.
	 * 
	 * @param message Represents the message to be sent
	 */
	public void sendMessage(Message message)
	{
		try
		{
			this.consoleWriter.writeMessageToConsole("Sende " + message.getClass().getSimpleName()
					+ " an SenderClient mit der ID " + this.session.getId());
			this.session.getBasicRemote().sendObject(message);
		}// end try
		catch (IOException | EncodeException e)
		{
			controller.handleError(e);
		}// end catch IOException
	}// end method sendMessage | EncodeException
	
	/**
	 * Closes the session with the sender client and therefore the WebSocket connection. If an error occurs, the controller
	 * is notified.
	 */
	public void closeSession()
	{
		this.consoleWriter.writeMessageToConsole("Session mit SenderClient " + this.session.getId()
				+ "  wird vom Server geschlossen");
		try
		{
			this.session.close();
		}// end try
		catch (IOException e)
		{
			e.printStackTrace();
		}// end catch
	}// end method closeSession
}// end class SenderEndpoint
