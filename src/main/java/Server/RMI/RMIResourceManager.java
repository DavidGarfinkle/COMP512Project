// -------------------------------
// adapted from Kevin T. Manley
// CSE 593
// -------------------------------

package Server.RMI;

import Server.Interface.*;
import Server.Common.*;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIResourceManager extends ResourceManager {
	private static String s_serverName = "Server";
	private static String s_rmiPrefix = "group28";
	RMIResourceManager server;
	Registry registry;

	public static Registry createOrGetRegistry(int registryPort) throws RemoteException
	{
		Registry registry;
		// Locate the registry
		try {
			registry = LocateRegistry.createRegistry(registryPort);
		} catch (RemoteException e) {
			registry = LocateRegistry.getRegistry(registryPort);
		}
		return registry;
	}

	public void bind() throws RemoteException
	{
		// Dynamically generate the stub (client proxy)
		IResourceManager resourceManager = (IResourceManager)UnicastRemoteObject.exportObject(this, 0);
		registry = createOrGetRegistry(1088);

		// Bind the remote object's stub in the registry
		try {
			registry.rebind(s_rmiPrefix + s_serverName, resourceManager);
			System.out.println("'" + s_serverName + "' resource manager server ready and bound to '" + s_rmiPrefix + s_serverName + "'");
		}
		catch(Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
		}
	}

	public void unbind()
	{
		try {
			registry.unbind(s_rmiPrefix + s_serverName);
			System.out.println("'" + s_serverName + "' resource manager unbound");
		}
		catch(Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
		}
	}

	public static void main(String args[])
	{
		if (args.length > 0)
		{
			s_serverName = args[0];
		}

		// Create the RMI server entry
		try {
			// Create a new Server object
			RMIResourceManager server = new RMIResourceManager(s_serverName);

			server.bind();

			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					server.unbind();
				}
			});
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}

		// Create and install a security manager
		if (System.getSecurityManager() == null)
		{
			System.setSecurityManager(new SecurityManager());
		}
	}

	public RMIResourceManager(String name)
	{
		super(name);
	}
}
