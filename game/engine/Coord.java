package engine;

public class Coord 
{
	private int coordX;
	private int coordY;
	
	// world coordinates to new coord.
	public Coord (double x, double y)
	{
		coordX = (int)x;
		coordY = (int)y;
	}
	// coord coordinates to new coord.
	public Coord (int x, int y)
	{
		coordX = x;
		coordY = y;
	}
	// create empty coord.
	public Coord ()
	{
		
	}
	
	public int getX () { return coordX; }
	public void setX (int coordY) { this.coordX = coordY; }
	public int getY () { return coordY; }
	public void setY (int coordY) { this.coordY = coordY; }
}
