package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;
import Server.LockManager.*;

import Server.Utils.*;

public class TestMiddleware {

    ResourceManager flightRM;
    ResourceManager carRM;
    ResourceManager roomRM;
    Middleware mw;

    @BeforeEach
    public void setUp()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      this.flightRM = new ResourceManager("Flight");
      this.carRM = new ResourceManager("Car");
      this.roomRM = new ResourceManager("Room");
      this.mw = new Middleware(flightRM, roomRM, carRM);
    }

    @Test
    public void testReserveFlight()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testReserveFlight");
      boolean res;

      int flightId = 1; int flightSeats = 100;

      int txid = mw.start();
      int customerId = mw.newCustomer(txid);
      res = mw.addFlight(txid, flightId, flightSeats, 100);
      Assertions.assertEquals(true, res, "addFlight() should succeed");
      mw.commit(txid);

      txid = mw.start();
      res = mw.reserveFlight(txid, customerId, flightId);
      Assertions.assertEquals(true, res, "reserveFlight() should succeed");
      mw.commit(txid);

      int remainingSeats = mw.queryFlight(mw.start(), flightId);
      Assertions.assertEquals(flightSeats-1, remainingSeats, "Commited flight reservation should decrease seat numbers");
    }

    @Test
    public void testAddFlight()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testAddFlight");

      boolean res;
      int flightId = 1; int flightSeats = 100;
      int txid = mw.start();
      int customerId = mw.newCustomer(txid);
      res = mw.addFlight(txid, flightId, flightSeats, 100);
      Assertions.assertEquals(true, res, "addFlight() should succeed");
      mw.commit(txid);

      int queryxid = mw.start();
      int remainingSeats = mw.queryFlight(queryxid, flightId);
      Assertions.assertEquals(flightSeats, remainingSeats, "We should have successfully added " + flightSeats + " flight seats.");
    }

    @Test
    public void testReserveRoomWithNonexistentCustomer()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testReserveRoomWithNonexistentCustomer");
      int numRooms = 5;
      int price = 100;
      String location = "mtl";
      int txid;
      boolean res;

      txid = mw.start();
      res = mw.addRooms(txid, location, numRooms, price);
      Assertions.assertEquals(true, res, "addRooms() should succeed");

      res = mw.reserveRoom(txid, 0, location);
      Assertions.assertEquals(false, res, "ReserveRoom() should fail when passed a nonexistent customerID.");
    }

    @Test
    public void testReserveCar()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testReserveCar");
      int numCars = 5;
      int price = 100;
      String location = "mtl";
      int txid, customerID;

      txid = mw.start();
      customerID = mw.newCustomer(txid);
      mw.addCars(txid, location, numCars, price);
      mw.reserveCar(txid, customerID, location);
      mw.commit(txid);

      Assertions.assertEquals(numCars-1, mw.queryCars(mw.start(), location));
    }

    @Test
    public void testBundleInsufficientResources()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testBundleInsufficientResources");

      int txid;

      txid = mw.start();
      int customerId = mw.newCustomer(txid);
      mw.commit(txid);

      Vector<String> flightNumbers = new Vector<String>();
      flightNumbers.add("1");
      flightNumbers.add("2");

      txid = mw.start();
      boolean res = mw.bundle(txid, customerId, flightNumbers, "mtl", true, true);
      mw.commit(txid);
      Assertions.assertEquals(res, false, "Bundle should fail due to inadequate resources");
    }

    @Test
    public void testBundleSufficientResources()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      Trace.info("===========================================================");
      Trace.info("testBundleSufficientResources\n\n");

      int txid;

      Vector<String> flightNumbers = new Vector<String>();
      flightNumbers.add("1");
      flightNumbers.add("2");

      String location = "mtl";
      boolean car = true;
      boolean room = true;

      txid = mw.start();
      int customerId = mw.newCustomer(txid);
      mw.commit(txid);

      txid = mw.start();
      // Add flights
      for (String flightNum : flightNumbers) {
        mw.addFlight(txid, Integer.parseInt(flightNum), 100, 100);
      }

      // Add cars
      mw.addCars(txid, location, 100, 100);

      // Add rooms
      mw.addRooms(txid, location, 100, 100);

      boolean res = mw.bundle(txid, customerId, flightNumbers, location, car, room);
      mw.commit(txid);
      Assertions.assertEquals(true, res, "Bundle should succeed");

      int cars = mw.queryCars(mw.start(), location);
      Assertions.assertEquals(99, cars);

      int rooms = mw.queryRooms(mw.start(), location);
      Assertions.assertEquals(99, rooms);
    }
}
