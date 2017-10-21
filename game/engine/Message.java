package engine;

import java.awt.Color;

public class Message 
{
	private int id;
	private Color color;
	private String message;
	
	public Message(int id, Color color, String message)
	{
		this.id = id;
		this.color = color;
		this.message = message;
	}
	
	public Color getColor ()
	{
		return this.color;
	}
	public String getMessage ()
	{
		return this.message;
	}
	public int getId()
	{
		return this.id;
	}
}
