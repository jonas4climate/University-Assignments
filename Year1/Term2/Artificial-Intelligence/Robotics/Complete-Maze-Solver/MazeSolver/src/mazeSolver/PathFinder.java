package mazeSolver;

import java.util.PriorityQueue;
import java.util.Stack;

/**
 * Calculates A* path
 * 
 * @author jonathancaines
 */
public class PathFinder
{

	private int[][] map = new int[Coordinator.MAP_WIDTH][Coordinator.MAP_HEIGHT];

	public PathFinder(int[][] map)
	{
		this.map = map;
	}

	/**
	 * Returns a Stack to move by to get from start to end
	 * 
	 * @param start
	 *            beginning of the path
	 * @param end
	 *            end of the path
	 * @param unknown
	 *            true allows travelling over unknowns, false only allows known
	 *            paths
	 * @return Stack of paths to travel from start to end
	 */
	public Stack<int[]> getPath(int[] start, int[] end, boolean unknown)
	{
		PriorityQueue<GridSquare> queue = new PriorityQueue<GridSquare>();
		boolean[][] discovered = new boolean[Coordinator.MAP_WIDTH][Coordinator.MAP_HEIGHT];
		GridSquare currentSquare = new GridSquare(start, null, 0, getHeuristic(start, end));
		discovered[currentSquare.coords[0]][currentSquare.coords[1]] = true;
		GridSquare newSquare;
		int[] newCoords;
		int[] newerCoords;
		int cuCost = 0;

		while (currentSquare.coords[0] != end[0] || currentSquare.coords[1] != end[1])
		{
			for (int i = 0; i < 360; i += 90)
			{
				newCoords = currentSquare.coords.clone();
				newCoords = coordUpdater(i, newCoords);
				newerCoords = coordUpdater(i, newCoords.clone());
				if (unknown)
					cuCost = currentSquare.cost + getCostUnknown(newCoords, i);
				else
					cuCost = currentSquare.cost + getCostKnown(newCoords, newerCoords, i);

				if (!discovered[newCoords[0]][newCoords[1]])
				{
					newSquare = new GridSquare(newerCoords, currentSquare, cuCost, getHeuristic(newerCoords, end));
					queue.add(newSquare);
					discovered[newCoords[0]][newCoords[1]] = true;
					discovered[currentSquare.coords[0]][currentSquare.coords[1]] = true;
				}
			}
			currentSquare = queue.poll();
		}

		Stack<int[]> gridStack = new Stack<int[]>();
		while (currentSquare.coords[0] != start[0] || currentSquare.coords[1] != start[1])
		{
			gridStack.push(currentSquare.coords);
			currentSquare = currentSquare.parent;
		}
		return gridStack;
	}

	/**
	 * Getter for the path
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public Stack<int[]> getPath(int[] start, int[] end)
	{
		return getPath(start, end, false);
	}

	/**
	 * updates Coordinates
	 * 
	 * @param i
	 * @param c
	 * @return
	 */
	private int[] coordUpdater(int i, int[] c)
	{
		if (i == 0)
			c[0]++;
		if (i == 90)
			c[1]++;
		if (i == 180)
			c[0]--;
		if (i == 270)
			c[1]--;
		return c;
	}

	/**
	 * Get the cost for the robot to travel across this tile treating unknowns
	 * similar to paths.
	 * 
	 * @param coords
	 *            The coordinates of the tile.
	 * @param direction
	 *            The direction the robot will cross this tile in
	 * @return Cost to cross this tile
	 */
	public int getCostUnknown(int[] coords, int direction)
	{
		int x = coords[0];
		int y = coords[1];
		if (map[x][y] == -1)
			return 7500 + x + y;

		if (direction == 0 || direction == 270)
		{
			if (x % 2 == 0)
				return 10;
			else
				return 30;
		}
		else
		{
			if (y % 2 == 0)
				return 10;
			else
				return 30;
		}
	}

	/**
	 * Get the cost for the robot to travel across this tile treating unknowns
	 * as walls.
	 * 
	 * @param coordsWall
	 *            The coordinates of the wall tile.
	 * @param coordsTile
	 *            The tile from the robot's perspective behind the wall tile
	 *            (i.e. the tile the robot would drive onto)
	 * @param direction
	 *            The direction the robot will cross this tile in
	 * @return Cost to cross this tile
	 */
	public int getCostKnown(int[] coordsWall, int[] coordsTile, int direction)
	{
		int x1 = coordsWall[0];
		int y1 = coordsWall[1];

		int x2 = coordsTile[0];
		int y2 = coordsTile[1];

		try
		{
			if (map[x1][y1] != 1 || map[x2][y2] != 1)
				return 7500 + x1 + y1;
		}
		catch (IndexOutOfBoundsException e)
		{
			return 8000 + x1 + y1;
		}

		if (direction == 0 || direction == 270)
		{
			if (x1 % 2 == 0)
				return 10;
			else
				return 30;
		}
		else
		{
			if (y1 % 2 == 0)
				return 10;
			else
				return 30;
		}
	}

	/**
	 * Get the estimated cost of the journey from one tile to another.
	 * 
	 * @param start
	 *            Tile we are currently at.
	 * @param end
	 *            Tile to get to.
	 * @return The cost.
	 */
	public int getHeuristic(int[] start, int[] end)
	{
		return 50 * ((Math.abs(start[0] - end[0])) + (Math.abs(start[1] - end[1])));
	}
}
