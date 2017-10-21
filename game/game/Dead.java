package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import engine.Input;

import settings.Colors;
import settings.Fonts;

public class Dead
{
	private Player player;
	
	public Dead (Player player)
	{
		this.player = player;
	}
	public void load ()
	{
		
	}
	public void input (Input input)
	{
		if (input.keyDownOnce(KeyEvent.VK_SPACE) || input.keyDownOnce(KeyEvent.VK_ENTER))
		{
			player.revive ();
		}
	}
	public void update ()
	{
		
	}
	public void draw (Graphics2D g2d)
	{
		g2d.setColor(Colors.BLACK_TRANSPARANT);
		g2d.fillRect(8, 8, 33*32, 21*32);
		
		Fonts.drawCenterString(g2d, "You died!", Colors.RED, Fonts.LOADING, 8, 260, 32*32, true);
		Fonts.drawCenterString(g2d, "Press SPACE to respawn...", Colors.ORANGE, Fonts.LOADING_SMALL, 8, 274, 32*32, true);
	}
}
