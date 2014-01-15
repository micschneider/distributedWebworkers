package fhws.masterarbeit.distributedWebworkers.model;

import fhws.masterarbeit.distributedWebworkers.server.SenderEndpoint;

/**
 * Represents a WebSocket session with a sender client
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class SenderSession extends MySession
{
	/**
	 * Stores a reference to the regarding SenderEndpoint
	 */
	private SenderEndpoint senderEndpoint;
	
	/**
	 * Constructor of a SenderSession
	 * @param sep Reference to the SenderEndpoint object which should be stored
	 */
	public SenderSession(SenderEndpoint sep)
	{
		this.senderEndpoint = sep;
		this.sessionId = this.senderEndpoint.getSession().getId();
	}// end constructor
	
	/**
	 * Returns a reference to the regarding SenderEndpoint
	 * @return A Reference to the stored SenderEndpoint
	 */
	public SenderEndpoint getSendWebsocket()
	{
		return this.senderEndpoint;
	}// end method getSendWebSocket
}// end class SenderSession
