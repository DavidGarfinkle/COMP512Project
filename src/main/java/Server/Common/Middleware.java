package Server.Common;

import Server.Interface.*;
import java.rmi.RemoteException;
import java.util.*;

public class Middleware implements IResourceManager {

  protected IResourceManager flightRM;
  protected IResourceManager carRM;
  protected IResourceManager roomRM;

  public Middleware() throws RemoteException {
  }

  public Middleware(IResourceManager flightRM, IResourceManager roomRM, IResourceManager carRM)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    this.flightRM = flightRM;
    this.carRM = carRM;
    this.roomRM = roomRM;
  }

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
        + flightPrice + ") called");
    return flightRM.addFlight(xid, flightnumber, flightSeats, flightPrice);
  }

  public boolean addCars(int xid, String location, int numCars, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price + ") called");
    return carRM.addCars(xid, location, numCars, price);
  }

  public boolean addRooms(int xid, String location, int numRooms, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info(
        "RM::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price + ") called");
    return roomRM.addRooms(xid, location, numRooms, price);
  }

  public int newCustomer(int xid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::newCustomer(" + xid + ") called");
    int cid = flightRM.newCustomer(xid);
    boolean roomSuccess = roomRM.newCustomer(xid, cid);
    boolean carSuccess = carRM.newCustomer(xid, cid);
    return cid;
  }

  public boolean newCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::newCustomer(" + xid + ", " + cid + ") called");
    return flightRM.newCustomer(xid, cid) && roomRM.newCustomer(xid, cid) && carRM.newCustomer(xid, cid);
  }

  public boolean deleteFlight(int xid, int flightnumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::deleteFlight(" + xid + ", " + flightnumber + ") called");
    return flightRM.deleteFlight(xid, flightnumber);
  }

  public boolean deleteCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::deleteCars(" + xid + ", " + location + ") called");
    return carRM.deleteCars(xid, location);
  }

  public boolean deleteRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::deleteRooms(" + xid + ", " + location + ") called");
    return roomRM.deleteRooms(xid, location);
  }

  public boolean deleteCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") called");
    return flightRM.deleteCustomer(xid, cid) && roomRM.deleteCustomer(xid, cid) && carRM.deleteCustomer(xid, cid);
  }

  public int queryFlight(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryFlight(" + xid + ", " + flightNumber + ") called");
    return flightRM.queryFlight(xid, flightNumber);
  }

  public int queryCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryCars(" + xid + ", " + location + ") called");
    return carRM.queryCars(xid, location);
  }

  public int queryRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryRooms(" + xid + ", " + location + ") called");
    return roomRM.queryRooms(xid, location);
  }

  public String queryCustomerInfo(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryCustomerInfo(" + xid + ", " + cid + ") called");
    return flightRM.queryCustomerInfo(xid, cid) + roomRM.queryCustomerInfo(xid, cid) + carRM.queryCustomerInfo(xid, cid);
  }

  public int queryFlightPrice(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryFlightPrice(" + xid + ", " + flightNumber + ") called");
    return flightRM.queryFlightPrice(xid, flightNumber);
  }

  public int queryCarsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryCarsPrice(" + xid + ", " + location + ") called");
    return carRM.queryCarsPrice(xid, location);
  }

  public int queryRoomsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::queryRoomsPrice(" + xid + ", " + location + ") called");
    return roomRM.queryRoomsPrice(xid, location);
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
    return flightRM.reserveFlight(xid, cid, flightNumber);
  }

  public boolean reserveCar(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::reserveCar(" + xid + ", " + cid + ", " + location + ") called");
    return carRM.reserveCar(xid, cid, location);
  }

  public boolean reserveRoom(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");
    return roomRM.reserveRoom(xid, cid, location);
  }

  public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location,
      boolean car, boolean room) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("RM::bundle(" + id + ", " + customerID + ", " + flightNumbers + ", " + location + ", "
        + car + ", " + room + ") called");
    if (car) {
      if (!reserveCar(id, customerID, location)) {
        return false;
      }
    }

    if (room) {
      if (!reserveRoom(id, customerID, location)) {
        return false;
      }
    }

    for (String flightNum : flightNumbers) {
      if (!reserveFlight(id, customerID, Integer.parseInt(flightNum))) {
        return false;
      }
    }

    return true;
  }

  public String getName() throws RemoteException {
    return null;
  }
}
