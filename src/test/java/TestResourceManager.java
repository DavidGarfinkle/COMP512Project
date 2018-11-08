package Tests;

import java.rmi.RemoteException;
import java.util.*;

import org.junit.jupiter.api.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import Server.Common.*;
import Server.RMI.*;
import Server.LockManager.*;

@RunWith(Parameterized.class)
public class TestResourceManager {

    // Prepare both ResourceManager and RMIReourceManager for tests
    @Parameters
    public static Collection<Object []> data() {
      ResourceManager rm = new ResourceManager("testResourceManager");
      RMIResourceManager rmRMI = new RMIResourceManager("testRMIResourceManager");
      String[] args = new String[1];
      rmRMI.main(args);

      return Arrays.asList(new Object[][] {
        { rm }, { rmRMI }
      });
    }

    private ResourceManager rm;

    public TestResourceManager(ResourceManager rm){
      this.rm = rm;
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
