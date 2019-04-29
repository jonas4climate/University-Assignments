package mazeSolver;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.util.Stack;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.Point;
import javax.swing.JProgressBar;

/**
 * GUI on the local machine displaying current progress of the robot in
 * exploring the Maze.
 * 
 * @author jonathancaines
 */
public class Display
{

	private final static int GRID_WIDTH  = Coordinator.MAP_WIDTH;
	private final static int GRID_HEIGHT = Coordinator.MAP_HEIGHT;

	//private boolean activeButtons;

	private JFrame           frmEvMazeSolver;
	private JProgressBar     progressBar;
	private JComponent[][]   grid        = new JComponent[GRID_HEIGHT][GRID_WIDTH];
	private JLabel           lblConnected;

	/**
	 * Update the display to have the correct colours.
	 * 
	 * @param map
	 *            A 2D array representation of the map status (see
	 *            CustomOccupancyMap).
	 */
	public void updateMap(int[][] map)
	{
		for (int i = 0; i < GRID_HEIGHT; i++)
			for (int j = 0; j < GRID_WIDTH; j++)
			{
				grid[i][j].setBackground(getBGColour(map[j][i]));
				grid[i][j].setForeground(getFGColour(map[j][i]));
			}
	}

	/**
	 * Set the Colour of a specific grid square.
	 * 
	 * @param xy
	 *            Coordinates of the grid square to be coloured in.
	 * @param c
	 *            The new colour of the grid square.
	 */
	public void setColour(int[] xy, Color c)
	{
		grid[xy[1]][xy[0]].setBackground(c);
	}

	/**
	 * To show on GUI when connected
	 */
	public void updateConnected()
	{
		lblConnected.setText("Connected");
	}

	/**
	 * Update the GUI to display the correct information about the robot.
	 * 
	 * @param data
	 *            The Robot's internal data to display.
	 */
	public void update(CustomOccupancyMap data)
	{
		updateMap(data.getMazeMap());
		progressBar.setValue(data.getCompletion());

		drawStack(data.visitStack, Color.CYAN);

		PathFinder pf = new PathFinder(data.getMazeMap());
		int[] end = data.getEndTilePosition();
		int[] position = data.getRobotPosition();

		if (end != null)
		{
			drawStack(pf.getPath(end, new int[] { 1, 1 }, true), Color.yellow);
			drawStack(pf.getPath(end, new int[] { 1, 1 }, false), Color.green);
			grid[end[1]][end[0]].setBackground(Color.red);
		}

		grid[position[1]][position[0]].setBackground(new Color(181, 70, 244));
		grid[position[1]][position[0]].setForeground(Color.BLACK);
	}

	/**
	 * Draws given stack on the GUI in specified colour
	 * 
	 * @param stack
	 * @param colour
	 */
	public void drawStack(Stack<int[]> stack, Color colour)
	{
		int[] path;
		while (!stack.isEmpty())
		{
			path = stack.pop();
			setColour(path, colour);
		}
	}

	/**
	 * Get the correct background colour for the grid square.
	 * 
	 * @param state
	 *            The state of the grid square (-1 occupied, 0 unknown, 1
	 *            clear).
	 * @return The colour for the GUI to display for this state.
	 */
	public static Color getBGColour(int state)
	{
		if (state == -1)
			return Color.BLACK;
		if (state == 0)
			return Color.LIGHT_GRAY;
		if (state == 1)
			return Color.WHITE;

		else
			return Color.ORANGE;
	}

	/**
	 * Get the correct foreground colour for the grid square.
	 * 
	 * @param state
	 *            The state of the grid square (-1 occupied, 0 unknown, 1,
	 *            clear).
	 * @return The colour for the GUI to display for this state.
	 */
	public static Color getFGColour(int state)
	{
		if (state == -1)
			return Color.WHITE;
		if (state == 0)
			return Color.BLACK;
		if (state == 1)
			return Color.BLACK;

		else
			return Color.BLACK;
	}

	/**
	 * Create the application with buttons inactive.
	 */
	public Display()
	{
		initialize();
		frmEvMazeSolver.setVisible(true);
		grid[1][0].setBackground(Color.BLUE);
	}

	/**
	 * Get the grid.
	 * 
	 * @return The 2D array containing all swing components on the grid.
	 */
	public JComponent[][] getGrid()
	{
		return grid;
	}

	/**
	 * Get a specific swing component from the grid.
	 * 
	 * @param x
	 *            x coordinate.
	 * @param y
	 *            y coordinate.
	 * @return Swing component at this location.
	 */
	public JComponent getItem(int x, int y)
	{
		return grid[y][x];
	}

	/**
	 * Get a specific swing component from the grid.
	 * 
	 * @param coords
	 *            Coordinates [x,y].
	 * @return Swing component at this location.
	 */
	public JComponent getItem(int[] coords)
	{
		return grid[coords[1]][coords[0]];
	}

	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize()
	{
		frmEvMazeSolver = new JFrame();
		frmEvMazeSolver.setResizable(true);
		frmEvMazeSolver.setTitle("EV3 Maze Solver");
		frmEvMazeSolver.setBounds(100, 100, 740, 600);
		frmEvMazeSolver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel pnlMiscInfo = new JPanel();
		pnlMiscInfo.setPreferredSize(new Dimension(740, 50));
		frmEvMazeSolver.getContentPane().add(pnlMiscInfo, BorderLayout.NORTH);
		pnlMiscInfo.setLayout(null);

		/*if (activeButtons)
			lblButtonsEnabled = new JLabel("Buttons Enabled.");
		else*/
		lblConnected = new JLabel("Awaiting Connection");
		lblConnected.setSize(158, 16);
		lblConnected.setLocation(new Point(12, 13));
		pnlMiscInfo.add(lblConnected);

		progressBar = new JProgressBar();
		progressBar.setValue(1);
		progressBar.setMaximum(247 - 70 - 18 - 12);
		progressBar.setStringPainted(true);
		progressBar.setBounds(564, 23, 146, 14);
		pnlMiscInfo.add(progressBar);

		JPanel pnlGridMap = new JPanel();
		pnlGridMap.setSize(new Dimension(740, 500));
		frmEvMazeSolver.getContentPane().add(pnlGridMap, BorderLayout.CENTER);
		GridBagLayout gbl_pnlGridMap = new GridBagLayout();
		
		gbl_pnlGridMap.rowHeights = new int[] { 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20 };
		gbl_pnlGridMap.columnWidths = new int[] { 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20, 60, 20 };
		pnlGridMap.setLayout(gbl_pnlGridMap);

		JLabel lblTemp;
		JButton btnTemp;
		GridBagConstraints gbc_btnTemp;
		GridBagConstraints gbc_lblTemp;
		for (int i = 0; i < GRID_HEIGHT; i++)
			for (int j = 0; j < GRID_WIDTH; j++)
			{
				if (i % 2 == 1)
				{
					//Create a tall label
					if (j % 2 == 0)
					{
						lblTemp = createWall(19, 59);
						gbc_lblTemp = new GridBagConstraints();
						gbc_lblTemp.gridx = j;
						gbc_lblTemp.gridy = 12 - i;
						pnlGridMap.add(lblTemp, gbc_lblTemp);
						grid[i][j] = lblTemp;

					}
					// Create a button
					else
					{
						btnTemp = new JButton("(" + String.valueOf(j / 2 + 1) + "," + String.valueOf(i / 2 + 1) + ")");
						btnTemp.addActionListener(new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								//The code executed when this button is pressed
							}
						});
						btnTemp.setPreferredSize(new Dimension(60, 60));
						btnTemp.setMinimumSize(new Dimension(59, 59));
						btnTemp.setMaximumSize(new Dimension(59, 59));
						btnTemp.setBackground(getBGColour(0));
						btnTemp.setForeground(getFGColour(0));
						btnTemp.setOpaque(true);
						gbc_btnTemp = new GridBagConstraints();
						gbc_btnTemp.gridx = j;
						gbc_btnTemp.gridy = 12 - i;
						pnlGridMap.add(btnTemp, gbc_btnTemp);
						grid[i][j] = btnTemp;
					}
				}

				else
				{
					// Create a small square label
					if (j % 2 == 0)
					{
						lblTemp = createWall(19, 19);
						gbc_lblTemp = new GridBagConstraints();
						gbc_lblTemp.gridx = j;
						gbc_lblTemp.gridy = 12 - i;
						pnlGridMap.add(lblTemp, gbc_lblTemp);
						grid[i][j] = lblTemp;
					}
					// Create a wide label
					else
					{
						lblTemp = createWall(59, 19);
						gbc_lblTemp = new GridBagConstraints();
						gbc_lblTemp.gridx = j;
						gbc_lblTemp.gridy = 12 - i;
						pnlGridMap.add(lblTemp, gbc_lblTemp);
						grid[i][j] = lblTemp;
					}
				}
			}
	}

	/**
	 * Creates a wall
	 * 
	 * @param width
	 * @param height
	 * @return JLabel
	 */
	private JLabel createWall(int width, int height)
	{
		JLabel out = new JLabel("");
		out.setSize(new Dimension(width, height));
		out.setPreferredSize(new Dimension(width, height));
		out.setMinimumSize(new Dimension(width, height));
		out.setMaximumSize(new Dimension(width, height));
		out.setBackground(getBGColour(0));
		out.setForeground(getFGColour(0));
		out.setOpaque(true);
		return out;
	}
}
