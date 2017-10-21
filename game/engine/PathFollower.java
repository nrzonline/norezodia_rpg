package engine;

import settings.Constants;
import engine.Path.Step;
import game.Npc;
import game.Player;

public class PathFollower 
{
	private Path path;
	private Path nextPath;
	private Step currentStep, firstStep, secondStep;
	
	public PathFollower ()
	{
		
	}
	public void follow (Object entity, double passedTime)
	{
		if (path != null)
		{
			double entityX, entityY, distance;
			
			if (entity instanceof Player)
			{
				entityX = ((Player)entity).getData().getWorldX();
				entityY = ((Player)entity).getData().getWorldY();
				distance = passedTime * ((Player)entity).getSpeed();
			}
			else if (entity instanceof Npc)
			{
				entityX = ((Npc)entity).getWorldX();
				entityY = ((Npc)entity).getWorldY();
				distance = passedTime * 45.0d;
			}
			else
			{
				entityX = entityY = distance = 0;
			}
			
			currentStep = path.getCurrentStep();
			firstStep = path.getFirstStep ();
			secondStep = path.getSecondStep ();
			
			// Er is een path beschikbaar met minstens 1 pas.
			if (currentStep != null && firstStep != null)
			{
				// De path is veranderd, en de speler zit nog niet op een x of y tegel lijn.
				if (entityX != currentStep.getX()*32 && entityY != currentStep.getY()*32)
				{				
					// De eerste tegel ligt op links of rechts, corrigeren omhoog of omlaag.
					if (currentStep.getX() > firstStep.getX() || currentStep.getX() < firstStep.getX())
					{
						// Omhoog corrigeren tot aan de start-tegel.
						if (entityY > currentStep.getY()*32)
						{
							updateEntityLocation (entity, Constants.UP, entityY-distance);
							
							if (entityY-distance < firstStep.getY()*32)
							{
								updateEntityLocation (entity, Constants.UP, currentStep.getY()*32);
							}
						}
						// Omlaag corrigeren tot aan de start-tegel.
						else if (entityY < currentStep.getY()*32)
						{
							updateEntityLocation (entity, Constants.DOWN, entityY+distance);
							
							if (entityY+distance > firstStep.getY()*32)
							{
								updateEntityLocation (entity, Constants.DOWN, currentStep.getY()*32);
							}
						}
					}
					// De eerste tegel ligt boven of onder, corrigeren naar links of rechts.
					else if (currentStep.getY() < firstStep.getY() || currentStep.getY() > firstStep.getY())
					{
						// naar links corrigeren tot aan de start-tegel.
						if (entityX > currentStep.getX()*32)
						{
							updateEntityLocation (entity, Constants.LEFT, entityX-distance);
							
							if (entityX-distance < firstStep.getX()*32)
							{
								updateEntityLocation (entity, Constants.LEFT, currentStep.getX()*32);
							}
						}
						// naar rechts corrigeren tot aan de start-tegel.
						else if (entityX < currentStep.getX()*32)
						{
							updateEntityLocation (entity, Constants.RIGHT, entityX+distance);
							
							if (entityX+distance > firstStep.getX()*32)
							{
								updateEntityLocation (entity, Constants.RIGHT, currentStep.getX()*32);
							}
						}
					}
				}
				// De volgende step is naar links.
				else if (currentStep.getX() > firstStep.getX())
				{			
					// We gaan lopen.
					updateEntityLocation (entity, Constants.LEFT, entityX-distance);
					
					// De volgende tick word de tile gepasseerd, naar de volgende step.
					if (entityX-distance < firstStep.getX()*32)
					{
						// De tweede stap in rij bestaat.
						if (secondStep != null)
						{
							// De tweede in rij is niet verder naar links.
							if (firstStep.getX() <= secondStep.getX())
							{
								updateEntityLocation (entity, Constants.LEFT, firstStep.getX()*32);
							}
						}
						// De tweede stap bestaat niet, dit was de laatste tile.
						else
						{
							updateEntityLocation (entity, Constants.LEFT, firstStep.getX()*32);
						}
						nextStep(entity);
					}
				}
				// De volgende step is naar rechts.
				else if (currentStep.getX() < firstStep.getX())
				{			
					// We gaan lopen.
					updateEntityLocation (entity, Constants.RIGHT, entityX+distance);
					
					// De volgende tick word de tile gepasseerd, naar de volgende step.
					if (entityX+distance > firstStep.getX()*32)
					{
						// De tweede stap in rij bestaat.
						if (secondStep != null)
						{
							// De tweede in rij is niet verder naar rechts.
							if (firstStep.getX() >= secondStep.getX())
							{
								updateEntityLocation (entity, Constants.RIGHT, firstStep.getX()*32);
							}
						}
						// De tweede stap bestaat niet, dit was de laatste tile.
						else
						{
							updateEntityLocation (entity, Constants.RIGHT, firstStep.getX()*32);
						}
						nextStep(entity);
					}
				}
				// De volgende step is naar boven.
				else if (currentStep.getY() > firstStep.getY())
				{			
					// We gaan lopen.
					updateEntityLocation (entity, Constants.UP, entityY-distance);
					
					// De volgende tick word de tile gepasseerd, naar de volgende step.
					if (entityY-distance < firstStep.getY()*32)
					{
						// De tweede stap in rij bestaat.
						if (secondStep != null)
						{
							// De tweede in rij is niet verder naar boven.
							if (firstStep.getY() <= secondStep.getY())
							{
								updateEntityLocation (entity, Constants.UP, firstStep.getY()*32);
							}
						}
						// De tweede stap bestaat niet, dit was de laatste tile.
						else
						{
							updateEntityLocation (entity, Constants.UP, firstStep.getY()*32);
						}
						nextStep(entity);
					}
				}
				// De volgende step is naar beneden.
				else if (currentStep.getY() < firstStep.getY())
				{			
					// We gaan lopen.
					updateEntityLocation (entity, Constants.DOWN, entityY+distance);
					
					// De volgende tick word de tile gepasseerd, naar de volgende step.
					if (entityY+distance > firstStep.getY()*32)
					{
						// De tweede stap in rij bestaat.
						if (secondStep != null)
						{
							// De tweede in rij is niet verder naar beneden.
							if (firstStep.getY() >= secondStep.getY())
							{
								updateEntityLocation (entity, Constants.DOWN, firstStep.getY()*32);
							}
						}
						// De tweede stap bestaat niet, dit was de laatste tile.
						else
						{
							updateEntityLocation (entity, Constants.DOWN, firstStep.getY()*32);
						}
						nextStep(entity);
					}
				}			
			}
		}
	}
	private void updateEntityLocation (Object entity, int direction, double location)
	{
		if (entity instanceof Player)
		{
			if (direction == Constants.LEFT || direction == Constants.RIGHT)
			{
				((Player) entity).getData().setWorldX(location);
			}
			else
			{
				((Player) entity).getData().setWorldY(location);
			}
			
			((Player) entity).changeAvatar(direction);
		}
		else if (entity instanceof Npc)
		{
			if (direction == Constants.LEFT || direction == Constants.RIGHT)
			{
				((Npc) entity).setWorldX(location);
			}
			else
			{
				((Npc) entity).setWorldY(location);
			}
			
			((Npc) entity).changeAvatar(direction);
		}
	}
	private void nextStep (Object entity)
	{
		if (nextPath != null)
		{
			path = nextPath;
			nextPath = null;
		}
		else if (!path.nextStep())
		{
			path = null;
			if (entity instanceof Npc)
			{
				((Npc) entity).setMoveTime();
			}
		}
	}
	public void setPath (Path path)
	{
		if (path != null)
		{
			this.path = path;
		}
	}
	public void unsetPath ()
	{
		path = null;
		nextPath = null;
	}
	public void setNextPath (Path path)
	{
		if (path != null)
		{
			if (this.path != null)
			{
				this.nextPath = path;
			}
			else
			{
				this.path = path;
			}
		}
	}
	public Path getPath ()
	{
		return path;
	}
	public boolean hasPath ()
	{
		return (path != null) ? true : false;
	}
}
