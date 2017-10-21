package engine;

import java.util.ArrayList;

import game.Npc;
import game.Player;

public class Combat 
{
	private Player player;
	private Npc npc;
	
	private ArrayList<Attack> attacks;
	
	private boolean isPlayer;
	private boolean isNpc;
	
	public Combat (Object entity)
	{
		attacks = new ArrayList<Attack>();
		
		if(entity instanceof Player)
		{
			isPlayer = true;
			player = (Player)entity;
		}
		else
		{
			isNpc = true;
			npc = (Npc)entity;
		}
	}
	public void update ()
	{
		for(int i=0; i<attacks.size(); i++)
		{
			Attack attack = attacks.get(i);
			
			if(isPlayer) { player.receiveAttack(attack);}
			else if (isNpc) { npc.receiveAttack(attack); }
			
			attacks.remove(i);
		}
	}
	
	public void addAttack(Attack attack)
	{
		attacks.add(attack);
	}
}
