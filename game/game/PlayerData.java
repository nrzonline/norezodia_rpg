package game;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import settings.Config;

import engine.Failure;
import engine.Grid;

public class PlayerData implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private static final boolean RESET_PLAYER_DATA = Config.RESET_PLAYER_DATA;
	private static final long SAVE_DELAY = Config.SAVE_DELAY;
	
	private long lastSave = System.currentTimeMillis();
	
	private int mapID = 0;
	private int displayX = 14*32;
	private int displayY = 10*32;
	private double worldX = 14*32;
	private double worldY = 22*32;
	
	private int level = 1;
	private int experience = 0;
	
	private boolean alive = true;
	private double health = 100d;
	private double maxHealth = 150d;
	private double damage = 20d;
	private double strength = 10d;
	private double critical = 0.05d;
	private double defence = 3.5d;
	private double attackSpeed = 1.3d;
	private long attackDistance = 40;
	
	public PlayerData ()
	{
		
	}
	
	public void loadProfile ()
	{
		if (RESET_PLAYER_DATA)
		{
			// Save empty data before loading profile for reset.
			save ();
		}
		else
		{
			try
			{
				FileInputStream fis = new FileInputStream("data/player.data");
				ObjectInputStream iis = new ObjectInputStream(fis);
				PlayerData playerData = (PlayerData) iis.readObject();
				iis.close ();
				
				this.mapID = playerData.getMapID ();
				this.displayX = playerData.getDisplayX ();
				this.displayY = playerData.getDisplayY ();
				this.worldX = playerData.getWorldX ();
				this.worldY = playerData.getWorldY ();
				
				this.level = playerData.getLevel();
				this.alive = playerData.isAlive ();
				this.maxHealth = playerData.getMaxHealth ();
				this.health = playerData.getHealth ();
				this.damage = playerData.getDamage ();
				this.critical = playerData.getCritical();
				this.defence = playerData.getDefence();
				this.experience = playerData.getExperience ();
			}
			catch (Exception e)
			{
				Failure.add("Failed to load player's saved data.", e);
			}
		}
	}
	public void save ()
	{
		if (lastSave + SAVE_DELAY < System.currentTimeMillis())
		{
			try 
			{
				FileOutputStream fos = new FileOutputStream("data/player.data");
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(this);
				oos.close ();
			}
			catch (Exception e)
			{
				Failure.add("Failed to save player's data.", e);
			}
			
			lastSave = System.currentTimeMillis();
		}
	}
	
	public void setMapID (int mapID) { this.mapID = mapID; }
	public void setDisplayX (int x) { this.displayX = x; }
	public void setDisplayY (int y) { this.displayY = y; }
	public void setWorldX (double x) { this.worldX = x; }
	public void setWorldY (double y) { this.worldY = y; }
	
	public void setHealth (double health) { this.health = health; }
	public void setAlive (boolean alive){ this.alive = alive; }
	
	public void addLevel () { this.level = this.level+1; }
	
	public PlayerData getData (){ return this; }
	public int getMapID () { return this.mapID; }
	public int getDisplayX () { return this.displayX; }
	public int getDisplayY () { return this.displayY; }
	public double getWorldX () { return this.worldX; }
	public double getWorldY () { return this.worldY; }
	public Grid getGrid () { return (new Grid (worldX, worldY)); }
	
	public double getMaxHealth () { return maxHealth; }
	public double getHealth () { return health; }
	public double getDamage () { return damage; }
	public double getStrength() { return strength; }
	public double getDefence() { return defence; }
	public double getCritical () { return critical; }
	public double getAttackSpeed() { return attackSpeed; }
	public long getAtackDistance() { return attackDistance; }
	public int getLevel() { return level; }
	public int getExperience () { return this.experience; }

	public boolean isAlive ()
	{
		return alive;
	}

	public void addExperience (int add) { this.experience = this.experience + add; }
}
