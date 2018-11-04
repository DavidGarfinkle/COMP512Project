package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;

public class TestMiddleware {

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testReserveFlight() throws RemoteException {

      ResourceManager flightRM = new ResourceManager("Flight");
      ResourceManager carRM = new ResourceManager("Car");
      ResourceManager roomRM = new ResourceManager("Room");
      Middleware mw = new Middleware(flightRM, roomRM, carRM);

      int customerId = 0;
      int flightId = 1;
      int flightSeats = 100;
      int txid = 0;
      int remainingSeats;

      customerId = mw.newCustomer(txid);
      mw.addFlight(txid, flightId, flightSeats, 100);
      mw.reserveFlight(customerId, customerId, flightId);

      remainingSeats = mw.queryFlight(0, flightId);
      Assertions.assertEquals(flightSeats-1, remainingSeats, "Flight seat numbers differ");
    }
}
