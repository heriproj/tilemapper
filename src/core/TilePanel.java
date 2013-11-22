package core;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import util.SpringUtilities;

/**
 * A panel which shows all of the currently loaded tiles to select from.
 */
public class TilePanel extends JPanel
{
	private static final long serialVersionUID = 7075610218036007161L;
	private int selectedTileIndex;
	private TileSheet tileSheet;
	private ArrayList<Tile> tiles;
	private boolean isObjectPanel;
	private MapPanel associatedMapPanel;
	
	private final int PADDING = 1;
	
	/**
	 * This class represents the panel which holds all of the
	 * tiles which can be selected to draw with
	 * @param tileSheet - The TileSheet to be displayed
	 */
	public TilePanel(TileSheet tileSheet, boolean isObjectSheet)
	{	
		// Set initial attributes
		this.tileSheet = tileSheet;
		selectedTileIndex = 0;
		isObjectPanel = isObjectSheet;
		tiles = new ArrayList<Tile>();
		
		setLayout(new SpringLayout());
		
		int idIndex = 0;
		
		ArrayList<AbstractTile> tempSheet;
		int tempSize;

		// Assign the size and ArrayList<Tile> based on whether this is a object sheet or not
		if (isObjectSheet)
		{
			setBackground(Color.LIGHT_GRAY);
			tempSheet = tileSheet.objects;
			tempSize = tileSheet.objects.size();
			
			Tile iconLabel = null;
			try 
			{
				Image eraseTile = ImageIO.read(new File("img/eraseTile.png"));
				eraseTile = eraseTile.getScaledInstance(tileSheet.getWidthOfTiles(), tileSheet.getHeightOfTiles(), 0);
				iconLabel = new Tile(eraseTile, this, -1);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			tiles.add(iconLabel);
			idIndex++;
			add(iconLabel);
		}
		else
		{
			tempSheet = tileSheet.tiles;
			tempSize = tileSheet.tiles.size();
			setBackground(Color.BLACK);
		}
		
		// Loop through the sheet and add each tile to the panel
		for (AbstractTile t : tempSheet)
		{
			Tile iconLabel = new Tile(t.getImage(), this, idIndex);
			tiles.add(iconLabel);
			idIndex++;
			add(iconLabel);
		}	
		
		// Determine amount of tile columns needed
		int tileColumns = calcTileColumns(200, tileSheet.getWidthOfTiles(), PADDING);
		
		// Determine amount of blank tiles needed
		int blankTiles = calcBlankTiles(tempSize, tileColumns);
		
		// Add the blank tiles to the panel
		for (int i = 0; i < blankTiles; i++)
			add(new JLabel());
		
		// Convert the layout into a compact grid
		SpringUtilities.makeCompactGrid(this, // parent
				tempSize/tileColumns+1, tileColumns, //tempSize / tileColumns + 1, tileColumns,
                0, 0,  // initX, initY
                PADDING, PADDING); // xPad, yPad
	}
	
	/**
	 * Calculates the number of blank tiles needed to  
	 * fill in the tile panel
	 * @param tileCount - total number of tiles
	 * @return - number of blank tiles needed
	 */
	public int calcBlankTiles(int tileCount, int tileColumns)
	{
		return ((tileCount / tileColumns + 1) * tileColumns) - tileCount;
	}
	
	/**
	 * Calculates the number of tile columns
	 * @param tilePanelWidth - Total width of the tile panel
	 * @param tileWidth - Width of tiles
	 * @param padding - Amount of pixels used for padding
	 * @return - number of tile columns
	 */
	public int calcTileColumns(int tilePanelWidth, int tileWidth, int padding)
	{
		return (Integer) tilePanelWidth/(tileWidth + padding);
	}
	
	/**
	 * Gets the MapPanel which this TilePanel is associated with
	 * @return
	 */
	public MapPanel getMapPanel()
	{
		return associatedMapPanel;
	}
	
	/**
	 * Get the TileSheet associated with this TilePanel
	 * @return - The TileSheet associated with this TilePanel
	 */
	public TileSheet getTileSheet()
	{
		return tileSheet;
	}
	
	/**
	 * Sets the selected tile to the incoming 
	 * index and clears the previously selected one
	 * @param index - The index to change the selected tile to
	 */
	public void setSelectedTile(int index)
	{
		tiles.get(selectedTileIndex).setSelected(false);
		selectedTileIndex = index;
		tiles.get(index).setSelected(true);
	}
	
	/**
	 * Gets the index of the currently selected tile in this panel
	 * @return The index of the currently selected tile
	 */
	public int getSelectedTileIndex()
	{
		return selectedTileIndex;
	}
	
	/**
	 * Gets the tile of the currently selected tile in this panel
	 * @return The tile of the currently selected tile
	 */
	public Image getSelectedTile()
	{
		return tiles.get(selectedTileIndex).getImage();
	}
	
	/**
	 * Gets the image of the specified tile
	 * @param id - The ID of the tile you want to retrieve the image from
	 * @return The image of the specified tile
	 */
	public Image getTileImage(int id)
	{
		return tiles.get(id).getImage();
	}
	
	/**
	 * Sets the MapPanel which this TilePanel is associated with
	 * @param panel - The MapPanel which this TilePanel is associated with
	 */
	public void setMapPanel(MapPanel panel)
	{
		associatedMapPanel = panel;
	}
	
	/**
	 * Tells whether or not this is an object panel
	 * @return True if this is an object panel; False if it's a tile panel
	 */
	public boolean isObjectPanel()
	{
		return isObjectPanel;
	}
}