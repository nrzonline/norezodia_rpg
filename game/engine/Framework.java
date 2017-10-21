package engine;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.util.Timer;
import java.util.TimerTask;

import editor.Editor;
import engine.Utilities.RateCounter;
import game.Game;

import base._Borders;
import base._Interface;

import settings.Colors;
import settings.Config;
import settings.Fonts;

@SuppressWarnings("serial")

public class Framework extends Canvas
{
	static
	{
		//System.setProperty("sun.java2d.trace", "timestamp,log,count");
		//System.setProperty("sun.java2d.transaccel", "True");
		System.setProperty("sun.java2d.opengl", "true");
	}
	
	private final static String TITLE = Config.TITLE;
	private final static boolean HIDE_DECORATION = Config.HIDE_DECORATION;
	private static int window_width = Config.WINDOW_WIDTH;
	private static int window_height = Config.WINDOW_HEIGHT;
	private static final boolean START_EDITOR = Config.START_EDITOR;
	private static final boolean DEVELOPMENT_MODE = Config.DEVELOPMENT_MODE;
	private boolean show_data = Config.SHOW_DATA;
	
	private final long UPDATE_RATE = 1000 / Config.UPDATE_RATE;
	
	private static Frame frame;
	private static Framework framework;
	private Graphics2D g2d;
	private BufferStrategy strategy;
	private _Borders _borders;
	private _Interface _interface;
	private Loading loading;
	private Editor editor;
	private Game game;
	private Failure failure;
	private Texture texture;
	private Input input;
	private RateCounter fpsCounter;
	
	private static enum FrameworkState
	{
		LOADING,
		PLAYING,
		EDITOR,
		FAILURE
	}
	private static FrameworkState frameworkState = FrameworkState.LOADING;
	
	private final Timer timer;
	private TimerTask updateTimer;
	
	public Framework()
	{
		timer = new Timer();
	}
	private static void createDisplay()
	{		
		if (!HIDE_DECORATION)
		{
			window_width += 18;
			window_height += 45;
		}
		
		frame = new Frame(TITLE);
		framework = new Framework();
		frame.add(framework);	
		frame.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				framework.stop();
				frame.setVisible(false);
				System.exit(0);
			}
		});
		frame.setSize(window_width, window_height);
		frame.setLocationRelativeTo(null);
		frame.setBackground (Colors.BACKGROUND);
		frame.setUndecorated(HIDE_DECORATION);
		frame.setVisible(true);
		
		Input input = new Input();
		framework.addKeyListener(input);
		framework.addMouseListener(input);
		framework.addMouseMotionListener(input);
		framework.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyReleased(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE) 
				{
					framework.stop();
					frame.setVisible(false);
					System.exit(0);
				}
			}
		});
		framework.setInput(input);
		framework.requestFocus();
		framework.setupBufferStrategy();
		framework.initialize ();
		framework.load();
		framework.update();
	}
	private void setInput(Input input)
	{
		this.input = input;
	}
	private void setupBufferStrategy()
	{
		this.setIgnoreRepaint(true);
		this.createBufferStrategy(2);
		strategy = this.getBufferStrategy();
	}
	private void initialize()
	{
		long startTime = System.currentTimeMillis();
		
		Log.out("Initializing application...");
		
		_borders = new _Borders();
		_interface = new _Interface(); 
		loading = new Loading();
		texture = new Texture();
		editor = new Editor(texture);
		game = new Game(texture);
		failure = new Failure();
		fpsCounter = new RateCounter ();
		
		Log.out("Application initialized in " + (System.currentTimeMillis() - startTime) + " ms.");
	}
	private void load()
	{		
		Log.out("Loading application...");
		
		Thread loadThread = new Thread ()
		{
			@Override 
			public void run ()
			{
				long startTime = System.currentTimeMillis();
				
				changeState ("LOADING");
				
				_interface.load ();
				texture.load ();
				editor.load ();
				game.load ();
				
				if (!failure.isFound ())
				{
					Log.out("Application loaded in " + (System.currentTimeMillis() - startTime) + " ms.");
					
					if (!START_EDITOR)
					{
						changeState ("PLAYING");
					}
					else
					{
						if (DEVELOPMENT_MODE)
						{
							changeState ("EDITOR");
						}
					}
				}
			}
		};
		loadThread.start ();
	}	
	private void input ()
	{				
		switch (frameworkState)
		{
			case LOADING:
				break;
			case PLAYING:
				game.input (input);
				break;
			case EDITOR:
				editor.input (input);
				break;
			case FAILURE:
				failure.input (input);
				break;
		}
		
		if (input.keyDownOnce(KeyEvent.VK_F2))
		{
			show_data = (show_data) ? false : true;
		}
	}
	private void update()
	{
		if(updateTimer != null) { updateTimer.cancel(); }
		fpsCounter = Utilities.newCounter ();
		
		updateTimer = new TimerTask()
		{
			long lastTime = System.currentTimeMillis();
			
			@Override
			public void run()
			{
				input.poll ();
				input();
				
				long currentTime = System.currentTimeMillis();
				double passedTime = (currentTime - lastTime) * 0.001;
				
				fpsCounter.count ();
				
				if (checkForFailure ())
				{
					frameworkState = FrameworkState.FAILURE;
				}
				
				switch (frameworkState)
				{
					case LOADING:
						break;
					case PLAYING:
						game.update (passedTime);
						break;
					case EDITOR:
						editor.update();
						break;
					case FAILURE:
						break;
				}
				
				lastTime = currentTime;
				
				draw();
			}
		};
		
		timer.schedule(updateTimer, 0, UPDATE_RATE);
	}
	private void draw()
	{
		do
		{
			do
			{
				g2d = (Graphics2D) strategy.getDrawGraphics();
				
				g2d.setColor(Colors.BACKGROUND);
				g2d.fillRect(0, 0, getWidth(), getHeight());
				
				switch (frameworkState)
				{
					case LOADING:
						loading.draw (g2d);
						break;
					case PLAYING:
						game.draw (g2d);
						drawData (g2d);
						break;
					case EDITOR:
						editor.draw (g2d);
						break;
					case FAILURE:
						failure.draw (g2d);
						break;
				}
				
				_borders.drawOuter (g2d);

				g2d.dispose();
			}
			while (strategy.contentsRestored());
			
			strategy.show();
			Toolkit.getDefaultToolkit().sync();
		} 
		while (strategy.contentsLost());
			
	}
	
	public static void changeState (String state)
	{
		Log.out ("Changing current frameworkState to '" + state + "'.");
		
		switch (state)
		{
			case "LOADING":
				frameworkState = FrameworkState.LOADING;
				break;
			case "PLAYING":
				frameworkState = FrameworkState.PLAYING;
				break;
			case "EDITOR":
				frameworkState = FrameworkState.EDITOR;
				break;
			case "FAILURE":
				frameworkState = FrameworkState.FAILURE;
				break;
			default:
				Failure.add ("Changing frameworkState failed, '" + state + "' does not exist!");
		}
	}
	private boolean checkForFailure ()
	{
		if (failure.isFound () && frameworkState != FrameworkState.FAILURE)
		{
			changeState ("FAILURE");
			return true;
		}
		
		return false;
	}
	private void stop ()
	{
		Log.out("Framework loop stopped, closing application.");
		
		updateTimer.cancel();
	}
	private void drawData (Graphics2D g2d)
	{
		
		if (DEVELOPMENT_MODE)
		{
			if (show_data)
			{
				int x = 850;
				int y = 20;
				
				int mb = 1024*1024;
				Runtime runtime = Runtime.getRuntime();
				long freeMem = runtime.freeMemory()/mb;
				long totalMem = runtime.totalMemory()/mb;
				long usedMem = totalMem-freeMem;
				long maxMem = runtime.maxMemory()/mb;
				
				game.drawData (g2d);
				
				g2d.setColor(Colors.DRAW_DATA);
				g2d.fillRect(x, y, 200, 120);
				
				Fonts.drawString(g2d, "Engine", Colors.ORANGE_DARK, Fonts.LOADING, x+10, y+20, true);
				Fonts.drawString(g2d, "FPS: " + fpsCounter.getRate(), Colors.WHITE, Fonts.LOADING, x+10, y+40, true);
				Fonts.drawString(g2d, "Free Mem: " + freeMem + "/" + totalMem + "MB", Colors.WHITE,  Fonts.LOADING,  x+10, y+60, true);
				Fonts.drawString(g2d, "Heap Size: " + usedMem + "MB", Colors.WHITE,  Fonts.LOADING,  x+10, y+80, true);
				Fonts.drawString(g2d, "MAX Mem: " + maxMem + "MB", Colors.WHITE,  Fonts.LOADING,  x+10, y+100, true);
			}
		}
	}
	public static void changeCursor(int cursor)
	{
		framework.setCursor(Cursor.getPredefinedCursor(cursor));
	}
	public static void main(String[] args)
	{
		createDisplay();
	}
}
