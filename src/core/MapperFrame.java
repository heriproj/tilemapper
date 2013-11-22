package core;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import menu.MenuPanel;
import core.MapIO;

/**
 * The JFrame which serves as the main GUI for the application.
 */
public class MapperFrame extends JFrame
{
	private static final long serialVersionUID = 3136220905581860671L;
	public static final int FRAME_WIDTH = 1024;
	public static final int FRAME_HEIGHT = 640;
	public static final int MINIMUM_WIDTH = 400;
	public static final int MINIMUM_HEIGHT = 300;
	
	private TilePanel tilePanel, objectPanel;
	private MapPanel mapPanel;
	private LayoutManager layoutManager;
	private MenuPanel menuPanel;
	
	/**
	 * Creates the main JFrame which is the core GUI for the map editor
	 */
	public MapperFrame()
	{
		// Setup the JFrame
		setSize(FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMinimumSize(new Dimension(MINIMUM_WIDTH, MINIMUM_HEIGHT));
		setTitle("Tile Mapper v0.7");
		setLayout(new GridBagLayout());
		
		// Set the IconImage
		try 
		{
			setIconImage(ImageIO.read(new File("img/blankTile.png")));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// Construct menu
		MenuPanel menuPanel = new MenuPanel(this);
		setJMenuBar(menuPanel);
		
		// Load the default map
		MapIO.loadProjectAsXML("example_map.tmf", this);
	}
	
	/**
	 * Creates a modal dialog used for creating a new map
	 */
	public void createNewMapDialog()
	{
		new NewMapDialog(this);
	}
	
	/**
	 * Gets the layout manager currently associated with this MapperFrame
	 * @return The manager to assign to this MapperFrame
	 */
	public LayoutManager getLayoutManager()
	{
		return layoutManager;
	}
	
	/**
	 * Gets the MapPanel object currently associated with this MapperFrame
	 * @return The MapPanel object which the MapperFrame currently contains
	 */
	public MapPanel getMapPanel()
	{
		return mapPanel;
	}
	
	/**
	 * Gets the MenuPanel currently associated with this MapperFrame
	 * @return The MenuPanel object which the MapperFrame currently contains
	 */
	public MenuPanel getMenuPanel()
	{
		return menuPanel;
	}
	
	/**
	 * Gets the TilePanel object (for objects) currently associated with this MapperFrame
	 * @return The TilePanel object which the MapperFrame currently contains
	 */
	public TilePanel getObjectPanel()
	{
		return objectPanel;
	}
	
	/**
	 * Gets the TilePanel object (for tiles) currently associated with this MapperFrame
	 * @return The TilePanel object which the MapperFrame currently contains
	 */
	public TilePanel getTilePanel()
	{
		return tilePanel;
	}
	
	/**
	 * Sets the layout manager currently associated with this MapperFrame
	 * @param manager - The manager to assign to this MapperFrame
	 */
	public void setLayoutManager(LayoutManager manager)
	{
		layoutManager = manager;
	}
	
	/**
	 * Sets the MapPanel object of this MapperFrame
	 * @param panel - The MapPanel object to assign to the MapperFrame
	 */
	public void setMapPanel(MapPanel panel)
	{
		mapPanel = panel;
	}
	
	/**
	 * Sets the MenuPanel currently associated with this MapperFrame
	 * @param panel - The MenuPanel to assign to this MapperFrame
	 */
	public void setMenuPanel(MenuPanel panel)
	{
		menuPanel = panel;
	}
	
	/**
	 * Sets the TilePanel object(for objects) of this MapperFrame
	 * @param panel - The TilePanel object to assign to the MapperFrame
	 */
	public void setObjectPanel(TilePanel panel)
	{
		objectPanel = panel;
	}
	
	/**
	 * Sets the TilePanel object(for tiles) of this MapperFrame
	 * @param panel - The TilePanel object to assign to the MapperFrame
	 */
	public void setTilePanel(TilePanel panel)
	{
		tilePanel = panel;
	}
	
	/**
	 * Displays the splash screen
	 */
	public void showSplash()
	{
		Splash splash = new Splash(this);
		splash.run();
	}
}