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
import fhws.masterarbeit.distributedWebworkers.model.Message;
import fhws.masterarbeit.distributedWebworkers.util.MessageDecoder;
import fhws.masterarbeit.distributedWebworkers.util.MessageEncoder;

@ServerEndpoint(value = "/sendWebsocket",
				encoders = {MessageEncoder.class},
				decoders = {MessageDecoder.class})
public class SenderEndpoint 
{
	private Session session;
	private SenderController controller;
	
	@OnOpen
	public void onOpen(Session session)
	{
		System.out.println("Neuer SenderClient mit der ID " + session.getId() + " connected");
		this.session = session;
		this.controller = SenderController.getController();
		this.controller.senderEndpointAdded(this);
	}//end method onOpen
	
	@OnMessage
	public void onMessage(Message message)
	{
		System.out.println("Neue Nachticht vom SenderClient " + this.session.getId() + " erhalten");
		try 
		{
			message.setSenderId(this.session.getId());
			this.controller.handleMessage(message, this);
		} catch (Exception e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}//end method onMessage
	
	@OnClose
	public void onClose()
	{
		System.out.println("Session mit SenderClient " + this.session.getId() + " wurde vom Client geschlossen");
		this.controller.senderEndpointRemoved(this.session.getId());
		//this.session.close();
	}//end method onClose
	
	@OnError
	public void onError(Throwable throwable)
	{
		System.out.println("SenWebsocket - onError");
		this.controller.handleError(throwable);
	}//end method onError
	
	public void closeSession()
	{	
		System.out.println("Session mit SenderClient " + this.session.getId() + "  wird vom Server geschlossen");
		try 
		{
			this.session.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}//end method closeSession
	
	public Session getSession()
	{
		return this.session;
	}
	
	public void sendMessage(Message message)
	{
		try 
		{
			System.out.println("Sende Nachricht an SenderClient mit der ID " + this.session.getId());
			this.session.getBasicRemote().sendObject(message);
		}//end try
		catch (IOException | EncodeException e) 
		{
			controller.handleError(e);
		}//end catch IOException
	}//end method sendJavaScript
}
