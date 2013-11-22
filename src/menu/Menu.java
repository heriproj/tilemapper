package menu;

import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import core.MapperFrame;

/**
 * Abstract Menu class. Each inheriting class must implement the 
 * createMenuItems() method so that the Menu will have its JMenuItems
 */
public abstract class Menu extends JMenu 
{	
	private static final long serialVersionUID = -7982875133245540994L;
	private ArrayList<JMenuItem> menuItems;
	private MapperFrame parent; 
	
	/**
	 * Constructs a Menu based on the explicit parameters
	 * @param name - The name of the menu
	 * @param parentFrame - The MapperFrame that contains the Menu
	 * @param seperatorLoc - Adds a separator to the menu after that number element
	 */
	public Menu(String name, MapperFrame parentFrame, int separatorLoc)
	{
		super(name);
		parent = parentFrame;
		menuItems = createMenuItems();
		
		// Add each MenuItem to the Menu
		for (int i = 0; i < menuItems.size(); i++)
		{
			add(menuItems.get(i));
			
			// If a separator has been specified, add it after the i'th element
			if (i == separatorLoc)
				add(new JSeparator());
		}
	}
	
	/**
	 * Must be implemented by the inheriting subclass. An ArrayList of the 
	 * JMenuItems desired to be in the Menu should be added here.
	 * @return The ArrayList of JMenuItems that should be added to the Menu
	 */
	public abstract ArrayList<JMenuItem> createMenuItems();
	
	/**
	 * Gets the list of JMenuItems contained by this Menu
	 * @return - The ArrayList<JMenuItem> contained within this Menu
	 */
	public ArrayList<JMenuItem> getMenuItems()
	{
		return menuItems;
	}
	
	/**
	 * Gets a reference to the MapperFrame which contains this Menu
	 * @return The MapperFrame that contains the Menu
	 */
	public MapperFrame getParentFrame()
	{
		return parent;
	}
}