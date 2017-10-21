package base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import engine.Animation;
import engine.Failure;
import engine.Log;

public class _Interface
{
	private static BufferedImage buttons, clickSheet;
	
	public _Interface ()
	{
		Log.out("Init interface");
	}
	public void load ()
	{
		try
		{
			buttons = ImageIO.read(this.getClass().getResource("/gui/buttons.png"));
			clickSheet = ImageIO.read(this.getClass().getResource("/gui/click.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed loading buttons image.", e);
		}
	}
	
	/*
	 * BUTTONS
	 */
	public static void squareButtonSelected (Graphics2D g2d, int x, int y)
	{
		g2d.drawImage (buttons.getSubimage(0, 0, 40, 40), x, y, null);
	}
	public static void squareButtonActive (Graphics2D g2d, int x, int y)
	{
    	g2d.drawImage (buttons.getSubimage(0, 40, 40, 40), x, y, null);
	}
	public static void squareButtonInactive (Graphics2D g2d, int x, int y)
	{
		g2d.drawImage (buttons.getSubimage(0, 80, 40, 40), x, y, null);
	}
	
	/*
	 * MOUSE
	 */
	public static Animation mouseMapClick ()
	{
		return new Animation(clickSheet, 10, 10, 0, 8, 80, false, 0, 0);
	}

	/*
	 * CHAT
	 */
	public static void chatBar ()
	{
		
	}
}
