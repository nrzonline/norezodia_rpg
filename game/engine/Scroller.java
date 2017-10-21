package engine;

import game.Player;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Scroller 
{
	private static final long EXPIRE_TIME = 2000;
	private static final int MESSAGE_DISTANCE = 30;
	
	private Player player;
	private ArrayList<Object> toScroll;
	
	public Scroller()
	{
		toScroll = new ArrayList<Object>();
	}
	public void update (Player player, double passedTime)
	{
		this.player = player;
		
		if (!toScroll.isEmpty())
		{
			for (int i=0; i<toScroll.size(); i++)
			{
				Object obj = toScroll.get(i);
				if(obj instanceof Damage){ updateDamage(obj, passedTime, i); }
				else if(obj instanceof Experience){ }
				else { Failure.add("Undefined object put into Scroller!"); }
			}
		}
	}
	public void draw(Graphics2D g2d)
	{
		for (Object obj : toScroll)
		{
			if(obj instanceof Damage){ ((Damage) obj).draw(g2d); }
			if(obj instanceof Experience){}
		}
	}
	
	private void updateDamage(Object obj, double passedTime, int index)
	{
		Damage damage = (Damage) obj;
		
		if(Utilities.now() > damage.getTime()+EXPIRE_TIME)
		{
			toScroll.remove(obj);
		}
		else
		{
			double frameDistance = ((passedTime/EXPIRE_TIME)*MESSAGE_DISTANCE)*1000;
			damage.setDistance(damage.getDistance() + frameDistance);
			damage.updateDisplayLocation(player);
		}
	}
	
	public void add(Object object)
	{
		toScroll.add(object);
	}
}
