package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;

import utils.*;

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

    public void testBundleInsufficientResources() throws RemoteException {
      int customerId = this.mw.newCustomer(1);
      Vector<String> flightNumbers = new Vector<String>();
      flightNumbers.add("1");
      flightNumbers.add("2");

      boolean res = this.mw.bundle(0, customerId, flightNumbers, "mtl", true, true);
      Assertions.assertEquals(res, false, "Bundle should fail due to inadequate resources");
    }

    public void testBundleSufficientResources() throws RemoteException {

      IncrementingInteger txid = new IncrementingInteger();
      Vector<String> flightNumbers = new Vector<String>();
      flightNumbers.add("1");
      flightNumbers.add("2");

      String location = "mtl";
      boolean car = true;
      boolean room = true;

      int customerId = this.mw.newCustomer(1);

      // Add flights
      for (String flightNum : flightNumbers) {
        this.mw.addFlight(txid.pick(), Integer.parseInt(flightNum), 100, 100);
      }

      // Add cars
      this.mw.addCars(txid.pick(), location, 100, 100);

      // Add rooms
      this.mw.addRooms(txid.pick(), location, 100, 100);

      boolean res = this.mw.bundle(txid.pick(), customerId, flightNumbers, location, car, room);
      Assertions.assertEquals(res, true, "Bundle should succeed");
    }
}
