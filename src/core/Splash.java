package core;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * A simple class for displaying a splash screen
 */
public class Splash implements Runnable
{
	private MapperFrame parent;
	private final int INITIAL_WAIT = 350;
	private final int SPLASH_DISPLAY_TIME = 4000;
	
	/**
	 * Creates a splash screen for use with a frame
	 * @param parentFrame - The frame on which the splash screen is to be displayed
	 */
	public Splash(MapperFrame parentFrame)
	{
		parent = parentFrame;
	}
	
	/**
	 * Draws the splash screen to the window for the desired time
	 */
	public void run() 
	{
		try 
		{
			Thread.sleep(INITIAL_WAIT);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		BufferedImage splash = null;
		try 
		{
			splash = ImageIO.read(new File("img/splash.jpg"));
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		// If splash image has been created, draw it to the screen
		if (splash != null)
		{
			Graphics g = parent.getGraphics();
			g.drawImage(splash, parent.getWidth() / 2 - splash.getWidth() / 2, parent.getHeight() / 2 - splash.getHeight() / 2, null);
		}

		// Wait for the desired amount of time
		try 
		{
			Thread.sleep(SPLASH_DISPLAY_TIME);
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
		
		// Then redraw the frame, removing the splash image
		parent.repaint();
	}

}
