package Server.RMI;

import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIMiddleware extends Middleware {

	private static String s_resourceServerName = "Resources";
	private static String s_resourceServer = "localhost";

	private static String s_flightServerName = "Flight";
	private static String s_carServerName = "Car";
	private static String s_roomServerName = "Room";
	private static String s_customerServerName = "Customer";

	private static String s_flightServer = "localhost";
	private static String s_carServer = "localhost";
	private static String s_roomServer = "localhost";
	private static String s_customerServer = "localhost";

	private static String s_rmiPrefix = "group28";

	public RMIMiddleware(String flightServer, String carServer, String roomServer, String customerServer) {
		super(flightServer, carServer, roomServer, customerServer);
	}

	public RMIMiddleware(String resourceServer) {
		super(resourceServer);
	}

	public static void main(String args[]) {

		if (args.length == 1) {
			s_resourceServer = args[0];

			// Create the RMI server entry
			try {
				// Create a new Server object that routes to four RMs
				RMIMiddleware server = new RMIMiddleware(s_resourceServer);

				// Dynamically generate the stub (client proxy)
				IResourceManager middleware = (IResourceManager) UnicastRemoteObject.exportObject(server, 0);

				// Bind the four remote objects to endpoints with different names, but to the
				// same middleware interface
				Registry l_registry;
				try {
					l_registry = LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					l_registry = LocateRegistry.getRegistry(1099);
				}
				final Registry registry = l_registry;
				registry.rebind(s_rmiPrefix + s_resourceServerName, middleware);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							registry.unbind(s_rmiPrefix + s_resourceServerName);
							System.out.println("'" + s_resourceServerName + "' resource manager unbound");
						} catch (Exception e) {
							System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
							e.printStackTrace();
						}
					}
				});
				System.out.println("'" + s_resourceServerName + "' resource manager server ready and bound to '" + s_rmiPrefix +
				s_resourceServerName + "'");
			} catch (Exception e) {
				System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
				e.printStackTrace();
				System.exit(1);
			}

			// Create and install a security manager
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}

		} else if (args.length > 3) {
			s_flightServer = args[0];
			s_carServer = args[1];
			s_roomServer = args[2];
			s_customerServer = args[3];

			// Create the RMI server entry
			try {
				// Create a new Server object that routes to four RMs
				RMIMiddleware server = new RMIMiddleware(s_flightServer, s_carServer, s_roomServer, s_customerServer);

				// Dynamically generate the stub (client proxy)
				IResourceManager middleware = (IResourceManager) UnicastRemoteObject.exportObject(server, 0);

				// Bind the four remote objects to endpoints with different names, but to the
				// same middleware interface
				Registry l_registry;
				try {
					l_registry = LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					l_registry = LocateRegistry.getRegistry(1099);
				}
				final Registry registry = l_registry;
				registry.rebind(s_rmiPrefix + s_flightServerName, middleware);
				registry.rebind(s_rmiPrefix + s_carServerName, middleware);
				registry.rebind(s_rmiPrefix + s_roomServerName, middleware);
				registry.rebind(s_rmiPrefix + s_customerServerName, middleware);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							registry.unbind(s_rmiPrefix + s_flightServerName);
							System.out.println("'" + s_flightServerName + "' resource manager unbound");
							registry.unbind(s_rmiPrefix + s_carServerName);
							System.out.println("'" + s_carServerName + "' resource manager unbound");
							registry.unbind(s_rmiPrefix + s_roomServerName);
							System.out.println("'" + s_roomServerName + "' resource manager unbound");
							registry.unbind(s_rmiPrefix + s_customerServerName);
							System.out.println("'" + s_customerServerName + "' resource manager unbound");
						} catch (Exception e) {
							System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
							e.printStackTrace();
						}
					}
				});
				System.out.println("'" + s_flightServerName + "' resource manager server ready and bound to '" + s_rmiPrefix +
					s_flightServerName + "'");
				System.out.println("'" + s_carServerName + "' resource manager server ready and bound to '" + s_rmiPrefix +
					s_carServerName + "'");
				System.out.println("'" + s_roomServerName + "' resource manager server ready and bound to '" + s_rmiPrefix +
					s_roomServerName + "'");
				System.out.println("'" + s_customerServerName + "' resource manager server ready and bound to '" + s_rmiPrefix +
					s_customerServerName + "'");
			} catch (Exception e) {
				System.err.println((char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
				e.printStackTrace();
				System.exit(1);
			}

			// Create and install a security manager
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new SecurityManager());
			}
		}
	}
}