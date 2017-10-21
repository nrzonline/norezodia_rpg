package editor;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import settings.Colors;
import settings.Config;

import engine.Animation;
import engine.Button;
import engine.Grid;
import engine.Input;
import engine.Texture;
import engine.Utilities;

public class Menu 
{	
	private static final int TILE_SIZE = Config.TILE_SIZE;
	private Texture texture;
	
	public static enum MenuState
	{
		BUILD,
		SETTINGS
	}
	public static enum EntityState
	{
		NONE,
		TEXTURES,
		ANIMATIONS,
		OBJECTS
	}
	private static MenuState menuState = MenuState.BUILD;
	private static EntityState entityState = EntityState.NONE;
	
	private ArrayList<Button> layerButtons = new ArrayList<Button>();
	private ArrayList<Button> entityButtons = new ArrayList<Button>();
	private ArrayList<Button> actionButtons = new ArrayList<Button>();
	
	private Button btnLayer0, btnLayer1, btnLayer2, btnLayer3, btnLayer4, btnLayer5, btnLayer6;
	private Button btnTextures, btnAnimations, btnObjects, btnRemove, btnClear;

	private int sheetID = 3;
	private int animationSheetID = 0;
	private BufferedImage sheet;
	private Animation[] animations;
	
	private Grid selectedGrid;
	private int sheetX = 1110;
	private int sheetY = 170;
	
	public Menu (Texture texture)
	{		
		this.texture = texture;
	}
	public void load ()
	{
		createButtons ();
		setSheet (sheetID);
		setAnimationSheet (animationSheetID);
	}
	public void input (Input input)
	{
		switch (menuState)
		{
			case BUILD:
				checkEntityHit (input);
				checkLayerHit (input);
				checkNumpadHit (input);
				break;
			case SETTINGS:
				break;
		}
	}
	public void update ()
	{
		
	}
	public void draw (Graphics2D g2d)
	{
		g2d.setColor(Colors.MENU);
		g2d.fillRect(1073, 8, 359, 884);
		
		switch (menuState)
		{
			case BUILD:
				drawLayerButtons (g2d);
				drawEntityButtons (g2d);
				drawActionButtons (g2d);
								
				switch (entityState)
				{
					case TEXTURES:
						drawTileSheet (g2d);
						break;
					case ANIMATIONS:
						drawAnimationSheet (g2d);
						break;
					case OBJECTS:
						break;
					default: break;
				}
				
				g2d.setColor(Colors.SELECTION);
				drawSelection (g2d);
				break;
			case SETTINGS:
				break;
		}
		
	}
	

	public void createButtons ()
	{
		int x = 1033;
		int y = 30;
		btnLayer0 = new Button ("layer0", x+50, y, 40, 40, "Walk", true, true);
		btnLayer1 = new Button ("layer1", x+100, y, 40, 40, "Lay1", true, false);
		btnLayer2 = new Button ("layer2", x+150, y, 40, 40, "Lay2", true, false);
		btnLayer3 = new Button ("layer3", x+200, y, 40, 40, "Lay3", true, false);
		btnLayer4 = new Button ("layer4", x+250, y, 40, 40, "Lay4", true, false);
		btnLayer5 = new Button ("layer5", x+300, y, 40, 40, "Lay5", true, false);
		btnLayer6 = new Button ("layer6", x+350, y, 40, 40, "Obj", true, false);
		
		btnTextures = new Button ("textures", x+75, y+45, 40, 40, "Tex", false, false);
		btnAnimations = new Button ("animations", x+125, y+45, 40, 40, "Ani", false, false);
		btnObjects = new Button ("objects", x+175, y+45, 40, 40, "Obj", false, false);
		btnRemove = new Button ("remove", x+275, y+45, 40, 40, "Rem", true, true);
		btnClear = new Button ("clear", x+325, y+45, 40, 40, "Clr", true, false);
		
		layerButtons.add(btnLayer0);
		layerButtons.add(btnLayer1);
		layerButtons.add(btnLayer2);
		layerButtons.add(btnLayer3);
		layerButtons.add(btnLayer4);
		layerButtons.add(btnLayer5);
		layerButtons.add(btnLayer6);
		
		entityButtons.add(btnTextures);
		entityButtons.add(btnAnimations);
		entityButtons.add(btnObjects);
		
		actionButtons.add(btnRemove);
		actionButtons.add(btnClear);
	}
	private void drawLayerButtons (Graphics2D g2d)
	{
		btnLayer0.draw(g2d);
		btnLayer1.draw(g2d);
		btnLayer2.draw(g2d);
		btnLayer3.draw(g2d);
		btnLayer4.draw(g2d);
		btnLayer5.draw(g2d);
		btnLayer6.draw(g2d);
	}
	private void drawEntityButtons (Graphics2D g2d)
	{
		btnTextures.draw(g2d);
		btnAnimations.draw(g2d);
		btnObjects.draw(g2d);
	}
	private void drawActionButtons (Graphics2D g2d)
	{
		btnRemove.draw (g2d);
		btnClear.draw (g2d);
	}
	private void drawTileSheet (Graphics2D g2d)
	{
		g2d.drawImage(sheet, 1110, 170, sheet.getWidth(), sheet.getHeight(), null);
	}
	private void drawAnimationSheet (Graphics2D g2d)
	{
		int x = sheetX;
		int y = sheetY;
		
		for (int i = 0; i < animations.length; i++)
		{
			int value = -(1*100+i+1);
			texture.drawTexture(g2d, value, x, y);
			x += TILE_SIZE;
		}
	}
	private void drawSelection (Graphics2D g2d)
	{
		if (selectedGrid != null)
		{
			g2d.drawRect(sheetX+(selectedGrid.getX()-1)*TILE_SIZE, sheetY+(selectedGrid.getY()-1)*32, TILE_SIZE, TILE_SIZE);
		}
	}
	
	private void selectButton (ArrayList<Button> group, String name)
	{
		for (int i = 0; i < group.size(); i++)
		{
			if (!group.get(i).isButton(name))
			{
				group.get(i).setSelected(false);
			}
			else
			{
				group.get(i).setSelected(true);
			}
		}
		
		activateButtons(name);
	}
	private void activateButtons (String name)
	{
		String[] buttonNames = new String[]{};
		ArrayList<Button> buttonGroup = new ArrayList<Button>();
		
		switch (name)
		{
			case "layer0": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{};
				break;
			case "layer1": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"textures", "animations"};
				break;
			case "layer2": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"textures", "animations"};
				break;
			case "layer3": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"textures", "animations"};
				break;
			case "layer4": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"textures", "animations"};
				break;
			case "layer5": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"textures", "animations"};
				break;
			case "layer6": 
				buttonGroup = entityButtons;
				buttonNames = new String[]{"objects"};
				break;
		}
		
		for (int i = 0; i < buttonGroup.size(); i++)
		{
			buttonGroup.get(i).setActive(false);
			
			if (buttonNames != null)
			{
				for (int j = 0; j < buttonNames.length; j ++)
				{
					if (buttonGroup.get(i).isButton(buttonNames[j]))
					{
						buttonGroup.get(i).setActive(true);
						btnTextures.setSelected(false);
						btnAnimations.setSelected(false);
						btnObjects.setSelected(false);
						
						switch (entityState)
						{
							case TEXTURES:
								btnTextures.setSelected(true);
								break;
							case ANIMATIONS:
								btnAnimations.setSelected(true);
								break;
							case OBJECTS:
								btnObjects.setSelected(true);
								break;
							case NONE: break;
						}
					}
				}
			}
		}
	}
	private void checkLayerHit (Input input)
	{
		if (input.buttonDownOnce(1) && btnLayer0.isHit (input) || input.keyDownOnce(KeyEvent.VK_Q))
		{
			entityState = EntityState.NONE;
			selectButton(layerButtons, "layer0"); 
			setWorkLayer (0);
		}
		else if (input.buttonDownOnce(1) && btnLayer1.isHit (input) || input.keyDownOnce(KeyEvent.VK_1)) 
		{
			if (entityState == EntityState.OBJECTS || entityState == EntityState.NONE)
			{
				entityState = EntityState.TEXTURES;
			}
			selectButton(layerButtons, "layer1"); 
			setWorkLayer (1); 
		}
		else if (input.buttonDownOnce(1) && btnLayer2.isHit (input) || input.keyDownOnce(KeyEvent.VK_2)) 
		{
			if (entityState == EntityState.OBJECTS || entityState == EntityState.NONE)
			{
				entityState = EntityState.TEXTURES;
			}
			selectButton(layerButtons, "layer2"); 
			setWorkLayer (2); 
		}
		else if (input.buttonDownOnce(1) && btnLayer3.isHit (input) || input.keyDownOnce(KeyEvent.VK_3)) 
		{ 
			if (entityState == EntityState.OBJECTS || entityState == EntityState.NONE)
			{
				entityState = EntityState.TEXTURES;
			}
			selectButton(layerButtons, "layer3"); 
			setWorkLayer (3); 
		}
		else if (input.buttonDownOnce(1) && btnLayer4.isHit (input) || input.keyDownOnce(KeyEvent.VK_4))
		{ 
			entityState = EntityState.TEXTURES; 
			selectButton(layerButtons, "layer4"); 
			setWorkLayer (4); 
		}else if (input.buttonDownOnce(1) && btnLayer5.isHit (input) || input.keyDownOnce(KeyEvent.VK_5))
		{ 
			entityState = EntityState.TEXTURES; 
			selectButton(layerButtons, "layer5"); 
			setWorkLayer (5); 
		}
		else if (input.buttonDownOnce(1) && btnLayer6.isHit (input) || input.keyDownOnce(KeyEvent.VK_E))
		{ 
			entityState = EntityState.OBJECTS; 
			selectButton(layerButtons, "layer6"); 
			setWorkLayer (6); 
		}
		else if (input.buttonDownOnce(1) && btnTextures.isHit(input)) 
		{ 
			entityState = EntityState.TEXTURES; 
			selectButton(entityButtons, "textures"); 
		}
		else if (input.buttonDownOnce(1) && btnAnimations.isHit(input)) 
		{  
			entityState = EntityState.ANIMATIONS; 
			selectButton(entityButtons, "animations"); 
		}
		else if (input.buttonDownOnce(1) && btnObjects.isHit(input)) 
		{
			entityState = EntityState.OBJECTS; 
			selectButton(entityButtons, "objects"); 
		}
		else if (input.buttonDownOnce(1) && btnRemove.isHit(input))
		{
			if (!btnRemove.isSelected())
			{
				MapEdit.setActionState ("REMOVE");
				selectButton(actionButtons, "remove"); 
			}
			else
			{
				btnRemove.setSelected(false);
				MapEdit.setActionState ("BUILD");
			}
		}
		else if (input.buttonDownOnce(1) && btnClear.isHit(input))
		{
			if (!btnClear.isSelected())
			{
				MapEdit.setActionState ("CLEAR");
				selectButton(actionButtons, "clear"); 
			}
			else
			{
				MapEdit.setActionState ("BUILD");
				btnClear.setSelected(false);
			}
		}
		
	}
	private void checkEntityHit (Input input)
	{
		if (input.buttonDownOnce(1))
		{
			int mouseX = input.getMouseX();
			int mouseY = input.getMouseY();
			
			if (Utilities.isWithin(mouseX, 1110, 1110+sheet.getWidth()) && Utilities.isWithin(mouseY, 170, 170+sheet.getHeight()))
			{
				Grid selectedGrid = new Grid ();
				selectedGrid.setX(1 + (input.getMouseX() - 1110)/32);
				selectedGrid.setY(1 + (input.getMouseY() - 170)/32);
				this.selectedGrid = selectedGrid;
				
				int value;
				
				if (entityState == EntityState.TEXTURES)
				{
					value = ((sheetID+1)*1000 + selectedGrid.getY()*10 + selectedGrid.getX());
				}
				else
				{
					value = -((animationSheetID+1)*100 + selectedGrid.getY() * selectedGrid.getX());
				}
				
				MapEdit.setValue(value);
			}
		}
	}
	private void checkNumpadHit (Input input)
	{
		if (input.keyDownOnce(KeyEvent.VK_NUMPAD1))
		{
			setSheet(1);
		}
		else if (input.keyDownOnce(KeyEvent.VK_NUMPAD2))
		{
			setSheet(2);
		}
		else if (input.keyDownOnce(KeyEvent.VK_NUMPAD3))
		{
			setSheet(3);
		}
	}
	
	private void setSheet (int sheetID)
	{
		sheet = texture.getSheet(sheetID);
		this.sheetID = sheetID;
	}
	private void setAnimationSheet (int sheetID)
	{
		animations = texture.getAnimationSheet(sheetID);
	}
	private void setWorkLayer (int layer)
	{
		MapEdit.setWorkLayer(layer);
	}
}
