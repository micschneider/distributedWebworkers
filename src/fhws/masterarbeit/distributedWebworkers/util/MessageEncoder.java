package fhws.masterarbeit.distributedWebworkers.util;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;
import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.Message;

/**
 * Encodes messages, which are sent to waiter and sender clients into JSON strings
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class MessageEncoder implements Encoder.Text<Message>
{
	/**
	 * Overrides the interface method destroy(), but is currently not used
	 */
	@Override
	public void destroy()
	{
		;
	}// end method destroy

	/**
	 * Overrides the interface method init(EndpointConfig), but is currently not used
	 * 
	 * @param arg0 Represents an EndpointConfig instance
	 */
	@Override
	public void init(EndpointConfig arg0)
	{
		;
	}// end method init

	/**
	 * Is called when a message object should be send over a WebSocket. Returns the JSON string the
	 * message is encoded to.
	 * 
	 * @param message Represents the message, which sould be sent over the WebSocket
	 * @return The String the message is encoded to
	 */
	@Override
	public String encode(Message message) throws EncodeException
	{
		JSONObject obj = new JSONObject();
		obj.put("type", message.getType().toString());
		obj.put("content", message.getContent());
		if (message instanceof ClientMessage)
		{
			ClientMessage cm = (ClientMessage) (message);
			obj.put("from", cm.getSenderId());
		}// end if
		return obj.toString();
	}// end method encode
}// end class MessageEncoder
