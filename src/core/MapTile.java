package core;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This represents a tile which is drawn to the MapPanel. It stores information such as the
 * image currently drawn to it (both object and tile layers).
 */
public class MapTile extends JLabel
{
	private static final long serialVersionUID = 7815646574424440259L;
	private final Color collisionColor = new Color(255, 0, 0, 145);
	private final Color hoverColor = new Color(120, 255, 120, 145);
	private MapPanel parentMapPanel;
	private Image tileLayer, objectLayer;
	private int index;
	private int objectLayerID, tileLayerID;
	private boolean collidable;
	private boolean hovered;
	
	
	/**
	 * Represents a tile which is drawn to the map
	 * @param image - The image to initialize the tile with
	 * @param mapPanel - The map panel to place this tile in
	 * @param index - The unique id for this tile
	 */
	public MapTile(ImageIcon image, MapPanel mapPanel, int index)
	{
		super(image);
		this.index = index;
		objectLayerID = tileLayerID = -1;
		collidable = false; 
		parentMapPanel = mapPanel;
		addMouseListener(new MapTileListener());
	}
	
	/**
	 * Gets the object layer id of this tile
	 * @return The object layer id of this tile
	 */
	public int getObjectLayerId()
	{
		return objectLayerID;
	}
	
	/**
	 * Gets the tile layer id of this tile
	 * @return The tile layer id of this tile
	 */
	public int getTileLayerId()
	{
		return tileLayerID;
	}

	/**
	 * Gets the image which is currently shown on this MapTile
	 * @return The image which is currently shown on this MapTile
	 */
	public BufferedImage getTileImage()
	{
		int width = parentMapPanel.getTilePanel().getTileSheet().getWidthOfTiles();
		int height = parentMapPanel.getTilePanel().getTileSheet().getHeightOfTiles();
		
		BufferedImage temp = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = temp.getGraphics();
		
		if (tileLayer != null)
			g.drawImage(tileLayer, 0, 0, this);
		if (objectLayer != null)
			g.drawImage(objectLayer, 0, 0, this);
		
		return temp;
	}
	
	/**
	 * Sets the object layer's image
	 * @param image - The image to set the object layer to
	 */
	public void setObjectLayer(Image image)
	{
		objectLayer = image;
	}
	
	/**
	 * Sets the tile layer's image
	 * @param image - The image to set the tile layer to
	 */
	public void setTileLayer(Image image)
	{
		tileLayer = image;
	}
	
	/**
	 * Draws the actual tile by drawing the tile layer followed by the object layer
	 * @param g - The graphics context to draw in
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		// Paint the two tile layers
		if (parentMapPanel.tileModeEnabled())
			g.drawImage(tileLayer, 0, 0, this);
		
		if (parentMapPanel.objectModeEnabled())
			g.drawImage(objectLayer, 0, 0, this);
		
		// Draw red tile to show collision mode
		if (parentMapPanel.collisionModeEnabled() && collidable)
		{
			g.setColor(collisionColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		else if (hovered)
		{
			g.setColor(hoverColor);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		
		// Draw grid if grid mode is enabled
		if (parentMapPanel.gridModeEnabled())
			g.drawRect(0, 0, getWidth(), getHeight());
	}
	
	/**
	 * Sets the object layer id of this tile
	 * @param id - The id to set this tile to
	 */
	public void setObjectLayerId(int id)
	{
		objectLayerID = id;
		
		if (id != -1 && id != 0)
			objectLayer = parentMapPanel.getObjectPanel().getTileImage(id);
		else
			objectLayer = null;
	}
	
	/**
	 * Sets the tile layer id of this tile
	 * @param id - The id to set this tile to
	 */
	public void setTileLayerId(int id)
	{
		tileLayerID = id;
		
		if (id != -1)
			tileLayer = parentMapPanel.getTilePanel().getTileImage(id);
	}

	/**
	 * Draws the correct tile based on the last one that was selected
	 */
	private void drawTile()
	{
		// Determine which tile should be drawn and then draw it
		if (parentMapPanel.objectPanelSelectedLast())
			setObjectLayerId(parentMapPanel.getObjectPanel().getSelectedTileIndex());
		else
			setTileLayerId(parentMapPanel.getTilePanel().getSelectedTileIndex());
		
		repaint();
	}
	
	/**
	 * Draws multiple tiles at once, for use with the multi-draw function
	 * @param totalTiles - Width or Height of the grid of tiles to be drawn
	 */
	private void drawTiles(int totalTiles)
	{
		// Iterate through each tile after the originally clicked tile
		for(int i = 0; i < totalTiles; i++)
		{
			// Draw the remaining rows
			for (int j = 0; j < totalTiles; j++)
			{
				// Calculate where each tile is
				int currentIndex = index + i + (j * parentMapPanel.getWidthInTiles());
				// Check to make sure that the tile index isn't out of bounds
				if (currentIndex >= parentMapPanel.getTotalNumberOfTiles())
					return;
			
				// Determine which tile should be drawn and then draw it
				if (parentMapPanel.objectPanelSelectedLast())
					parentMapPanel.getTile(currentIndex).setObjectLayerId(parentMapPanel.getObjectPanel().getSelectedTileIndex());
				else
					parentMapPanel.getTile(currentIndex).setTileLayerId(parentMapPanel.getTilePanel().getSelectedTileIndex());
			
				// Redraw
				parentMapPanel.getTile(currentIndex).repaint();
			}
		}
	}
	
	/**
	 * Applies a transparent color to the map tile that is currently being hovered over
	 */
	private void applyHoverColor()
	{
		// Repaint all the old projected tiles
		parentMapPanel.repaintProjectedTiles();
		
		// Clear the index list
		parentMapPanel.clearProjectedIndexes();

		// Determine which tiles should have the hover color applied to them
		int drawCount = parentMapPanel.getDrawCount();
		
		for (int i = 0; i < drawCount; i++)
		{
			for (int j = 0; j < drawCount; j++)
			{
				// Calculate where each tile is
				int currentIndex = index + i + (j * parentMapPanel.getWidthInTiles());
				// Check to make sure that the tile index isn't out of bounds
				if (!(currentIndex >= parentMapPanel.getTotalNumberOfTiles()))
				{
					parentMapPanel.getTile(currentIndex).setHovered(true);
					parentMapPanel.getTile(currentIndex).repaint();
					parentMapPanel.addToProjectedIndexes(currentIndex);
				}
			}
		}
	}
	
	/**
	 * Simple function to determine if a tile is collidable or not
	 * @return If 1, collidable. If 0, not collidable.
	 */
	public byte getCollidable()
	{
		if (collidable)
			return 1;
		else
			return 0;
	}
	/**
	 * Sets whether or not a tile is collidable
	 * @param flag - Whether or not the tile is collidable
	 */
	public void setCollidable(byte flag)
	{
		if (flag == 0)
			collidable = false;
		else
			collidable = true;
	}
	
	/**
	 * Sets whether or not the tile is being hovered over
	 * @param flag - Whether or not the tile is being hovered over
	 */
	public void setHovered(boolean flag)
	{
		hovered = flag;
	}
	
	/**
	 * Specialized MouseListener for this class
	 */
	class MapTileListener implements MouseListener
	{
		public void mouseEntered(MouseEvent e) 
		{	
			
			if (e.getModifiers() == 16)	// If the mouse button is down as the mouse enters
			{
				// Check to see if collision mode isn't on
				if (!parentMapPanel.collisionModeEnabled())
				{
					// Calculate draw count
					int drawCount = parentMapPanel.getDrawCount();
					
					// If it's only 1, call single tile draw method
					if (drawCount == 1)
						drawTile();
					// Otherwise, draw multiple tiles
					else
						drawTiles(drawCount);
					applyHoverColor();
				}
	
				// If it is on, and clicked, toggle the collidable bool
				else
					collidable = !collidable;
				repaint();
				
			}
			else
			{
				if (parentMapPanel.collisionModeEnabled())
				{
					Graphics g = getGraphics();
					g.setColor(collisionColor);
					g.fillRect(0, 0, getWidth(), getHeight());
				}
				else
					applyHoverColor();
			}
		}

		public void mousePressed(MouseEvent e) 
		{
			// Only execute if it's a left click
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				// Check to see if collision mode isn't on
				if (!parentMapPanel.collisionModeEnabled())
				{
					// Calculate draw count
					int drawCount = parentMapPanel.getDrawCount();
					// If it's only 1, call single tile draw method
					if (drawCount == 1)
						drawTile();
					// Otherwise, draw multiple tiles
					else
						drawTiles(drawCount);
				}
				
				// If it is on, and clicked, toggle the collidable bool
				else
					collidable = !collidable;
				repaint();
			}
		}
		
		public void mouseClicked(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {repaint();}
		public void mouseReleased(MouseEvent e) {}
	}
}