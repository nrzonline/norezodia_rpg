package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import settings.Colors;
import settings.Config;
import settings.Fonts;

public class Loading 
{
	private static final boolean DEVELOPMENT_MODE = Config.DEVELOPMENT_MODE;
	private static final boolean LOAD_WAIT = Config.LOAD_WAIT;
	private static final int LOAD_WAIT_MS = Config.LOAD_WAIT_MS;
	private static final int WINDOW_WIDTH = Config.WINDOW_WIDTH;
	
	private static String loadString;
	private BufferedImage loadbar;
	
	public Loading ()
	{
		Log.out("Init loading");
		
		try
		{
			loadbar = ImageIO.read(this.getClass().getResource("/gui/loadbar.png"));
		}
		catch (Exception e)
		{	
			Failure.add("Failed to load the splash image.", e);
		}
	}
	public void load ()
	{
		
	}
	public void draw (Graphics2D g2d)
	{
		g2d.drawImage (loadbar, Utilities.getCenter(WINDOW_WIDTH, 387), 200, 387, 79, null);
		Fonts.drawCenterString (g2d, "No'Re-Zodia", Colors.ORANGE_DARK, Fonts.LOADING_SMALL, 0, 215, WINDOW_WIDTH, true);
		Fonts.drawCenterString (g2d, loadString, Colors.RED, Fonts.LOADING, 0, 260, WINDOW_WIDTH, true);
	}
	
	public static void changeState (String state)
	{
		Log.out ("Changing current loadingState to '" + state + "'.");
				
		switch (state)
		{
			case "MOUSE":
				loadString = "Loading mouse graphics";
			break;
			case "PLAYER":
				loadString = "Loading player graphics";
				break;
			case "PLAYER_DATA":
				loadString = "Loading player data";
				break;
			case "TEXTURES":
				loadString = "Loading world textures";
				break;
			case "AVATARS":
				loadString = "Loading the avatar graphics";
				break;
			case "UPDATE_WORLD":
				loadString = "Updating the world map";
				break;
			case "WORLD":
				loadString = "Loading the world";
				break;
			case "NPC_LIST":
				loadString = "Loading the npc's";
				break;
			default:
				Failure.add ("Changing loadingState failed, '" + state + "' does not exist!");
		}
		
		if (DEVELOPMENT_MODE)
		{
			if (LOAD_WAIT)
			{
				try
				{
					Thread.sleep(LOAD_WAIT_MS);
				}
				catch (Exception ex) {}
			}
		}
	}
}
