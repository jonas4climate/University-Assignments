package mazeSolver;

import java.io.*;
import java.net.*;

import lejos.hardware.lcd.LCD;
import lejos.utility.Delay;

/**
 * Set up EV3 as a server to send data to the PCClient.
 * 
 * @author jonasschafer
 */
public class EV3Server
{

	/**
	 * Port of Server set to first digits of Euler's number.
	 */
	public static final int     PORT = 2718;

	/**
	 * Socket of the EV3 server.
	 */
	private static ServerSocket server;

	/**
	 * Socket of the PC client.
	 */
	private static Socket       client;

	/**
	 * Initialises Connection with the PCClient.
	 * 
	 * @throws IOException
	 */
	public static void initializeBluetoothConnection()
		throws IOException
	{
		LCD.clear();
		LCD.drawString("Awaiting client...", 0, 0);
		Delay.msDelay(500);
		server = new ServerSocket(PORT);
		client = server.accept();
		OutputStream out = client.getOutputStream();
		LCD.clear();
		LCD.drawString("CONNECTED", 0, 1);
		Delay.msDelay(500);
		ObjectOutputStream oOut = new ObjectOutputStream(out);
		oOut.writeObject(Coordinator.map);
		LCD.clear();
		oOut.flush();
		Delay.msDelay(100);
	}
	
	/**
	 * Sends map to the Client
	 * 
	 * @throws IOException
	 */
	public static void sendMap()
		throws IOException
	{
		OutputStream out = client.getOutputStream();
		ObjectOutputStream oOut = new ObjectOutputStream(out);
		oOut.writeObject(Coordinator.map);
		oOut.flush();
		Delay.msDelay(100);
	}

	/**
	 * Ends connection with the PCClient.
	 * 
	 * @throws IOException
	 */
	public static void closeBluetoothConnection()
		throws IOException
	{
		OutputStream out = client.getOutputStream();
		DataOutputStream dOut = new DataOutputStream(out);
		dOut.writeUTF("Closing Server...");
		dOut.close();
		out.close();
		server.close();
	}
}
