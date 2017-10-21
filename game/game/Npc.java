package game;

import java.awt.Graphics2D;
import java.util.ArrayList;

import base._Symbols;

import engine.Animation;
import engine.Attack;
import engine.Avatar;
import engine.Combat;
import engine.Coord;
import engine.Failure;
import engine.Grid;
import engine.Input;
import engine.Log;
import engine.Path;
import engine.PathFinder;
import engine.PathFollower;
import engine.Scroller;
import engine.Utilities;

import settings.Colors;
import settings.Config;
import settings.Fonts;
import static settings.Constants.*;

public class Npc implements Comparable<Npc>
{
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private static final long HIDE_BODY_DELAY = Config.HIDE_BODY_DELAY;
	
	private PathFinder pathFinder;
	private PathFollower pathFollower;
	private Scroller scroller;
	private Combat combat;
	
	private Animation currentAvatar;
	private Animation[] avatarSet;
	private PlayerData playerData;
	private Player player;
	
	private String name;
	
	private double worldX;
	private double worldY;
	private double displayX;
	private double displayY;
	private ArrayList<Grid> walkableGrids = new ArrayList<Grid>();
	private Grid previousTargetGrid = null;	
	private long lastMoveTime = Utilities.now ();
	private long lastPathTime = Utilities.now ();
	
	private double health = 100;
	private double maxHealth = 100;
	private boolean alive = true;
	private boolean respawning;
	private int spawnDelay = 5;
	private long respawnTime = Utilities.now();
	private long hideBodyTime;
	
	private double damage = 1.0d;
	private double strength = 1.0d;
	private double critical = 0.1d;
	private double defence = 0.1d;
	private Object target = null;
	private boolean agressive = false;
	private double attackSpeed = 1.5d;
	private long lastAttackTime = Utilities.now ();
	private long attackDistance = 35;
	
	private boolean hover = false;
	private boolean playersTarget = false;
	
	public Npc (String name, double worldX, double worldY, int health, double strength, double damage, double critical, double defence, String setName, Avatar avatar, PathFinder pathFinder, Scroller scroller)
	{
		this.pathFinder = pathFinder;
		this.pathFollower = new PathFollower ();
		this.scroller = scroller;
		this.combat = new Combat(this);
		this.avatarSet = avatar.createAvatarSet(setName);
		this.currentAvatar = avatarSet[2];
		
		this.name = name;
		this.worldX = worldX*TILE_SIZE;
		this.worldY = worldY*TILE_SIZE;
		this.health = this.maxHealth = health;
		this.strength = strength;
		this.damage = damage;
		this.critical = critical;
		this.defence = defence;
		
		// TODO: Remove, temporarily.
		walkableGrids.add(new Grid (14, 19));
		walkableGrids.add(new Grid (11, 22));
		walkableGrids.add(new Grid (17, 22));
		walkableGrids.add(new Grid (14, 29));
		walkableGrids.add(new Grid (14, 22));
	}
	public void input (Input input)
	{
		hover = (Utilities.isWithin(input.getMouseX(), (int)displayX, (int)displayX+32) && Utilities.isWithin(input.getMouseY(), (int)displayY, (int)displayY+32)) ? true : false;
	}
	public void update (Player player, double passedTime)
	{
		if (isAlive())
		{	
			this.player = player;
			playerData = player.getData();
			
			findAndFollowTarget (player);
			moveAround (false);
			pathFollower.follow(this, passedTime);
			attackTarget (target);
			
			combat.update();
		}
		else
		{
			respawn ();
		}
		
		updateScreenLocation ();
	}
	public void draw (Graphics2D g2d, boolean topLayer)
	{
		drawNpc(g2d, topLayer);
		drawHover(g2d);
	}
	private void drawNpc (Graphics2D g2d, boolean topLayer)
	{
		if (isAlive() || !isAlive() && hideBodyTime > Utilities.now() || respawning)
		{
			if (playerData != null) // Prevents possible error...
			{
				if (!topLayer && playerData.getWorldY() >= worldY)
				{	
					currentAvatar.draw(g2d);
				}
				if (topLayer && playerData.getWorldY() < worldY)
				{
					currentAvatar.draw(g2d);
				}			
				
				if (topLayer)
				{
					drawIcons(g2d);
				}
			}
		}
	}
	public void drawIcons (Graphics2D g2d)
	{
		if (hasTarget())
		{
			_Symbols.redMarker(g2d,  (int)displayX+21, (int)displayY-15);
		}
	}
	private void drawHover (Graphics2D g2d)
	{
		if (hover && isAlive() || hover && !isAlive() && hideBodyTime > Utilities.now() || playersTarget)
		{
			Fonts.drawString(g2d, (int)health + " / " + (int)maxHealth, Colors.LIGHT_GRAY, Fonts.HEALTH, (int)displayX, (int)displayY, true);
			Fonts.drawCenterString(g2d, this.name, Colors.ORANGE, Fonts.CHAT, (int)displayX-80, (int)displayY-15, 200, true);
		}
	}
	
	private void moveAround (boolean justLeftCombat)
	{
		if (!hasTarget() && Utilities.passedTime(lastMoveTime+2000) && lastMoveTime != 0 || justLeftCombat)
		{
			Grid start = new Grid (worldX, worldY);
			Grid target = walkableGrids.get((int)(Math.random() * walkableGrids.size()));
			Path path = null;
			
			// Nieuwe pad zoeken als er geen pad beschikbaar is of als we net een gevech hebben verlaten.
			if (!pathFollower.hasPath() || justLeftCombat)
			{
				path = pathFinder.find(start.getX(), start.getY(), target.getX(), target.getY(), false);
				pathFollower.setPath(path);
			}
			
			// Hebben we een nieuwe path, dan gaan we die volgen en zetten we lastMoveTime op 0 om te voorkomen dat
			// we tijdens het lopen een nieuwe path gaan zoeken. Anders geven we de huidige tijd mee, zodat we straks
			// kunnen controleren of de delay voor het zoeken naar een nieuwe path al is verstreken.
			lastMoveTime = (path != null) ? 0 : System.currentTimeMillis();
		}
	}
	private void findAndFollowTarget (Player player)
	{
		double xDistance = Utilities.getDifference(worldX, playerData.getWorldX());
		double yDistance = Utilities.getDifference(worldY, playerData.getWorldY());
		
		// Target is binnen berijk.
		if (playerData.isAlive() && agressive && xDistance+yDistance < 4*TILE_SIZE && Utilities.passedTime(lastPathTime+250))
		{
			
			int targetGridX = playerData.getGrid().getX();
			int targetGridY = playerData.getGrid().getY();
			double targetWorldX = playerData.getWorldX();
			double targetWorldY = playerData.getWorldY();
			Grid npcGrid = new Grid (worldX, worldY);
			Grid sideGrid = null;
			Path sidePath = null;
			
			if (previousTargetGrid != null)
			{
				if (previousTargetGrid.getX() != targetGridX || previousTargetGrid.getY() != targetGridY)
				{
					// Nu gaan we een nieuwe sideGrid ophalen, aangezien de oude target locatie niet hetzelfde is als de nieuwe locatie.
					sideGrid = findTargetSideGrid (new Grid(worldX, worldY), new Grid(targetWorldX, targetWorldY));
					previousTargetGrid = new Grid (targetGridX, targetGridY);
				}
			}
			else
			{
				// Nu gaan we een nieuwe sideGrid ophalen, er nog geen oude target locatie is.
				sideGrid = findTargetSideGrid (new Grid(worldX, worldY), new Grid(targetWorldX, targetWorldY));
				previousTargetGrid = new Grid(targetGridX, targetGridY);
			}
			
			if (sideGrid != null)
			{
				sidePath = pathFinder.find(npcGrid.getX(), npcGrid.getY(), sideGrid.getX(), sideGrid.getY(), false);
				if (sidePath != null)
				{
					pathFollower.setNextPath(sidePath);
					target = player;
					setPathTime();
				}
			}
			
			// Npc reached player's sideGrid, look towards player.
			if (pathFollower.getPath() == null)
			{
				if (worldX < playerData.getWorldX())
					changeAvatar (RIGHT);
				else if (worldX > playerData.getWorldX())
					changeAvatar (LEFT);
				else if (worldY < playerData.getWorldY())
					changeAvatar (DOWN);
				else if (worldY > playerData.getWorldY())
					changeAvatar(UP);
			}
		}
		// We hadden de target, maar is nu buiten bereik geraakt of dood.
		else if (hasTarget() && playerData.isAlive() && xDistance+yDistance > 4*TILE_SIZE || hasTarget() && !playerData.isAlive())
		{
			resetTarget ();
		}
	}
	private Grid findTargetSideGrid (Grid npcGrid, Grid targetGrid)
	{
		int npcGridX = npcGrid.getX();
		int npcGridY = npcGrid.getY();
		int targetGridX = targetGrid.getX();
		int targetGridY = targetGrid.getY();
		
		ArrayList<Grid> targetSideGrids = new ArrayList<Grid>();
		
		boolean up = false;
		boolean right = false;
		boolean down = false;
		boolean left = false;
		
		// Controleren of de grids naast de speler niet geblokkeerd zijn.
		boolean upBlocked = pathFinder.isBlocked (targetGridX, targetGridY-1);
		boolean rightBlocked = pathFinder.isBlocked (targetGridX+1, targetGridY);
		boolean downBlocked = pathFinder.isBlocked (targetGridX, targetGridY+1);
		boolean leftBlocked = pathFinder.isBlocked (targetGridX-1, targetGridY);
		
		// We gaan kijken welke mogelijkheden de beste zijn vanuit welke hoek de target staat.
		if (npcGridX == targetGridX && npcGridY < targetGridY) { up=true; }
		else if (npcGridX > targetGridX && npcGridY < targetGridY) { up=true; right=true; }
		else if (npcGridY == targetGridY && npcGridX > targetGridX) { right=true; }
		else if (npcGridX > targetGridX && npcGridY > targetGridY) { right=true; down=true; }
		else if (npcGridX == targetGridX && npcGridY > targetGridY) { down=true; }
		else if (npcGridY > targetGridY && npcGridX < targetGridX) { down=true; left=true; }
		else if (npcGridY == targetGridY && npcGridX < targetGridX) { left=true; }
		else if (npcGridX < targetGridX && npcGridY < targetGridY) { left=true; up=true; }
		else if (npcGridX == targetGridX && npcGridY == targetGridY) { up=true; right=true; down=true; left=true; }
		
		// We gaan de voorkeurs mogelijkheden in een array stoppen, zodat we er random één uit kunnen pakken.
		if (up && !upBlocked) { targetSideGrids.add(new Grid (targetGridX, targetGridY-1)); }
		if (right && !rightBlocked) { targetSideGrids.add(new Grid (targetGridX+1, targetGridY)); }
		if (down && !downBlocked) { targetSideGrids.add(new Grid (targetGridX, targetGridY+1)); }
		if (left && !leftBlocked) { targetSideGrids.add(new Grid (targetGridX-1, targetGridY)); }
		
		// We gaan controleren of er een voorkeurs mogelijkheid beschikbaar is, en sturen die terug.
		if (targetSideGrids.size() > 0)
		{
			return targetSideGrids.get((int)(Math.random()*targetSideGrids.size()));
		}
		// Er is geen voorkeurs mogelijkheid beschikbaar. We gaan alle mogelijke side grids in een ArrayList plaatsen,
		// en daar er één willekeurig uit halen.
		else
		{
			if (!upBlocked) targetSideGrids.add(new Grid (targetGrid.getX(), targetGrid.getY()-1));
			if (!rightBlocked) targetSideGrids.add(new Grid (targetGrid.getX()+1, targetGrid.getY()));
			if (!downBlocked) targetSideGrids.add(new Grid (targetGrid.getX(), targetGrid.getY()+1));
			if (!leftBlocked) targetSideGrids.add(new Grid (targetGrid.getX()-1, targetGrid.getY()));
			
			return (targetSideGrids.size() > 0) ? targetSideGrids.get((int)(Math.random()*targetSideGrids.size())) : null;
		}
	}
	private void updateScreenLocation ()
	{
		double playerX = playerData.getWorldX();
		double playerY = playerData.getWorldY();
		double playerDisplayX = playerData.getDisplayX();
		double playerDisplayY = playerData.getDisplayY();
		
		if (playerDisplayX < 512) { displayX = worldX; }
		else if (playerDisplayX == 512) { displayX =  (int) ((worldX <= playerX) ? 512 - (playerX-worldX) : 512 + (worldX-playerX)); }
		else { displayX = 0; Log.out ("build Npc.updateScreenLocation();"); }
		
		if (playerDisplayY < 320) { displayY = worldY; }
		else if (playerDisplayY == 320) { displayY =  (int) ((worldY <= playerY) ? 320 - (playerY-worldY) : 320 + (worldY-playerY)); }
		else { displayY = 0; Failure.add ("build Npc.updateScreenLocation();"); }
		
		currentAvatar.setLocation((int)displayX+BORDER_OFFSET, (int)displayY+BORDER_OFFSET);
	}
	public void changeAvatar (int id)
	{
		switch (id)
		{
			case UP: currentAvatar = avatarSet[UP]; break;
			case RIGHT: currentAvatar = avatarSet[RIGHT];	break;
			case DOWN: currentAvatar = avatarSet[DOWN]; break;
			case LEFT: currentAvatar = avatarSet[LEFT]; break;
			case DIE: currentAvatar = avatarSet[DIE]; break;
			case RESPAWN: currentAvatar = avatarSet[RESPAWN]; break;
			default: Failure.add ("Failed to change avatar ID: " + id); break;
		}
		currentAvatar.setLocation((int)displayX+BORDER_OFFSET, (int)displayY+BORDER_OFFSET);
	}
	@Override
	public int compareTo(Npc toCompare) 
	{
		double compareWorldY = ((Npc) toCompare).getWorldY();
		return (int) (this.worldY - compareWorldY);
	}
	
	private void resetTarget ()
	{
		previousTargetGrid = null;
		target = null;
		setPlayersTarget(false);
		moveAround (true);
	}
	private void attackTarget (Object target)
	{
		boolean delayPassed = (Utilities.passedTime(lastAttackTime + (long)(attackSpeed * 1000))) ? true : false;
		
		if (hasTarget() && delayPassed)
		{
			if (target instanceof Player) 
			{
				double xDistance = Utilities.getDifference(worldX, playerData.getWorldX());
				double yDistance = Utilities.getDifference(worldY, playerData.getWorldY());
				
				if (playerData.isAlive() && xDistance+yDistance < attackDistance) 
				{
					new Attack (this, target);
					lastAttackTime = Utilities.now ();
				}
			}
			else if (target instanceof Npc) 
			{
				double xDistance = Utilities.getDifference(worldX, ((Npc)target).getWorldX());
				double yDistance = Utilities.getDifference(worldY, ((Npc)target).getWorldY());
				
				if(((Npc)target).isAlive() && xDistance+yDistance < attackDistance)
				{
					new Attack (this, target);
					lastAttackTime = Utilities.now ();
				}
			}
			else
			{
				Failure.add("Undefined object type in npc.java");				
			}
		}
	}
	public void receiveAttack (Attack attack)
	{		
		if (!hasTarget()) 
		{
			setTarget (attack.getAttacker());
		}
		
		if (health-attack.getFinalDamage() > 0)
		{
			health = health - attack.getFinalDamage();
		}
		else
		{
			die ();
			
			attack.rewardExp(1000);
		}
		
		scroller.add(attack.createReport(false));
	}
	private void die ()
	{
		setPlayersTarget(false);
		setAlive (false);
		changeAvatar(DIE);
		setTarget(null);
		setHealth(0);
		setRespawnTime();
		setHideBodyTime();
	}
	private void respawn()
	{
		if(!isAlive() && respawnTime < Utilities.now() && !respawning)
		{
			previousTargetGrid = null;
			avatarSet[DIE].restart();
			changeAvatar(RESPAWN);
			setHealth(maxHealth);
			respawning = true;
			playersTarget = false;
		}
		else if (!isAlive() && respawning && avatarSet[RESPAWN].isReady())
		{
			avatarSet[RESPAWN].restart();
			changeAvatar(DOWN);
			setAlive(true);
			respawning = false;
		}
	}
	
	public void setWorldX (double worldX) { this.worldX = worldX; }
	public void setWorldY (double worldY) { this.worldY = worldY; }
	public void setAlive(boolean alive) { this.alive = alive; }
	private void setRespawnTime() { this.respawnTime = Utilities.now() + (spawnDelay*1000); }
	public void setHealth(double health) { this.health = health; }
	private void setHideBodyTime() { this.hideBodyTime = Utilities.now() + HIDE_BODY_DELAY; }
	public void setAgressive (boolean agressive) { this.agressive = agressive; }
	public void setMoveTime () { lastMoveTime = System.currentTimeMillis(); }
	public void setPathTime () { lastPathTime = System.currentTimeMillis(); }
	public void setTarget(Object target) { this.target = target; }
	public void setPlayersTarget(boolean playersTarget){ this.playersTarget = playersTarget; }
	
	public Combat combat() { return combat; }
	public double getWorldX () { return worldX; }
	public double getWorldY () { return worldY; }
	public Coord getCoord() { return new Coord(worldX, worldY); }
	public Coord getDisplayCoord(){ return new Coord(displayX, displayY); }
	public double getHealth() { return health; }
	public double getDamage() { return damage; }
	public double getCritical() { return critical; }
	public double getDefence() { return defence; }
	public double getStrength(){ return strength; }
	public double getMaxHealth() { return maxHealth; }
	public double getAttackSpeed(){ return attackSpeed; }
	
	public boolean hasTarget () { return (target != null) ? true : false; }
	public boolean isAlive() { return alive; }
}
