package fhws.masterarbeit.distributedWebworkers.server;

import java.io.IOException;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/waitWebsocket")
public class WaitWebsocket 
{
	Session session;
	
	@OnOpen
	public void OnOpen(Session session)
	{
		this.session = session;
		//Partner als möglichen Empfänger in Liste eintragen
		try {
			this.session.getBasicRemote().sendText("waitWebsocket says: Connected");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@OnMessage
	public void OnMessage(String message)
	{
		//Ergebnis weiterleiten
	}
}
