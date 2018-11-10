package Server.Common;

import Server.Interface.*;
import Server.LockManager.*;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.*;

public class Middleware implements IResourceManager {

  private static final Logger performanceLogger = Logger.getLogger("performance");

  protected IResourceManager flightRM;
  protected IResourceManager carRM;
  protected IResourceManager roomRM;

  // Transaction Manager
  protected TransactionManager TM;

  public Middleware() throws RemoteException {
    this.TM = new TransactionManager();
    try {
      performanceLogger.addHandler(new FileHandler("perf.log"));
    }
    catch(IOException e){
      System.out.println("Couldn't set up performance logger file handler");
    }
  }

  public Middleware(IResourceManager flightRM, IResourceManager roomRM, IResourceManager carRM)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    try {
      performanceLogger.addHandler(new FileHandler("perf.log"));
    }
    catch(IOException e){
      System.out.println("Couldn't set up performance logger file handler");
    }
    this.flightRM = flightRM;
    this.carRM = carRM;
    this.roomRM = roomRM;
    this.TM = new TransactionManager();
  }

  // dummy method
  public void start(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
  }

  public int start() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::start called");

    long begin = System.currentTimeMillis();
    int xid = TM.start();
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::start()", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return xid;
  }

  public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::commit called");

    long begin = System.currentTimeMillis();
    boolean res = TM.commit(xid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::commit(" + xid + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public void abort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::abort called");

    long begin = System.currentTimeMillis();
    TM.abort(xid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::abort(" + xid +")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));
  }

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
        + flightPrice + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    boolean res = flightRM.addFlight(xid, flightnumber, flightSeats, flightPrice);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::addFlight(" + String.join(",", String.valueOf(xid), String.valueOf(flightnumber), String.valueOf(flightSeats), String.valueOf(flightPrice)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean addCars(int xid, String location, int numCars, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    boolean res = carRM.addCars(xid, location, numCars, price);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::addCars(" + String.join(",", String.valueOf(xid), location, String.valueOf(numCars), String.valueOf(price)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean addRooms(int xid, String location, int numRooms, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info(
        "MW::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    boolean res = roomRM.addRooms(xid, location, numRooms, price);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::addRooms(" + String.join(",", String.valueOf(xid), location, String.valueOf(numRooms), String.valueOf(price)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int newCustomer(int xid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    TM.processTransaction(xid, carRM);
    int cid = flightRM.newCustomer(xid);
    boolean roomSuccess = roomRM.newCustomer(xid, cid);
    boolean carSuccess = carRM.newCustomer(xid, cid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::newCustomer(" + xid +")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return cid;
  }

  public boolean newCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ", " + cid + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    boolean res = flightRM.newCustomer(xid, cid) && roomRM.newCustomer(xid, cid) && carRM.newCustomer(xid, cid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::newCustomer(" + String.join(",", String.valueOf(xid), String.valueOf(cid)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean deleteFlight(int xid, int flightnumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteFlight(" + xid + ", " + flightnumber + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    boolean res =  flightRM.deleteFlight(xid, flightnumber);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::deleteFlight(" + String.join(",", String.valueOf(xid), String.valueOf(flightnumber)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean deleteCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCars(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    boolean res = carRM.deleteCars(xid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::deleteCars(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean deleteRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteRooms(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    boolean res = roomRM.deleteRooms(xid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::deleteRooms(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean deleteCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCustomer(" + xid + ", " + cid + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    boolean res = flightRM.deleteCustomer(xid, cid) && roomRM.deleteCustomer(xid, cid) && carRM.deleteCustomer(xid, cid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::deleteCustomer(" + String.join(",", String.valueOf(xid), String.valueOf(cid)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryFlight(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlight(" + xid + ", " + flightNumber + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    int res = flightRM.queryFlight(xid, flightNumber);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryFlight(" + String.join(",", String.valueOf(xid), String.valueOf(flightNumber)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCars(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    long end = System.currentTimeMillis();
    int res = carRM.queryCars(xid, location);
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryCars(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRooms(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    int res = roomRM.queryRooms(xid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryRooms(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public String queryCustomerInfo(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCustomerInfo(" + xid + ", " + cid + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    String res = "Flight bill: \n" + flightRM.queryCustomerInfo(xid, cid) + "\nRoom bill: \n" + roomRM.queryCustomerInfo(xid, cid) + "\nCar bill: \n" + carRM.queryCustomerInfo(xid, cid);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryCustomerInfo(" + String.join(",", String.valueOf(xid), String.valueOf(cid)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryFlightPrice(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlightPrice(" + xid + ", " + flightNumber + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    int res = flightRM.queryFlightPrice(xid, flightNumber);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryFlightPrice(" + String.join(",", String.valueOf(xid), String.valueOf(flightNumber)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryCarsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCarsPrice(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    int res = carRM.queryCarsPrice(xid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryCarsPrice(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public int queryRoomsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRoomsPrice(" + xid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    int res = roomRM.queryRoomsPrice(xid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::queryRoomsPrice(" + String.join(",", String.valueOf(xid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
    //involvedResourceManagers.put(xid, flightRM);
    
    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    boolean res = flightRM.reserveFlight(xid, cid, flightNumber);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::reserveFlight(" + String.join(",", String.valueOf(xid), String.valueOf(cid), String.valueOf(flightNumber)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean reserveCar(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveCar(" + xid + ", " + cid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, carRM);
    boolean res = carRM.reserveCar(xid, cid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::reserveCar(" + String.join(",", String.valueOf(xid), String.valueOf(cid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean reserveRoom(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, roomRM);
    boolean res = roomRM.reserveRoom(xid, cid, location);
    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::reserveRoom(" + String.join(",", String.valueOf(xid), String.valueOf(cid), location) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return res;
  }

  public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location,
      boolean car, boolean room) throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::bundle(" + xid + ", " + customerID + ", " + flightNumbers + ", " + location + ", "
        + car + ", " + room + ") called");

    long begin = System.currentTimeMillis();
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    TM.processTransaction(xid, carRM);

    if (car) {
      if (!reserveCar(xid, customerID, location)) {
        return false;
      }
    }

    if (room) {
      if (!reserveRoom(xid, customerID, location)) {
        return false;
      }
    }

    for (String flightNum : flightNumbers) {
      if (!reserveFlight(xid, customerID, Integer.parseInt(flightNum))) {
        return false;
      }
    }

    long end = System.currentTimeMillis();
    performanceLogger.log(Level.INFO, String.join(",", "MW::bundle(" + String.join(",", String.valueOf(xid), String.valueOf(customerID), "flightnumVector", location, String.valueOf(car), String.valueOf(room)) + ")", String.valueOf(begin), String.valueOf(end), String.valueOf(end - begin)));

    return true;
  }

  public String getName() throws RemoteException {
    return null;
  }
}
