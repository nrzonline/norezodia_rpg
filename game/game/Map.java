package game;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import settings.Maps;

import engine.Failure;

public class Map implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private Map mapLoaded;
	
	private int[][][] map;
	private int offsetX = 0;
	private int offsetY = 0;
	
	public Map ()
	{
		
	}
	
	public Map loadMap (int mapID)
	{
		String mapName = Maps.getMapName (mapID, false);
		String mapFile = Maps.getMapName (mapID, true);
		
		try
		{
			FileInputStream fis = new FileInputStream("data/maps/" + mapFile + ".map");
			ObjectInputStream iis = new ObjectInputStream(fis);
			mapLoaded = (Map) iis.readObject();
			iis.close ();
			
			return mapLoaded;
		}
		catch (Exception e)
		{
			Failure.add("Failed to load " + mapName + "(" + mapFile + ".map)", e);
			
			return null;
		}
	}
	
	public boolean tileBlocked (int x, int y)
	{
		return (this.map[0][y][x] == 0) ? true : false;
	}
	public int getTileCost (int x, int y)
	{
		return map[0][y][x];
	}
	
	public int getGridWidth () { return this.map[0][0].length; }
	public int getGridHeight () { return this.map[0].length; }
	public void setOffsetX (int offset) { this.offsetX = offset; }
	public void setOffsetY (int offset) { this.offsetY = offset; }
	public int getOffsetX () { return this.offsetX; }
	public int getOffsetY () { return this.offsetY; }
	public int[][][] getMap () { return this.map; }
	public int getValue (int layer, int x, int y) throws IndexOutOfBoundsException { return this.map[layer][y][x]; }
	public void setValue (int layer, int x, int y, int value) { this.map[layer][y][x] = value; }
	public void setMap (int[][][] map) { this.map = map; }
}
