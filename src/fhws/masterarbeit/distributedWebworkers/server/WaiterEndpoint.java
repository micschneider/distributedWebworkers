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
import fhws.masterarbeit.distributedWebworkers.util.MessageDecoder;
import fhws.masterarbeit.distributedWebworkers.util.MessageEncoder;

/**
 * A class representing the WebSocket connection between a waiter and the server.
 * This class is automatically instantiated when the client includes the framework in his HTML file.
 * @author Michael Schneider
 * @version 1.0 
 */
@ServerEndpoint(value = "/waitWebsocket",
				encoders = {MessageEncoder.class},
				decoders = {MessageDecoder.class})
public class WaiterEndpoint 
{
	/**
	 * Stores a reference to the session object.
	 */
	private Session session;
	/**
	 * Stores a reference to the static Waiter controller. 
	 */
	private WaiterController controller;
	
	/**
	 * Is called when a new waiter client connects to the server and notifies the controller, 
	 * it has to add a new connection.
	 * @param session Represents the new session between the server and the waiter client
	 */
	@OnOpen
	public void onOpen(Session session)
	{
		System.out.println("Neuer WaiterClient mit der ID " + session.getId() + " connected");
		this.session = session;
		this.controller = WaiterController.getController();
		this.controller.waiterEndpointAdded(this);
	}//end method onOpen
	
	/**
	 * Informs the controller, that a new message from a waiter client is incoming.
	 * @param message represents the message
	 */
	@OnMessage
	public void onMessage(ClientMessage message)
	{
		System.out.println("Neue " + message.getClass().getSimpleName() + " vom WaiterClient " + this.session.getId() + " erhalten");
		message.setSenderId(this.session.getId());
		this.controller.handleMessage(message, this);
	}//end method onMessage
	
	/**
	 * Informs the controller it has to deal with an occurred error.
	 * @param throwable represents the error object
	 */
	@OnError
	public void onError(Throwable throwable)
	{
		this.controller.handleError(throwable);
	}//end method onError
	
	/**
	 * Is called when the connection to a waiter client is closed and notifies the controller, 
	 * that the regarding session has to be removed.
	 */
	@OnClose
	public void onClose()
	{
		System.out.println("Session mit WaiterClient " + this.session.getId() + " wurde geschlossen");
		this.controller.waiterEndpointRemoved(this.session.getId());
	}//end method onClose
	
	/**
	 * Sends JavaScript code to a certain waiter client.
	 * @param code represents the code to send as a string
	 */
	public void sendMessage(Message message)
	{
		try 
		{
			System.out.println("Sende " + message.getClass().getSimpleName() + " an WaiterClient mit der ID " + this.session.getId());
			this.session.getBasicRemote().sendObject(message);
		}//end try
		catch (IOException | EncodeException e) 
		{
			controller.handleError(e);
		}//end catch IOException
	}//end method sendJavaScript

	/**
	 * Closes a certain session
	 * @throws IOException if there is an error while closing the session
	 */
	public void closeSession()
	{
		try 
		{
			System.out.println("Session mit WaiterClient " + this.session.getId() + "  wird vom Server geschlossen");
			this.session.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}//end method closeSession
	
	public Session getSession()
	{
		return this.session;
	}
}//end class WaitWebsocket
