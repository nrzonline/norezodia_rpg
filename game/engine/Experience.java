package engine;

import settings.Config;

public class Experience 
{
	private final static short MAX_LEVEL = Config.MAX_LEVEL;
	private static long[] levelExp = new long[MAX_LEVEL];
	
	public Experience()
	{
		long exp = 0;
		for(int level=0; level<MAX_LEVEL; level++)
		{
			levelExp[level] = exp += (50*level*level)-(level*50);
		}
	}
	public static long getLevelExperience(int level)
	{
		return levelExp[level];
	}
}
