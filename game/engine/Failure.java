package engine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Failure
{
	private static ArrayList<String> failures = new ArrayList<String>();
	
	public Failure ()
	{
		
	}
	public void input (Input input)
	{
		if (input.keyDownOnce(KeyEvent.VK_SPACE))
		{
			Failure.reset ();
		}
	}
	public void draw (Graphics2D g2d)
	{
		for (int i = 0; i < failures.size (); i++)
		{
			g2d.setColor (Color.RED);
			g2d.drawString (failures.get(i), 50, 50+(i*15));
		}
	}
	
	public static void add (String message)
	{
		failures.add (message);
		Log.failure (message);
	}
	public static void add (String message, Exception e)
	{
		failures.add (message);
		Log.failure (message, e);
	}
	public static void add (String message, Exception e, boolean terminate)
	{
		failures.add (message);
		Log.failure (message + " (Terminal)", e);
		System.exit(0);
	}
	public boolean isFound ()
	{
		return (failures.size () == 0) ? false : true;
	}
	public static void reset ()
	{
		failures.clear ();
		Log.notice("Attempting to reload application after failure.");
		Framework.changeState ("LOADING");
	}
}
