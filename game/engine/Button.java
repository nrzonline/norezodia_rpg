package engine;

import java.awt.Graphics2D;

import base._Interface;

import settings.Colors;
import settings.Fonts;

public class Button
{
	private String name;
	private int x, y, width, height;
	private String string;
	private boolean active;
	private boolean selected;
	
	public Button (String name, int x, int y, int width, int height, String string, boolean active, boolean selected)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.string = string;
		this.active = active;
		this.selected = selected;
	}
	public void draw (Graphics2D g2d)
	{
		if (selected)
		{
			_Interface.squareButtonSelected (g2d, x, y);
		}
		else if (active)
		{
			_Interface.squareButtonActive (g2d, x, y);
		}
		else
		{
			_Interface.squareButtonInactive (g2d, x, y);
		}
		
		Fonts.drawCenterString(g2d, string, Colors.WHITE, Fonts.BUTTON, x, y+24, width);
	}
	
	public boolean isHit (Input input)
	{
		return isActive() && Utilities.isWithin(input.getMouseX(), x, x+width) && Utilities.isWithin (input.getMouseY(), y, y+height);
	}
	public boolean isHover (Input input)
	{
		return isHit (input);
	}
	public boolean isActive () { return active; }
	public boolean isSelected () { return selected; }
	public boolean isButton (String name) { return (this.name == name) ? true : false; }
	public void setActive (boolean active) { this.active = active; }
	public void setSelected (boolean selected) { this.selected = selected; }
}
