package core;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.Icon;

/**
 * A tile which can be selected in one of the TilePanels.
 */
public class Tile extends JLabel
{
	private static final long serialVersionUID = 4917332631093002577L;
	private boolean selected = false;
	private Image image;
	private Icon iconImage;
	private TilePanel parentTilePanel;
	private int id;
	
	/**
	 * This constructs a drawable tile to be displayed on a TilePanel
 	 * which can be added to a TilePanel
	 * @param eraseTile - The image to be drawn
	 * @param tilePanel - The TilePanel to which this instance of Tile belongs
	 * @param id - A unique ID, used for the selection functionality
	 */
	public Tile(Image eraseTile, TilePanel tilePanel, int id)
	{
		super(new ImageIcon(eraseTile));
		iconImage = new ImageIcon(eraseTile);
		this.id = id;
		this.image = eraseTile;
		parentTilePanel = tilePanel;
		addMouseListener(new TileListener());
	}
	
	/**
	 * Gets the TilePanel to which this Tile belongs
	 * @return the TilePanel to which this Tile belongs
	 */
	public TilePanel getParentTilePanel()
	{
		return parentTilePanel;
	}
	
	/**
	 * @Override of the JLabel method
	 * Paints the component. If selected,
	 * paints a box around the tile
	 */
	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		if (selected)
		{
			// Draw the selection square 
			Graphics2D g2 = (Graphics2D) g;
			g.setColor(Color.CYAN);
			g2.setStroke(new BasicStroke(3.5f));
			g2.drawRect(0, 0, iconImage.getIconWidth(), iconImage.getIconHeight());
		}
	}
	
	/**
	 * Sets the selection status of this Tile
	 * @param flag - Whether or not this is the currently selected Tile
	 */
	public void setSelected(boolean flag)
	{
		selected = flag;
		repaint();
	}
	
	/**
	 * Sets the id of this Tile
	 * @param id - Tile id to set
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * Gets the image displayed in this Tile
	 * @return The image displayed in this Tile
	 */
	public Image getImage()
	{
		return image;
	}
	
	/**
	 * Simple MouseListener used for updating the selected Tile
	 */
	class TileListener implements MouseListener
	{
		public void mousePressed(MouseEvent e)
		{
			// Tell the MapPanel which panel was selected last
			if (parentTilePanel.isObjectPanel())
				parentTilePanel.getMapPanel().setObjectPanelSelectedLast(true);
			else
				parentTilePanel.getMapPanel().setObjectPanelSelectedLast(false);
			
			// Tell the parent TilePanel that this tile has been selected
			if (id < 0)
				parentTilePanel.setSelectedTile(0);
			else
				parentTilePanel.setSelectedTile(id);
			repaint();
		}
		
		public void mouseEntered(MouseEvent e)
		{
			if (e.getModifiers() == 16)
			{
				// Tell the MapPanel which panel was selected last
				if (parentTilePanel.isObjectPanel())
					parentTilePanel.getMapPanel().setObjectPanelSelectedLast(true);
				else
					parentTilePanel.getMapPanel().setObjectPanelSelectedLast(false);
				
				// Tell the parent TilePanel that this tile has been selected
				if (id < 0)
					parentTilePanel.setSelectedTile(0);
				else
					parentTilePanel.setSelectedTile(id);
				repaint();
			}
		}
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}