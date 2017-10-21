package engine;

import settings.Config;

public class Log 
{
	private static final boolean DEVELOPMENT_MODE = Config.DEVELOPMENT_MODE;
	private static final boolean LOG_PROGRESS = Config.LOG_PROGRESS;
	private static final boolean STACK_TRACING = Config.STACK_TRACING;
	
	public static void out (Object message)
	{
		if (DEVELOPMENT_MODE)
		{
			if (LOG_PROGRESS)
			{
				if (message instanceof String)
					System.out.println ("                 " + (String) message);
				else if (message instanceof Integer)
					System.out.println ("                 " + (int) message);
				else if (message instanceof Double)
					System.out.println ("                 " + (double) message);
				else if (message instanceof Float)
					System.out.println ("                 " + (float) message);
				else if (message instanceof Long)
					System.out.println ("                 " + (long) message);
			}
		}
	}	
	public static void notice (String message)
	{
		System.out.println ("NOTICE:          " + message);
	}
	public static void failure (String message)
	{
		System.out.println ("## FAILURE ##    " + message);
	}
	public static void failure (String message, Exception exception)
	{
		System.out.println ("## FAILURE ##    " + message);
		stackTrace (exception);
	}
	public static void stackTrace (Exception exception)
	{
		if (DEVELOPMENT_MODE)
		{
			if (STACK_TRACING)
			{
				exception.printStackTrace ();
			}
		}
	}
	public static boolean devMode ()
	{
		return DEVELOPMENT_MODE;
	}
}
