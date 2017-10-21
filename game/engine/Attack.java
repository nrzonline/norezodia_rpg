package engine;

import settings.Constants;
import game.Npc;
import game.Player;

public class Attack 
{
	private final int PLAYER = Constants.PLAYER;
	private final int NPC = Constants.NPC;
	
	private Object attacker;
	private Object victem;
	private int attackerType;
	private int victemType;

	private long time = Utilities.now();
	
	private boolean isCritical = false;
	private double baseDamage;
	
	public Attack (Object attacker, Object victem)
	{
		this.attacker = attacker;
		this.victem = victem;
		defineTypes();
		handleAttack();
	}
	private void defineTypes()
	{
		if(attacker instanceof Player) { attackerType = PLAYER; }
		else if(attacker instanceof Npc) { attackerType = NPC; }
		else { Failure.add("Undefined object type in attack.java"); }
		
		if(victem instanceof Player){ victemType = PLAYER; }
		else if(victem instanceof Npc){ victemType = NPC; }
		else { Failure.add("Undefined object type in attack.java"); }
	}
	private void handleAttack()
	{		
		calcBaseDamage();
		attackToCombat();
	}
	
	private void calcBaseDamage()
	{
		double damage = 0d;
		double critical = 0d;
		
		if(attackerType == PLAYER)
		{
			damage = ((Player) attacker).getData().getDamage();
			critical = ((Player) attacker).getData().getCritical();
		}
		else if(attackerType == NPC)
		{
			damage = ((Npc) attacker).getDamage();
			critical = ((Npc) attacker).getCritical();
		}
		
		baseDamage = (Math.random()*damage);
		if (Math.random() <= critical)
		{
			isCritical = true;
			baseDamage = baseDamage*(Math.random()*2.3);
		}
	}
	private void attackToCombat()
	{
		if(victemType == PLAYER){ ((Player)victem).combat().addAttack(this); }
		else if(victemType == NPC){ ((Npc)victem).combat().addAttack(this); }
	}
	public Damage createReport(boolean toPlayer)
	{
		Damage damage = new Damage();
		damage.setToPlayer(toPlayer);
		damage.setDamage((int)getFinalDamage());
		damage.setVictem(victem);
		
		return damage;
	}
	public void rewardExp(int exp)
	{
		if(attackerType == PLAYER)
		{
			((Player) attacker).receiveExperience(exp);
		}
		else if(victemType == PLAYER)
		{
			((Player) victem).receiveExperience(exp);
		}
	}
	
	public Object getAttacker() { return attacker; }
	public Object getVictem() { return victem; }
	public double getFinalDamage(){ return baseDamage; }
	
	public boolean isCritical() { return isCritical; }
}
