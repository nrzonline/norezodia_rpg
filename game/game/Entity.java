package game;

import java.awt.Graphics2D;

import engine.Input;

public abstract class Entity 
{
	public void load(){}
	public void update(double passedTime){}
	public void input(Input input){}
	public void draw(Graphics2D g2d){}
	
}
