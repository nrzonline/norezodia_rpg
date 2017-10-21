package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import settings.Config;

public class Texture
{	
	private static final int TILE_SIZE = Config.TILE_SIZE;
	
	private String[] staticSheets = new String[]
	{
		"editor",
		"water",
		"ground",
		"town"
	};			
	private String[] animationSheets = new String[]
	{
		"ground",
		"townAni"
	};
	private BufferedImage[] loadedStaticSheets = new BufferedImage[staticSheets.length];
	private BufferedImage[] loadedAnimationSheets = new BufferedImage[animationSheets.length];
	private BufferedImage[][][] textures = new BufferedImage[staticSheets.length][50][9];
	private Animation animations[][] = new Animation[animationSheets.length][50];
	
	public Texture ()
	{
		Log.out("Init texture");
	}	
	public void load ()
	{
		Loading.changeState ("TEXTURES");
		
		loadStaticSheets ();
		loadAnimationSheets ();
		createStaticTextures ();
		createAnimatedTextures ();
	}

	private void loadStaticSheets ()
	{
		for (int i = 0; i < staticSheets.length; i++)
		{
			try
			{
				loadedStaticSheets[i] = ImageIO.read(this.getClass().getResource("/textures/" + staticSheets[i] + ".png"));
			}
			catch (Exception e)
			{
				Failure.add ("Failed to load texture sheet: " + staticSheets[i] + ".png", e);
			}
		}
	}
	private void loadAnimationSheets ()
	{
		for (int j = 0; j < animationSheets.length; j++)
		{
			try
			{
				loadedAnimationSheets[j] = ImageIO.read(this.getClass().getResource("/textures/animations/" + animationSheets[j] + ".png"));
			}
			catch (Exception e)
			{
				Failure.add ("Failed to load animated texture sheet: " + animationSheets[j] + ".png", e);
			}
		}
	}
	private void createStaticTextures ()
	{
		try
		{
			for (int i = 0; i < staticSheets.length; i ++)
			{
				int rows = loadedStaticSheets[i].getHeight(null)/TILE_SIZE;
				
				for (int y = 0; y < rows; y++)
				{
					for (int x = 0; x < 9; x++)
					{
						textures[i][y][x] = loadedStaticSheets[i].getSubimage(x*TILE_SIZE, y*TILE_SIZE, TILE_SIZE, TILE_SIZE);
					}
				}
			}
		}
		catch (Exception e)
		{
			Failure.add ("Failed to create the texture sets from sheets", e);
		}
	}
	private void createAnimatedTextures ()
	{
		try
		{
			animations[0][0] = new Animation (loadedAnimationSheets[0], 32, 32, 1, 5, 200, true, 0, 0);
			animations[0][1] = new Animation (loadedAnimationSheets[0], 32, 32, 2, 5, 200, true, 0, 0);
			animations[0][2] = new Animation (loadedAnimationSheets[1], 32, 32, 0, 4, 200, true, 0, 0);
			animations[0][3] = new Animation (loadedAnimationSheets[1], 32, 32, 1, 4, 200, true, 0, 0);
			animations[0][4] = new Animation (loadedAnimationSheets[1], 32, 32, 2, 4, 200, true, 0, 0);
			animations[0][5] = new Animation (loadedAnimationSheets[1], 32, 32, 3, 4, 300, true, 0, 0);
			animations[0][6] = new Animation (loadedAnimationSheets[1], 32, 32, 4, 3, 200, true, 0, 0);
		}
		catch (Exception e)
		{
			Failure.add ("Failed to create animated texture from sheet", e);
		}
	}
	
	public void drawTexture (Graphics2D g2d, int value, int x, int y)
	{
		try
		{
			if (value > 0)
			{
				int sheet = (int) Math.floor (value/1000);
				int row = (int) Math.floor ((value-(sheet*1000))/10);
				int col = (value-(sheet*1000)-(row*10));
				
				g2d.drawImage(textures[sheet-1][row-1][col-1], x, y, TILE_SIZE, TILE_SIZE, null);
			}
			else if (value < 0)
			{
				value = Math.abs(value);
				int sheet = (int) Math.floor(value/100);
				int id = (int) Math.floor(value-(sheet*100));
				
				if (animations[sheet-1][id-1] != null)
				{
					animations[sheet-1][id-1].setLocation (x, y);
					animations[sheet-1][id-1].draw(g2d);
				}
			}
		}
		catch (Exception e)
		{
			Failure.add ("Failed to draw the texture to the screen", e, true);
		}
	}
	
	public String[] getStaticSheets() { return staticSheets; }
	public BufferedImage getSheet (int sheet) { return loadedStaticSheets[sheet]; }
	public Animation[] getAnimationSheet (int sheetID) { return animations[sheetID]; }
}
