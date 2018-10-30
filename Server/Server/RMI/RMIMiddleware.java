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

  private static int s_serverPort = 1099;

	private static String s_resourceServerName = "Resources";
	private static String s_resourceServer = "localhost";

	private static String s_flightServerName = "Flights";
	private static String s_carServerName = "Cars";
	private static String s_roomServerName = "Rooms";
	private static String s_customerServerName = "Customers";

	private static String s_flightServer = "localhost";
	private static String s_carServer = "localhost";
	private static String s_roomServer = "localhost";
	private static String s_customerServer = "localhost";

	private static String s_rmiPrefix = "group28";

	public RMIMiddleware(String flightServer, String carServer, String roomServer,
			String customerServer) {
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
				// Create a new Server object that routes to one test resource manager
				RMIMiddleware middleware = new RMIMiddleware(s_resourceServer);

				middleware.connectServers();
				// Dynamically generate the stub (client proxy)
				IResourceManager middlewareEndpoint =
						(IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

				// Bind the four remote objects to endpoints with different names, but to the
				// same middleware interface
				Registry l_registry;
				try {
					l_registry = LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					l_registry = LocateRegistry.getRegistry(1099);
				}
				final Registry registry = l_registry;
				registry.rebind(s_rmiPrefix + s_resourceServerName, middlewareEndpoint);

				Runtime.getRuntime().addShutdownHook(new Thread() {
					public void run() {
						try {
							registry.unbind(s_rmiPrefix + s_resourceServerName);
							System.out.println("'" + s_resourceServerName + "' resource manager unbound");
						} catch (Exception e) {
							System.err.println(
									(char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
							e.printStackTrace();
						}
					}
				});
				System.out
						.println("'" + s_resourceServerName + "' resource manager server ready and bound to '"
								+ s_rmiPrefix + s_resourceServerName + "'");
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

		} else if (args.length > 3) {
			s_flightServer = args[0];
			s_carServer = args[1];
			s_roomServer = args[2];
			s_customerServer = args[3];

			// Create the RMI server entry
			try {
				// Create a new Server object that routes to four RMs
				RMIMiddleware middleware =
						new RMIMiddleware(s_flightServer, s_carServer, s_roomServer, s_customerServer);

				middleware.connectServers();

				// Dynamically generate the stub (client proxy)
				IResourceManager middlewareEndpoint =
						(IResourceManager) UnicastRemoteObject.exportObject(middleware, 0);

				// Bind the four remote objects to endpoints with different names, but to the
				// same middleware interface
				Registry l_registry;
				try {
					l_registry = LocateRegistry.createRegistry(1099);
				} catch (RemoteException e) {
					l_registry = LocateRegistry.getRegistry(1099);
				}
				final Registry registry = l_registry;
				registry.rebind(s_rmiPrefix + s_flightServerName, middlewareEndpoint);
				registry.rebind(s_rmiPrefix + s_carServerName, middlewareEndpoint);
				registry.rebind(s_rmiPrefix + s_roomServerName, middlewareEndpoint);
				registry.rebind(s_rmiPrefix + s_customerServerName, middlewareEndpoint);

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
				System.out
						.println("'" + s_customerServerName + "' resource manager server ready and bound to '"
								+ s_rmiPrefix + s_customerServerName + "'");
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

		// connectServer(s_resourceServer, s_serverPort, s_resourceServerName);

		connectServer(s_flightServer, s_serverPort, s_flightServerName);
		connectServer(s_carServer, s_serverPort, s_carServerName);
		connectServer(s_roomServer, s_serverPort, s_roomServerName);
		connectServer(s_customerServer, s_serverPort, s_customerServerName);
	}
	
	public void connectServer(String server, int port, String name) {
		try {
			boolean first = true;
			while (true) {
				try {
					Registry registry = LocateRegistry.getRegistry(server, port);
					switch (name) {
						case "Resources": {
							m_resourceManager =
								(IResourceManager) registry.lookup(s_rmiPrefix + name);
						}
						case "Flights": {
							m_flightResourceManager =
								(IResourceManager) registry.lookup(s_rmiPrefix + name);
						}
						case "Cars": {
							m_carResourceManager =
								(IResourceManager) registry.lookup(s_rmiPrefix + name);
						}
						case "Rooms": {
							m_roomResourceManager =
								(IResourceManager) registry.lookup(s_rmiPrefix + name);
						}
						case "Customers": {
							m_customerResourceManager =
								(IResourceManager) registry.lookup(s_rmiPrefix + name);
						}
					}
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port
						+ "/" + s_rmiPrefix + name + "]");
					break;
				} catch (NotBoundException | RemoteException e) {
					if (first) {
					System.out.println("Waiting for '" + name + "' server [" + server + ":"
						+ port + "/" + s_rmiPrefix + name + "]");
					first = false;
					}
				}
			Thread.sleep(500);
			}
		} catch (Exception e) {
			System.err.println(
				(char) 27 + "[31;1mServer exception: " + (char) 27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}
}
