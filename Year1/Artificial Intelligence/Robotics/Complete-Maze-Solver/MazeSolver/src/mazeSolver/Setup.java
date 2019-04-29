package mazeSolver;

import java.io.IOException;

import lejos.hardware.BrickFinder;
import lejos.hardware.Keys;
import lejos.hardware.ev3.EV3;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.navigation.MovePilot;
import lejos.utility.Delay;

/**
 * Setup exported to have a clearer Coordinator class
 *
 * Main @author jonasschafer
 * Calibration by @author jakepierrepont
 */
public class Setup
{
	// Motors, Sensors and other mandatory variables
	public static EV3                     ev3Brick;
	public static Keys                    buttons;

	public static EV3LargeRegulatedMotor  LEFT_MOTOR;
	public static EV3LargeRegulatedMotor  RIGHT_MOTOR;
	public static EV3MediumRegulatedMotor ROTATION_MOTOR;

	public static Wheel                   wheel1;
	public static Wheel                   wheel2;
	public static Chassis                 chassis;
	public static MovePilot               pilot;

	public static EV3IRSensor             IRSensor;
	public static EV3UltrasonicSensor     USSensor;
	public static EV3ColorSensor          ColourSensor;
	public static EV3GyroSensor           GyroSensor;

	public static SampleProvider          IRSampler;
	public static SampleProvider          USSampler;
	public static SampleProvider          ColourSampler;
	public static SampleProvider          GyroSampler;

	/**
	 * Distance to travel from one centre of a path grid to the centre of the
	 * next path grid.
	 */
	public static final double            DISTANCE                             = 39.65;

	/**
	 * Long front side of the maze
	 */
	public static final int               MAP_WIDTH                            = 19;
	/**
	 * Height seen as bird's eye perspective on the maze, equivalent to "length"
	 * or "depth"
	 */
	public static final int               MAP_HEIGHT                           = 13;

	/**
	 * CURRENTLY NOT USED Offset necessary to perform a correction in orientation of the
	 * robot
	 */
	public static final int               RECALIBRATION_THRESHHOLD             = 3;

	/**
	 * Manipulate to change time point when scanning colours is ignored and
	 * scanning walls begins in moveCarefully method. Set to 0 to abandon colour
	 * checking and set close to Coordinator.DISTANCE to measure walls only when
	 * movement has (almost) finished
	 */
	public static final double            DETECT_COLOUR_WHILE_MOVING_THRESHOLD = 0.66 * Coordinator.DISTANCE;

	/* 
	 * 						VISUALISATION OF GRID OBJECTS
	 * 
	 * Paths are always 30x30 squares, small paths of 10x10 can exist at corners 
	 * but are ignored as the robot cannot move here.
	 * 
	 *   30(W)
	 *   _____
	 *  |     |
	 *  |     |  30(L)
	 *  |_____|
	 *    
	 */

	/*
	 * Walls can be 10x10, 30x10 or 10x30. Again 10x10 segments are not perceived by the robot and ignored
	 * 
	 *   10(W)               10(W)            30(W)  
	 *    __                  __            ________    
	 *   |  |                |  |  10(L)   |        |10(L)
	 *   |  |  30(L)         |__|          |________|
	 *   |  |
	 *   |__|
	 */

	/**
	 * The maze representation and information.
	 */
	public static CustomOccupancyMap      map;

	/**
	 * Sets values for all motors, sensors, controls and sets up a Bluetooth
	 * connection with the PClient.
	 * 
	 * @throws IOException
	 *             Default exception
	 */
	public static void setup()
		throws IOException
	{
		ev3Brick = (EV3) BrickFinder.getLocal();
		buttons = ev3Brick.getKeys();

		// Set up direction the robot is facing
		LCD.drawString("Press Button for", 0, 0);
		LCD.drawString("Direction-Setup", 0, 1);
		LCD.drawString("Hold down button", 0, 3);
		LCD.drawString("until new text", 0, 4);
		LCD.drawString("appears...", 0, 5);

		map = new CustomOccupancyMap(MAP_WIDTH, MAP_HEIGHT, 90);

		LCD.clear();
		LCD.drawString("Setting up", 0, 0);
		LCD.drawString("Motors", 0, 1);
		LEFT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.C);
		RIGHT_MOTOR = new EV3LargeRegulatedMotor(MotorPort.A);
		ROTATION_MOTOR = new EV3MediumRegulatedMotor(MotorPort.D);

		LCD.clear();
		LCD.drawString("Setting up", 0, 0);
		LCD.drawString("Wheels,Chassis", 0, 1);
		wheel1 = WheeledChassis.modelWheel(LEFT_MOTOR, 5.5).offset(-4.9);
		wheel2 = WheeledChassis.modelWheel(RIGHT_MOTOR, 5.5).offset(4.9); //4.6
		chassis = new WheeledChassis(new Wheel[] { wheel1, wheel2 }, WheeledChassis.TYPE_DIFFERENTIAL);
		pilot = new MovePilot(chassis);
		pilot.setAngularSpeed(50);
		pilot.setLinearSpeed(50);
		pilot.setLinearAcceleration(30);
		ROTATION_MOTOR.setAcceleration(10000);

		LCD.clear();
		LCD.drawString("Setting up sensors...", 0, 0);
		IRSensor = new EV3IRSensor(SensorPort.S1);
		ColourSensor = new EV3ColorSensor(SensorPort.S2);

		IRSampler = IRSensor.getDistanceMode();
		ColourSampler = ColourSensor.getRGBMode();

		// Set up Bluetooth Connection
		EV3Server.initializeBluetoothConnection();

		// Mapping start warning
		LCD.clear();
		LCD.drawString("Setup complete!", 0, 0);
		Delay.msDelay(500);
		LCD.drawString("Begin Mapping in", 0, 1);
		Delay.msDelay(1000);
		LCD.drawString("3", 8, 4);
		Delay.msDelay(1000);
		LCD.drawString("2", 8, 4);
		Delay.msDelay(1000);
		LCD.drawString("1", 8, 4);
		Delay.msDelay(1000);
		LCD.clear();
	}
}
