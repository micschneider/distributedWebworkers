package fhws.masterarbeit.distributedWebworkers.server;

import java.io.IOException;

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
public class SendWebsocket 
{
	private Session session;
	private SenderController controller;
	
	@OnOpen
	public void onOpen(Session session)
	{
		this.session = session;
		this.controller = SenderController.getController();
		this.controller.sessionAdded(this.session);
	}//end method onOpen
	
	@OnMessage
	public void onMessage(Message message)
	{
		this.controller.handleMessage(message, this.session);
	}//end method onMessage
	
	@OnClose
	public void onClose()
	{
		this.controller.sessionRemoved(this.session);
		//this.session.close();
	}//end method onClose
	
	@OnError
	public void onError(Throwable throwable)
	{
		this.controller.handleError(throwable);
	}//end method onError
	
	public static void closeSession(Session session) throws IOException
	{
		session.close();
	}//end method closeSession
}
