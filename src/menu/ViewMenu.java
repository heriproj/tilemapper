package menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import core.MapperFrame;

/**
 * The View menu of the GUI
 */
public class ViewMenu extends Menu
{
	private static final long serialVersionUID = 5739872015701945346L;
	private JCheckBoxMenuItem collisionItem, gridItem;
	private JCheckBoxMenuItem tileLayerItem, objectLayerItem;
	private ArrayList<JMenuItem> mapDependentItems;
	private MapperFrame parentFrame;

	/**
	 * Constructor for the ViewMenu
	 * @param name - The text to be displayed at the top of the menu
	 * @param parentFrame - The frame in which the menu exists
	 */
	public ViewMenu(String name, MapperFrame parentFrame) 
	{
		super(name, parentFrame, 1);
		this.parentFrame = parentFrame;
	}
	
	/**
	 * Creates all of the menu items in the View menu
	 */
	public ArrayList<JMenuItem> createMenuItems()
	{	
		// Create map dependent items
		collisionItem = new JCheckBoxMenuItem("Show Collision", false);
		gridItem = new JCheckBoxMenuItem("Show Grid", true);
		tileLayerItem = new JCheckBoxMenuItem("Show Tile Layer", true);
		objectLayerItem = new JCheckBoxMenuItem("Show Object Layer", true);
		
		// Add repaint item listeners
		collisionItem.addActionListener(new RepaintActionListener());
		gridItem.addActionListener(new RepaintActionListener());
		tileLayerItem.addActionListener(new RepaintActionListener());
		objectLayerItem.addActionListener(new RepaintActionListener());
		
		// Add accelerators (for key shortcuts)
		collisionItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0 /* 0 means no modifier keys */));
		gridItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
		tileLayerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		objectLayerItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		
		// Add them to the ArrayList of map dependent items
		mapDependentItems = new ArrayList<JMenuItem>();
		mapDependentItems.add(collisionItem);
		mapDependentItems.add(gridItem);
		mapDependentItems.add(tileLayerItem);
		mapDependentItems.add(objectLayerItem);
		
		// Create and add each JMenuItem to the Menu
		ArrayList<JMenuItem> temp = new ArrayList<JMenuItem>();
		temp.add(collisionItem);
		temp.add(gridItem);
		temp.add(tileLayerItem);
		temp.add(objectLayerItem);
		
		return temp;
	}

	/**
	 * Gets all of the map dependent menu items
	 * @return Menu items which are dependent on the map being loaded
	 */
	public ArrayList<JMenuItem> getMapDependentItems()
	{
		return mapDependentItems;
	}
	
	/**
	 * Gets whether or not collision mode is enabled
	 * @return Whether or not collision mode is enabled
	 */
	public boolean getCollisionMode()
	{
		return collisionItem.getState();
	}
	
	/**
	 * Gets whether or not grid mode is enabled
	 * @return Whether or not grid mode is enabled
	 */
	public boolean getGridMode()
	{
		return gridItem.getState();
	}
	
	/**
	 * Gets whether or not object mode is enabled
	 * @return Whether or not object mode is enabled
	 */
	public boolean getObjectLayerMode()
	{
		return objectLayerItem.getState();
	}
	
	/**
	 * Gets whether or not tile mode is enabled
	 * @return Whether or not tile mode is enabled
	 */
	public boolean getTileLayerMode()
	{
		return tileLayerItem.getState();
	}
	
	/**
	 * ActionListener which repaints all tiles on the map panel
	 */
	class RepaintActionListener implements ActionListener
	{
		public void actionPerformed(ActionEvent arg0) 
		{
			parentFrame.getMapPanel().repaintAllTiles();	
		}
	}	
}
