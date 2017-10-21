package editor;

import java.awt.Graphics2D;

import settings.Colors;

public class LowerMenu
{
	public LowerMenu ()
	{
		
	}
	public void load ()
	{
	}
	public void draw (Graphics2D g2d)
	{
		g2d.setColor(Colors.MENU);
		g2d.fillRect(8, 680, 1056, 212);
	}
}
