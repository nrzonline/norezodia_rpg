package engine;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import settings.Config;

public class Input implements KeyListener, MouseListener, MouseMotionListener
{
	private static final int BORDER_OFFSET = Config.BORDER_OFFSET;
	
	private enum InputState
	{
		RELEASED,
		PRESSED,
		ONCE
	}
	
	private final int KEY_COUNT = 256;
	private boolean[] currentKeys = null;
	private InputState[] currentKeyStates = null;
	
	private final int BUTTON_COUNT = 3;
	private Point pollMousePoint = null;
	private Point currentMousePoint = null;
	private boolean[] currentButtons = null;
	private InputState[] currentButtonStates = null;
	
	public Input ()
	{
		currentKeys = new boolean[KEY_COUNT];
		currentKeyStates = new InputState[KEY_COUNT];
		for (int i = 0; i < KEY_COUNT; i++)
		{
			currentKeyStates[i] = InputState.RELEASED;
		}
		
		pollMousePoint = new Point (0, 0);
		currentMousePoint = new Point (0, 0);
		currentButtons = new boolean[BUTTON_COUNT];
		currentButtonStates = new InputState[BUTTON_COUNT];
		for (int j = 0; j < BUTTON_COUNT; j++)
		{
			currentButtonStates[j] = InputState.RELEASED;
		}
	}
	
	public void poll ()
	{
		keyStatePoll ();
		mouseStatePoll ();
	}
	private synchronized void keyStatePoll ()
	{
		for (int i = 0; i < KEY_COUNT; i++)
		{
			if (currentKeys[i])
			{
				if (currentKeyStates[i] == InputState.RELEASED)
				{
					currentKeyStates[i] = InputState.ONCE;
				}
				else
				{
					currentKeyStates[i] = InputState.PRESSED;
				}
			} 
			else 
			{
				currentKeyStates[i] = InputState.RELEASED;
			}
		}
	}
	private synchronized void mouseStatePoll ()
	{
		pollMousePoint = new Point (currentMousePoint);
		
		for (int j = 0; j < BUTTON_COUNT; j++)
		{
			if (currentButtons[j])
			{
				if (currentButtonStates[j] == InputState.RELEASED){
					currentButtonStates[j] = InputState.ONCE;
				}
				else
				{
					currentButtonStates[j] = InputState.PRESSED;
				}
			}
			else
			{
				currentButtonStates[j] = InputState.RELEASED;
			}
		}
	}
	
	public boolean keyDownOnce (int keyCode)
	{
		return currentKeyStates[keyCode] == InputState.ONCE;
	}
	public boolean keyDownOnce (int keyCode1, int keyCode2)
	{
		return currentKeyStates[keyCode1] == InputState.ONCE || currentKeyStates[keyCode2] == InputState.ONCE;
	}
	public boolean keyDown (int keyCode)
	{
		return currentKeyStates[keyCode] == InputState.ONCE || currentKeyStates[keyCode] == InputState.PRESSED;
	}
	public boolean keyDown (int keyCode1, int keyCode2)
	{
		return 	currentKeyStates[keyCode1] == InputState.ONCE || 
				currentKeyStates[keyCode1] == InputState.PRESSED ||
				currentKeyStates[keyCode2] == InputState.ONCE ||
				currentKeyStates[keyCode2] == InputState.PRESSED;
	}
	@Override
	public void keyPressed (KeyEvent e)
	{
		int keyCode = e.getKeyCode ();
		
		if (keyCode >= 0 && keyCode < KEY_COUNT)
		{
			currentKeys[keyCode] = true;
		}
	}
	@Override
	public synchronized void keyReleased (KeyEvent e)
	{
		int keyCode = e.getKeyCode ();
		
		if (keyCode >= 0 && keyCode < KEY_COUNT)
		{
			currentKeys[keyCode] = false;
		}
	}
	@Override
	public void keyTyped (KeyEvent e){}
	
	public boolean buttonDownOnce (int buttonCode)
	{
		return currentButtonStates[buttonCode-1] == InputState.ONCE;
	}
	public boolean buttonDown (int buttonCode)
	{
		return currentButtonStates[buttonCode-1] == InputState.ONCE || currentButtonStates[buttonCode-1] == InputState.PRESSED;
	}

	@Override
	public void mouseEntered (MouseEvent e)
	{
		mouseMoved (e);
	}
	@Override
	public synchronized void mouseExited (MouseEvent e)
	{
		mouseMoved (e);
	}
	@Override
	public synchronized void mouseDragged (MouseEvent e)
	{
		mouseMoved (e);
	}
	@Override
	public synchronized void mouseMoved (MouseEvent e) 
	{
		currentMousePoint = e.getPoint ();
	}
	@Override
	public synchronized void mousePressed (MouseEvent e)
	{
		currentButtons[e.getButton()-1] = true;
	}
	@Override
	public void mouseReleased (MouseEvent e)
	{
		currentButtons[e.getButton()-1] = false;
	}
	@Override
	public void mouseClicked (MouseEvent e){}

	public boolean clickedWithin (int xMin, int xMax, int yMin, int yMax)
	{
		return (getMouseX() > xMin && getMouseX() < xMax && getMouseY() > yMin && getMouseY() < yMax) ? true : false;
	}
	
	public int getMouseX () { return pollMousePoint.x; }
	public int getMouseY () { return pollMousePoint.y; }
	public Grid getMouseGrid () { return new Grid ((double)getMouseX()-BORDER_OFFSET, (double)getMouseY()-BORDER_OFFSET); }
}
