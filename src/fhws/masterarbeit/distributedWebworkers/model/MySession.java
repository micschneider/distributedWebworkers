package fhws.masterarbeit.distributedWebworkers.model;

/**
 * Abstract class, which represents a WebSocket session.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public abstract class MySession
{
	/**
	 * Stores the ID of the WebSocket session
	 */
	protected String sessionId;

	/**
	 * Returns the ID of the WebSocket session
	 * 
	 * @return The ID of the session stored in a string
	 */
	public String getSessionId()
	{
		return this.sessionId;
	}// end method getSessionId

	/**
	 * Sets the ID of the WebSocket session
	 * 
	 * @param sessionId The session ID to be set
	 */
	public void setSessionId(String sessionId)
	{
		this.sessionId = sessionId;
	}// end method setSessionId

	/**
	 * Overrides the toString method
	 * 
	 * @return A String describing the MySession object
	 */
	@Override
	public String toString()
	{
		return "ID: " + this.sessionId;
	}// end method toString
}// end class MySession
