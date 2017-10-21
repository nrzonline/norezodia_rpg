package base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import engine.Failure;
import engine.Log;

public class _Borders
{
	private static BufferedImage borders;
	
	public _Borders ()
	{
		Log.out("Init borders");
		
		try
		{
			borders = ImageIO.read(this.getClass().getResource("/gui/borders.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed during image loading.", e);
		}
	}
	
	public void drawOuter (Graphics2D g2d)
	{
    	g2d.drawImage (borders.getSubimage(0, 0, 1440, 8), 0, 0, null);
    	g2d.drawImage (borders.getSubimage(0, 8, 8, 884), 0, 8, null);
    	g2d.drawImage (borders.getSubimage(0, 892, 1440, 8), 0, 892, null);
    	g2d.drawImage (borders.getSubimage(1432, 8, 8, 884), 1432, 8, null);
	}
	
	public static void drawInner (Graphics2D g2d)
	{
		g2d.drawImage (borders.getSubimage(8, 690, 1056, 9), 8, 690, null);
		g2d.drawImage (borders.getSubimage(1064, 8, 9, 884), 1064, 8, null);	
	}
	public static void drawInnerEditor (Graphics2D g2d)
	{
		g2d.drawImage (borders.getSubimage(8, 690, 1056, 9), 8, 680, null);
		g2d.drawImage (borders.getSubimage(1064, 8, 9, 884), 1064, 8, null);	
	}
}
