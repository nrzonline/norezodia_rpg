package game;

import java.awt.Graphics2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import settings.Colors;
import settings.Fonts;

public class Menu 
{	
	private SimpleDateFormat dateFormat;
	
	public Menu ()
	{
		dateFormat = new SimpleDateFormat ("H:mm:ss");
	}
	
	public void load ()
	{
		
	}
	public void draw (Graphics2D g2d)
	{
		String date = dateFormat.format(new Date());
		
		g2d.setColor(Colors.MENU);
		g2d.fillRect(1073, 8, 359, 884);
		
		Fonts.drawCenterString(g2d, date, Colors.ORANGE_DARK, Fonts.LOADING, 1073, 24, 359, true);
	}
}
