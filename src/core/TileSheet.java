package core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * Contains all of the information about the tile sheet that was loaded, such as
 * image loaded, tile width and height, and the transparent color associated with it
 */
public class TileSheet 
{
	public ArrayList<AbstractTile> tiles, objects;
	private BufferedImage rawTileSheet;
	private int tileWidth, tileHeight;
	private Color transparentColor;
	
	/**
	 * Constructs a TileSheet which is based on an incoming sprite sheet image
	 * file and custom height and width sizes for each Tile
	 * @param spriteSheet - The raw image file which contains all of the Tile images
	 * @param tileW - The width of each Tile
	 * @param tileH - The height of each Tile
	 */
	public TileSheet(BufferedImage spriteSheet, int tileW, int tileH, Color transparentColor) 
	{
		tiles = new ArrayList<AbstractTile>();
		objects = new ArrayList<AbstractTile>();
		this.transparentColor = transparentColor;
		rawTileSheet = spriteSheet;
		tileWidth = tileW;
		tileHeight = tileH;
		splitTileSheet();
	}
	
	/**
	 * TileSheet constructor which is based on an incoming sprite sheet image
	 * file and custom height and width sizes for each Tile
	 * @param spriteSheetFile - The image file which contains all of the Tile images
	 * @param tileW - The width of each Tile
	 * @param tileH - The height of each Tile
	 */
	public TileSheet(File spriteSheetFile, int tileW, int tileH, Color transparentColor)
	{
		tiles = new ArrayList<AbstractTile>();
		objects = new ArrayList<AbstractTile>();
		try 
		{
			rawTileSheet = ImageIO.read(spriteSheetFile);
		}
		catch (IOException e)
		{
			System.out.println("Error loading spritesheet.");
		}
		
		this.transparentColor = transparentColor;
		tileWidth = tileW;
		tileHeight = tileH;
		splitTileSheet();
	}

	/**
	 * Adds a Tile to the specific instance of the TileSheet
	 * @param t - The tile to be added
	 */
	public void addTile(AbstractTile t)
	{
		tiles.add(t);
	}
	
	/**
	 * Gets the Tile at the specified index
	 * @param index - The index in the Tile ArrayList to access
	 * @return The Tile at the specified index
	 */
	public AbstractTile getTile(int index) 
	{
		return tiles.get(index);
	}

	/**
	 * Gets the number of tiles in the TileSheet
	 * @return The number of Tiles contained in the TileSheet
	 */
	public int getNumberOfTiles() 
	{
		return tiles.size();
	}
	
	/**
	 * Gets the width of tiles in the TileSheet
	 * @return The width of Tiles contained in the TileSheet
	 */
	public int getWidthOfTiles()
	{
		return tileWidth;
	}
	
	/**
	 * Gets the height of tiles in the TileSheet
	 * @return The height of Tiles contained in the TileSheet
	 */
	public int getHeightOfTiles() 
	{
		return tileHeight;
	}
	
	/**
	 * Gets the raw image that was initially passed into this TileSheet
	 * @return - The image used by this TileSheet before it was split up
	 */
	public BufferedImage getRawImage()
	{
		return rawTileSheet;
	}
	
	/**
	 * Gets the color which represents transparency in this TileSheet
	 * @return This TileSheet's transparent color
	 */
	public Color getTransparentColor()
	{
		return transparentColor;
	}
	
	/**
	 * Determines whether the image is considered an object tile or not
	 * based on whether or not it contains a pixel which is the same 
	 * color as the transparentColor field
	 * @param i - The image to be checked
	 * @return True if the image is an object tile, false otherwise
	 */
	private boolean checkObjectTile(BufferedImage i)
	{
		// Determine the r,g,b values of the transparent color
		int transRed = transparentColor.getRed();
		int transGreen = transparentColor.getGreen();
		int transBlue = transparentColor.getBlue();
		
		// Check each pixel of the image
		for (int x = 0; x < i.getWidth(); x++)
		{
			for (int y = 0; y < i.getHeight(); y++)
			{
				int color = i.getRGB(x, y);
				int alpha = (color & 0xFF000000) >> 32;
				int red = (color & 0x00FF0000) >> 16;
		    	int green = (color & 0x0000FF00) >> 8;
		    	int blue = color & 0x000000FF;
		    	
		    	// If all colors are the same
		    	if (red == transRed && green == transGreen && blue == transBlue)
		    		return true;
		    	// If a pixel is completely transparent
		    	if (alpha == 0)
		    		return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Converts all of the specified transparent color pixels into
	 * actually transparent pixels
	 * @param i - The image to convert
	 * @return The new transparent image
	 */
	private BufferedImage makeTransparentImage(BufferedImage i)
	{	
		// Determine the r,g,b values of the transparent color
		int transRed = transparentColor.getRed();
		int transGreen = transparentColor.getGreen();
		int transBlue = transparentColor.getBlue();
		
		// Check each pixel of the image
		for (int x = 0; x < i.getWidth(); x++)
		{
			for (int y = 0; y < i.getHeight(); y++)
			{
				int color = i.getRGB(x, y);
				
				int red = (color & 0x00FF0000) >> 16;
		    	int green = (color & 0x0000FF00) >> 8;
		    	int blue = color & 0x000000FF;
		    	
		    	// If the pixel matches the specified transparent color
		    	// Then set it to an absolute white with alpha at 0
		    	if (red == transRed && green == transGreen && blue == transBlue)
		    		i.setRGB(x, y, 0x00FFFFFF);
			}
		}
		
		return i;
	}
	
	/**
	 * Called at the end of each constructor,
	 * this method breaks up the raw sprite sheet image
	 * into individual Tiles and adds them to the TileSheet's
	 * ArrayList which stores the Tiles
	 */
	private void splitTileSheet() 
	{
		// Calculate the amount of tiles in each row and column
		int xTiles = rawTileSheet.getWidth() / tileWidth;
		int yTiles = rawTileSheet.getHeight() / tileHeight;

		// Split the raw sprite sheet into individual tiles accordingly
		// And add them to the ArrayList of Tiles
		for (int y = 0; y < yTiles; y++) 
		{
			for (int x = 0; x < xTiles; x++)
			{
				// The BufferedImage must support an alpha channel
				BufferedImage temp = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_ARGB);
				
				temp.getGraphics().drawImage(rawTileSheet.getSubimage(x * tileWidth, y
						* tileWidth, tileWidth, tileHeight), 0, 0, null);
				
				if (checkObjectTile(temp))
				{
					temp = makeTransparentImage(temp);
					objects.add(new AbstractTile(temp));
				}
				else
					tiles.add(new AbstractTile(temp));
			}
		}
	}
	
}