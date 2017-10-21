package dev;

import java.util.ArrayList;

public class Chat 
{
	private static final int MAX_SIZE = 5;
	private ArrayList<String> messageList;
	
	public Chat ()
	{
		messageList = new ArrayList<String>();
		
		addMessage("1 test");
		addMessage("2 test");
		addMessage("3 test");
		addMessage("4 test");
		addMessage("5 test");
		addMessage("6 test");
		addMessage("7 test");
		addMessage("8 test");
		
		displayChat();
	}
	
	private void addMessage (String message)
	{
		if(countMessages () == MAX_SIZE)
		{
			messageList.remove(0);
		}
		messageList.add(message);
	}
	
	private int countMessages ()
	{
		return messageList.size();
	}
	
	private void displayChat()
	{
		for (String msg : messageList)
		{
			System.out.println(msg);
		}
	}
	
	public static void main (String[] args)
	{
		new Chat();
	}
}
