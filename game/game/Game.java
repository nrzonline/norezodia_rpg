package game;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;

import settings.Config;

import base._Borders;
import base._Chat;
import base._Player;
import base._Symbols;

import engine.Avatar;
import engine.Experience;
import engine.Failure;
import engine.Framework;
import engine.Input;
import engine.Mouse;
import engine.PathFinder;
import engine.Scroller;
import engine.Texture;

public class Game
{
	private static final boolean DEVELOPMENT_MODE = Config.DEVELOPMENT_MODE;
	
	private _Player _player;
	private _Symbols _symbols;
	private _Chat _chat;
	
	private Scroller scroller;
	private Mouse mouse;
	private Actions optionPopup;
	private Player player;
	private Avatar avatar;
	private NpcList npcList;
	private World world;
	private Menu menu;
	private Interaction history;
	private PathFinder pathfinder;
	private Dead dead;
	
	private static enum GameState
	{
		LOAD_MAP,
		ALIVE,
		DEAD
	}
	private static GameState gameState = GameState.ALIVE;
	
	public Game (Texture texture)
	{
		_player = new _Player();
		_symbols = new _Symbols();
		_chat = new _Chat();
		
		new Experience(); // static
		scroller = new Scroller();
		mouse = new Mouse ();
		optionPopup = new Actions ();
		player = new Player (scroller);
		pathfinder = new PathFinder ();
		avatar = new Avatar ();
		npcList = new NpcList (avatar, pathfinder, scroller);
		world = new World (texture, player, pathfinder);
		menu = new Menu ();
		history = new Interaction ();
		dead = new Dead (player);
		
		pathfinder.setWorld (world);
	}
	public void load ()
	{
		_player.load();
		_symbols.load();
		_chat.load();
		
		mouse.load ();
		avatar.load ();
		npcList.load ();
		player.load ();
		world.load ();
		menu.load ();
	}	
	public void input (Input input)
	{
		mouse.input(input);
		
		if (input.keyDownOnce(KeyEvent.VK_F1)) 
		{ 
			if (DEVELOPMENT_MODE)
			{
				Framework.changeState("EDITOR");
			}
		}
		
		switch (gameState)
		{
			case LOAD_MAP:
				break;
			case ALIVE:
				player.input (input);
				optionPopup.input (input);
				world.input (input);
				npcList.input(input);
				break;
			case DEAD:
				dead.input (input);
				break;
		}
	}
	public void update (double passedTime)
	{
		mouse.update();
		npcList.update (player, passedTime);
		scroller.update(player, passedTime);
		world.update ();
		
		switch (gameState)
		{
			case LOAD_MAP:
				world.load();
				changeState("ALIVE");
				break;
			case ALIVE:
				player.update (passedTime);
				if (!player.getData().isAlive()) 
				{
					changeState ("DEAD");
				}
				break;
			case DEAD:
				dead.update ();
				break;
		}
	}
	public void draw (Graphics2D g2d)
	{
		try
		{
			world.drawLayer (g2d, 1);
			world.drawLayer (g2d, 2);
			world.drawLayer (g2d, 3);
			world.draw (g2d);
			npcList.draw(g2d, false);
			player.draw (g2d);
			npcList.draw(g2d, true);
			world.drawLayer (g2d, 4);
			world.drawLayer (g2d, 5);
			scroller.draw(g2d);
			menu.draw (g2d);
			history.draw (g2d);
			player.drawInterface(g2d);
			
			_Borders.drawInner (g2d);
			
			switch (gameState)
			{
				case LOAD_MAP:
					break;
				case ALIVE:
					optionPopup.draw (g2d);
					break;
				case DEAD:
					dead.draw (g2d);
					break;
			}
			
			mouse.draw (g2d);
		}
		catch (Exception ex)
		{
			Failure.add("Error during drawing components!", ex);
		}
	}
	public void drawData (Graphics2D g2d)
	{
		player.drawData(g2d);
		world.drawData(g2d);
	}

	public static void changeState (String state)
	{
		switch (state)
		{
			case "LOAD_MAP":
				gameState = GameState.LOAD_MAP;
				break;
			case "ALIVE":
				gameState = GameState.ALIVE;
				break;
			case "DEAD":
				gameState = GameState.DEAD;
				break;	
			default:
				Failure.add("gameState " + state + " does not exist!");
		}
	}
}
