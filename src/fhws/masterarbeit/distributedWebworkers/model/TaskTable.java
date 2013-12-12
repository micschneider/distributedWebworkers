package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;

public class TaskTable 
{
	private ArrayList<TableEntry> entryList;
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
}
