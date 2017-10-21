package engine;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import settings.Constants;

public class Avatar 
{
	private String[] avatarSheets = new String[]
	{
		"human1",
		"human2",
		"human3"
	};
	private BufferedImage[] loadedAvatarSheets = new BufferedImage[avatarSheets.length];
	
	public Avatar ()
	{
	}
	public void load ()
	{
		Loading.changeState ("AVATARS");
		
		loadAvatarSheets ();
	}
	
	private void loadAvatarSheets ()
	{
		for (int i = 0; i < avatarSheets.length; i++)
		{
			try
			{
				loadedAvatarSheets[i] = ImageIO.read(this.getClass().getResource("/avatar/" + avatarSheets[i] + ".png"));
			}
			catch (Exception e)
			{
				Failure.add ("Failed to load avatar sheet: " + avatarSheets[i] + ".png", e);
			}
		}
	}
	public Animation[] createAvatarSet (String setName)
	{
		int setID;
		
		switch (setName)
		{
			case "human1": setID = 0; break;
			case "human2": setID = 1; break;
			case "human3": setID = 2; break;
			default: setID = 0; Log.failure("unknow avatarSet " + setName + " (non-critical, set changed)");
		}
		
		try
		{
			Animation avatarSet[] = new Animation[30];
		
			avatarSet[Constants.UP] = new Animation (loadedAvatarSheets[setID], 32, 32, 2, 2, 400, true, 0, 0); // up hold
			avatarSet[Constants.RIGHT] = new Animation (loadedAvatarSheets[setID], 32, 32, 3, 2, 400, true, 0, 0); // right hold
			avatarSet[Constants.DOWN] = new Animation (loadedAvatarSheets[setID], 32, 32, 0, 2, 400, true, 0, 0); // down hold
			avatarSet[Constants.LEFT] = new Animation (loadedAvatarSheets[setID], 32, 32, 1, 2, 400, true, 0, 0); // left hold
			avatarSet[Constants.DIE] = new Animation (loadedAvatarSheets[setID], 32, 32, 4, 4, 200, false, 0, 0); // dead
			avatarSet[Constants.RESPAWN] = new Animation (loadedAvatarSheets[setID], 32, 32, 5, 4, 100, false, 0, 0); // respawn
		
			return avatarSet;
		}
		catch (Exception e)
		{
			Failure.add ("Failed to create animated texture from sheet", e);
			return null;
		}
	}
} 
