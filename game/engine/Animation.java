package engine;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class Animation
{
	private BufferedImage animationSheet;
	
	private int frameWidth;
	private int frameHeight;
	private int frameRow = 0;
	private int frameLength = 0;
	private long frameTime;
	private long nextFrameTime = 0;
	private int currentFrame = 0;
	private boolean loop;
	
	private int x;
	private int y;
	
	private int offsetX;
	private int offsetY;
	
	public Animation
	(
			BufferedImage animationSheet, 
			int frameWidth, 
			int frameHeight, 
			int frameRow, 
			int frameLength, 
			long frameTime,
			boolean loop, 
			int x, 
			int y
	)
	{
		this.animationSheet = animationSheet;
		this.frameWidth = frameWidth;
		this.frameHeight = frameHeight;
		this.frameRow = frameRow;
		this.frameLength = frameLength;
		this.frameTime = frameTime;
		this.loop = loop;
		this.x = x;
		this.y = y;
	}
	
	public void update()
	{
		if(nextFrameTime <= System.currentTimeMillis ())
		{
			currentFrame++;
			
			if(currentFrame >= this.frameLength)
			{
				currentFrame = (loop) ? 0 : this.frameLength-1;
			}
			
			offsetX = currentFrame * this.frameWidth;
			offsetY = frameRow * this.frameHeight;
			
			nextFrameTime = System.currentTimeMillis() + this.frameTime;
		}
	}
	public void draw (Graphics2D g2d)
	{
		update ();
		g2d.drawImage (this.animationSheet, this.x, this.y, this.x+frameWidth, this.y+this.frameHeight, offsetX, offsetY, offsetX+this.frameWidth, offsetY+this.frameHeight, null);
	}
	
	public void setLocation (int x, int y){
		this.x = x;
		this.y = y;
	}
	public void restart ()
	{
		if(currentFrame > 0)
		{
			currentFrame = 0;
		}
	}
	public boolean isReady()
	{
		return (currentFrame == frameLength-1) ? true : false;
	}
}
