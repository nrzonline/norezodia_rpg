package engine;

import game.Npc;
import game.Player;

import java.awt.Color;
import java.awt.Graphics2D;

import settings.Colors;
import settings.Fonts;

public class Damage 
{
	private int damage;
	private Object victem;
	private boolean toPlayer;
	
	private double distance;
	private Coord coord;
	private double displayX;
	private double displayY;
	
	private long time = Utilities.now();
	
	public Damage()
	{
		
	}
	public Damage(int damage)
	{
		this.damage = damage;
	}
	public void draw(Graphics2D g2d)
	{
		Color color = Colors.WHITE;
		if(toPlayer){ color = Colors.ORANGE; }
		
		Fonts.drawString(g2d, ""+damage, color, Fonts.DAMAGE, (int)displayX, (int)displayY, true);
	}
	public void update()
	{
		
	}
	
	public void updateDisplayLocation(Player player)
	{
		prepareCoordinates(victem);
	}
	private void prepareCoordinates (Object object)
	{
		if (object instanceof Npc){ coord = ((Npc) object).getDisplayCoord();	}
		else if(object instanceof Player){ coord = ((Player) object).getDisplayCoord();}
		else
		{
			Failure.add("Invalid object type in Damage.java");
		}
		
		if(coord != null)
		{
			displayX = coord.getX()+16;
			displayY = coord.getY()-distance-8;
		}
	}
	
	public void setDistance(double distance){ this.distance = distance; }
	public void setDamage(int damage){ this.damage = damage; }
	public void setVictem(Object victem){ this.victem = victem; }
	public void setToPlayer(boolean toPlayer){ this.toPlayer = toPlayer; }
	
	public long getTime() { return time; }
	public double getDistance() { return distance; }
}
