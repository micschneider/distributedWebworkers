package fhws.masterarbeit.distributedWebworkers.model;

public class TableEntry 
{
	private static int entryId = 0;
	
	private int id;
	private String sender;
	private String worker;
	
	public TableEntry(String sender, String worker)
	{
		this.id = entryId;
		entryId++;
		this.sender = sender;
		this.worker = worker;
	}
	
	public int getId() 
	{
		return id;
	}
	public String getSender() 
	{
		return sender;
	}
	
	public String getWorker() 
	{
		return worker;
	}
}
