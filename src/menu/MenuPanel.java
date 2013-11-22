package menu;

import java.util.ArrayList;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import core.MapperFrame;

/**
 * The menu bar of the GUI which contains all of the menus.
 */
public class MenuPanel extends JMenuBar
{
	private static final long serialVersionUID = 1L;
	private ArrayList<Menu> menus;
	private MapperFrame parent;
	private FileMenu fileMenu;
	private ViewMenu viewMenu;
	private DrawMenu drawMenu;
	
	/**
	 * Constructs the MenuPanel, which is a child of JMenuBar that holds all of the
	 * Frame's custom Menus
	 * @param parentFrame - The MapperFrame which contains this MenuPanel
	 */
	public MenuPanel(MapperFrame parentFrame)
	{
		super();
		parent = parentFrame;
		menus = new ArrayList<Menu>();
		createMenus();
		parentFrame.setMenuPanel(this);
	}
	
	/**
	 * Creates the individual and specific menus to be added to the MenuPanel.
	 * After being created, they are stored in the ArrayList of menus.
	 */
	private void createMenus()
	{
		// File menu
		fileMenu = new FileMenu("File", parent);
		
		// View menu
		viewMenu = new ViewMenu("View", parent);
		
		// Draw menu
		drawMenu = new DrawMenu("Draw", parent);
		
		menus.add(fileMenu);
		menus.add(viewMenu);
		menus.add(drawMenu);
		
		// Add all of the menus to the MenuBar
		for (int i = 0; i < menus.size(); i++)
		{
			add(menus.get(i));
		}
		
		// Disable all map dependent items when the menu is first created
		setMapDependentItems(false);
	}
	
	/**
	 * Disables or enables the map dependent items
	 * @param flag - Whether or not to enable the map dependent items
	 */
	public void setMapDependentItems(boolean flag)
	{
		
		// Get the map dependent items from the menus
		ArrayList<JMenuItem> mapDependentItems = fileMenu.getMapDependentItems();
		mapDependentItems.addAll(viewMenu.getMapDependentItems());
		
		// Set their status
		for (JMenuItem item : mapDependentItems)
			item.setEnabled(flag);
	}
	
	/**
	 * Gets the file menu
	 * @return The file menu
	 */
	public FileMenu getFileMenu()
	{
		return fileMenu;
	}
	
	/**
	 * Gets the view menu
	 * @return The view menu
	 */
	public ViewMenu getViewMenu()
	{
		return viewMenu;
	}
	
	/**
	 * Gets the draw menu
	 * @return The draw menu
	 */
	public DrawMenu getDrawMenu()
	{
		return drawMenu;
	}
	
}