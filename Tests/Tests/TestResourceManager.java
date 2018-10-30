package Tests;

import org.junit.Test;
import org.junit.Assert.*;

import Server.Common.*;

public class TestResourceManager {

    @BeforeEach
    public void setUp() {
      this.rm = new ResourceManager("testResource");
    }

    @Test
    public void testAddFlight() {
      int customerId = 0;
      int flightId = 1;
      int flightSeats = 100;

      this.rm.newCustomer(customerId);
      this.rm.addFlight(0, flightId, flightSeats, 100);
      this.rm.reserveFlight(0, customerId, flightId);

      int remainingSeats = this.rm.queryFlight(0, flightId);

      assertEquals("should be same", remainingSeats, flightSeats - 1);
    }
}
