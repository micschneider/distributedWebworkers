package fhws.masterarbeit.distributedWebworkers.util;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;
import fhws.masterarbeit.distributedWebworkers.model.Message;

public class MessageEncoder implements Encoder.Text<Message>
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
	public String encode(Message message) throws EncodeException 
	{
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType().toString());
		obj.put("content", message.getContent());
		return obj.toString();
	}
}
