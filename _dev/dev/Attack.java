package dev;

public class Attack 
{
	private double damage =	13.4d;
	private double critical = 0.1d;
	
	public Attack ()
	{
		for(int i = 0; i < 50; i++)
		{
			double dmg = Math.random()*damage;
			double crit = 0d;
			if (Math.random() <= critical)
			{
				crit = (dmg*2.3);
			}
			
			System.out.println((int)dmg + ", " + (int)crit + " :: " + (int)(dmg+crit));
		}
	}
	
	public static void main (String[] args)
	{
		new Attack();
	}
}
