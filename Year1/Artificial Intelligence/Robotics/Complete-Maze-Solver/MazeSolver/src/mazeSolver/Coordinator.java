package mazeSolver;

import java.io.IOException;
import java.util.Arrays;

/**
 * Main class of the maze exploration.
 * 
 * @author jonasschafer
 */
public class Coordinator extends Setup
{

	/**
	 * Main method the robot will execute.
	 * 
	 * @param args
	 *            The default parameter for main().
	 * @throws IOException
	 *             IOException.
	 */
	public static void main(String[] args)
		throws IOException
	{
		// Sets up map, sensors, motors, Bluetooth, etc.
		setup();
		// Scan once
		Action.scanSurrounding(map);

		findEndOfMaze(map);

		// Scan once now as red square was found
		Action.scanSurrounding(map);

		moveBack(map);

		// Done exploring
		EV3Server.closeBluetoothConnection();
	}

	/**
	 * Maps the maze until it finds the end tile (red)
	 * 
	 * @param map
	 * @throws IOException
	 */
	public static void findEndOfMaze(CustomOccupancyMap map)
		throws IOException
	{
		while (map.getEndTilePosition() == null)
		{
			Action.makeMoveStep(map);
			Action.checkForRed(map);
		}
		map.visitStack.removeAllElements();
	}

	/**
	 * While the robot has not returned to the beginning i.e. not followed or
	 * found the shortest path to the start, keep looking for such a path
	 * 
	 * @param map
	 * @throws IOException
	 */
	public static void moveBack(CustomOccupancyMap map)
		throws IOException
	{
		while (!Arrays.equals(map.getRobotPosition(), new int[] { 1, 1 }))
		{
			Action.shortestPathBack(map);
			EV3Server.sendMap();
		}
	}
}
