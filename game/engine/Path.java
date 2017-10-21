package engine;

import java.awt.Graphics2D;
import java.util.ArrayList;

public class Path 
{	
	private ArrayList<Step> steps = new ArrayList<Step> ();
	private int stepID = 0;
	
	public Path ()
	{
		
	}
	public void draw (Graphics2D g2d)
	{
	}
	
	public void appendStep (int x, int y)
	{
		steps.add (new Step (x, y));
	}
	public void prependStep (int x, int y)
	{
		steps.add (0, new Step (x, y));
	}
	public void remove (int index)
	{
		steps.remove(index);
	}
	public int getLength ()
	{
		return steps.size ();
	}
	public Step getStep (int index)
	{
		return steps.get (index);
	}
	public Step getCurrentStep ()
	{
		return (stepID < getLength()) ? getStep(stepID) : null;
	}
	public Step getFirstStep ()
	{
		return (stepID+1 < getLength()) ? getStep(stepID+1) : null;
	}
	public Step getSecondStep ()
	{
		return (stepID+2 < getLength()) ? getStep(stepID+2) : null;
	}
	public int getX (int index)
	{
		return getStep (index).getX ();
	}
	public int getY (int index)
	{
		return getStep (index).getY ();
	}
	public boolean nextStep ()
	{
		if (stepID+2 < steps.size())
		{
			stepID++;
			return true;
		}
		else
		{
			stepID = 0;
			steps.clear();
			
			return false;
		}
	}
	public int getStepID()
	{
		return stepID;
	}
	public boolean isSet ()
	{
		return (steps.size() == 0) ? false : true;
	}
	
	public class Step
	{
		private int x;
		private int y;
		
		public Step (int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		
		public int getX ()
		{
			return x;
		}
		
		public int getY ()
		{
			return y;
		}
	}
}
