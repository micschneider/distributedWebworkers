package fhws.masterarbeit.distributedWebworkers.messages;

/**
 * Contains all types, a message can represent
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public enum MessageType
{
	CODE_MESSAGE, 
	RESULT_MESSAGE, 
	ID_MESSAGE, 
	NO_WAITER_MESSAGE, 
	POST_MESSAGE, 
	TERMINATE_MESSAGE, 
	NO_RECIPIENT_POST_MESSAGE, 
	NO_RECIPIENT_TERMINATE_MESSAGE, 
	WORKER_DOWN_MESSAGE,
	WORKER_ERROR_MESSAGE
}// end enum MessageType
