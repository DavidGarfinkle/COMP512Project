package Server.RMI;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import Server.Interface.*;
import Server.Common.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware extends Middleware {

  private static int s_serverPort = 1091;
	private static String s_rmiPrefix = "group28";

	private static String s_flightServerName = "Flight";
	private static String s_carServerName = "Car";
	private static String s_roomServerName = "Room";

	private static String s_flightServer = "localhost";
	private static String s_carServer = "localhost";
	private static String s_roomServer = "localhost";

	public static void main(String args[]) {

		if (args.length > 2) {
			s_flightServer = args[0];
			s_carServer = args[1];
			s_roomServer = args[2];

			// Create the RMI server entry
			try {

				// Create a new Server object that routes to four RMs
				RMIMiddleware middleware =new RMIMiddleware();
				middleware.connectServers();

				// Dynamically generate the stub (client proxy)
				IResourceManager middlewareEndpoint =
						(IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

				// Bind the four remote objects to endpoints with different names, but to the
				// same middleware interface
				Registry l_registry;
				try {
					l_registry = LocateRegistry.createRegistry(1091);
				} catch (RemoteException e) {
					l_registry = LocateRegistry.getRegistry(1091);
				}
				final Registry registry = l_registry;
				registry.rebind(s_rmiPrefix + s_flightServerName, middlewareEndpoint);
				registry.rebind(s_rmiPrefix + s_carServerName, middlewareEndpoint);
				registry.rebind(s_rmiPrefix + s_roomServerName, middlewareEndpoint);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							registry.unbind(s_rmiPrefix + s_flightServerName);
							System.out.println("'" + s_flightServerName + "' resource manager unbound");
							registry.unbind(s_rmiPrefix + s_carServerName);
							System.out.println("'" + s_carServerName + "' resource manager unbound");
							registry.unbind(s_rmiPrefix + s_roomServerName);
							System.out.println("'" + s_roomServerName + "' resource manager unbound");
						} catch (Exception e) {
							System.err.println(
									(char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
							e.printStackTrace();
						}
					}
				});
				System.out
						.println("'" + s_flightServerName + "' resource manager server ready and bound to '"
								+ s_rmiPrefix + s_flightServerName + "'");
				System.out.println("'" + s_carServerName + "' resource manager server ready and bound to '"
						+ s_rmiPrefix + s_carServerName + "'");
				System.out.println("'" + s_roomServerName + "' resource manager server ready and bound to '"
						+ s_rmiPrefix + s_roomServerName + "'");
			} catch (Exception e) {
				System.err
						.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
				e.printStackTrace();
				System.exit(1);
			}

			// Create and install a security manager
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
		}
	}

	public void connectServers() {
		connectServer(s_flightServer, s_serverPort, s_flightServerName);
		connectServer(s_carServer, s_serverPort, s_carServerName);
		connectServer(s_roomServer, s_serverPort, s_roomServerName);
	}

	public void connectServer(String server, int port, String name) {
		boolean first = true;
		while (true) {
			try {
				Registry registry = LocateRegistry.getRegistry(server, port);
				switch (name) {
					case "Flight": {
						flightRM =
							(IResourceManager) registry.lookup(s_rmiPrefix + name);
					}
					case "Car": {
						carRM =
							(IResourceManager) registry.lookup(s_rmiPrefix + name);
					}
					case "Room": {
						roomRM =
							(IResourceManager) registry.lookup(s_rmiPrefix + name);
					}
				}
				System.out.println("Connected to '" + name + "' server [" + server + ":" + port
					+ "/" + s_rmiPrefix + name + "]");
				Thread.sleep(500);
				break;
			} catch (NotBoundException | RemoteException | InterruptedException e) {
				if (first) {
				System.out.println("Waiting for '" + name + "' server [" + server + ":"
					+ port + "/" + s_rmiPrefix + name + "]");
				first = false;
				}
			}
		}
	}

	public RMIMiddleware() throws RemoteException
	{ 
		super();
	}
}
