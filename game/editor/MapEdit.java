package editor;

import engine.Failure;
import engine.Framework;
import engine.Grid;
import engine.Input;
import engine.Texture;
import engine.Utilities;

import game.Game;
import game.Map;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import settings.Colors;
import settings.Config;
import settings.Maps;

public class MapEdit
{	
	private static final long EDITOR_SAVE_DELAY = Config.EDITOR_SAVE_DELAY;
	private static final long NAVIGATE_DELAY = Config.NAVIGATE_DELAY;
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private static final int DISPLAY_WIDTH = Config.DISPLAY_WIDTH;
	private static final int DISPLAY_HEIGHT = Config.DISPLAY_HEIGHT;
	private static final int HALF_WIDTH = DISPLAY_WIDTH/2;
	private static final int HALF_HEIGHT = DISPLAY_HEIGHT/2;
	
	private Map map;
	private Texture texture;
	
	public static enum ActionState
	{
		REMOVE,
		CLEAR
	}
	public static ActionState actionState = ActionState.REMOVE;
	
	ObjectInputStream ois;
	ObjectOutputStream oos;
	
	private int xMin, xMax, yMin, yMax;
	private int mapWidth, mapHeight;
	private int mapID;
	private Grid currentGrid = new Grid ((double)(HALF_WIDTH+1)*TILE_SIZE, (double)(HALF_HEIGHT+1)*TILE_SIZE);
	private static int workLayer = 0;
	private static int textureValue = 0;
	private long lastNavigation = System.currentTimeMillis();
	private long lastSave = System.currentTimeMillis();
	
	public MapEdit (Texture texture)
	{
		this.texture = texture;
	}
	public void load ()
	{
		loadMap (0);
	}
	public void input (Input input)
	{
		if (input.keyDownOnce(KeyEvent.VK_F1)) 
		{
			saveMap (true);
			Game.changeState("LOAD_MAP");
			Framework.changeState("PLAYING"); 
		}
		
		if (Utilities.isWithin(input.getMouseX(), BORDER_OFFSET, DISPLAY_WIDTH*TILE_SIZE) && Utilities.isWithin(input.getMouseY(), BORDER_OFFSET, DISPLAY_HEIGHT*TILE_SIZE))
		{
			changeWalkableValue (input);
			changeTileValue (input);
			switch (actionState)
			{
				case REMOVE:
					removeTileValue (input);
					break;
				case CLEAR:
					clearTile (input);
					break;	
				default: break;
			}
		}
		navigate (input);
	}
	public void update ()
	{
		updateRenderLimits ();
		saveMap (false);
	}
	public void draw (Graphics2D g2d)
	{
		drawLayer(g2d, 1);
		drawLayer(g2d, 2);
		drawLayer(g2d, 3);
		drawLayer(g2d, 4);
		drawLayer(g2d, 5);
		drawLayer(g2d, 0);
	}
	
	private void updateRenderLimits ()
	{	
		xMin = (currentGrid.getX() > HALF_WIDTH) ? currentGrid.getX() - HALF_WIDTH : 0;
		xMin = (xMin > 0) ? xMin - 1 : xMin;
		xMin = (currentGrid.getX() + HALF_WIDTH <= mapWidth + 1) ? xMin : mapWidth - DISPLAY_WIDTH;
		
		xMax = (currentGrid.getX() < mapWidth - HALF_WIDTH) ? currentGrid.getX() + HALF_WIDTH + 1: mapWidth;
		xMax = (xMax > DISPLAY_WIDTH) ? xMax : DISPLAY_WIDTH;
		xMax = (xMax < mapWidth) ? xMax + 2: xMax + 1;
		
		yMin = (currentGrid.getY() > HALF_HEIGHT) ? currentGrid.getY() - HALF_HEIGHT : 0;
		yMin = (yMin > 0) ? yMin - 1 : yMin;
		yMin = (currentGrid.getY() + HALF_HEIGHT <= mapHeight + 1) ? yMin : mapHeight - DISPLAY_HEIGHT;
		
		yMax = (currentGrid.getY() < mapHeight - HALF_HEIGHT) ? currentGrid.getY() + HALF_HEIGHT + 1 : mapHeight;
		yMax = (yMax > DISPLAY_HEIGHT) ? yMax : DISPLAY_HEIGHT;
		yMax = (yMax < mapHeight) ? yMax + 2 : yMax + 1;
	}
	private void drawLayer (Graphics2D g2d, int layer)
	{
		if (workLayer == layer)
		{
			g2d.setColor (Colors.BLACK_TRANSPARANT);
			g2d.fillRect(BORDER_OFFSET, BORDER_OFFSET, DISPLAY_WIDTH*TILE_SIZE, DISPLAY_HEIGHT*TILE_SIZE);
		}
		
		try
		{
			int x = 0;
			for (int width = xMin; width < xMax; width++)
			{
				int y = 0;
				for (int height = yMin; height < yMax; height++)
				{
					if (layer == 0 && workLayer == 0)
					{
						if (map.getValue (layer, width, height) == 0)
						{
							g2d.setColor (Colors.NON_WALKABLE);
							g2d.fillRect (x * TILE_SIZE + BORDER_OFFSET, y * TILE_SIZE + BORDER_OFFSET, TILE_SIZE, TILE_SIZE);
							g2d.setColor (Colors.RED);
							g2d.drawLine (x*TILE_SIZE+BORDER_OFFSET, y*TILE_SIZE+BORDER_OFFSET, (x+1)*TILE_SIZE+BORDER_OFFSET-1, (y+1)*TILE_SIZE+BORDER_OFFSET-1);
						}
					}
					else if (Utilities.isWithin(layer, 1, 5))
					{
						texture.drawTexture
						(
							g2d, 
							map.getValue (layer, width, height), 
							x * TILE_SIZE + BORDER_OFFSET, 
							y * TILE_SIZE + BORDER_OFFSET
						);
					}
					y++;
				}
				x++;
			}
		}
		catch (IndexOutOfBoundsException e)
		{
			Failure.add ("Could not draw one of the tiles, array index does not exist.");
		}
		catch (Exception e)
		{
			Failure.add ("Error while drawing layer");
		}
	}
	
	public Grid mouseToTargetGrid (Grid mouseGrid)
	{		
		Grid targetGrid = new Grid ();		
		
		if (currentGrid.getX() <= HALF_WIDTH+1)
		{
			targetGrid.setX (mouseGrid.getX ());
		}
		else if (currentGrid.getX() > HALF_WIDTH)
		{
			targetGrid.setX (currentGrid.getX() - HALF_WIDTH-1 + mouseGrid.getX());
		}
		
		if (currentGrid.getY() <= HALF_HEIGHT+1)
		{
			targetGrid.setY (mouseGrid.getY());
		}
		else if (currentGrid.getY () > HALF_HEIGHT)
		{
			targetGrid.setY (currentGrid.getY() - HALF_HEIGHT-1 + mouseGrid.getY());
		}
		
		return targetGrid;
	}
	private void navigate (Input input)
	{
		if (lastNavigation + NAVIGATE_DELAY < System.currentTimeMillis())
		{
			if (input.keyDown(KeyEvent.VK_UP, KeyEvent.VK_W) && currentGrid.getY()-1 >= HALF_HEIGHT+1)
			{
				currentGrid.setY(currentGrid.getY()-1);
			}
			if (input.keyDown(KeyEvent.VK_RIGHT, KeyEvent.VK_D) && currentGrid.getX()+1 <= map.getGridWidth() - HALF_WIDTH)
			{
				currentGrid.setX(currentGrid.getX()+1);
			}
			if (input.keyDown(KeyEvent.VK_DOWN, KeyEvent.VK_S)  && currentGrid.getY()+1 <= map.getGridHeight() - HALF_HEIGHT)
			{
				currentGrid.setY(currentGrid.getY()+1 );
			}
			if (input.keyDown(KeyEvent.VK_LEFT, KeyEvent.VK_A) && currentGrid.getX()-1 >= HALF_WIDTH + 1)
			{
				currentGrid.setX(currentGrid.getX()-1);
			}
			
			lastNavigation = System.currentTimeMillis();
		}
	}
	private void changeWalkableValue (Input input)
	{
		if (input.buttonDownOnce(1) && workLayer == 0)
		{
			Grid targetGrid = mouseToTargetGrid (input.getMouseGrid());
			map.setValue (workLayer, targetGrid.getX(), targetGrid.getY(), (map.getValue(0, targetGrid.getX(), targetGrid.getY()) == 1) ? 0 : 1);
		}
	}
	private void changeTileValue (Input input)
	{			
		if (input.buttonDown(1) && Utilities.isWithin(workLayer, 1, 5))
		{
			try
			{
				Grid targetGrid = mouseToTargetGrid (input.getMouseGrid());
				map.setValue(workLayer,  targetGrid.getX(), targetGrid.getY(), textureValue);
			}
			catch (Exception e)
			{
				Failure.add ("Failed to change tile value.", e);
			}
		}
	}
	private void removeTileValue (Input input)
	{
		if (input.buttonDown(3))
		{
			if (workLayer != 0)
			{
				Grid targetGrid = mouseToTargetGrid (input.getMouseGrid());
				map.setValue(workLayer, targetGrid.getX(), targetGrid.getY(), 0);
			}
			else
			{
				changeWalkableValue (input);
			}
		}
	}
	private void clearTile (Input input)
	{
		if (input.buttonDown(3))
		{
			Grid targetGrid = mouseToTargetGrid (input.getMouseGrid());
			for (int i = 0; i < 6; i ++)
			{
				if (i != 0)
				{
					map.setValue(i, targetGrid.getX(), targetGrid.getY(), 0);
				}
				else
				{
					map.setValue(i, targetGrid.getX(), targetGrid.getY(), 1);
				}
			}
		}
	}

	public Map loadMap (int mapID)
	{
		ois = null;
		this.mapID = mapID;
		String mapFile = Maps.getMapName (mapID, true);
		
		try
		{
			FileInputStream fis = new FileInputStream("data/maps/" + mapFile + ".map");
			ois = new ObjectInputStream(fis);
			map = (Map) ois.readObject();
			ois.close ();
			
			mapWidth = map.getGridWidth () - 1;
			mapHeight = map.getGridHeight () - 1;
			
			return map;
		}
		catch (Exception e)
		{
			return null;
		}
		finally
		{
			if (ois != null) { try { ois.close(); } catch (Exception e) {} }
		}
	}
	public void saveMap (boolean ignoreLastSave)
	{
		oos = null;
		
		if (lastSave + EDITOR_SAVE_DELAY < System.currentTimeMillis() || ignoreLastSave)
		{			
			String mapFile = Maps.getMapName (mapID, true);
			
			
			//int[][][] nMap = new int[7][120][120];
			/*
			int[][][] oMap = map.getMap();
			int[][][] nMap = new int[7][map.getGridHeight()][map.getGridWidth()];
			nMap[6] = oMap[5];	// topper 2
			nMap[5] = oMap[4];	// topper 1
			nMap[4] = oMap[3];	// bottom over 3 (new)
			nMap[3] = oMap[5];	// bottom over 2
			nMap[2] = oMap[2];	// bottom over 1
			nMap[1] = oMap[1];	// base layer
			nMap[0] = oMap[0];	// walkable layer
			map.setMap(nMap);
			
			for (int y = 0; y < map.getGridHeight(); y++)
			{
				for (int x = 0; x < map.getGridWidth(); x++)
				{
					nMap[4][y][x] = 0;
				}
			}
			
			for (int y = 0; y < map.getGridHeight(); y++)
			{
				for (int x = 0; x < map.getGridWidth(); x++)
				{
					nMap[0][y][x] = 1;
				}
			}
			
			int[][][] nMap = new int[7][120][120];
			for (int y = 0; y < map.getGridHeight(); y++)
			{
				for (int x = 0; x < map.getGridWidth(); x++)
				{
					nMap[0][y][x] = 1;
				}
			}
			map.setMap(nMap);
			*/			
			
			try 
			{
				FileOutputStream fos = new FileOutputStream("data/maps/" + mapFile + ".map");
				oos = new ObjectOutputStream(fos);
				oos.writeObject(map);
				oos.close ();
				
				lastSave = System.currentTimeMillis();
			}
			catch (Exception e)
			{
				Failure.add("Failed to save data/maps/" + mapFile + ".map", e);
			}
			finally
			{
				if (oos != null) { try { oos.close (); } catch (Exception e) {}}
			}
		}
	}

	public static void setWorkLayer (int layer)
	{
		workLayer = layer;
	}
	public static void setValue (int value)
	{
		textureValue = value;
	}
	public static void setActionState (String state)
	{
		switch (state)
		{
			case "REMOVE":
				actionState = ActionState.REMOVE;
				break;
			case "CLEAR":
				actionState = ActionState.CLEAR;
				break;
		}
	}
}
