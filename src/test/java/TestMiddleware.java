package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;

public class TestMiddleware {

    ResourceManager flightRM;
    ResourceManager carRM;
    ResourceManager roomRM;
    Middleware mw;

    @BeforeEach
    public void setUp() throws RemoteException {
      this.flightRM = new ResourceManager("Flight");
      this.carRM = new ResourceManager("Car");
      this.roomRM = new ResourceManager("Room");
      this.mw = new Middleware(flightRM, roomRM, carRM);
    }

    @Test
    public void testReserveFlight() throws RemoteException {

      int customerId = 0;
      int flightId = 1;
      int flightSeats = 100;
      int txid = 0;
      int remainingSeats;

      customerId = this.mw.newCustomer(txid);
      this.mw.addFlight(txid, flightId, flightSeats, 100);
      this.mw.reserveFlight(customerId, customerId, flightId);

      remainingSeats = this.mw.queryFlight(0, flightId);
      Assertions.assertEquals(flightSeats-1, remainingSeats, "Flight seat numbers differ");
    }
}
