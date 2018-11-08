package Server.Common;

import Server.Interface.*;
import Server.LockManager.*;
import java.rmi.RemoteException;
import java.util.*;

import utils.*;

public class Middleware implements IResourceManager {

  protected IResourceManager flightRM;
  protected IResourceManager carRM;
  protected IResourceManager roomRM;

  // Transaction Manager
  protected static Hashtable<Integer, TransactionObject> activeTransactions = new Hashtable<Integer, TransactionObject>();
  //protected static RMHashtable involvedResourceManagers = new RMHashtable();
  protected static Hashtable<Integer, Vector<IResourceManager>> involvedResourceManagers = new Hashtable<Integer, Vector<IResourceManager>>();
  protected static IncrementingInteger xidPicker = new IncrementingInteger();

  public Middleware() throws RemoteException {
  }

  public Middleware(IResourceManager flightRM, IResourceManager roomRM, IResourceManager carRM)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    this.flightRM = flightRM;
    this.carRM = carRM;
    this.roomRM = roomRM;
  }

  // dummy method
  public void start(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
  }

  public int start() throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::start called");
    int xid = xidPicker.pick();
    TransactionObject tx = new TimeObject(xid);

    if (!activeTransactions.contains(tx)) {
      Trace.info("MW::new txn");
      activeTransactions.put(xid, tx);
    }
    else{
      Trace.info("MW::existing txn");
    }

    return xid;
  }

  public boolean commit(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::commit called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Middleware cannot commit a transaction that has not been initialized");
		}

    if (involvedResourceManagers.containsKey(xid)) {
      for (IResourceManager rm : involvedResourceManagers.get(xid)) {
        rm.commit(xid);
      }
    }

    return true;
  }

  public void abort(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::abort called");
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Middleware cannot abort a transaction that has not been initialized");
		}

    for (IResourceManager rm : involvedResourceManagers.get(xid)) {
      rm.abort(xid);
    }
  }

  public void checkTransaction(int xid) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
		if (!activeTransactions.containsKey(xid)){
			throw new InvalidTransactionException(xid, "Middleware cannot add operation to uninitialized transaction");
		}
  }

  public void processTransaction(int xid, IResourceManager rm) throws RemoteException, TransactionAbortedException, InvalidTransactionException {
    Trace.info("MW::processTransaction(" + xid + ", " + rm.getName() + ")");

    // A transaction must be initialized with start() before it can handle operations
    checkTransaction(xid);

    // Init resource manager vector
    if (!involvedResourceManagers.containsKey(xid)) {
      Trace.info("MW::processTransaction --- initializing RM vector for tx " + xid);
      involvedResourceManagers.put(xid, new Vector<IResourceManager>());
    }

    // If this tx doesn't have this rm, add the rm, and init its tx
    if (!involvedResourceManagers.get(xid).contains(rm)) {
      Trace.info("MW::processTransaction --- add & start " + rm.getName() + " to RM vector for tx " + xid);
      rm.start(xid);
      involvedResourceManagers.get(xid).add(rm);
    }
  }

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
        + flightPrice + ") called");
    //involvedResourceManagers.put(xid, flightRM);
    //checkTransaction(xid);
    processTransaction(xid, flightRM);
    return flightRM.addFlight(xid, flightnumber, flightSeats, flightPrice);
  }

  public boolean addCars(int xid, String location, int numCars, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price + ") called");
    processTransaction(xid, carRM);
    return carRM.addCars(xid, location, numCars, price);
  }

  public boolean addRooms(int xid, String location, int numRooms, int price)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info(
        "MW::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price + ") called");
    processTransaction(xid, roomRM);
    return roomRM.addRooms(xid, location, numRooms, price);
  }

  public int newCustomer(int xid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ") called");
    processTransaction(xid, flightRM);
    processTransaction(xid, roomRM);
    processTransaction(xid, carRM);
    int cid = flightRM.newCustomer(xid);
    boolean roomSuccess = roomRM.newCustomer(xid, cid);
    boolean carSuccess = carRM.newCustomer(xid, cid);
    return cid;
  }

  public boolean newCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::newCustomer(" + xid + ", " + cid + ") called");
    processTransaction(xid, carRM);
    processTransaction(xid, flightRM);
    processTransaction(xid, roomRM);
    return flightRM.newCustomer(xid, cid) && roomRM.newCustomer(xid, cid) && carRM.newCustomer(xid, cid);
  }

  public boolean deleteFlight(int xid, int flightnumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteFlight(" + xid + ", " + flightnumber + ") called");
    processTransaction(xid, flightRM);
    return flightRM.deleteFlight(xid, flightnumber);
  }

  public boolean deleteCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCars(" + xid + ", " + location + ") called");
    processTransaction(xid, carRM);
    return carRM.deleteCars(xid, location);
  }

  public boolean deleteRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteRooms(" + xid + ", " + location + ") called");
    processTransaction(xid, roomRM);
    return roomRM.deleteRooms(xid, location);
  }

  public boolean deleteCustomer(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::deleteCustomer(" + xid + ", " + cid + ") called");
    processTransaction(xid, roomRM);
    processTransaction(xid, carRM);
    processTransaction(xid, flightRM);
    return flightRM.deleteCustomer(xid, cid) && roomRM.deleteCustomer(xid, cid) && carRM.deleteCustomer(xid, cid);
  }

  public int queryFlight(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlight(" + xid + ", " + flightNumber + ") called");
    processTransaction(xid, flightRM);
    return flightRM.queryFlight(xid, flightNumber);
  }

  public int queryCars(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCars(" + xid + ", " + location + ") called");
    processTransaction(xid, carRM);
    return carRM.queryCars(xid, location);
  }

  public int queryRooms(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRooms(" + xid + ", " + location + ") called");
    processTransaction(xid, roomRM);
    return roomRM.queryRooms(xid, location);
  }

  public String queryCustomerInfo(int xid, int cid)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCustomerInfo(" + xid + ", " + cid + ") called");
    processTransaction(xid, carRM);
    processTransaction(xid, flightRM);
    processTransaction(xid, roomRM);
    return flightRM.queryCustomerInfo(xid, cid) + roomRM.queryCustomerInfo(xid, cid) + carRM.queryCustomerInfo(xid, cid);
  }

  public int queryFlightPrice(int xid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryFlightPrice(" + xid + ", " + flightNumber + ") called");
    processTransaction(xid, flightRM);
    return flightRM.queryFlightPrice(xid, flightNumber);
  }

  public int queryCarsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryCarsPrice(" + xid + ", " + location + ") called");
    processTransaction(xid, carRM);
    return carRM.queryCarsPrice(xid, location);
  }

  public int queryRoomsPrice(int xid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::queryRoomsPrice(" + xid + ", " + location + ") called");
    processTransaction(xid, roomRM);
    return roomRM.queryRoomsPrice(xid, location);
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
    //involvedResourceManagers.put(xid, flightRM);
    processTransaction(xid, flightRM);
    return flightRM.reserveFlight(xid, cid, flightNumber);
  }

  public boolean reserveCar(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveCar(" + xid + ", " + cid + ", " + location + ") called");
    processTransaction(xid, carRM);
    return carRM.reserveCar(xid, cid, location);
  }

  public boolean reserveRoom(int xid, int cid, String location)
      throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");
    processTransaction(xid, roomRM);
    return roomRM.reserveRoom(xid, cid, location);
  }

  public boolean bundle(int xid, int customerID, Vector<String> flightNumbers, String location,
      boolean car, boolean room) throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
    Trace.info("MW::bundle(" + xid + ", " + customerID + ", " + flightNumbers + ", " + location + ", "
        + car + ", " + room + ") called");
    processTransaction(xid, flightRM);
    processTransaction(xid, roomRM);
    processTransaction(xid, carRM);

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
}
