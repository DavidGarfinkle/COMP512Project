package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;
import Server.LockManager.*;

public class TestResourceManager {

    private ResourceManager rm = new ResourceManager("testResource");

    @BeforeEach
    public void setUp() {
    }

    @Disabled
    public void testNewCustomer() throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
        int customerId = 0;
        rm.newCustomer(customerId);
    }

    @Disabled
    public void testReserveFlight() throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      int customerId = 0;
      int flightId = 1;
      int flightSeats = 100;
      int txid = 0;
      int remainingSeats;

      customerId = rm.newCustomer(txid);
      rm.addFlight(txid, flightId, flightSeats, 100);
      rm.reserveFlight(txid, customerId, flightId);
      rm.commit(txid);

      remainingSeats = rm.queryFlight(txid + 1, flightId);
      Assertions.assertEquals(flightSeats - 1, remainingSeats, "Flight seat numbers differ");
    }
}
