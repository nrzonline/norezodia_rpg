package game;

import java.awt.Graphics2D;
import java.util.ArrayList;

import engine.Message;

import base._Chat;

import settings.Colors;
import settings.Config;
import settings.Fonts;

public class Interaction
{
	private static final int MAX_CHAT_MESSAGES = Config.MAX_CHAT_MESSAGES;
	private static final int SHOW_CHAT_MESSAGES = Config.SHOW_CHAT_MESSAGES;
	private static final String WELCOME_MESSAGE = Config.WELCOME_MESSAGE;
	
	private static ArrayList<Message> chatMessages;
	
	public Interaction ()
	{
		chatMessages = new ArrayList<Message>();
		addMessage(new Message(0, Colors.ORANGE, WELCOME_MESSAGE));
	}
	
	public void load ()
	{
		
	}
	public void draw (Graphics2D g2d) throws Exception
	{
		g2d.setColor(Colors.MENU);
		g2d.fillRect(8, 699, 1056, 193);
		drawMessages(g2d);
		_Chat.chatBar(g2d);
	}
	public void drawMessages(Graphics2D g2d)
	{
		int offsetHeight = 703;
		int offsetIndex = (chatMessages.size() >= SHOW_CHAT_MESSAGES) ? chatMessages.size() - SHOW_CHAT_MESSAGES : 0;
		int currentRow = 0;
		for(int i=offsetIndex; i<chatMessages.size(); i++)
		{
			Message message = chatMessages.get(i);
			currentRow++;
			Fonts.drawString(g2d, message.getMessage(), message.getColor(), Fonts.CHAT, 20, offsetHeight+(currentRow*16), true);
		}
	}
	
	public static void addMessage(Message message)
	{
		if(chatMessages.size() == MAX_CHAT_MESSAGES)
		{
			chatMessages.remove(0);
		}
		chatMessages.add(message);
	}
}
