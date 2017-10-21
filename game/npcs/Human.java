package npcs;

import engine.Avatar;
import engine.PathFinder;
import engine.Scroller;
import game.Npc;

public class Human extends Npc
{
	public Human (String name, int x, int y, int health, double strength, double damage, double critical, double defence, String setName, Avatar avatar, PathFinder pathfinder, Scroller scroller)
	{
		super (name, x, y, health, strength, damage, critical, defence, setName, avatar, pathfinder, scroller);
	}
}
