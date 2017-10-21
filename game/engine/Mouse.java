package engine;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import base._Interface;

public class Mouse 
{
	private BufferedImage clickSheet;
	private Animation click;
	
	private int x, y;
	
	public Mouse ()
	{
		click = _Interface.mouseMapClick ();
	}
	public void load ()
	{
		Loading.changeState ("MOUSE");
		
		try
		{
			clickSheet = ImageIO.read(this.getClass().getResource("/gui/click.png"));
			
			click = new Animation(clickSheet, 10, 10, 0, 8, 80, false, 0, 0);
		}
		catch (Exception e)
		{
			System.out.println ("Fail load interface");
		}
	}
	
	public void input (Input input)
	{
		x = input.getMouseX();
		y = input.getMouseY();
		
		if (input.buttonDownOnce(1))
		{
			click.setLocation(x, y);
			click.restart();
		}
		if(input.keyDown(KeyEvent.VK_G))
		{
			setCursor(Cursor.CROSSHAIR_CURSOR);
		}
		if(input.keyDown(KeyEvent.VK_H))
		{
			setCursor(Cursor.HAND_CURSOR);
		}
	}
	public void update ()
	{
		setCursor(Cursor.DEFAULT_CURSOR);
	}
	public void draw (Graphics2D g2d)
	{
		click.draw(g2d);
	}
	
	public void setCursor(int cursor)
	{
		Framework.changeCursor(cursor);
	}
}
