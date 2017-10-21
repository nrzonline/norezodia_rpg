package editor;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import base._Borders;

import engine.Framework;
import engine.Input;
import engine.Texture;

public class Editor
{	
	Texture texture;
	MapEdit mapEdit;
	Menu menu;
	LowerMenu lowerMenu;
	
	public Editor (Texture texture)
	{
		this.texture = texture;
		
		mapEdit = new MapEdit (texture);
		menu = new Menu (texture);
		lowerMenu = new LowerMenu ();
	}
	public void load ()
	{
		mapEdit.load ();
		menu.load();
	}
	public void input (Input input)
	{
		if (input.keyDownOnce(KeyEvent.VK_F1)) 
		{ 
			Framework.changeState("PLAYING");
		}
		
		mapEdit.input (input);
		menu.input(input);
	}
	public void update ()
	{
		mapEdit.update();
	}
	public void draw (Graphics2D g2d)
	{
		mapEdit.draw (g2d);
		menu.draw (g2d);
		lowerMenu.draw (g2d);
		
		_Borders.drawInnerEditor (g2d);
	}
}
