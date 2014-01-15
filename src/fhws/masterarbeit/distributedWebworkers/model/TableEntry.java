package fhws.masterarbeit.distributedWebworkers.model;

/**
 * Represents an entry in the task table. It contains an consecutively increased ID, the ID of the
 * sender client, which sent the work package and the ID of the waiter client, which executes the
 * work.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class TableEntry
{
	/**
	 * A static variable representing a consecutively increased ID
	 */
	private static int entryId = 0;

	/**
	 * The ID of the table entry
	 */
	private int id;

	/**
	 * The ID of the sender client, the work is done for
	 */
	private String sender;

	/**
	 * The ID of the waiter client, which executes the work
	 */
	private String worker;

	/**
	 * The constructor for a new TableEntry. The sender ID and the worker ID have to be passed in,
	 * the ID of the Table Entry is filled in automatically.
	 * 
	 * @param sender The ID of the sender client, the work is done for
	 * @param worker The ID of the waiter client, which executes the work
	 */
	public TableEntry(String sender, String worker)
	{
		this.id = entryId;
		entryId++;
		this.sender = sender;
		this.worker = worker;
	}// end constructor

	/**
	 * Returns the ID of the TableEntry
	 * 
	 * @return The ID of the table entry
	 */
	public int getId()
	{
		return id;
	}// end method getId()

	/**
	 * Returns the ID of the sender client of the table entry
	 * 
	 * @return The ID of the sender client the work is done for
	 */
	public String getSender()
	{
		return sender;
	}// end method getSender()

	/**
	 * Returns the ID of the waiter client, which executes the work
	 * 
	 * @return The ID of the waiter client, which executes the work
	 */
	public String getWorker()
	{
		return worker;
	}// end method getWorker()

	/**
	 * Overrides the toString method. Returns a String which represents the table entry.
	 * @return The string representing the TableEntry
	 */
	@Override
	public String toString()
	{
		return "ID: " + this.id + ", Sender: " + this.sender + ", Worker: " + this.worker;
	}// end method toString()
}// end class TableEntry
