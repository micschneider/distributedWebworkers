package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskTable implements Iterable<TableEntry>
{
	private ArrayList<TableEntry> entryList = new ArrayList<TableEntry>();
	private static TaskTable table = new TaskTable();
	
	private TaskTable()
	{
	}
	
	public static TaskTable getTaskTable()
	{
		return table;
	}//end method getTaskTable
	
	public ArrayList<TableEntry> getEntryList()
	{
		return this.entryList;
	}
	
	public void addTableEntry(TableEntry entry)
	{
		entryList.add(entry);
	}
	public void removeTableEntry(TableEntry entry)
	{
		entryList.remove(entry);
	}
	
	public TableEntry getTableEntryBySenderId(String senderId)
	{
		Iterator<TableEntry> it = iterator();
		while(it.hasNext())
		{
			TableEntry te = it.next();
			if(te.getSender().equals(senderId))
				return te;
		}
		return null;
	}
	
	public ArrayList<TableEntry> getTableEntriesByWorkerId(String workerId)
	{
		Iterator<TableEntry> it = iterator();
		ArrayList<TableEntry> entryList = new ArrayList<TableEntry>();
		while(it.hasNext())
		{
			TableEntry te = it.next();
			if(te.getWorker().equals(workerId))
				entryList.add(te);
		}
		return entryList;
	}

	@Override
	public Iterator<TableEntry> iterator() 
	{
		Iterator<TableEntry> iter = entryList.iterator();
		return iter;
	}
}
