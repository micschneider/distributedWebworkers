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

	@Override
	public Iterator<TableEntry> iterator() 
	{
		Iterator<TableEntry> iter = entryList.iterator();
		return iter;
	}
}
