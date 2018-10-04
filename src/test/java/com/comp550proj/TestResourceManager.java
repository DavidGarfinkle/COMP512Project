package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;

public class TestResourceManager {

    private ResourceManager rm = new ResourceManager("testResource");

    @BeforeEach
    public void setUp() throws RemoteException {
    }

    @Test
    public void testNewCustomer() throws RemoteException {
        int customerId = 0;
        rm.newCustomer(customerId);
    }

    @Test
    public void testReserveFlight() throws RemoteException {
      int customerId = 0;
      int flightId = 1;
      int flightSeats = 100;
      int txid = 0;
      int remainingSeats;

      customerId = rm.newCustomer(txid);
      rm.addFlight(txid, flightId, flightSeats, 100);
      rm.reserveFlight(customerId, customerId, flightId);

      remainingSeats = this.rm.queryFlight(0, flightId);
      Assertions.assertEquals(flightSeats-1, remainingSeats, "Flight seat numbers differ");
    }
}
