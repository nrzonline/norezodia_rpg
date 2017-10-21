package dev;

public class Exp 
{	
	private final short MAX_LEVEL = 150;
	private long[] levelExp = new long[MAX_LEVEL];

	public Exp ()
	{
		long exp = 0;
		for(int level=0; level<MAX_LEVEL; level++)
		{
			levelExp[level] = exp += (50*level*level)-(level*50);
		}
		
		for(int i=0; i<levelExp.length; i++)
		{
			System.out.println(i + ": " + levelExp[i]);
		}
	}
	
	public static void main (String[] args)
	{
		new Exp();
	}
}
