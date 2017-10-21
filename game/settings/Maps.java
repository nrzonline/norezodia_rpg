package settings;

import engine.Failure;

public class Maps
{
	private static String maps[][] = new String[][]
	{
		{
			"Ghorrad Island"
			//"Forrest of Iziva"
		},
		{
			"ghorrad_island"
			//"forrest_of_iziva"
		}
	};
	
	public Maps ()
	{
		
	}
	
	public static String getMapName (int mapID, boolean asFile)
	{		
		try
		{
			String mapName = maps[0][mapID];
			String mapFile = maps[1][mapID];
			
			return (!asFile) ? mapName : mapFile;
		}
		catch (Exception e)
		{
			Failure.add ("Failed to retreive map name or file.", e);
		}
		
		return new String();
	}
	public static int getMapCount () { return maps[0].length; }
}
