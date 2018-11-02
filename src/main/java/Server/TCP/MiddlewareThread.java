package Server.TCP;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

import Server.Common.*;

public class MiddlewareThread  implements Runnable {

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
				// try {
				System.out.print((char)27 + "[32;1m\n>] " + (char)27 + "[0m");
				// String lines = reader.lines().collect(Collectors.joining());
				// command = lines.trim();
				command = reader.readLine().trim();
				System.out.println("Received request message: " + command);
				// }
				// catch (IOException io) {
					// System.err.println((char)27 + "[31;1mClient exception: " + (char)27 + "[0m" + io.getLocalizedMessage());
					// io.printStackTrace();
					// return;
					// System.exit(1);
				// }

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

				return sendRequest(flightServer, flightPort, msg);
			}
			case AddCars: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new cars [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));
				System.out.println("-Number of Cars: " + arguments.elementAt(3));
				System.out.println("-Car Price: " + arguments.elementAt(4));

				return sendRequest(carServer, carPort, msg);
			}
			case AddRooms: {
				checkArgumentsCount(5, arguments.size());

				System.out.println("Adding new rooms [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));
				System.out.println("-Number of Rooms: " + arguments.elementAt(3));
				System.out.println("-Room Price: " + arguments.elementAt(4));

				return sendRequest(roomServer, roomPort, msg);
			}
			case AddCustomer: {
				checkArgumentsCount(2, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");

				return sendRequest(customerServer, customerPort, msg);
			}
			case AddCustomerID: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Adding a new customer [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				return sendRequest(customerServer, customerPort, msg);
			}
			case DeleteFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				return sendRequest(flightServer, flightPort, msg);
			}
			case DeleteCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all cars at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				return sendRequest(carServer, carPort, msg);
			}
			case DeleteRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting all rooms at a particular location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				return sendRequest(roomServer, roomPort, msg);
			}
			case DeleteCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Deleting a customer from the database [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				
				return sendRequest(customerServer, customerPort, msg);
			}
			case QueryFlight: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));
				
				return sendRequest(flightServer, flightPort, msg);
			}
			case QueryCars: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));
				
				return sendRequest(carServer, carPort, msg);
			}
			case QueryRooms: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));
				
				return sendRequest(roomServer, roomPort, msg);
			}
			case QueryCustomer: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying customer information [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));

				return sendRequest(customerServer, customerPort, msg);
			}
			case QueryFlightPrice: {
				checkArgumentsCount(3, arguments.size());
				
				System.out.println("Querying a flight price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Flight Number: " + arguments.elementAt(2));

				return sendRequest(flightServer, flightPort, msg);
			}
			case QueryCarsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying cars price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Car Location: " + arguments.elementAt(2));

				return sendRequest(carServer, carPort, msg);
			}
			case QueryRoomsPrice: {
				checkArgumentsCount(3, arguments.size());

				System.out.println("Querying rooms price [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Room Location: " + arguments.elementAt(2));

				return sendRequest(roomServer, roomPort, msg);
			}
			case ReserveFlight: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving seat in a flight [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Flight Number: " + arguments.elementAt(3));

				return sendRequest(customerServer, customerPort, msg);
			}
			case ReserveCar: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a car at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Car Location: " + arguments.elementAt(3));

				return sendRequest(carServer, carPort, msg);
			}
			case ReserveRoom: {
				checkArgumentsCount(4, arguments.size());

				System.out.println("Reserving a room at a location [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				System.out.println("-Room Location: " + arguments.elementAt(3));

				return sendRequest(roomServer, roomPort, msg);
			}
			case Bundle: {
				if (arguments.size() < 7) {
					System.err.println((char)27 + "[31;1mCommand exception: " + (char)27 + "[0mBundle command expects at least 7 arguments. Location \"help\" or \"help,<CommandName>\"");
					break;
				}

				System.out.println("Reserving an bundle [xid=" + arguments.elementAt(1) + "]");
				System.out.println("-Customer ID: " + arguments.elementAt(2));
				for (int i = 0; i < arguments.size() - 6; ++i)
				{
					System.out.println("-Flight Number: " + arguments.elementAt(3+i));
				}
				System.out.println("-Location for Car/Room: " + arguments.elementAt(arguments.size()-3));
				System.out.println("-Book Car: " + arguments.elementAt(arguments.size()-2));
				System.out.println("-Book Room: " + arguments.elementAt(arguments.size()-1));

				return sendRequest(customerServer, customerPort, msg);
			}
			case Quit:
				checkArgumentsCount(1, arguments.size());

				System.out.println("Quitting Socket Thread");
				System.exit(0);
		}
		return FAIL;
	}

}