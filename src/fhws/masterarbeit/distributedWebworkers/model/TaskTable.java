package fhws.masterarbeit.distributedWebworkers.model;

import java.util.ArrayList;
import java.util.Iterator;

public class TaskTable implements Iterable<TableEntry>
{
	private ArrayList<TableEntry> entryList = new ArrayList<TableEntry>();
	private SessionMonitor sessionMonitor = SessionMonitor.getSessionMonitor();
	private static TaskTable table = new TaskTable();
	private static final int MAX_WORKERS_PER_CLIENT = 3;
	
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
		checkIfBusy(entry);
	}
	public void removeTableEntry(TableEntry entry)
	{
		entryList.remove(entry);
		checkIfBusy(entry);
	}
	
	public String getWorkerIdBySenderId(String senderId)
	{
		Iterator<TableEntry> it = iterator();
	    while (it.hasNext()) 
	    {
	       TableEntry te = it.next();
	       if(te.getSender().equals(senderId))
	    	   return te.getWorker();
	    }
	    return null;
	}
	
	private void checkIfBusy(TableEntry entry)
	{
		int counter = 0;
		Iterator<TableEntry> it = iterator();
	    while (it.hasNext()) 
	    {
	       TableEntry te = it.next();
	       if(te.getWorker().equals(entry.getWorker()))
	    	   counter++;
	    }
	    if(counter >= MAX_WORKERS_PER_CLIENT)
	    	sessionMonitor.getWaiterSessionById(entry.getWorker()).setBusy();
	    else
	    	sessionMonitor.getWaiterSessionById(entry.getWorker()).setFree();
	    System.out.println("Der Worker mit der ID: "+ entry.getWorker()+ "bearbeitet derzeit " + counter + " fremde Aufgaben");
	}

	@Override
	public Iterator<TableEntry> iterator() 
	{
		Iterator<TableEntry> iter = entryList.iterator();
		return iter;
	}
}
