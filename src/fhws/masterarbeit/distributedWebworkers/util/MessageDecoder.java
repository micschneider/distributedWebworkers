package fhws.masterarbeit.distributedWebworkers.util;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import fhws.masterarbeit.distributedWebworkers.model.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.model.Message;
import fhws.masterarbeit.distributedWebworkers.model.ResultMessage;

public class MessageDecoder implements Decoder.Text<Message>
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
	public Message decode(String message)
	{
		JSONObject obj = (JSONObject) JSONSerializer.toJSON(message);   
		
		if(obj.get("type").equals("code"))
		{
			CodeMessage cm = new CodeMessage();
			cm.setContent(obj.getString("content"));
			cm.setWaiterId(obj.getString("waiterId"));
			return cm;
		}
		else
		{
			return new ResultMessage();
		}
	}

	@Override
	public boolean willDecode(String arg0) 
	{
		return true;
	}
}