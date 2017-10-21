package base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import engine.Failure;
import engine.Log;

public class _Chat 
{
	private static BufferedImage chat;
	
	public _Chat ()
	{
		Log.out("Init chat interface");
	}
	public void load ()
	{
		try
		{
			chat = ImageIO.read(this.getClass().getResource("/gui/chat.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed loading buttons image.", e);
		}
	}
	
	public static void chatBar(Graphics2D g2d)
	{
		g2d.drawImage(chat.getSubimage(0, 155, 1056, 36), 8, 856, null);
	}
}
