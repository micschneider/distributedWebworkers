package fhws.masterarbeit.distributedWebworkers.util;

import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import fhws.masterarbeit.distributedWebworkers.messages.ClientMessage;
import fhws.masterarbeit.distributedWebworkers.messages.CodeMessage;
import fhws.masterarbeit.distributedWebworkers.messages.MessageType;
import fhws.masterarbeit.distributedWebworkers.messages.PostMessage;
import fhws.masterarbeit.distributedWebworkers.messages.ResultMessage;
import fhws.masterarbeit.distributedWebworkers.messages.TerminateMessage;
import fhws.masterarbeit.distributedWebworkers.messages.WorkerErrorMessage;

/**
 * Decodes incoming JSON Strings into Messages. In this version the Decoder can work with CODE,
 * RESULT, POST and TERMINATE messages.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class MessageDecoder implements Decoder.Text<ClientMessage>
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
	 * Is called when a text message is incoming and the willDecode(String) method returned 'true'
	 * before. Creates a ClientMessage from the incoming data.
	 * 
	 * @param The String representing the incoming message
	 * @return A Client message instance, which is created by reading the incoming data
	 */
	@Override
	public ClientMessage decode(String message)
	{
		// create a JSON object from the message string
		JSONObject obj = (JSONObject) JSONSerializer.toJSON(message);

		// check the message type, which is encoded in the 'type' attribute of the JSON message
		// object
		switch (obj.get("type").toString())
		{
			case "CODE_MESSAGE":
			{
				// create a new CodeMessage object and set the content an the waiter ID
				CodeMessage cm = new CodeMessage();
				cm.setContent(obj.getString("content"));
				cm.setWaiterId(obj.getString("waiterId"));
				return cm;
			}// end case CodeMessage
			case "RESULT_MESSAGE":
			{
				// create a new ResultMessage object and set the content and the recipient (sender)
				// ID
				ResultMessage rm = new ResultMessage();
				rm.setContent(obj.getString("content"));
				rm.setRecipientId(obj.getString("recipientId"));
				return rm;
			}// end case ResultMessage
			case "POST_MESSAGE":
			{
				// create a new PostMessage object an set the content
				PostMessage pm = new PostMessage();
				pm.setContent(obj.getString("content"));
				return pm;
			}// end case PostMessage
			case "TERMINATE_MESSAGE":
			{
				// create a new TerminateMessage and set a content
				TerminateMessage tm = new TerminateMessage();
				tm.setContent("Closing");
				return tm;
			}// end case TerminateMessage
			case "WORKER_ERROR_MESSAGE":
			{
				// create a new WorkerErrorMessage object and set the content and the recipient
				// (sender) ID
				WorkerErrorMessage wem = new WorkerErrorMessage();
				wem.setContent(obj.getString("content"));
				wem.setRecipientId(obj.getString("recipientId"));
				return wem;
			}
			default:
			{
				return null;
			}// end default
		}// end switch
	}// end method decode

	/**
	 * Is called when a new text message is incoming. Will check if the incoming message can be
	 * decoded or not. It only can be decoded if it has a kown message type
	 * 
	 * @param message Represents the incoming text message
	 * @return 'True' if the type is known, 'false' if the message cannot be decoded
	 */
	@Override
	public boolean willDecode(String message)
	{
		// Read out the message type
		JSONObject obj = (JSONObject) JSONSerializer.toJSON(message);
		String type = obj.get("type").toString();

		// Iterate over all known message types and check if the incoming message type fits one
		for (MessageType mt : MessageType.values())
		{
			if (mt.toString().equals(type))
				return true;
		}// end for
		return false;
	}// end method willDecode
}// end class MessageDecoder
