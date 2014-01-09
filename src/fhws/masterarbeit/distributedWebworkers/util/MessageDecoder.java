package fhws.masterarbeit.distributedWebworkers.util;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.messages.PostMessage;
import fhws.masterarbeit.distributedWebworkers.messages.ResultMessage;
import fhws.masterarbeit.distributedWebworkers.messages.TerminateMessage;

public class MessageDecoder implements Decoder.Text<ClientMessage>
{
	@Override
	public void destroy() 
	{
		;	
	}

	@Override
	public void init(EndpointConfig arg0) 
	{
		;
	}

	@Override
	public ClientMessage decode(String message)
	{
		JSONObject obj = (JSONObject) JSONSerializer.toJSON(message);   
		
		switch(obj.get("type").toString())
		{
		
			case "code":
			{
				CodeMessage cm = new CodeMessage();
				cm.setContent(obj.getString("content"));
				cm.setWaiterId(obj.getString("waiterId"));
				return cm;
			}
			case "result":
			{
				ResultMessage rm = new ResultMessage();
				rm.setContent(obj.getString("content"));
				rm.setRecipientId(obj.getString("recipientId"));
				return rm;
			}
			case "post":
			{
				PostMessage pm = new PostMessage();
				pm.setContent(obj.getString("content"));
				return pm;
			}
			case "terminate":
			{
				TerminateMessage tm = new TerminateMessage();
				tm.setContent("Closing");
				return tm;
			}
			default:
			{
				return null;
			}
		}
	}

	@Override
	public boolean willDecode(String arg0) 
	{
		return true;
	}
}
