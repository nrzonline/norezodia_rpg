package base;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import settings.Colors;
import settings.Fonts;

import engine.Failure;
import engine.Log;

public class _Player 
{
	private static BufferedImage xpBar;
	
	public _Player ()
	{
		Log.out("Init player interface");
	}
	public void load ()
	{
		try
		{
			xpBar = ImageIO.read(this.getClass().getResource("/gui/xpbar.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed loading player interface images.", e);
		}
	}
	
	public static void xpBar (Graphics2D g2d, long prevLvlExp, long nxtLvlExp, long currExp)
	{
		g2d.drawImage (xpBar.getSubimage(0, 5, 1056, 17), 8, 673, null);
		
		float lvlExp = nxtLvlExp - prevLvlExp;
		float progressExp = currExp - prevLvlExp;
		
		if(progressExp > 0)
		{
			float lvlPercent = (progressExp/lvlExp)*100;
			int expBarLength = (int)Math.floor(lvlPercent*10);
			
			if(expBarLength > 0 && expBarLength<= 1000)
			{
				g2d.drawImage(xpBar.getSubimage(29, 0, expBarLength, 6), 37, 683, expBarLength, 6, null);
			}
		}
		Fonts.drawString(g2d, currExp + "/" + nxtLvlExp, Colors.WHITE, Fonts.HEALTH, 14, 665, true);
	}
}
