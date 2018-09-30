package Server.Common;

import Server.Interface.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;

public class Middleware implements IResourceManager {

  // test with one resource manager
  private static String s_resourceServer = "ResourceServer";
  private static String s_resourceServerName = "Resources";
  IResourceManager m_resourceManager = null;

  private static String s_flightServer = "FlightServer";
  private static String s_carServer = "CarServer";
  private static String s_roomServer = "RoomServer";
  private static String s_customerServer = "CustomerServer";

  private static String s_flightServerName = "Flight";
  private static String s_carServerName = "Car";
  private static String s_roomServerName = "Room";
  private static String s_customerServerName = "Customer";

  IResourceManager m_flightResourceManager = null;
  IResourceManager m_carResourceManager = null;
  IResourceManager m_roomResourceManager = null;
  IResourceManager m_customerResourceManager = null;

  private static int s_serverPort = 1099;
  private static String s_rmiPrefix = "group28";

  public Middleware(String flightServer, String carServer, String roomServer,
	  String customerServer) {
	s_flightServer = flightServer;
	s_carServer = carServer;
	s_roomServer = roomServer;
	s_customerServer = customerServer;
	connectServers();
  }

  // test with one resource manager
  public Middleware(String resourceServer) {
	s_resourceServer = resourceServer;
	connectServer(s_resourceServer, s_serverPort, s_resourceServerName);
  }

  public void connectServers() {
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
			case "Car": {
			  m_carResourceManager =
				  (IResourceManager) registry.lookup(s_rmiPrefix + name);
			}
			case "Room": {
			  m_roomResourceManager =
				  (IResourceManager) registry.lookup(s_rmiPrefix + name);
			}
			case "Customer": {
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

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice)
	  throws RemoteException {
	Trace.info("RM::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
		+ flightPrice + ") called");
	return m_resourceManager.addFlight(xid, flightnumber, flightSeats, flightPrice);
  }

  public boolean addCars(int xid, String location, int numCars, int price)
	  throws RemoteException {
	Trace.info("RM::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price
		+ ") called");
	return m_resourceManager.addCars(xid, location, numCars, price);
  }

  public boolean addRooms(int xid, String location, int numRooms, int price)
	  throws RemoteException {
	Trace.info("RM::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price
		+ ") called");
	return m_resourceManager.addRooms(xid, location, numRooms, price);
  }

  public int newCustomer(int xid) throws RemoteException {
	Trace.info("RM::newCustomer(" + xid + ") called");
	return m_resourceManager.newCustomer(xid);
  }

  public boolean newCustomer(int xid, int cid) throws RemoteException {
	Trace.info("RM::newCustomer(" + xid + ", " + cid + ") called");
	return m_resourceManager.newCustomer(xid, cid);
  }

  public boolean deleteFlight(int xid, int flightnumber) throws RemoteException {
	Trace.info("RM::deleteFlight(" + xid + ", " + flightnumber + ") called");
	return m_resourceManager.deleteFlight(xid, flightnumber);
  }

  public boolean deleteCars(int xid, String location) throws RemoteException {
	Trace.info("RM::deleteCars(" + xid + ", " + location + ") called");
	return m_resourceManager.deleteCars(xid, location);
  }

  public boolean deleteRooms(int xid, String location) throws RemoteException {
	Trace.info("RM::deleteRooms(" + xid + ", " + location + ") called");
	return m_resourceManager.deleteRooms(xid, location);
  }

  public boolean deleteCustomer(int xid, int cid) throws RemoteException {
	Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") called");
	return m_resourceManager.deleteCustomer(xid, cid);
  }

  public int queryFlight(int xid, int flightNumber) throws RemoteException {
	Trace.info("RM::queryFlight(" + xid + ", " + flightNumber + ") called");
	return m_resourceManager.queryFlight(xid, flightNumber);
  }

  public int queryCars(int xid, String location) throws RemoteException {
	Trace.info("RM::queryCars(" + xid + ", " + location + ") called");
	return m_resourceManager.queryCars(xid, location);
  }

  public int queryRooms(int xid, String location) throws RemoteException {
	Trace.info("RM::queryRooms(" + xid + ", " + location + ") called");
	return m_resourceManager.queryRooms(xid, location);
  }

  public String queryCustomerInfo(int xid, int cid) throws RemoteException {
	Trace.info("RM::queryCustomerInfo(" + xid + ", " + cid + ") called");
	return m_resourceManager.queryCustomerInfo(xid, cid);
  }

  public int queryFlightPrice(int xid, int flightNumber) throws RemoteException {
	Trace.info("RM::queryFlightPrice(" + xid + ", " + flightNumber + ") called");
	return m_resourceManager.queryFlightPrice(xid, flightNumber);
  }

  public int queryCarsPrice(int xid, String location) throws RemoteException {
	Trace.info("RM::queryCarsPrice(" + xid + ", " + location + ") called");
	return m_resourceManager.queryCarsPrice(xid, location);
  }

  public int queryRoomsPrice(int xid, String location) throws RemoteException {
	Trace.info("RM::queryRoomsPrice(" + xid + ", " + location + ") called");
	return m_resourceManager.queryRoomsPrice(xid, location);
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber) throws RemoteException {
	Trace.info("RM::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
	return m_resourceManager.reserveFlight(xid, cid, flightNumber);
  }

  public boolean reserveCar(int xid, int cid, String location) throws RemoteException {
	Trace.info("RM::reserveCar(" + xid + ", " + cid + ", " + location + ") called");
	return m_resourceManager.reserveCar(xid, cid, location);
  }

  public boolean reserveRoom(int xid, int cid, String location) throws RemoteException {
	Trace.info("RM::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");
	return m_resourceManager.reserveRoom(xid, cid, location);
  }

  public boolean bundle(int xid, int cid, Vector<String> flightNumbers, String location,
	  boolean car, boolean room) throws RemoteException {
	String flights = "";
	for (String flightNumber : flightNumbers) {
	  flights = flights + flightNumber + " ";
	}
	Trace.info("RM::bundle(" + xid + ", " + cid + ", " + flights + ", " + location + ", " + car
		+ ", " + room + ") called");
	return m_resourceManager.reserveRoom(xid, cid, location);
  }

  public String getName() throws RemoteException {
	return null;
  }
}
