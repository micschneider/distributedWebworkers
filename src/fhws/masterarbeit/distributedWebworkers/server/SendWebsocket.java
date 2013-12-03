package fhws.masterarbeit.distributedWebworkers.server;

import java.io.IOException;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/sendWebsocket")
public class SendWebsocket 
{
	Session session;
	
	@OnOpen
	public void OnOpen(Session session)
	{
		this.session = session;
	}
	
	@OnMessage
	public void OnMessage(String message)
	{
		//Package verteilen
		try {
			session.getBasicRemote().sendText("sendWebsocket got this: " + message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
