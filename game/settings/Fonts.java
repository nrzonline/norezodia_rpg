package settings;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class Fonts 
{
	public static final Font LOADING = new Font("verdana", Font.CENTER_BASELINE, 13);
	public static final Font LOADING_SMALL = new Font ("verdana", Font.CENTER_BASELINE, 9);
	public static final Font BUTTON = new Font ("verdana", Font.CENTER_BASELINE, 10);
	public static final Font HEALTH = new Font ("Monospaced", Font.CENTER_BASELINE, 8);
	public static final Font CHAT = new Font("verdana", Font.CENTER_BASELINE, 10);
	public static final Font DAMAGE = new Font("Monospaced", Font.CENTER_BASELINE, 12);
	
	public static void drawString (Graphics2D g2d, String string, Color color, Font font, int x, int y)
	{
		g2d.setFont(font);
		
		g2d.setColor(color);
		g2d.drawString(string, x, y);
	}
	public static void drawString (Graphics2D g2d, String string, Color color, Font font, int x, int y, boolean shadow)
	{
		g2d.setFont(font);
		
		if (shadow)
		{
			g2d.setColor(Color.black);
			g2d.drawString(string, x+1, y+1);
		}
		g2d.setColor(color);
		g2d.drawString(string, x, y);
	}
	public static void drawCenterString (Graphics2D g2d, String string, Color color, Font font, int x, int y, int boxWidth)
	{
		g2d.setFont(font);
		
		int center = getStringCenter (g2d, string, x, boxWidth);
		g2d.setColor(color);
		g2d.drawString(string, center, y);
	}
	public static void drawCenterString (Graphics2D g2d, String string, Color color, Font font, int x, int y, int boxWidth, boolean shadow)
	{
		g2d.setFont(font);
		
		int center = getStringCenter (g2d, string, x, boxWidth);
		if (shadow)
		{
			g2d.setColor(Color.black);
			g2d.drawString(string, center+1, y+1);
		}
		g2d.setColor(color);
		g2d.drawString(string, center, y);
	}
	public static int getStringCenter (Graphics2D g2d, String string, int x, int boxWidth)
	{
		FontMetrics fm = g2d.getFontMetrics();
		int stringWidth = fm.stringWidth(string);
		
		return x + (boxWidth-stringWidth)/2;
	}
}
