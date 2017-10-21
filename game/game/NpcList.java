package game;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;

import engine.Avatar;
import engine.Input;
import engine.Loading;
import engine.PathFinder;
import engine.Scroller;

import npcs.Human;

public class NpcList
{
	private Avatar avatar;
	private PathFinder pathfinder;
	private Scroller scroller;
	
	private ArrayList<Npc> npcs = new ArrayList<Npc>();
	
	public NpcList (Avatar avatar, PathFinder pathfinder, Scroller scroller)
	{
		this.avatar = avatar;
		this.pathfinder = pathfinder;
		this.scroller = scroller;
		add();
	}
	
	public void input (Input input)
	{
		for (int i = 0; i < npcs.size (); i++)
		{
			Object npc = npcs.get(i);
			
			if (npc instanceof Human) ((Human)npc).input(input);
		}
	}
	public void update (Player player, double passedTime)
	{
		// Sort the Npc's on there worldY location, for drawing them in the correct order.
		Collections.sort(npcs);
		
		for (int i = 0; i < npcs.size (); i++)
		{
			Object npc = npcs.get(i);
			
			if (npc instanceof Human) ((Human)npc).update(player, passedTime);
		}
	}
	public void draw (Graphics2D g2d, boolean topLayer)
	{
		for (int i = 0; i < npcs.size (); i++)
		{
			Object npc = npcs.get(i);
			
			if (npc instanceof Human) ((Human)npc).draw(g2d, topLayer);
		}
	}
	
	public void add ()
	{
		
	}
	public void load ()
	{
		Loading.changeState("NPC_LIST");
		//											name					x		y		hp		str		dmg	crit	def
		Human 	human1 = new Human ("Town's Council", 14, 	22, 	120, 	10,	4,		0.3, 	5, 		"human1", avatar, pathfinder, scroller);
		Human 	human2 = new Human ("Drunken Ed", 		18, 	24, 	30,	6,		20,		0.3,	3,		"human2", avatar, pathfinder, scroller);
		Human 	human3 = new Human ("Merlin's Spirit", 	16, 	21, 	100, 	8,		6,		0.5,	6,		"human3", avatar, pathfinder, scroller);
				human1.setAgressive(false);
				human2.setAgressive(true);
				human3.setAgressive(false);
		npcs.add(human1);
		npcs.add(human2);
		npcs.add(human3);
	}
	public void save ()
	{
		
	}
}
