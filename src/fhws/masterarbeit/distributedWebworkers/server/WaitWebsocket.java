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
import fhws.masterarbeit.distributedWebworkers.model.Message;
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
public class WaitWebsocket 
{
	/**
	 * Stores a reference to the session object.
	 */
	private Session session;
	/**
	 * Stores a static reference to the controller. 
	 */
	private static WaiterController controller;
	
	/**
	 * Is called when a new waiter client connects to the server and notifies the controller, 
	 * it has to add a new session.
	 * @param session Represents the new session
	 */
	@OnOpen
	public void onOpen(Session session)
	{
		this.session = session;
		controller = WaiterController.getController();
		controller.sessionAdded(session);
	}//end method onOpen
	
	/**
	 * Informs the controller, that a new message from a waiter client is incoming.
	 * @param message represents the message as a string
	 */
	@OnMessage
	public void onMessage(String message)
	{
		controller.handleTextMessage(message);
	}//end method onMessage
	
	/**
	 * Informs the controller it has to deal with an occurred error.
	 * @param throwable represents the error object
	 */
	@OnError
	public void onError(Throwable throwable)
	{
		controller.handleError(throwable);
	}//end method onError
	
	/**
	 * Is called when the connection to a waiter client is closed and notifies the controller, 
	 * that the regarding session has to be removed.
	 */
	@OnClose
	public void onClose()
	{
		controller.sessionRemoved(this.session);
	}//end method onClose
	
	/**
	 * Sends JavaScript code to a certain waiter client.
	 * @param session represents the session where to send the code
	 * @param code represents the code to send as a string
	 */
	public static void sendMessage(Session session, Message message)
	{
		try 
		{
			session.getBasicRemote().sendObject(message);
		}//end try
		catch (IOException | EncodeException e) 
		{
			controller.handleError(e);
		}//end catch IOException
	}//end method sendJavaScript

	/**
	 * Closes a certain session
	 * @param session represents the session to be closed
	 * @throws IOException if there is an error while closing the session
	 */
	public static void closeSession(Session session) throws IOException 
	{
		session.close();
	}//end method closeSession
}//end class WaitWebsocket
