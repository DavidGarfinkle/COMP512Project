package Server.TCP;

import java.io.*;
import java.net.*;
import java.util.*;
import Server.Common.*;
import Server.Interface.ITCPResourceManager;

public class MiddlewareThread extends Thread {

	private static String SUCCESS = "SUCCESS";
	private static String FAIL = "FAIL";
	private static RMHashMap m_data = new RMHashMap();
	private static String flightServer;
	private static String carServer;
	private static String roomServer;
	private static String customerServer;

	private static int middlewarePort = 1098;
	private static int flightPort = 1098;
	private static int carPort = 1098;
	private static int roomPort = 1098;
	private static int customerPort = 1098;

	private Socket clientSocket;

	public MiddlewareThread(Socket clientSocket)
	{
		this.clientSocket = clientSocket;
		this.flightServer = TCPMiddleware.flightServer;
		this.carServer = TCPMiddleware.carServer;
		this.roomServer = TCPMiddleware.roomServer;
		this.customerServer = TCPMiddleware.customerServer;
		this.flightPort = TCPMiddleware.flightPort;
		this.carPort = TCPMiddleware.carPort;
		this.roomPort = TCPMiddleware.roomPort;
		this.customerPort = TCPMiddleware.customerPort;
	}
 
	public void run() {
		try {
			// Prepare for reading commands
			System.out.println();
			System.out.println("Prepare to read commands");

			InputStream input = clientSocket.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			OutputStream output = clientSocket.getOutputStream();
			PrintWriter writer = new PrintWriter(output, true);
			String command = "";
			do {
				// Read the next command
				Vector<String> arguments = new Vector<String>();
				try {
					System.out.print((char)27 + "[32;1m\n>] " + (char)27 + "[0m");
					command = reader.readLine().trim();
					System.out.println("Received request message: " + command);
				}
				catch (IOException io) {
					// System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0m" + io.getLocalizedMessage());
					// io.printStackTrace();
					return;
					// System.exit(1);
				}

				try {
					arguments = parse(command);
					Command cmd = Command.fromString((String)arguments.elementAt(0));
					System.out.println("Routing request message '" + command + "' to TCPResourceManager" );
					String response = execute(cmd, arguments, command);
					writer.println(response);
					}
					catch (Exception e) {
						System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mUncaught exception");
						e.printStackTrace();
				}

			} while (!command.equals("bye"));

			clientSocket.close();

		} catch (IOException ex) {
			System.out.println("Server exception: " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static Vector<String> parse(String command)
	{
		Vector<String> arguments = new Vector<String>();
		StringTokenizer tokenizer = new StringTokenizer(command,",");
		String argument = "";
		while (tokenizer.hasMoreTokens())
		{
			argument = tokenizer.nextToken();
			argument = argument.trim();
			arguments.add(argument);
		}
		return arguments;
	}
	
	public static void checkArgumentsCount(Integer expected, Integer actual) throws IllegalArgumentException
	{
		if (expected != actual)
		{
			throw new IllegalArgumentException("Invalid number of arguments. Expected " + (expected - 1) + ", received " + (actual - 1) + ". Location \"help,<CommandName>\" to check usage of this command");
		}
	}

	public static int toInt(String string) throws NumberFormatException
	{
		return (new Integer(string)).intValue();
	}

	public static boolean toBoolean(String string)// throws Exception
	{
		return (new Boolean(string)).booleanValue();
	}

	public String sendRequest(String hostname, int port, String msg) {

		try (Socket socket = new Socket(hostname, port)) {

				OutputStream output = socket.getOutputStream();
				PrintWriter writer = new PrintWriter(output, true);
				System.out.println("Sending request message: " + msg);
				writer.println(msg);

				InputStream input = socket.getInputStream();
				BufferedReader reader = new BufferedReader(new InputStreamReader(input));

				String response = reader.readLine();
				System.out.println("Got response: " + response);
				return response;    

		} catch (UnknownHostException ex) {

				System.out.println("Server not found: " + ex.getMessage());

		} catch (IOException ex) {

				System.out.println("I/O error: " + ex.getMessage());

		}
		return FAIL;
	}

	public String execute(Command cmd, Vector<String> arguments, String msg){
		switch (cmd)
		{
			case AddFlight: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding a new flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));
				System.out.println("-Flight Seats: " + arguments.elementAt(3));
				System.out.println("-Flight Price: " + arguments.elementAt(4));

				int id = toInt(arguments.elementAt(1));
				int flightNum = toInt(arguments.elementAt(2));
				int flightSeats = toInt(arguments.elementAt(3));
				int flightPrice = toInt(arguments.elementAt(4));

				return sendRequest(flightServer, flightPort, msg);
			}
		// 	case AddCars: {
		// 		checkArgumentsCount(5, arguments.size());

		// 		System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Car Location: " + arguments.elementAt(2));
		// 		System.out.println("-Number of Cars: " + arguments.elementAt(3));
		// 		System.out.println("-Car Price: " + arguments.elementAt(4));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);
		// 		int numCars = toInt(arguments.elementAt(3));
		// 		int price = toInt(arguments.elementAt(4));

		// 		if (addCars(id, location, numCars, price)) {
		// 			return SUCCESS;
		// 		}
		// 		break;
		// 	}
		// 	case AddRooms: {
		// 		checkArgumentsCount(5, arguments.size());

		// 		System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Room Location: " + arguments.elementAt(2));
		// 		System.out.println("-Number of Rooms: " + arguments.elementAt(3));
		// 		System.out.println("-Room Price: " + arguments.elementAt(4));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);
		// 		int numRooms = toInt(arguments.elementAt(3));
		// 		int price = toInt(arguments.elementAt(4));

		// 		if (addRooms(id, location, numRooms, price)) {
		// 			return SUCCESS;
		// 		}
		// 		break;
		// 	}
		// 	case AddCustomer: {
		// 		checkArgumentsCount(2, arguments.size());

		// 		System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

		// 		int id = toInt(arguments.elementAt(1));
		// 		String customer = Integer.toString(newCustomer(id));

		// 		System.out.println("Add customer ID: " + customer);
		// 		return customer;
		// 	}
		// 	case AddCustomerID: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));

		// 		if (newCustomer(id, customerID)) {
		// 			System.out.println("Add customer ID: " + customerID);
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Customer could not be added");
		// 		}
		// 		break;
		// 	}
		// 	case DeleteFlight: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Flight Number: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int flightNum = toInt(arguments.elementAt(2));

		// 		if (deleteFlight(id, flightNum)) {
		// 			System.out.println("Flight Deleted");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Flight could not be deleted");
		// 		}
		// 		break;
		// 	}
		// 	case DeleteCars: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Car Location: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		if (deleteCars(id, location)) {
		// 			System.out.println("Cars Deleted");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Cars could not be deleted");
		// 		}
		// 		break;
		// 	}
		// 	case DeleteRooms: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Car Location: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		if (deleteRooms(id, location)) {
		// 			System.out.println("Rooms Deleted");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Rooms could not be deleted");
		// 		}
		// 		break;
		// 	}
		// 	case DeleteCustomer: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));
				
		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));

		// 		if (deleteCustomer(id, customerID)) {
		// 			System.out.println("Customer Deleted");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Customer could not be deleted");
		// 		}
		// 		break;
		// 	}
		// 	case QueryFlight: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Flight Number: " + arguments.elementAt(2));
				
		// 		int id = toInt(arguments.elementAt(1));
		// 		int flightNum = toInt(arguments.elementAt(2));

		// 		String seats = Integer.toString(queryFlight(id, flightNum));
		// 		System.out.println("Number of seats available: " + seats);
		// 		return seats;
		// 	}
		// 	case QueryCars: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Car Location: " + arguments.elementAt(2));
				
		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		String numCars = Integer.toString(queryCars(id, location));
		// 		System.out.println("Number of cars at this location: " + numCars);
		// 		return numCars;
		// 	}
		// 	case QueryRooms: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Room Location: " + arguments.elementAt(2));
				
		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		String numRoom = Integer.toString(queryRooms(id, location));
		// 		System.out.println("Number of rooms at this location: " + numRoom);
		// 		return numRoom;
		// 	}
		// 	case QueryCustomer: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));

		// 		String bill = queryCustomerInfo(id, customerID);
		// 		return bill;
		// 	}
		// 	case QueryFlightPrice: {
		// 		checkArgumentsCount(3, arguments.size());
				
		// 		System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Flight Number: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int flightNum = toInt(arguments.elementAt(2));

		// 		String price = Integer.toString(queryFlightPrice(id, flightNum));
		// 		System.out.println("Price of a seat: " + price);
		// 		return price;
		// 	}
		// 	case QueryCarsPrice: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Car Location: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		String price = Integer.toString(queryCarsPrice(id, location));
		// 		System.out.println("Price of cars at this location: " + price);
		// 		return price;
		// 	}
		// 	case QueryRoomsPrice: {
		// 		checkArgumentsCount(3, arguments.size());

		// 		System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Room Location: " + arguments.elementAt(2));

		// 		int id = toInt(arguments.elementAt(1));
		// 		String location = arguments.elementAt(2);

		// 		String price = Integer.toString(queryRoomsPrice(id, location));
		// 		System.out.println("Price of rooms at this location: " + price);
		// 		return price;
		// 	}
		// 	case ReserveFlight: {
		// 		checkArgumentsCount(4, arguments.size());

		// 		System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));
		// 		System.out.println("-Flight Number: " + arguments.elementAt(3));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));
		// 		int flightNum = toInt(arguments.elementAt(3));

		// 		if (reserveFlight(id, customerID, flightNum)) {
		// 			System.out.println("Flight Reserved");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Flight could not be reserved");
		// 		}
		// 		break;
		// 	}
		// 	case ReserveCar: {
		// 		checkArgumentsCount(4, arguments.size());

		// 		System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));
		// 		System.out.println("-Car Location: " + arguments.elementAt(3));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));
		// 		String location = arguments.elementAt(3);

		// 		if (reserveCar(id, customerID, location)) {
		// 			System.out.println("Car Reserved");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Car could not be reserved");
		// 		}
		// 		break;
		// 	}
		// 	case ReserveRoom: {
		// 		checkArgumentsCount(4, arguments.size());

		// 		System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));
		// 		System.out.println("-Room Location: " + arguments.elementAt(3));
				
		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));
		// 		String location = arguments.elementAt(3);

		// 		if (reserveRoom(id, customerID, location)) {
		// 			System.out.println("Room Reserved");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Room could not be reserved");
		// 		}
		// 		break;
		// 	}
		// 	case Bundle: {
		// 		if (arguments.size() < 7) {
		// 			System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
		// 			break;
		// 		}

		// 		System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
		// 		System.out.println("-Customer ID: " + arguments.elementAt(2));
		// 		for (int i = 0; i < arguments.size() - 6; ++i)
		// 		{
		// 			System.out.println("-Flight Number: " + arguments.elementAt(3+i));
		// 		}
		// 		System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size()-3));
		// 		System.out.println("-Book Car: " + arguments.elementAt(arguments.size()-2));
		// 		System.out.println("-Book Room: " + arguments.elementAt(arguments.size()-1));

		// 		int id = toInt(arguments.elementAt(1));
		// 		int customerID = toInt(arguments.elementAt(2));
		// 		Vector<String> flightNumbers = new Vector<String>();
		// 		for (int i = 0; i < arguments.size() - 6; ++i)
		// 		{
		// 			flightNumbers.addElement(arguments.elementAt(3+i));
		// 		}
		// 		String location = arguments.elementAt(arguments.size()-3);
		// 		boolean car = toBoolean(arguments.elementAt(arguments.size()-2));
		// 		boolean room = toBoolean(arguments.elementAt(arguments.size()-1));

		// 		if (bundle(id, customerID, flightNumbers, location, car, room)) {
		// 			System.out.println("Bundle Reserved");
		// 			return SUCCESS;
		// 		} else {
		// 			System.out.println("Bundle could not be reserved");
		// 		}
		// 		break;
		// 	}
		// 	case Quit:
		// 		checkArgumentsCount(1, arguments.size());

		// 		System.out.println("Quitting Socket Thread");
		// 		System.exit(0);
		}
		return FAIL;
	}

}