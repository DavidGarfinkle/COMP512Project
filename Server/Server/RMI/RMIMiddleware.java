package Server.RMI;

import java.util.*;

import Server.Interface.IResourceManager;
import Server.Common.Middleware;

import java.rmi.RemoteException;
import java.io.*;

public class RMIMiddleware extends Middleware{
  private static String s_flightServer = "FlightServer";
  private static String s_carServer = "CarServer";
  private static String s_roomServer = "RoomServer";
  private static String s_customerServer = "CustomerServer";
	private static String s_rmiPrefix = "group28";
	public static void main(String args[])
	{
		if (args.length > 3)
		{
      s_flightServer = args[0];
      s_carServer = args[1];
      s_roomServer = args[2];     
      s_customerServer = args[3];       
		}

	public RMIMiddleware(String flightServer, String carServer, String roomServer, String customerServer, )
	{
		super(flightServer, carServer, roomServer, customerServer);
	}
}