package game;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import base._Player;

import settings.Colors;
import settings.Config;
import settings.Constants;
import settings.Fonts;

import engine.Animation;
import engine.Attack;
import engine.Combat;
import engine.Coord;
import engine.Experience;
import engine.Failure;
import engine.Grid;
import engine.Input;
import engine.Loading;
import engine.Log;
import engine.Message;
import engine.Path;
import engine.PathFollower;
import engine.Scroller;
import engine.Utilities;

public class Player
{
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private static final int DISPLAY_WIDTH = Config.DISPLAY_WIDTH;
	private static final int DISPLAY_HEIGHT = Config.DISPLAY_HEIGHT;
	private static final int HALF_WIDTH = DISPLAY_WIDTH/2;
	private static final int HALF_HEIGHT = DISPLAY_HEIGHT/2;
	
	private PlayerData data;
	private PathFollower pathFollower;
	private World world;
	private Scroller scroller;
	private BufferedImage playerSheet;
	private Animation playerAnimation, downHold, leftHold, upHold, rightHold, death;
	private Combat combat;
	
	private final double speed = 63.0d;
	private long lastAttackTime = Utilities.now();
	private Object focus = null;
	private Object target = null;
	
	public Player (Scroller scroller)
	{
		data = new PlayerData ();
		combat = new Combat(this);
		pathFollower = new PathFollower ();
		this.scroller = scroller;
	}
	public void load ()
	{
		Loading.changeState ("PLAYER_DATA");
		data.loadProfile();
		
		Loading.changeState ("PLAYER");
		try
		{
			playerSheet = ImageIO.read(this.getClass().getResource("/player/player.png"));
			createPlayerAnimations ();
		}
		catch (Exception e)
		{
			Failure.add("Failed to load the player sheet.", e);
		}
	}
	public void input (Input input)
	{
		
	}
	public void update (double passedTime)
	{		
		combat.update();
		updateDisplayLocation ();
		pathFollower.follow(this, passedTime);
		attackTarget ();
		data.save();
	}
	public void draw (Graphics2D g2d)
	{
		playerAnimation.draw(g2d);
	}
	
	private void createPlayerAnimations ()
	{
		try
		{
			int displayX = data.getDisplayX () + BORDER_OFFSET;
			int displayY = data.getDisplayY () + BORDER_OFFSET;
			
			downHold = new Animation(playerSheet, 32, 32, 0, 2, 350, true, displayX, displayY);
			leftHold = new Animation(playerSheet, 32, 32, 1, 2, 350, true, displayX, displayY);
			upHold = new Animation(playerSheet, 32, 32, 2, 2, 350, true, displayX, displayY);
			rightHold = new Animation(playerSheet, 32, 32, 3, 2, 350, true, displayX, displayY);
			death = new Animation(playerSheet, 32, 32, 4, 4, 100, false, displayX, displayY);
			
			playerAnimation = downHold;
		}
		catch (Exception e)
		{
			Failure.add ("Failed to create player animations", e);
		}
	}
	private void updateDisplayLocation ()
	{		
		int worldWidth = (world.getMap().getGridWidth()-1) * TILE_SIZE;
		int worldHeight = (world.getMap().getGridHeight()-1) * TILE_SIZE;
		
		int worldX = (int) data.getWorldX();
		int worldY = (int) data.getWorldY();
		int displayX = data.getDisplayX();
		int displayY = data.getDisplayY();
		
		if (displayX >= 0 && worldX <= HALF_WIDTH*TILE_SIZE)
		{
			data.setDisplayX (worldX);
		}
		else if (displayX <= worldWidth && worldX >= worldWidth-(HALF_WIDTH*TILE_SIZE))
		{
			data.setDisplayX ((DISPLAY_WIDTH-1)*TILE_SIZE - (worldWidth-worldX));
		}
		else
		{
			data.setDisplayX (HALF_WIDTH*TILE_SIZE);
		}
		
		if (displayY >= 0 && worldY <= HALF_HEIGHT*TILE_SIZE)
		{
			data.setDisplayY (worldY);
		}
		else if (displayY <= worldHeight && worldY >= worldHeight-(HALF_HEIGHT*TILE_SIZE))
		{
			data.setDisplayY ((DISPLAY_HEIGHT-1)*TILE_SIZE - (worldHeight-worldY));
		}
		else
		{
			data.setDisplayY (HALF_HEIGHT*TILE_SIZE);
		}
	}
	public void changeAvatar (int id)
	{
		int x = data.getDisplayX()+BORDER_OFFSET;
		int y = data.getDisplayY()+BORDER_OFFSET;
		
		switch (id)
		{
			case Constants.UP: playerAnimation = upHold; break;
			case Constants.RIGHT: playerAnimation = rightHold; break;
			case Constants.DOWN: playerAnimation = downHold; break;
			case Constants.LEFT: playerAnimation = leftHold; break;
			case Constants.DIE: playerAnimation = death; break;
		}
		
		playerAnimation.setLocation(x, y);
	}
	
	private void resetTarget()
	{
		target = null;
	}
	public void attackTarget ()
	{
		boolean delayPassed = (Utilities.passedTime(lastAttackTime + (long)(data.getAttackSpeed() * 1000))) ? true : false;
		
		if (isTargetAlive() && delayPassed)
		{
			if(target instanceof Npc)
			{
				Npc npc = (Npc) target;
				double xDistance = Utilities.getDifference(data.getWorldX(), npc.getWorldX());
				double yDistance = Utilities.getDifference(data.getWorldY(), npc.getWorldY());
				
				if(xDistance+yDistance < data.getAtackDistance())
				{
					new Attack(this, npc);
					
					npc.setPlayersTarget(true);
					lastAttackTime = System.currentTimeMillis();
				}
			}
			else
			{
				Failure.add("Player attacking unknown object -> build!");
			}
		}
		else
		{
			resetTarget();
		}
	}
	public void receiveAttack (Attack attack)
	{
		if (!hasTarget()) { setTarget (attack.getAttacker()); }
		
		if ((int)(data.getHealth()-attack.getFinalDamage()) > 0)
		{
			data.setHealth(data.getHealth () - attack.getFinalDamage());
		}
		else
		{
			die ();
		}
		
		scroller.add(attack.createReport(true));
	}
	public void receiveExperience(int exp)
	{
		while(Experience.getLevelExperience(data.getLevel()+1) <= data.getExperience() + exp)
		{
			levelUp();
		}
		data.addExperience(exp);
	}
	
	private void die ()
	{
		data.setHealth (0);
		data.setAlive(false);
		target = null;
		death.restart();
		changeAvatar (Constants.DIE);
		pathFollower.unsetPath();
		
		Interaction.addMessage(new Message(0, Colors.RED, "Oh crap, you just got killed. You will be resurrected by the Town's Council..."));
	}
	public void revive ()
	{
		data.setHealth(data.getMaxHealth());
		data.setWorldX(448);
		data.setWorldY(256);
		data.setDisplayX(448);
		data.setDisplayY(256);
		changeAvatar (Constants.DOWN);
		data.setAlive (true);
		Game.changeState("ALIVE");
	}
	private void levelUp()
	{
		data.addLevel();
		Interaction.addMessage(new Message(0, Colors.CYAN, "Congratulations, you have gained a level! You are now level " + data.getLevel() + "."));
	}
	
	public void drawInterface (Graphics2D g2d) 
	{
		Fonts.drawString(g2d, (int)getData().getHealth() + " / " + (int)getData().getMaxHealth(), Colors.GREEN, Fonts.LOADING, 15, 25, true);
		_Player.xpBar(g2d, Experience.getLevelExperience(data.getLevel()), Experience.getLevelExperience(data.getLevel()+1), data.getExperience());
		Fonts.drawString(g2d, "LVL: " + data.getLevel(), Colors.ORANGE, Fonts.HEALTH, 14, 650, true);
	}
	public void drawData (Graphics2D g2d)
	{
		int x = 850;
		int y = 160;
		
		g2d.setColor(Colors.DRAW_DATA);
		g2d.fillRect(x, y, 200, 120);
		
		Fonts.drawString(g2d, "player data", Colors.ORANGE_DARK, Fonts.LOADING, x+10, y+20, true);
		Fonts.drawString
		(
				g2d, 
				"W: " + (int) data.getWorldX() + ", " + (int) data.getWorldY() + "  /  " + (int) data.getWorldX()/TILE_SIZE + ", " + (int) data.getWorldY()/TILE_SIZE, 
				Colors.WHITE, Fonts.LOADING, x+10, y+40, true
		);
		Fonts.drawString
		(
				g2d, 
				"D:  "+ data.getDisplayX() + ", " + data.getDisplayY() + "  /  " + data.getDisplayX()/TILE_SIZE + ", " + data.getDisplayY()/TILE_SIZE, 
				Colors.WHITE, Fonts.LOADING, x+10, y+60, true
		);
		
		Fonts.drawString
		(
				g2d, 
				"F:  "+ (hasFocus() ? "yes" : "no"),
				Colors.WHITE, Fonts.LOADING, x+10, y+90, true
		);
		Fonts.drawString
		(
				g2d, 
				"T:  "+ (hasTarget() ? "yes" : "no"),
				Colors.WHITE, Fonts.LOADING, x+10, y+110, true
		);
	}
	
	public void setWorld (World world) { this.world = world; }
	private void setTarget (Object target){ this.target = target; }
	public void setPath (Path path){ if (path != null){ pathFollower.setPath (path); } }
	
	private boolean isTargetAlive()
	{
		if(target != null)
		{
			if(target instanceof Player){ return ((Player) target).getData().isAlive();}
			else if(target instanceof Npc){ return ((Npc) target).isAlive(); }
			else { Failure.add("Invalid object type in player.java"); return false; }
		}
		else
		{
			return false;
		}
	}
	
	public PlayerData getData (){ return data; }
	public Combat combat() { return combat; }
	public Grid getGrid () { return new Grid (data.getWorldX(), data.getWorldY()); }
	public Coord getCoord() { return new Coord(data.getWorldX(), data.getWorldY()); }
	public Coord getDisplayCoord(){ return new Coord(data.getDisplayX(), data.getDisplayY()); }
	public Path getPath (){ return pathFollower.getPath();	}
	public int getPathStep (){ return pathFollower.getPath().getStepID(); }
	public double getSpeed () { return speed; }
	
	public boolean hasFocus() { return (focus != null) ? true : false; }
	public boolean hasTarget () { return (target != null) ? true : false; }
}
