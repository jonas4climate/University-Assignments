package mazeSolver;

import java.io.*;
import java.net.*;

/**
 * The Client that receives information from the EV3 Server.
 * 
 * @author jonasschaefer
 */
public class PCClient
{
	public static String  ip;

	public static Socket  sock;

	public static Display display;

	/**
	 * Connects from the client side.
	 * 
	 * @param args
	 *            Default arguments.
	 * @throws IOException
	 *             Default exception.
	 * @throws ClassNotFoundException
	 */
	public static void main(String[] args)
		throws IOException,
			ClassNotFoundException
	{
		setup(args);

		while (true)
		{
			try
			{
				System.out.println("Sending update...");
				CustomOccupancyMap map;
				InputStream in = sock.getInputStream();
				ObjectInputStream oIn = new ObjectInputStream(in);
				map = (CustomOccupancyMap) oIn.readObject();
				display.update(map);
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sets up the connection to the EV3Server
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void setup(String[] args)
		throws IOException,
			ClassNotFoundException
	{
		display = new Display();
		ip = "10.0.1.2"; // BT
		if (args.length > 0)
			ip = args[0];

		while (true)
		{
			try
			{
				sock = new Socket(ip, EV3Server.PORT);
				break;
			}
			catch (Exception e)
			{
				System.out.println("Trying to connect...");
			}
		}

		display.updateConnected();
		InputStream in = sock.getInputStream();
		ObjectInputStream oIn = new ObjectInputStream(in);
		CustomOccupancyMap map;
		map = (CustomOccupancyMap) oIn.readObject();
		display.update(map);
	}
}
