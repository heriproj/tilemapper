package core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.*;

import org.jdom2.*;
import org.jdom2.input.*;
import org.jdom2.output.*;


/**
 * Handles loading and saving maps. Also handles Base64 encoding and decoding
 * for the tile sheet image.
 */
public class MapIO 
{
	/*
	 * Information about Base64 can be found at http://en.wikipedia.org/wiki/Base64
	 */
	
	// Code characters for values 0 ... 63
	private static char alpha[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();
	
	// Lookup table for converting base64 characters to value in range 0 ... 63
	private static byte[] base64Codes = new byte[256];
	static
	{
		// set all to -1
		for(int i = 0; i < 256; i++) base64Codes[i] = -1;
		// 'A' ... 'Z' = 0 ... 25
		for(int i = 'A'; i <= 'Z'; i++) base64Codes[i] = (byte)(i - 'A');
		// 'a' ... 'z' = 26 ... 51
		for(int i = 'a'; i <= 'z'; i++) base64Codes[i] = (byte)(26 + i - 'a');
		// '0' ... '9' = 52 ... 61
		for(int i = '0'; i <= '9'; i++) base64Codes[i] = (byte)(52 + i - '0');
		// '+' = 62
		base64Codes['+'] = 62;
		// '/' = 63
		base64Codes['/'] = 63;
	}
	
	/**
	 * 
	 * This method generates a base64 character array from a byte array.
	 * 
	 * Usage (Image):
	 * BufferedImage image = BUFFEREDIMAGE_CREATED_FROM_IMAGE;
	 * ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	 * ImageIO.write(image, FILE_EXTENSION, outputStream);
	 * char[] encodedImage = MapIO.base64Encode(outputStream.toByteArray());
	 * 
	 * Usage (String):
	 * String string = "";
	 * char[] encodedString = MapIO.base64Encode(string.getBytes());
	 * 
	 * See documentation if you need to encode something else.
	 * 
	 * Information about Base64 can be found at http://en.wikipedia.org/wiki/Base64
	 * 
	 * @param data - The byte[] to encode
	 * @return The Base64 encoded char[]
	 */
	public static char[] base64Encode(byte[] data)
	{
		// Integer division so will always be multiple of 4
		char[] out = new char[((data.length + 2) / 3) * 4];
		
		//
		// 3 bytes encode to 4 characters. out.length % 4 == 0 (ALWAYS)
		//
		for (int i = 0, ii = 0; i < data.length; i += 3, ii += 4)
		{
			boolean fours = false;
			boolean threes = false;
			
			int val = (0xFF & (int)data[i]);
			val <<= 8;
			if ((i + 1) < data.length)
			{
				val |= (0xFF & (int)data[i+1]);
				threes = true;
			}
			val <<= 8;
			if ((i + 2) < data.length)
			{
				val |= (0xFF & (int)data[i+2]);
				fours = true;
			}
			out[ii + 3] = alpha[fours ? (val & 0x3F) : 64];
			val >>= 6;
			out[ii + 2] = alpha[threes ? (val & 0x3F) : 64];
			val >>= 6;
			out[ii+1] = alpha[val & 0x3F];
			val >>= 6;
			out[ii] = alpha[val & 0x3F];
		}
		
		return out;
	}
	
	/**
	 * 
	 * This method decodes a base64 character array and outputs a byte array.
	 * 
	 * Usage (Image):
	 * char[] base64Chars = BASE64CHARS;
	 * byte[] imageBytes = MapIO.base64Decode(base64Chars);
	 * InputStream in = new ByteArrayInputStream(imageBytes);
	 * BufferedImage decodedImage = ImageIO.read(in);
	 * 
	 * Usage (String):
	 * char[] base64Chars = BASE64CHARS;
	 * byte[] stringBytes = MapIO.base64Decode(base64Chars);
	 * String decodedString = new String(stringBytes);
	 * 
	 * Information about Base64 can be found at http://en.wikipedia.org/wiki/Base64
	 * 
	 * @param data - The char[] to be decoded
	 * @return The Base64 decoded byte[]
	 */
	public static byte[] base64Decode(char[] data)
	{
		int inputLength = data.length;
		for (int i = 0; i < data.length; i++)
		{
			if(data[i] > 255 || base64Codes[data[i]] < 0) inputLength--; // Ignore non-valid chars and padding
		}
		
		//
		// 3 bytes for every 4 valid base64 chars.
		// Also add 2 bytes if there are 3 extra base64 chars, or add 1 byte if there are 2 extra
		//
		int len = (inputLength / 4) * 3;
		if((inputLength % 4) == 3) len += 2;
		if((inputLength % 4) == 2) len += 1;
		
		byte[] out = new byte[len];
		
		int shift = 0;
		int excess = 0;
		int index = 0;
		
		for (int i = 0; i < data.length; i++)
		{
			int value = (data[i] > 255) ? -1 : base64Codes[data[i]];
			
			// Skip over invalid base64 chars
			if (value >= 0)
			{
				// Bits shift up 6 each iteration, and new bits get put at bottom
				excess <<= 6;
				shift += 6;
				excess |= value;
				// If there are more than 8 shifted in, write them out. (leave excess at bottom)
				if (shift >= 8)
				{
					shift -= 8;
					out[index++] = (byte)((excess >> shift) & 0xff);
				}
			}
		}
		
		if (index != out.length)
		{
			throw new Error("Data Length Calculation Error: (wrote " + index + " instead of " + out.length + ")");
		}
		
		return out;
	}

	
	/**
	 * Loads the .tmf file into the program and sets it up to be edited
	 * @param filePath - The file path to read the project from
	 * @param frame - The MapperFrame in use
	 */
	public static void loadProjectAsXML(String fileName, MapperFrame parentFrame)
	{
		SAXBuilder parser = new SAXBuilder();
		
		try
		{
			Document doc = parser.build(new File(fileName));
			Element root = doc.getRootElement();
			
			// Get tilesheet_image
			Element tilesheet_image = root.getChild("tilesheet_image");
			String tileSheetOriginal = tilesheet_image.getText();
			
			// Get map width and height
			Element map_width = root.getChild("map_width");
			Element map_height = root.getChild("map_height");
			
			int mapWidth = Integer.parseInt(map_width.getText());
			int mapHeight = Integer.parseInt(map_height.getText());
			
			// Get tile width and height
			Element tile_width = root.getChild("tile_width");
			Element tile_height = root.getChild("tile_height");
			
			int tileWidth = Integer.parseInt(tile_width.getText());
			int tileHeight = Integer.parseInt(tile_height.getText());
			
			// Get transparent color
			Element transparent_color = root.getChild("transparent_color");
			
			Element eRed = transparent_color.getChild("red");
			Element eGreen = transparent_color.getChild("green");
			Element eBlue= transparent_color.getChild("blue");
			
			int red = Integer.parseInt(eRed.getText());
			int green = Integer.parseInt(eGreen.getText());
			int blue = Integer.parseInt(eBlue.getText());
			
			// Get tile, object, and collision layer data
			ArrayList<Integer> tilesLayerData = new ArrayList<Integer>();
			ArrayList<Integer> objectsLayerData = new ArrayList<Integer>();
			ArrayList<Byte> collisionLayerData = new ArrayList<Byte>(); 
			
			List<Element> list = root.getChildren("tile");
			
			// Iterate through each tile element
			for (int i = 0; i < list.size(); i++)
			{
				// Get the value of each ID
				int tileLayerID = Integer.parseInt((list.get(i)).getChild("tile_layer_id").getText());
				int objectLayerID = Integer.parseInt((list.get(i)).getChild("object_layer_id").getText());
				byte collisionLayerID = Byte.parseByte(list.get(i).getChild("collision_layer_id").getText());
				
				// Add them to the list
				tilesLayerData.add(tileLayerID);
				objectsLayerData.add(objectLayerID);
				collisionLayerData.add(collisionLayerID);
			}
			
			// Convert Base64 string to the image
			byte[] imageBytes = MapIO.base64Decode(tileSheetOriginal.toCharArray());
			InputStream in = new ByteArrayInputStream(imageBytes);
			BufferedImage decodedImage = ImageIO.read(in);
						
			// Create the tile sheet from the variables that have been read
			TileSheet sheet = new TileSheet(decodedImage, tileWidth, tileHeight, new Color(red, green, blue));
						
			// Create the map, tile, and object panels					
			TilePanel tilePanel = new TilePanel(sheet, false);
			TilePanel objectPanel = new TilePanel(sheet, true);
			MapPanel mapPanel = new MapPanel(parentFrame, mapWidth, mapHeight, tilePanel, objectPanel);
						
			// Assign the panels to the main frame
			parentFrame.setTilePanel(tilePanel);
			parentFrame.setObjectPanel(objectPanel);
			parentFrame.setMapPanel(mapPanel);
						
			// Assign the map panel to the tile selection panels
			tilePanel.setMapPanel(mapPanel);
			objectPanel.setMapPanel(mapPanel);
						
			LayoutManager manager;
						
			// If a layout manager doesn't already exist, create one
			if (parentFrame.getLayoutManager() == null)
			{
				manager = new LayoutManager(parentFrame, mapPanel);
				parentFrame.setLayoutManager(manager);
			}
			
			// If one does exist, clear old layout and update it with the new info
			else
			{
				manager = parentFrame.getLayoutManager();
				manager.clearExistingLayout();
				manager.setNewInfo(parentFrame, mapPanel);
			}
						
			// Initialize the new layout
			manager.initializeLayout();
						
			// Set tiles and object data to the MapPanel
			mapPanel.setLayerData(tilesLayerData, objectsLayerData, collisionLayerData);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Exports the currently open map to a .map file
	 * @param filePath - The file path to write the project to
	 * @param frame - The MapperFrame in use
	 */
	public static void exportProjectAsXML(String filePath, MapperFrame frame)
	{
		String xml =
                "<map>" +
                "   <tilesheet_image>" +
                "   </tilesheet_image>" +
                "   <map_width></map_width>" +
                "   <map_height></map_height>" +
                "   <tile_width></tile_width>" +
                "   <tile_height></tile_height>" +
                "   <transparent_color>" +
                "      <red></red>" +
                "      <green></green>" +
                "      <blue></blue>" +
                "   </transparent_color>" +
                "</map>";

        SAXBuilder builder = new SAXBuilder();
        try 
        {
        	
        	// Create XML document
            Document document = builder.build(new StringReader(xml));
            
            // Get root 
            Element map = document.getRootElement();
            
            // Calculate original tilesheet image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		ImageIO.write(frame.getTilePanel().getTileSheet().getRawImage(), "png", baos);
    		char[] encodedImage = MapIO.base64Encode(baos.toByteArray());
    		
    		// Store it
            Element tilesheet_image = map.getChild("tilesheet_image");
            tilesheet_image.setText(new String(encodedImage));

            // Calculate map width and height
            Element map_width = map.getChild("map_width");
            map_width.setText(frame.getMapPanel().getWidthInTiles() + "");
            
            Element map_height = map.getChild("map_height");
            map_height.setText(frame.getMapPanel().getHeightInTiles() + "");
            
            // Calculate tile width and height
            Element tile_width = map.getChild("tile_width");
            tile_width.setText(frame.getTilePanel().getTileSheet().getWidthOfTiles() + "");
            
            Element tile_height = map.getChild("tile_height");
            tile_height.setText(frame.getTilePanel().getTileSheet().getHeightOfTiles() + "");
            
            // Calculate transparent color
            Element transparent_color = map.getChild("transparent_color");
            Element red = transparent_color.getChild("red");
            Element green = transparent_color.getChild("green");
            Element blue = transparent_color.getChild("blue");
            
            Color transparent = frame.getTilePanel().getTileSheet().getTransparentColor();
            red.setText(transparent.getRed() + "");
            green.setText(transparent.getGreen() + "");
            blue.setText(transparent.getBlue() + "");
            
            // Get object and tile layer data
            ArrayList<Integer> tileIDs = frame.getMapPanel().getTileLayerData();
            ArrayList<Integer> objectIDs = frame.getMapPanel().getObjectLayerData();
            ArrayList<Byte> collisionIDs = frame.getMapPanel().getCollisionLayerData();
            
    		for(int i = 0; i < tileIDs.size(); i++)
    		{
    			// Create a tile element
    			Element tempTile = new Element("tile");
    			
    			// Tile layer data
    			Element tile_layer_id = new Element("tile_layer_id");
    			tile_layer_id.setText(tileIDs.get(i) + "");
    			
    			// Object layer data
    			Element object_layer_id = new Element("object_layer_id");
    			object_layer_id.setText(objectIDs.get(i) + "");
    			
    			// Collision layer data
    			Element collision_layer_id = new Element("collision_layer_id");
    			collision_layer_id.setText(collisionIDs.get(i) + "");
    			
    			// Add attributes to tile element
    			tempTile.addContent(tile_layer_id);
    			tempTile.addContent(object_layer_id);
    			tempTile.addContent(collision_layer_id);
    			
    			// Add tile element to map
    			map.addContent(tempTile);
    		}
             
            // Output the file
            FileWriter file;
			
			// Check to see if the .map extension doesn't already exist on the file
			if (!filePath.contains(".tmf"))
				// If it doesn't, add it
				file = new FileWriter(filePath + ".tmf");
			else
				// Otherwise, don't add it
				file = new FileWriter(filePath);
			
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            outputter.output(document, file);
        } 
            
        catch (Exception e) 
        {
            e.printStackTrace();
        }
	}
}