package mazeSolver;

/**
 * Helper Class for PathFinder
 *
 * @author jonathancaines
 */
public class GridSquare implements Comparable<GridSquare>
{

	public int[]      coords = new int[2];
	public GridSquare parent;
	public int        cost;
	public int        heuristic;

	/**
	 * Constructor
	 * 
	 * @param coords
	 * @param parent
	 * @param cost
	 * @param heuristic
	 */
	GridSquare(int[] coords, GridSquare parent, int cost, int heuristic)
	{
		this.coords = coords;
		this.cost = cost;
		this.heuristic = heuristic;
		this.parent = parent;
	}

	/**
	 * Getter for total cost
	 * 
	 * @return total cost
	 */
	public int getTotalCost()
	{
		return cost + heuristic;
	}

	@Override
	public int compareTo(GridSquare arg0)
	{
		return getTotalCost() - arg0.getTotalCost();
	}

	@Override
	public String toString()
	{
		if (parent != null)
		{
			return "[(" + String.valueOf(coords[0]) + "," + String.valueOf(coords[1]) + "), " + "{" + String.valueOf(parent.coords[0]) + ","
					+ String.valueOf(parent.coords[1]) + "}, " + String.valueOf(cost + heuristic) + "]";
		}
		return "[(" + String.valueOf(coords[0]) + "," + String.valueOf(coords[1]) + "), " + "{" + "null" + "}, "
				+ String.valueOf(cost + heuristic) + "]";
	}
}
