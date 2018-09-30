package Server.Common;

import Server.Interface.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;

import java.util.*;
import java.io.*;

public class Middleware implements IResourceManager{

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
  
    public Middleware(String flightServer, String carServer, String roomServer, String customerServer) {
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

    public void connectServers()
	{
        connectServer(s_flightServer, s_serverPort, s_flightServerName);
        connectServer(s_carServer, s_serverPort, s_carServerName);
        connectServer(s_roomServer, s_serverPort, s_roomServerName);
        connectServer(s_customerServer, s_serverPort, s_customerServerName);
	}

    public void connectServer(String server, int port, String name)
	{
		try {
			boolean first = true;
			while (true) {
				try {
                    Registry registry = LocateRegistry.getRegistry(server, port);
                    switch (name){
                        case "Resources": {
                            m_resourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                        }
                        case "Car": {
                            m_carResourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                        }
                        case "Room": {
                            m_roomResourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                        }
                        case "Customer": {
                            m_customerResourceManager = (IResourceManager)registry.lookup(s_rmiPrefix + name);
                        }
                    }
					System.out.println("Connected to '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
					break;
				}
				catch (NotBoundException|RemoteException e) {
					if (first) {
						System.out.println("Waiting for '" + name + "' server [" + server + ":" + port + "/" + s_rmiPrefix + name + "]");
						first = false;
					}
				}
				Thread.sleep(500);
			}
		}
		catch (Exception e) {
			System.err.println((char)27 + "[31;1mServer exception: " + (char)27 + "[0mUncaught exception");
			e.printStackTrace();
			System.exit(1);
		}
	}

    public boolean addFlight(int xid, int flightNum, int flightSeats, int flightPrice) 
	throws RemoteException {
		Trace.info("RM::addFlight(" + xid + ", " + flightNum + ", " + flightSeats + ", $" + flightPrice + ") called");
        return m_resourceManager.addFlight(xid, flightNum, flightSeats, flightPrice);
    }
    
    public boolean addCars(int xid, String location, int numCars, int price) 
	throws RemoteException {
		Trace.info("RM::addCars(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        return m_resourceManager.addCars(xid, location, numCars, price);
    }
   
    public boolean addRooms(int xid, String location, int numRooms, int price) 
	throws RemoteException {
        Trace.info("RM::addRooms(" + xid + ", " + location + ", " + count + ", $" + price + ") called");
        return m_resourceManager.addRooms(xid, location, numRooms, price);
    }			    
			    
    public int newCustomer(int xid) 
	throws RemoteException {
        Trace.info("RM::newCustomer(" + xid + ") called");
        return m_resourceManager.newCustomer(xid);
    }
    
    public boolean newCustomer(int id, int cid)
    throws RemoteException {
        Trace.info("RM::newCustomer(" + xid + ", " + cid + ") called");
        return m_resourceManager.newCustomer(id, cid);
    }

    public boolean deleteFlight(int id, int flightNum) 
	throws RemoteException {
        Trace.info("RM::deleteFlight(" + id + ", " + flightNum + ") called");
        return m_resourceManager.deleteFlight(id, flightNum);
    }
    
    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */		    
    public boolean deleteCars(int id, String location) 
	throws RemoteException {
        return false;
    }

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(int id, String location) 
	throws RemoteException {
        return false;
    }
    
    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int id, int customerID) 
	throws RemoteException {
        return false;
    }

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int id, int flightNumber) 
	throws RemoteException {
        return 0;
    }

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(int id, String location) 
	throws RemoteException {
        return 0;
    }

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(int id, String location) 
	throws RemoteException {
        return 0;
    }

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int id, int customerID) 
	throws RemoteException {
        return null;
    }
    
    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    public int queryFlightPrice(int id, int flightNumber) 
	throws RemoteException {
        return 0;
    }

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(int id, String location) 
	throws RemoteException {
        return 0;
    }

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(int id, String location) 
	throws RemoteException {
        return 0;
    }

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int id, int customerID, int flightNumber) 
	throws RemoteException {
        return false;
    }

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int id, int customerID, String location) 
	throws RemoteException {
        return false;
    }

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int id, int customerID, String location) 
	throws RemoteException {
        return false;
    }

    /**
     * Reserve a bundle for the trip.
     *
	 * //TODO unreserveItem(): If any one of the individual reservations within the bundle fails, no changes will occur.
     *
     * @return Success
     */
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room)
	throws RemoteException {

        if (car) {
            reserveCar(id, customerId, location);
        }

        if (room) {
            reserveRoom(id, customerId, location);
        }

        for (int flightNum : flightNumbers) {
            reserveFlight(id, customerId, flightNum);
        }

        return false;
    }

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    public String getName()
    throws RemoteException {
        return null;
    }

}
