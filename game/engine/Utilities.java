package engine;

public class Utilities
{
	public static int getCenter (int centerOfWidth, int width)
	{
		return (centerOfWidth-width)/2;
	}	
	public static boolean isWithin (int current, int x, int y)
	{
		return (current >= x && current <= y) ? true : false;
	}
	public static boolean passedTime (long timeToPass)
	{
		return (System.currentTimeMillis() > timeToPass) ? true : false;
	}
	public static long now ()
	{
		return System.currentTimeMillis();
	}
	public static double getDifference (double first, double second)
	{
		return (Math.max(first, second) - Math.min(first, second));
	}
	public static int getDifference (int first, int second)
	{
		return (Math.max(first, second) - Math.min(first, second));
	}
	public static RateCounter newCounter ()
	{
		RateCounter counter = new RateCounter ();
		return counter;
	}
	public static class RateCounter
	{
		private int currentCount;
		private int countPerSecond;
		private long nextCount = System.currentTimeMillis()+1000;
		
		public void count ()
		{
			if (System.currentTimeMillis() < nextCount)
			{
				currentCount++;
			}
			else
			{
				countPerSecond = currentCount;
				currentCount = 0;
				nextCount += 1000;
			}
		}
		public int getRate ()
		{
			return countPerSecond;
		}
	}
}