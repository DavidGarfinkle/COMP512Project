package Server.Common;

import Server.Utils.*;
import Server.Interface.*;
import Server.LockManager.*;
import java.rmi.RemoteException;
import java.util.*;

public class Middleware implements IResourceManager {

  public IResourceManager flightRM;
  public IResourceManager carRM;
  public IResourceManager roomRM;

  // Transaction Manager
  public TransactionManager TM;

  public static Hashtable<String, TimeManager> timeManagers = new Hashtable<String, TimeManager>();

  public Middleware() throws RemoteException {
    this.TM = new TransactionManager(timeManagers, this);
  }

  public Middleware(IResourceManager flightRM, IResourceManager roomRM, IResourceManager carRM)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    this.flightRM = flightRM;
    this.carRM = carRM;
    this.roomRM = roomRM;
    this.TM = new TransactionManager(timeManagers, this);
  }

  public boolean checkConnection() throws RemoteException {
    return true;
  }

  public void checkConnection(String rm) throws RemoteException {
    switch(rm){
      case("Flight"):{
        flightRM.checkConnection();
        break;
      }
      case("Car"):{
        carRM.checkConnection();
        break;
      }
      case("Room"):{
        roomRM.checkConnection();
        break;
      }
    }
  }

  public int start() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::start called");

    return TM.start();
  }

  public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::commit called");

    return TM.commit(xid);
  }

  public void abort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::abort called");

		TM.abort(xid);
  }

  public void crashMiddleware(int mode) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::Middleware/TransactionManager crash called");

    TM.crash(mode);
  }

  public void crashResourceManager(String rm ,int mode) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::ResourceManager crash called");

    if(rm.equalsIgnoreCase("F")){
      if (mode == 3){
        TM.crashResourceManager(rm,mode);
      }
      else{
        flightRM.crashResourceManager(rm,mode);
      }
    }
    else if (rm.equalsIgnoreCase("C")){
      if (mode == 3){
        TM.crashResourceManager(rm,mode);
      }
      else{
        carRM.crashResourceManager(rm,mode);
      }
    }
    else if (rm.equalsIgnoreCase("R")){
      if (mode == 3){
        TM.crashResourceManager(rm,mode);
      }
      else{
        roomRM.crashResourceManager(rm,mode);
      }
    }
  }

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
        + flightPrice + ") called");
    TM.processTransaction(xid, flightRM);
    return flightRM.addFlight(xid, flightnumber, flightSeats, flightPrice);
  }

  public boolean addCars(int xid, String location, int numCars, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price + ") called");
    TM.processTransaction(xid, carRM);
    return carRM.addCars(xid, location, numCars, price);
  }

  public boolean addRooms(int xid, String location, int numRooms, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info(
        "MW::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price + ") called");
    TM.processTransaction(xid, roomRM);
    return roomRM.addRooms(xid, location, numRooms, price);
  }

  public int newCustomer(int xid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ") called");
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    TM.processTransaction(xid, carRM);
    int cid = flightRM.newCustomer(xid);
    boolean roomSuccess = roomRM.newCustomer(xid, cid);
    boolean carSuccess = carRM.newCustomer(xid, cid);
    return cid;
  }

  public boolean newCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ", " + cid + ") called");
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
    return flightRM.newCustomer(xid, cid) && roomRM.newCustomer(xid, cid) && carRM.newCustomer(xid, cid);
  }

  public boolean deleteFlight(int xid, int flightnumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteFlight(" + xid + ", " + flightnumber + ") called");
    TM.processTransaction(xid, flightRM);
    return flightRM.deleteFlight(xid, flightnumber);
  }

  public boolean deleteCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCars(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, carRM);
    return carRM.deleteCars(xid, location);
  }

  public boolean deleteRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteRooms(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, roomRM);
    return roomRM.deleteRooms(xid, location);
  }

  public boolean deleteCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCustomer(" + xid + ", " + cid + ") called");
    TM.processTransaction(xid, roomRM);
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    return flightRM.deleteCustomer(xid, cid) && roomRM.deleteCustomer(xid, cid) && carRM.deleteCustomer(xid, cid);
  }

  public int queryFlight(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlight(" + xid + ", " + flightNumber + ") called");
    TM.processTransaction(xid, flightRM);
    return flightRM.queryFlight(xid, flightNumber);
  }

  public int queryCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCars(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, carRM);
    return carRM.queryCars(xid, location);
  }

  public int queryRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRooms(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, roomRM);
    return roomRM.queryRooms(xid, location);
  }

  public String queryCustomerInfo(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCustomerInfo(" + xid + ", " + cid + ") called");
    TM.processTransaction(xid, carRM);
    TM.processTransaction(xid, flightRM);
    TM.processTransaction(xid, roomRM);
		String s = "Bill for customer " + cid + ";";
    s += flightRM.queryCustomerInfo(xid, cid);
    s += roomRM.queryCustomerInfo(xid, cid);
    s += carRM.queryCustomerInfo(xid, cid);
    return s;
  }

  public int queryFlightPrice(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlightPrice(" + xid + ", " + flightNumber + ") called");
    TM.processTransaction(xid, flightRM);
    return flightRM.queryFlightPrice(xid, flightNumber);
  }

  public int queryCarsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCarsPrice(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, carRM);
    return carRM.queryCarsPrice(xid, location);
  }

  public int queryRoomsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRoomsPrice(" + xid + ", " + location + ") called");
    TM.processTransaction(xid, roomRM);
    return roomRM.queryRoomsPrice(xid, location);
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
    //involvedResourceManagers.put(xid, flightRM);
    TM.processTransaction(xid, flightRM);
    return flightRM.reserveFlight(xid, cid, flightNumber);
  }

  public boolean reserveCar(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveCar(" + xid + ", " + cid + ", " + location + ") called");
    TM.processTransaction(xid, carRM);
    return carRM.reserveCar(xid, cid, location);
  }

  public boolean reserveRoom(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");
    TM.processTransaction(xid, roomRM);
    return roomRM.reserveRoom(xid, cid, location);
  }

  public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location,
      boolean car, boolean room) throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::bundle(" + xid + ", " + customerID + ", " + flightNumbers + ", " + location + ", "
        + car + ", " + room + ") called");
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

    return true;
  }

  public String getName() throws RemoteException {
    return null;
  }

  // dummy methods
  public void start(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {}
  public boolean voteRequest(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {return true;}
  public void crash(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException{}
  public boolean doCommit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {return true;}
  public void doAbort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {}
}
