package engine;

import settings.Config;

public class Grid
{
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private int gridX;
	private int gridY;
	
	// world coordinates to new grid.
	public Grid (double x, double y)
	{
		gridX = (int)x/TILE_SIZE;
		gridY = (int)y/TILE_SIZE;
	}
	// grid coordinates to new grid.
	public Grid (int x, int y)
	{
		gridX = x;
		gridY = y;
	}
	// create empty grid.
	public Grid ()
	{
		
	}
	
	public int getX () { return gridX; }
	public void setX (int gridY) { this.gridX = gridY; }
	public int getY () { return gridY; }
	public void setY (int gridY) { this.gridY = gridY; }
}
