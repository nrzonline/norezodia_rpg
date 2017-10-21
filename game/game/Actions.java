package game;

import java.awt.Graphics2D;

import settings.Colors;
import settings.Config;

import engine.Input;

public class Actions
{
	private static final int PLAY_WIDTH = Config.DISPLAY_WIDTH * Config.TILE_SIZE;
	private static final int PLAY_HEIGHT = Config.DISPLAY_HEIGHT * Config.TILE_SIZE;
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	
	private int x = 0;
	private int y = 0;
	private int width = 170;
	private int height = 200;
	private boolean displayPopup = false;
	
	public Actions ()
	{
		
	}
	
	public void input (Input input)
	{
		
	}
	public void draw (Graphics2D g2d)
	{
		if (displayPopup)
		{
			g2d.setColor (Colors.MENU_POPUP);
			g2d.fillRect (x, y, width, height);
			g2d.setColor (Colors.BLACK);
			g2d.drawRect (x, y, width, height);
		}
	}
	
	public void createPopup (int x, int y)
	{
		this.x = x;
		this.y = y;
		
		if (x > BORDER_OFFSET && x < (PLAY_WIDTH + BORDER_OFFSET) && y > BORDER_OFFSET && y < (PLAY_HEIGHT + 2*BORDER_OFFSET))
		{
			displayPopup = true;
		}
		else
		{
			displayPopup = false;
		}
	}
	public boolean isPopupHit (int mouseX, int mouseY) 
	{
		return false; 
	}
	
	public void hidePopup () { displayPopup = false; }
}
