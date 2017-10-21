package base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import engine.Failure;
import engine.Log;

public class _Symbols 
{
	private static BufferedImage symbols;
	
	public _Symbols()
	{
		Log.out("Init symbols interface");
	}
	public void load ()
	{
		try
		{
			symbols = ImageIO.read(this.getClass().getResource("/gui/symbols.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed loading player interface images.", e);
		}
	}
	
	public static void redMarker(Graphics2D g2d, int x, int y)
	{
		g2d.drawImage (symbols.getSubimage(0, 0, 13, 22), x, y, 8, 14, null);
	}
	public static void greenMarer(Graphics2D g2d, int x, int y)
	{
		
	}
	public static void yellowMarker(Graphics2D g2d, int x, int y)
	{
		
	}
	public static void greyMarker(Graphics2D g2d, int x, int y)
	{
		
	}
	public static void blueMarker(Graphics2D g2d, int x, int y)
	{
		
	}
}
