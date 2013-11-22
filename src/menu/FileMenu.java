package menu;

import java.awt.Event;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileNameExtensionFilter;

import core.MapIO;
import core.MapTile;
import core.MapperFrame;
import core.MapPanel;

/**
 * The File menu of the GUI
 */
public class FileMenu extends Menu
{
	private static final long serialVersionUID = 8643576875688401357L;
	private MapperFrame parentFrame;
	private ArrayList<JMenuItem> mapDependentItems;
	private String currentMapFilePath;
	private JMenuItem saveMapItem;
	
	/**
	 * Constructs the file menu
	 * @param name - The name of the menu
	 * @param parentFrame - The frame to which the menu belongs
	 */
	public FileMenu(String name, MapperFrame parentFrame) 
	{
		super(name, parentFrame, -1);
	}

	/**
	 * Implementation of Menu class's abstract method; adds the necessary JMenuItems
	 */
	public ArrayList<JMenuItem> createMenuItems()
	{	
		mapDependentItems = new ArrayList<JMenuItem>();
		
		// Get a reference to the main MapperFrame
		parentFrame = getParentFrame();
		saveMapItem = createSaveMapItem();
		
		JMenuItem saveAsMapItem = createSaveAsMapItem();
		JMenuItem exportMapItem = createExportMapItem();
		
		mapDependentItems.add(saveAsMapItem);
		mapDependentItems.add(exportMapItem);
		
		// Create and add each JMenuItem to the Menu
		ArrayList<JMenuItem> temp = new ArrayList<JMenuItem>();
		temp.add(createNewMapItem());
		temp.add(saveMapItem);
		temp.add(saveAsMapItem);
		temp.add(createLoadMapItem());
		temp.add(exportMapItem);
		temp.add(createFileExitItem());
		
		return temp;
	}
	
	/**
	 * Creates the File->New map menu item and sets its action listener.
	 * @return The JMenuItem 
	 */
	private JMenuItem createNewMapItem()
	{
		JMenuItem item = new JMenuItem("New map..");
	
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				parentFrame.createNewMapDialog();
			}
		}
		item.addActionListener(new MenuItemListener());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		return item;
	}
	
	/**
	 * Creates the File->Save menu item and sets its action listener.
	 * @return The menu item
	 */
	private JMenuItem createSaveMapItem()
	{
		JMenuItem item = new JMenuItem("Save");
		item.setEnabled(false);
		
		class SaveItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event) 
			{
				MapIO.exportProjectAsXML(currentMapFilePath, parentFrame);
			}
		}
		item.addActionListener(new SaveItemListener());
		
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.ALT_MASK));
		return item;
	}
	
	/**
	 * Creates the File->Save As menu item and sets its action listener.
	 * @return The menu item
	 */
	private JMenuItem createSaveAsMapItem()
	{
		JMenuItem item = new JMenuItem("Save As..");
		
		class SaveItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event) 
			{
				JFileChooser dialog = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Tile Mapper files", "map");
				dialog.setFileFilter(filter);
				
				int response = dialog.showSaveDialog(parentFrame); 
				if (response == JFileChooser.APPROVE_OPTION)
				{ 
					currentMapFilePath = dialog.getSelectedFile().toString();
					MapIO.exportProjectAsXML(dialog.getSelectedFile().toString(), parentFrame);
					parentFrame.setTitle("Tile Mapper - " + currentMapFilePath + ".map");
					saveMapItem.setEnabled(true);
				}
			}
		}
		item.addActionListener(new SaveItemListener());
		
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
		return item;
	}
	
	/**
	 * Creates the File->Load menu item and sets its action listener.
	 * @return The menu item
	 */
	private JMenuItem createLoadMapItem()
	{
		JMenuItem item = new JMenuItem("Load..");
		
		class LoadItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event) 
			{
				JFileChooser dialog = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"Tile Mapper files", "tmf");
				dialog.setFileFilter(filter);	
				
				int response = dialog.showOpenDialog(parentFrame);
				if (response == JFileChooser.APPROVE_OPTION) 
				{ 
					currentMapFilePath = dialog.getSelectedFile().toString();
					MapIO.loadProjectAsXML(currentMapFilePath, parentFrame);
					parentFrame.setTitle("Tile Mapper - " + currentMapFilePath);
					saveMapItem.setEnabled(true);
				}
				
			}
		}
		item.addActionListener(new LoadItemListener());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_MASK));
		return item;
	}
	
	/**
	 * Creates the File->Export map as image menu item and sets its action listener.
	 * @return The menu item
	 */
	public JMenuItem createExportMapItem()
	{
		JMenuItem item = new JMenuItem("Export as image..");
		
		
		class ExportImageListener implements ActionListener
		{

			public void actionPerformed(ActionEvent arg0) 
			{
				JFileChooser dialog = new JFileChooser();
				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						"PNG images", "png");
				dialog.setFileFilter(filter);
				
				int response = dialog.showSaveDialog(parentFrame); 
				if (response == JFileChooser.APPROVE_OPTION) 
				{ 
					exportMapImage(dialog.getSelectedFile().toString());
				}
			}	
		}
		item.addActionListener(new ExportImageListener());
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK));
		return item;
	}
	
	/**
	 * Creates the File->Exit menu item and sets its action listener.
	 * @return The menu item
	 */
	private JMenuItem createFileExitItem()
	{
		JMenuItem item = new JMenuItem("Exit");
		class MenuItemListener implements ActionListener
		{
			public void actionPerformed(ActionEvent event)
			{
				System.exit(0);
			}
		}
		item.addActionListener(new MenuItemListener());
		return item;
	}
	
	/**
	 * Exports the currently displayed map as an image
	 * @param filePath - The file path to write the image to
	 */
	private void exportMapImage(String filePath)
	{
		MapPanel mapPanel = parentFrame.getMapPanel();
		
		// Create a blank image
		BufferedImage export = new BufferedImage(mapPanel.getWidth(), mapPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
		ArrayList<MapTile> tiles = mapPanel.getMapTiles();
		
		// Get the graphics context
		Graphics g = export.getGraphics();
		
		// Get map width and height
		int mapWidth = mapPanel.getWidthInTiles();
		int mapHeight = mapPanel.getHeightInTiles();
		
		// Get tile width and height
		int tileWidth = parentFrame.getTilePanel().getTileSheet().getWidthOfTiles();
		int tileHeight = parentFrame.getTilePanel().getTileSheet().getHeightOfTiles();
		
		int counter = 0;
		// Iterate through each map tile and draw it to the image
		for (int y = 0; y < mapHeight; y++)
		{
			for (int x = 0; x < mapWidth; x++)
			{
				g.drawImage(tiles.get(counter).getTileImage(), x * tileWidth, y * tileHeight, null);
				counter++;
			}
		}
			
		try 
		{
			// Write the image to the destination file path
			ImageIO.write(export, "png", new File(filePath + ".png"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.out.println("Error exporting image.");
		}
	}
	
	/**
	 * Gets all of the JMenuItems that are dependent on the map being loaded
	 * @return All of the JMenuItems that are dependent on the map being loaded
	 */
	public ArrayList<JMenuItem> getMapDependentItems()
	{
		return mapDependentItems;
	}
	
}