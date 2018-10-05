package Server.Common;

import Server.Interface.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.util.*;
import java.io.*;

public abstract class Middleware implements IResourceManager {

  // test with one resource manager
  protected String s_resourceServer = "ResourceServer";
  protected String s_resourceServerName = "Resources";
  protected IResourceManager m_resourceManager = null;

  protected String s_flightServer = "FlightServer";
  protected String s_carServer = "CarServer";
  protected String s_roomServer = "RoomServer";
  protected String s_customerServer = "CustomerServer";

  protected String s_flightServerName = "Flight";
  protected String s_carServerName = "Car";
  protected String s_roomServerName = "Room";
  protected String s_customerServerName = "Customer";

  protected IResourceManager m_flightResourceManager = null;
  protected IResourceManager m_carResourceManager = null;
  protected IResourceManager m_roomResourceManager = null;
  protected IResourceManager m_customerResourceManager = null;

  protected int s_serverPort = 1099;
  protected String s_rmiPrefix = "group28";

  public Middleware(String flightServer, String carServer, String roomServer,
	  String customerServer) {
	s_flightServer = flightServer;
	s_carServer = carServer;
	s_roomServer = roomServer;
	s_customerServer = customerServer;
  }

  // test with one resource manager
  public Middleware(String resourceServer) {
	s_resourceServer = resourceServer;
  }

  public abstract void connectServers();

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

  public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room)
	throws RemoteException {
    Trace.info("RM::bundle(" + xid + ", " + customerID + ", " + flights + ", " + location + ", " + car
		+ ", " + room + ") called");
        if (car) {
            m_resourceManager.reserveCar(id, customerId, location);
        }

        if (room) {
            m_resourceManager.reserveRoom(id, customerId, location);
        }

        for (int flightNum : flightNumbers) {
            m_resourceManager.reserveFlight(id, customerId, flightNum);
        }

        return false;
    }
}
