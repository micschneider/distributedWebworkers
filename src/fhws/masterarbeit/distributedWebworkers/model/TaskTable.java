package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Represents a table, where all task are stored, which are executed by foreign worker clients. It
 * serves as a lookup table, so messages to be exchanged between sender and worker clients are
 * forwarded correctly.
 * 
 * @author Michael Schneider
 * @version 1.0
 */
public class TaskTable implements Iterable<TableEntry>
{
	/**
	 * Represents the list of table entries
	 */
	private ArrayList<TableEntry> entryList = new ArrayList<TableEntry>();

	/**
	 * A static reference to the only existing TaskTable
	 */
	private static TaskTable table = new TaskTable();

	/**
	 * Private constructor for the TaskTable. There is only one TaskTable.
	 */
	private TaskTable()
	{
	}// end constructor

	/**
	 * Returns a reference to the task table
	 * 
	 * @return The task table
	 */
	public static TaskTable getTaskTable()
	{
		return table;
	}// end method getTaskTable()

	/**
	 * Returns a reference to the entry list of the task table
	 * 
	 * @return The entry list of the task table
	 */
	public synchronized ArrayList<TableEntry> getEntryList()
	{
		return this.entryList;
	}// end method getEntryList()

	/**
	 * Adds a table entry to the task table
	 * 
	 * @param entry Represents the entry to be added to the task table
	 */
	public synchronized void addTableEntry(TableEntry entry)
	{
		entryList.add(entry);
	}// end method addTableEntry()

	/**
	 * Removes a table entry from the task table
	 * 
	 * @param entry Represents the entry to be removed
	 */
	public synchronized void removeTableEntry(TableEntry entry)
	{
		entryList.remove(entry);
	}// end method removeTableEntry()

	/**
	 * Returns the table entry for a given sender ID. There can only be one entry for a certain
	 * sender ID, because a new WebSocket connection is open for every single task.
	 * 
	 * @param senderId The sender ID, which should be found in the task table
	 * @return The regarding table entry for the given sender ID, if found
	 */
	public synchronized TableEntry getTableEntryBySenderId(String senderId)
	{
		// Iterate over the task table and look up the sender ID
		Iterator<TableEntry> it = iterator();
		while (it.hasNext())
		{
			TableEntry te = it.next();
			if (te.getSender().equals(senderId))
				return te;
		}// end while

		// If nothing found
		return null;
	}// end method getTableEntryBySenderId()

	/**
	 * Returns a list of table entries for a given waiter ID. There can be multiple table entries
	 * for a certain worker ID, because a waiter can do more than only one task at once.
	 * 
	 * @param workerId The ID of the waiter client, which should be looked up in the task table
	 * @return A list of table entries, where the waiter client is registered as the worker
	 */
	public synchronized ArrayList<TableEntry> getTableEntriesByWorkerId(String workerId)
	{
		// Iteratate over the task table and look up the worker ID
		Iterator<TableEntry> it = iterator();
		ArrayList<TableEntry> entryList = new ArrayList<TableEntry>();
		while (it.hasNext())
		{
			TableEntry te = it.next();
			// Add to the returned list, if the worker ID is equal
			if (te.getWorker().equals(workerId))
				entryList.add(te);
		}// end while
		return entryList;
	}// end method get TableEntriesByWorkerId()

	/**
	 * Overrides the iterator() method of the Iterable interface. Returns an interator, which can
	 * iterate over the task table.
	 * 
	 * @return A reference to an iterator, which can iterate over the task table
	 */
	@Override
	public synchronized Iterator<TableEntry> iterator()
	{
		Iterator<TableEntry> iter = entryList.iterator();
		return iter;
	}// end method iterator()
}// end class TaskTable
