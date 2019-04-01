package mazeSolver;

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * Implement all actions the robot takes while mapping and solving the maze.
 * 
 * Main @author jonasschafer with additions of @author jonathancaines
 * and @author jakepierrepont
 */
public class Action
{

	/**
	 * Measuring fields around the robot if they have not already been measured
	 * before. Fetching each one sample. Avoids re-measuring already measured
	 * grids as measurements of IR sensor are reliable. Make multiple measures
	 * and take average to get more accurate values.
	 * 
	 * @throws IOException
	 */
	public static void scanSurrounding(CustomOccupancyMap map)
		throws IOException
	{
		for (int i = -90; i < 180; i += 90)
		{
			int[] tile = map.getSquareInDirection(i);
			if (map.getMazeMap()[tile[0]][tile[1]] == 0)
			{
				Coordinator.ROTATION_MOTOR.rotateTo(i);
				float[] IR = new float[1];
				Coordinator.IRSampler.fetchSample(IR, 0);
				// TODO Delay necessary? Try to do without delay
				Delay.msDelay(50);

				if (IR[0] < 25)
					// Set to wall
					map.updateMazeMap(tile[0], tile[1], -1);
				else
					// Set to path
					map.updateMazeMap(tile[0], tile[1], 1);
			}
		}
		Coordinator.ROTATION_MOTOR.rotateTo(0);
		EV3Server.sendMap();
	}

	/**
	 * Make next move in exploring the maze. Try to move to an unexplored path,
	 * otherwise backtrack to the previous tile with help of the visitStack.
	 * move forward > move right > move left > backtrack
	 * 
	 * @param map
	 * @throws IOException
	 */
	public static void makeMoveStep(CustomOccupancyMap map)
		throws IOException
	{
		int[][] mazeMap = map.getMazeMap();

		// squares out of the view of the robot on the left, front and right
		int[] left = map.getSquareInDirection(-90);
		int[] front = map.getSquareInDirection(0);
		int[] right = map.getSquareInDirection(90);

		// Determines where to drive
		// Calls movements for movements - front > right > left

		// If this is not a wall
		if (mazeMap[front[0]][front[1]] != -1)
		{
			// Set to following path
			int[] front2 = map.getSquareInDirection(front, 0);
			// If unexplored
			if (mazeMap[front2[0]][front2[1]] == 0)
			{
				// move forwards
				moveCarefullyAndMeasure(map, 0);
				return;
			}
		}
		if (mazeMap[right[0]][right[1]] != -1)
		{
			int[] right2 = map.getSquareInDirection(right, 90);
			if (mazeMap[right2[0]][right2[1]] == 0)
			{
				moveCarefullyAndMeasure(map, 90);
				return;
			}
		}
		if (mazeMap[left[0]][left[1]] != -1)
		{
			int[] left2 = map.getSquareInDirection(left, -90);
			if (mazeMap[left2[0]][left2[1]] == 0)
			{
				moveCarefullyAndMeasure(map, -90);
				return;
			}
		}

		// Otherwise backtrack to the previous square
		boolean backtrack = moveToTileFromStack(map, map.visitStack);
		// Invalid maze
		if (!backtrack)
		{
			LCD.clear();
			LCD.drawString("Detected green smh", 0, 0);
			Coordinator.buttons.waitForAnyPress();
			LCD.clear();
		}
		EV3Server.sendMap();
	}

	/**
	 * Checks for a red tile
	 * 
	 * @param map
	 * @throws IOException
	 */
	public static void checkForRed(CustomOccupancyMap map)
		throws IOException
	{
		float[] RGB = new float[3];
		Coordinator.ColourSampler.fetchSample(RGB, 0);
		String detectedColour = determineColour(RGB);
		if (detectedColour == "RED")
		{
			int[] robotPosition = map.getRobotPosition().clone();
			map.setEndTilePosition(robotPosition);
			map.updateMazeMap(robotPosition[0], robotPosition[1], 1);
		}
		EV3Server.sendMap();
	}

	/**
	 * Moves to the top of the stack + Use after endtile: if green return false
	 * 
	 * @param map
	 * @param stack
	 * @return true if performed as expected, false if found green
	 * @throws IOException
	 */
	public static boolean moveToTileFromStack(CustomOccupancyMap map, Stack<int[]> stack)
		throws IOException
	{
		if (stack.isEmpty())
			System.exit(1);

		int[] backtrackSquare = stack.pop();
		int angle = map.getAngleToSquare(backtrackSquare);

		if (angle == -270)
			angle = 90;
		if (angle == 270)
			angle = -90;

		if (angle == 180 || angle == -180)
		{
			Coordinator.pilot.rotate(angle / 2);
			map.updateRobotOrientation(angle / 2);
			Coordinator.pilot.rotate(angle / 2);
			map.updateRobotOrientation(angle / 2);
		}
		else
		{
			Coordinator.pilot.rotate(angle);
			map.updateRobotOrientation(angle);
		}

		Coordinator.pilot.travel(Coordinator.DISTANCE, true);
		float[] RGB = new float[3];

		while (Coordinator.pilot.isMoving())
		{
			Coordinator.ColourSampler.fetchSample(RGB, 0);
			Delay.msDelay(30);

			String detectedColour = determineColour(RGB);

			if (detectedColour == "GREEN")
			{
				double drivenDistance = (double) Coordinator.pilot.getMovement().getDistanceTraveled();
				Coordinator.pilot.stop(); //necessary?
				Delay.msDelay(250);

				// Travel back
				Coordinator.pilot.travel(-drivenDistance);
				int[] front = map.getSquareInDirection(0);
				int[] greenTile = map.getSquareInDirection(front, 0);
				// Set everything surrounding the green tile to walls
				for (int i = 0; i < 360; i += 90)
				{
					int[] tile = map.getSquareInDirection(greenTile, i);
					map.updateMazeMap(tile[0], tile[1], -1);
				}
				map.updateMazeMap(greenTile[0], greenTile[1], -1);
				return false;
			}
		}
		map.updateRobotPosition();
		EV3Server.sendMap();
		return true;
	}

	/**
	 * Returns colour name
	 * 
	 * @param RGB
	 * @return colour name "WHITE", "GREEN" or "RED"
	 */
	public static String determineColour(float[] RGB)
	{
		float average = (RGB[0] + RGB[1] + RGB[2]) / 3.0f;
		if (RGB[0] > 2 * average)
			return "RED";
		if (RGB[1] > 1.3 * average && average > 0.05)
			return "GREEN";

		//Anything else is considered white
		return "WHITE";
	}

	/**
	 * Moves to the next tile and simultaneously checks for coloured tiles.
	 * Handles all situations of coloured tiles internally
	 * 
	 * @param map
	 * @param direction
	 * @throws IOException
	 */
	public static void moveCarefullyAndMeasure(CustomOccupancyMap map, int direction)
		throws IOException
	{
		map.visitStack.push(map.getRobotPosition().clone());

		float[] RGB = new float[3];

		Coordinator.pilot.rotate(direction);
		map.updateRobotOrientation(direction);
		// True means it returns right away and allows for measurements while moving
		Coordinator.pilot.travel(Coordinator.DISTANCE, true);

		while (Coordinator.pilot.getMovement().getDistanceTraveled() < Coordinator.DETECT_COLOUR_WHILE_MOVING_THRESHOLD)
		{
			Coordinator.ColourSampler.fetchSample(RGB, 0);
			Delay.msDelay(30);

			String detectedColour = determineColour(RGB);

			if (detectedColour == "GREEN")
			{
				double drivenDistance = (double) Coordinator.pilot.getMovement().getDistanceTraveled();
				Coordinator.pilot.stop(); //necessary?
				Delay.msDelay(250);

				map.visitStack.pop();

				// Travel back
				Coordinator.pilot.travel(-(drivenDistance + 3.5));
				int[] front = map.getSquareInDirection(0);
				int[] greenTile = map.getSquareInDirection(front, 0);
				// Set everything surrounding the green tile to walls
				for (int i = 0; i < 360; i += 90)
				{
					int[] tile = map.getSquareInDirection(greenTile, i);
					map.updateMazeMap(tile[0], tile[1], -1);
				}
				map.updateMazeMap(greenTile[0], greenTile[1], -1);

				// Turn back
				Coordinator.pilot.rotate(-direction);
				map.updateRobotOrientation(-direction);
				EV3Server.sendMap();
				return;
			}
		}
		// If no special colours
		map.updateRobotPosition();
		int[] robotPosition = map.getRobotPosition();
		map.updateMazeMap(robotPosition[0], robotPosition[1], 1);

		scanSurrounding(map);
		EV3Server.sendMap();
	}

	/**
	 * Calculates possible shortest paths based on the known and unknown state
	 * of the map until the shortest path is found (explored as a path). Then it
	 * travels back to the end of the maze and follows the shortest path back to
	 * the start of the maze.
	 * 
	 * @param map
	 */
	public static void shortestPathBack(CustomOccupancyMap map)
		throws IOException
	{
		PathFinder pathFinder = new PathFinder(map.getMazeMap());
		int[] startTile = new int[] { 1, 1 };

		scanSurrounding(map);

		Stack<int[]> pathWithoutUnknowns = new Stack<>();
		Stack<int[]> pathWithUnknowns = new Stack<>();

		pathWithUnknowns = pathFinder.getPath(map.getEndTilePosition(), startTile, true);
		pathWithoutUnknowns = pathFinder.getPath(map.getEndTilePosition(), startTile, false);
		
		/*
		 * Segment 1: Check if there exists a shortest explored route
		 * 
		 * If so, travel back to the end of the maze and follow the route to the start
		 */
		if (pathWithUnknowns.size() == pathWithoutUnknowns.size())
		{

			Stack<int[]> pathToEndTile = new Stack<>();
			pathToEndTile = pathFinder.getPath(map.getRobotPosition(), map.getEndTilePosition(), false);
			while (!pathToEndTile.isEmpty())
			{
				boolean usual = Action.moveToTileFromStack(map, pathToEndTile);
				EV3Server.sendMap();
				if (!usual)
					return;
			}
			while (!pathWithoutUnknowns.isEmpty())
			{
				boolean usual = Action.moveToTileFromStack(map, pathWithoutUnknowns);
				EV3Server.sendMap();
				if (!usual)
					return;
			}
			return;
		}

		int[][] pathCopy = new int[pathWithUnknowns.size()][2];
		pathWithUnknowns.toArray(pathCopy);
		int oldStackSize = pathWithUnknowns.size();

		/*
		 * Segment 2: Check if robot is on end tile
		 * 
		 * Move one tile along the unexplored shortest path
		 */
		if (Arrays.equals(map.getRobotPosition(), map.getEndTilePosition()))
		{
			Action.scanSurrounding(map);
			moveCarefullyAndMeasure(map, map.getAngleToSquare(pathWithUnknowns.peek()));
			map.visitStack.removeAllElements();
			return;
		}

		/*
		 * Segment 3: Check if robot is currently on the to-be-explored path
		 * 
		 * Manipulate stack so that the robot can follow the currently unexplored path from where he is
		 */
		int counter = 0;
		for (int i = pathCopy.length - 1; i >= 0; i--)
		{
			counter++;
			if (Arrays.equals(map.getRobotPosition(), pathCopy[i]))
			{
				while (counter != 0)
				{
					pathWithUnknowns.pop();
					counter--;
				}
				break;
			}
		}

		/*
		 * Segment 4: If the stack has been changed i.e. if the robot is on the to-be-explored path
		 * 
		 * Follow the path from now on
		 */
		if (oldStackSize != pathWithUnknowns.size())
		{
			while (!pathWithUnknowns.isEmpty())
			{
				int[] path = pathWithUnknowns.peek();
				if (map.getMazeMap()[path[0]][path[1]] != 0)
				{
					boolean usual = moveToTileFromStack(map, pathWithUnknowns);
					if (!usual)
						return;
				}
				else
				{
					scanSurrounding(map);
					moveCarefullyAndMeasure(map, map.getAngleToSquare(pathWithUnknowns.peek()));
					map.visitStack.removeAllElements();
					break;
				}
				EV3Server.sendMap();
			}
			return;
		}

		/*
		 * Segment 5: Not on any earlier segment means the robot is not on the to-be-explored path, 
		 * has not found a shortest path
		 * 
		 * Return on a known route back to the end of the maze and follow the to-be-explored route
		 */
		Stack<int[]> pathToEndTile = new Stack<>();
		pathToEndTile = pathFinder.getPath(map.getRobotPosition(), map.getEndTilePosition(), false);
		while (!pathToEndTile.isEmpty())
		{
			boolean usual = moveToTileFromStack(map, pathToEndTile);
			EV3Server.sendMap();
			if (!usual)
				return;
		}
	}
}
