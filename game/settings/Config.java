package settings;

public abstract class Config 
{
	// Window settings
	public static final String 		TITLE = "No'Re-Zodia SinglePlayer V0.10";
	public static final boolean 	HIDE_DECORATION = false;
	public static int 					WINDOW_WIDTH = 1440;
	public static int 					WINDOW_HEIGHT = 900;
	public static final int 			BORDER_OFFSET = 8;
	
	// Logging settings
	public static final boolean	LOG_ENABLED = true;
	public static final boolean	LOG_INIT = true;
	public static final boolean	LOG_STATE = true;
	
	// Development settings
	public static final boolean 	DEVELOPMENT_MODE = true;
	public static final boolean 	LOG_PROGRESS = true;
	public static final boolean 	STACK_TRACING = true;
	public static final boolean 	LOAD_WAIT = false;
	public static final int 			LOAD_WAIT_MS = 1*000;
	public static final boolean	SHOW_DATA = false;
	public static final boolean 	RESET_PLAYER_DATA =  false;
	
	// Editor settings
	public static final boolean 	START_EDITOR = false;
	public static final long		NAVIGATE_DELAY = 80;
	public static final long		EDITOR_SAVE_DELAY = 3*1000;
	
	// Framework settings
	public static final int 			UPDATE_RATE = 100;
	
	// World settings
	public static final int 			TILE_SIZE = 32;
	public static final int 			DISPLAY_WIDTH = 33;
	public static final int 			DISPLAY_HEIGHT = 21;
	
	// Pathfinder settings
	public static final int 			MAX_STEPS = 40;
	public static final int 			MAX_DEPTH = 60;
	public static final boolean 	DIAGONAL_MOVEMENT = false;
	public static final boolean 	SHOW_PATH = true;
	
	// Player settings
	public static final long		SAVE_DELAY = 1000;
	public static final double		DEFAULT_SPEED = 65.0d;
	public static final short		MAX_LEVEL = 150;

	// NPC settings
	public static final long		DEFAULT_RESPAWN_DELAY = 30*1000;
	public static final long		HIDE_BODY_DELAY = 3*1000;
	
	// Chat settings
	public static final String		WELCOME_MESSAGE = "Welcome to No'Re-Zodia Version 0.10 SinglePlayer.";
	public static final int			MAX_CHAT_MESSAGES = 30;
	public static final int 			SHOW_CHAT_MESSAGES =9;
}
