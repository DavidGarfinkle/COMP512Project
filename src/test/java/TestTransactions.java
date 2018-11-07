package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;
import Server.LockManager.*;

import utils.*;

public class TestTransactions {

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
    public void testUninitializedTransaction()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      try {
        int txid = 0; int flightId = 1; int flightSeats = 100;
        int customerId = this.mw.newCustomer(txid);
        this.mw.addFlight(txid, flightId, flightSeats, 100);
        Assertions.fail("Adding a flight on an uninitialized transaction ID should throw an InvalidTransactionException error");
      }
      catch (InvalidTransactionException e){
        // Should throw this exception
      }
    }

    @Test
    public void testUncommitedTransaction()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {

      int flightId = 1; int flightSeats = 100;

      int txid = mw.start();
      int customerId = mw.newCustomer(txid);
      mw.addFlight(txid, flightId, flightSeats, 100);
      mw.commit(txid);

      mw.reserveFlight(mw.start(), customerId, flightId);

      int queryxid = mw.start();
      int remainingSeats = mw.queryFlight(queryxid, flightId);
      Assertions.assertEquals(flightSeats, remainingSeats, "Uncommited change should not affect ResourceManager data");
    }
}
