package menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

import core.MapperFrame;

/**
 * The Draw menu of the GUI
 */
public class DrawMenu extends Menu
{
	private static final long serialVersionUID = -4098491945692245016L;
	private MapperFrame parentFrame;
	private JRadioButtonMenuItem button1;
	private JRadioButtonMenuItem button2;
	private JRadioButtonMenuItem button3;
	private JRadioButtonMenuItem button5;
	private JRadioButtonMenuItem button10;
	
	/**
	 * Creates the Draw menu
	 * @param name - The text to be displayed at the top of the menu
	 * @param parentFrame - The frame on which the menu exists
	 */
	public DrawMenu(String name, MapperFrame parentFrame) 
	{
		super(name, parentFrame, -1);
		this.parentFrame = parentFrame;
	}

	@Override
	/**
	 * Creates all of the menu items of the Draw menu
	 */
	public ArrayList<JMenuItem> createMenuItems() 
	{
		ArrayList<JMenuItem> temp = new ArrayList<JMenuItem>();
		
		// Create  the radio buttons
		button1 = new JRadioButtonMenuItem("1 x 1");
		button2 = new JRadioButtonMenuItem("2 x 2");
		button3 = new JRadioButtonMenuItem("3 x 3");
		button5 = new JRadioButtonMenuItem("5 x 5");
		button10 = new JRadioButtonMenuItem("10 x 10");
		
		// Create the button group
		ButtonGroup group = new ButtonGroup();
		button1.setSelected(true);
		group.add(button1);
		group.add(button2);
		group.add(button3);
		group.add(button5);
		group.add(button10);
		
		// Add action listeners
		button1.addActionListener(new DrawCountActionListener());
		button2.addActionListener(new DrawCountActionListener());
		button3.addActionListener(new DrawCountActionListener());
		button5.addActionListener(new DrawCountActionListener());
		button10.addActionListener(new DrawCountActionListener());
		
		// Set key accelerators
		button1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, 0));
		button2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, 0));
		button3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, 0));
		button5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, 0));
		button10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, 0));
		
		// Add each item
		temp.add(button1);
		temp.add(button2);
		temp.add(button3);
		temp.add(button5);
		temp.add(button10);
		
		return temp;
	}
	
	/**
	 * Gets the selected amount to be drawn
	 * @return The amount of tiles to be drawn (amount x amount grid)
	 */
	private int drawAmount()
	{
		if(button1.isSelected())
			return 1;
		else if (button2.isSelected())
			return 2;
		else if (button3.isSelected())
			return 3;
		else if (button5.isSelected())
			return 5;
		else
			return 10;
	}
	
	/**
	 * ActionListener which listens for menu clicks and sets the draw count accordingly
	 */
	class DrawCountActionListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e) 
		{
			parentFrame.getMapPanel().setDrawCount(drawAmount());
			parentFrame.getMapPanel().repaintProjectedTiles();
		}
	}
	
}
