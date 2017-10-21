package game;

import java.awt.Graphics2D;

import settings.Colors;
import settings.Config;
import settings.Fonts;

import engine.Failure;
import engine.Grid;
import engine.Input;
import engine.Loading;
import engine.Path;
import engine.PathFinder;
import engine.Texture;

public class World 
{	
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private static final int DISPLAY_WIDTH = Config.DISPLAY_WIDTH;
	private static final int DISPLAY_HEIGHT = Config.DISPLAY_HEIGHT;
	private static final int HALF_WIDTH = DISPLAY_WIDTH/2;
	private static final int HALF_HEIGHT = DISPLAY_HEIGHT/2;
	private static final boolean SHOW_PATH = Config.SHOW_PATH;
	
	private Map map;
	private Player player;
	private Texture texture;
	private PathFinder pathfinder;
	
	private int xMin, xMax, yMin, yMax;
	private int mapWidth, mapHeight;
	private int xWorldOffset, yWorldOffset;
	private int xTileOffset, yTileOffset;
	
	public World (Texture texture, Player player, PathFinder pathfinder)
	{
		this.texture = texture;
		this.player = player;
		this.pathfinder = pathfinder;
		map  = new Map ();
		
		player.setWorld(this);
	}
	public void load ()
	{
		Loading.changeState ("WORLD");
		map = map.loadMap(player.getData().getMapID());
		mapWidth = map.getGridWidth () - 1;
		mapHeight = map.getGridHeight () - 1;
		
		pathfinder.fillNodes();
	}
	public void input (Input input)
	{
		if (input.buttonDownOnce (1) && input.clickedWithin(BORDER_OFFSET, DISPLAY_WIDTH*TILE_SIZE+BORDER_OFFSET, BORDER_OFFSET, DISPLAY_HEIGHT*TILE_SIZE+BORDER_OFFSET))
		{
			player.setPath(createPath(player.getGrid(), mouseToTargetGrid (input.getMouseGrid())));
		}
	}
	public void update ()
	{
		updateRenderLimits ();
		updateOffsets ();
	}
	public void draw (Graphics2D g2d)
	{
		drawPath (g2d);
	}
	
	private void updateRenderLimits ()
	{
		Grid playerGrid = player.getGrid();
		
		xMin = (playerGrid.getX() > HALF_WIDTH) ? playerGrid.getX() - HALF_WIDTH : 0;
		xMin = (xMin > 0) ? xMin - 1 : xMin;
		xMin = (playerGrid.getX() + HALF_WIDTH <= mapWidth) ? xMin : mapWidth - DISPLAY_WIDTH;
		
		xMax = (playerGrid.getX() < mapWidth - HALF_WIDTH) ? playerGrid.getX() + HALF_WIDTH : mapWidth;
		xMax = (xMax > DISPLAY_WIDTH) ? xMax : DISPLAY_WIDTH;
		xMax = (xMax < mapWidth) ? xMax + 2: xMax + 1;
		
		yMin = (playerGrid.getY() > HALF_HEIGHT) ? playerGrid.getY() - HALF_HEIGHT : 0;
		yMin = (yMin > 0) ? yMin - 1 : yMin;
		yMin = (playerGrid.getY() + HALF_HEIGHT <= mapHeight) ? yMin : mapHeight - DISPLAY_HEIGHT;
		
		yMax = (playerGrid.getY() < mapHeight - HALF_HEIGHT) ? playerGrid.getY() + HALF_HEIGHT + 1 : mapHeight;
		yMax = (yMax > DISPLAY_HEIGHT) ? yMax : DISPLAY_HEIGHT;
		yMax = (yMax < mapHeight) ? yMax + 2 : yMax + 1;
	}
	private void updateOffsets ()
	{
		int playerX = (int)player.getData().getWorldX ();
		int playerY = (int)player.getData().getWorldY ();
		
		xWorldOffset = yWorldOffset = 0;
		xTileOffset = yTileOffset = 0;
		
		if (playerX > HALF_WIDTH*TILE_SIZE && playerX < mapWidth*TILE_SIZE - HALF_WIDTH*TILE_SIZE)
		{
			xTileOffset = (playerX - playerX/TILE_SIZE*TILE_SIZE);
		}
		xWorldOffset = (playerX >= (HALF_WIDTH+1)*TILE_SIZE) ? TILE_SIZE : 0;
		
		if (playerY > HALF_HEIGHT*TILE_SIZE && playerY < (mapHeight*TILE_SIZE) - HALF_HEIGHT*TILE_SIZE)
		{
			yTileOffset = (playerY - playerY/TILE_SIZE*TILE_SIZE);
		}
		yWorldOffset = (playerY >= (HALF_HEIGHT+1)*TILE_SIZE) ? TILE_SIZE : 0;
	}
	private Grid mouseToTargetGrid (Grid mouseGrid)
	{
		
		int worldWidth = (map.getGridWidth () - 1) * TILE_SIZE;
		int worldHeight = (map.getGridHeight () - 1) * TILE_SIZE;
		
		Grid worldGrid = player.getGrid();
		Grid displayGrid = new Grid((double)player.getData().getDisplayX(), (double)player.getData().getDisplayY());
		Grid targetGrid = new Grid ();		
		
		if (player.getData().getDisplayX() >= 0 && player.getData().getWorldX() <= HALF_WIDTH*TILE_SIZE)
		{
			targetGrid.setX (mouseGrid.getX ());
		}
		else if (player.getData().getDisplayX() <= worldWidth && player.getData().getWorldX() >= worldWidth - (HALF_WIDTH*TILE_SIZE))
		{
			if (mouseGrid.getX () < displayGrid.getX ())
			{
				targetGrid.setX (worldGrid.getX () - (displayGrid.getX () - mouseGrid.getX ()));
			}
			else if (mouseGrid.getX () > displayGrid.getX ())
			{
				targetGrid.setX (worldGrid.getX() + (mouseGrid.getX () - displayGrid.getX()));
			}
			else
			{
				targetGrid.setX (worldGrid.getX());
			}
		}
		else
		{
			if (mouseGrid.getX () < HALF_WIDTH)
			{
				targetGrid.setX (worldGrid.getX () - (HALF_WIDTH - mouseGrid.getX ()));
			}
			else if (mouseGrid.getX () > HALF_WIDTH)
			{
				targetGrid.setX (worldGrid.getX () + mouseGrid.getX () - HALF_WIDTH);
			}
			else
			{
				targetGrid.setX (worldGrid.getX ());
			}
		}
		
		if (player.getData().getDisplayY() >= 0 && player.getData().getWorldY() <= HALF_HEIGHT*TILE_SIZE)
		{
			targetGrid.setY (mouseGrid.getY ());
		}
		else if (player.getData().getDisplayY() <= worldHeight && player.getData().getWorldY() >= worldHeight - (HALF_HEIGHT*TILE_SIZE))
		{
			if (mouseGrid.getY () < displayGrid.getY ())
			{
				targetGrid.setY (worldGrid.getY () - (displayGrid.getY () - mouseGrid.getY ()));
			}
			else if (mouseGrid.getY () > displayGrid.getY ())
			{
				targetGrid.setY (worldGrid.getY() + (mouseGrid.getY () - displayGrid.getY()));
			}
			else
			{
				targetGrid.setY (worldGrid.getY());
			}
		}
		else
		{
			if (mouseGrid.getY () < HALF_HEIGHT)
			{
				targetGrid.setY (worldGrid.getY () - (HALF_HEIGHT - mouseGrid.getY ()));
			}
			else if (mouseGrid.getY () > HALF_HEIGHT)
			{
				targetGrid.setY (worldGrid.getY () + mouseGrid.getY () - HALF_HEIGHT);
			}
			else
			{
				targetGrid.setY (worldGrid.getY ());
			}
		}
		
		return targetGrid;
	}
	private Path createPath (Grid start, Grid target) 
	{
		return pathfinder.find(start.getX(), start.getY(), target.getX(), target.getY(), true);
	}
	private void drawPath (Graphics2D g2d)
	{
		if (SHOW_PATH)
		{			
			Path path = player.getPath ();
			if (path != null)
			{
				int pathStep = player.getPathStep ();
				
				g2d.setColor (Colors.PATH);
				for (int i = pathStep+1; i < path.getLength(); i++)
				{
					int x = path.getX(i)*32-xWorldOffset-xTileOffset+BORDER_OFFSET-(xMin*32)+12;
					int y = path.getY(i)*32-yWorldOffset-yTileOffset+BORDER_OFFSET-(yMin*32)+12;
					g2d.fillOval(x, y, 6, 6);
				}
			}
		}
	}

	public void drawLayer (Graphics2D g2d, int layer)
	{
		try
		{
			int x = 0;
			for (int width = xMin; width < xMax; width++)
			{
				int y = 0;
				for (int height = yMin; height < yMax; height++)
				{
					int tileValue = map.getValue (layer, width, height);
					if (tileValue != 0)
					{
						texture.drawTexture
						(
							g2d, 
							tileValue, 
							x * TILE_SIZE - xWorldOffset - xTileOffset + BORDER_OFFSET, 
							y * TILE_SIZE - yWorldOffset - yTileOffset + BORDER_OFFSET
						);
					}
					y++;
				}
				x++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			Failure.add ("Could not draw one of the tiles, array index does not exist.", e);
		}
		catch (Exception e)
		{
			Failure.add ("Error while drawing textured layer.", e);
		}
	}
	
	public Map getMap () { return map; }
	public int getGridX () { return (int)player.getData().getWorldX() / TILE_SIZE; }
	public int getGridY () { return (int)player.getData().getWorldY() / TILE_SIZE; }

	public void drawData (Graphics2D g2d)
	{
		int x = 850;
		int y = 300;
		
		g2d.setColor(Colors.DRAW_DATA);
		g2d.fillRect(x, y, 200, 120);
		
		Fonts.drawString(g2d, "world data", Colors.ORANGE_DARK, Fonts.LOADING, x+10, y+20, true);
		Fonts.drawString
		(
			g2d, 
			"L:  " + xMin + ", " + yMin + "  /  " + xMax + ", " + yMax, 
			Colors.WHITE, Fonts.LOADING, x+10, y+40, true
		);
		Fonts.drawString(g2d, "R:  " + mapWidth + ", " + mapHeight, Colors.WHITE, Fonts.LOADING, x+10, y+60, true);
		Fonts.drawString(g2d, "W: " + xWorldOffset + ", " + yWorldOffset, Colors.WHITE, Fonts.LOADING, x+10, y+80, true);
		Fonts.drawString(g2d, "T:  " + xTileOffset + ", " + yTileOffset, Colors.WHITE, Fonts.LOADING, x+10, y+100, true);
	}
}
