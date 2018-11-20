package Tests;

import java.rmi.RemoteException;
import java.util.*;
import org.junit.jupiter.api.*;

import Server.Common.*;
import Server.LockManager.*;

import Server.Utils.*;

public class TestCommandsM2 {

    ResourceManager flightRM;
    ResourceManager carRM;
    ResourceManager roomRM;
    Middleware mw;

    public String customerBillTemplate(int customerId, Integer[] counts, String[] itemIds, Integer[] prices){
  		String s = "Bill for customer " + customerId + ";";

      for (int i = 0; i < counts.length; i++){
  			s += + counts[i] + " " + itemIds[i] + " $" + prices[i] + ";";
      }

      return s;
    }

    @BeforeEach
    public void setUp()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      this.flightRM = new ResourceManager("Flight");
      this.carRM = new ResourceManager("Car");
      this.roomRM = new ResourceManager("Room");
      mw = new Middleware(flightRM, roomRM, carRM);
    }

    @Test
    public void testAll()
        throws RemoteException, TransactionAbortedException, InvalidTransactionException, DeadlockException {
      int txid, txidTwo;
      int resultCount;
      String expected, result;

      /*
        1. Simple commit
        Start                        # [xid=1]
        AddFlight,1,1,10,10
        AddRooms,1,Montreal,15,15
        AddCustomerID,1,1
        ReserveFlight,1,1,1
        ReserveRoom,1,1,Montreal
        QueryCustomer,1,1            # Returns flight-1, room-montreal
        Commit,1
        ----
        Start                        # [xid=2]
        QueryCustomer,2,1            # [flight-1, room-montreal]
        Commit,2
      */

      txid = mw.start();
      Assertions.assertEquals(true, mw.addFlight(txid, 1, 10, 10));
      Assertions.assertEquals(true, mw.addRooms(txid, "mtl", 15, 15));
      Assertions.assertEquals(true, mw.newCustomer(txid, 1));
      Assertions.assertEquals(true, mw.reserveFlight(txid, 1, 1)); //reserve one flight-1 seat
      Assertions.assertEquals(true, mw.reserveRoom(txid, 1, "mtl")); //reserve one room-mtl

      result = mw.queryCustomerInfo(txid, 1);
      expected = customerBillTemplate(1, new Integer[]{1, 1}, new String[]{"flight-1", "room-mtl"}, new Integer[]{10, 15});
      Assertions.assertEquals(expected, result);
      mw.commit(txid);

      txid = mw.start();
      result = mw.queryCustomerInfo(txid, 1);
      expected = customerBillTemplate(1, new Integer[]{1, 1}, new String[]{"flight-1", "room-mtl"}, new Integer[]{10, 15});
      mw.commit(txid);

      /***************** END OF ONE **********************/

      /*
        2. Simple abort
        Start                        # [xid=3]
        AddCars,3,Monteal,20,20
        AddRooms,3,Montreal,10,10
        AddCustomerID,3,2
        ReserveFlight,3,1,1
        ReserveFlight,3,2,1
        QueryCustomer,3,1            # [2xflight-1, room-montreal]
        QueryCustomer,3,2            # [flight-1]
        Abort,3
        ----
        Start                        # [xid=4]
        QueryCustomer,4,1            # [flight-1, room-montreal]
        QueryCustomer,4,2            # []
        Commit,4
      */

      txid = mw.start();

      mw.addCars(txid, "mtl", 20, 20);
      mw.addRooms(txid, "mtl", 10, 10); /** QUESTION Is this supposed to overwrite a price of 20? **/
      mw.newCustomer(txid, 2);
      mw.reserveFlight(txid, 1, 1);
      mw.reserveFlight(txid, 2, 1);

      result = mw.queryCustomerInfo(txid, 1);
      expected = customerBillTemplate(1, new Integer[]{2, 1}, new String[]{"flight-1", "room-mtl"}, new Integer[]{10, 15});
      Assertions.assertEquals(expected, result);

      result = mw.queryCustomerInfo(txid, 2);
      expected = customerBillTemplate(2, new Integer[]{1}, new String[]{"flight-1"}, new Integer[]{10});
      mw.queryCustomerInfo(txid, 2);
      Assertions.assertEquals(expected, result);

      mw.abort(txid);

      /**************** END OF TWO ************************/

      /*
        3. Customer lock
        Start                        # [xid=5]
        Start                        # [xid=6]
        ReserveFlight,5,1,1
        QueryCustomer,6,1            # Blocked, timeout
        Abort,5
      */

      txid = mw.start();
      txidTwo = mw.start();

      mw.reserveFlight(txid, 1, 1);
      result = mw.queryCustomerInfo(txidTwo, 1); // TODO how to test for timeout?

      mw.abort(5);

      /********************* END OF THREE ************************/

      /*
        4. Lock conversion
        Start                        # [xid=7]
        QueryRooms,7,Montreal
        AddRooms,7,Montreal,5,5      # Succeeds
        Abort,7
      */

      txid = mw.start();
      resultCount = mw.queryRooms(txid, "mtl");
      Assertions.assertEquals(14, resultCount);
      Assertions.assertEquals(true, mw.addRooms(txid, "mtl", 5, 5));
      mw.abort(txid);

      /*********************** END OF FOUR ************************/

      /*
        5. Deadlock
        ==Client 1==                        ==Client 2==
        Start        # [xid=8]              Start        # [xid=9]
        QueryFlight,8,1                     QueryRooms,9,Montreal
        *AddRooms,8,Montreal,10,10          **AddFlight,9,1,10,10

        If * is executed before **, then we expect * to deadlock timeout + abort, and ** to succeed
      */

      txid = mw.start();
      txidTwo = mw.start();

      mw.queryFlight(txid, 1);
      mw.queryRooms(txidTwo, "mtl");

      mw.addRooms(txid, "mtl", 10, 10);
      mw.addFlight(txidTwo, 1, 10, 10); // TODO EXPECTED DEADLOCK EXCEPTION


      /*
6. Time-to-live check
Start                        # [xid=10]
AddFlight,10,1,10,10
...wait...                   # Timeout at your TTL time, and aborts

7. Bundle atomicity
Start                        # [xid=11]
QueryFlight,11,1             # Quantity >= 1
QueryCars,11,Montreal        # Quantity == 0
QueryRooms,11,Montreal       # Quantity >= 1
QueryCustomer,11,1           # [flight-1, room-montreal]
Bundle,11,1,1,Montreal,1,1   # Fails, and does *not* reserve any flight or room
QueryCustomer,11,1           # [flight-1, room-montreal]
Abort,11

8(a). Multi-operations for those using an undo-map
Start                        # [xid=12]
AddFlight,12,10,10,10
Commit,12
----
Start                        # [xid=13]
QueryFlight,13,10            # Quantity == 10
DeleteFlight,13,10
Abort,13
----
Start                        # [xid=14]
QueryFlight,14,10            # Quantity == 10
DeleteFlight,14,10
AddFlight,14,10,15,15
Abort,14
----
Start                        # [xid=15]
QueryFlight,15,10            # Quantity == 10


8(b). Multi-operations for those using an isolation store per transaction
Start                        # [xid=12]
AddFlight,12,10,10,10
Commit,12
----
Start                        # [xid=13]
QueryFlight,13,10            # Quantity == 10
DeleteFlight,13,10
Commit,13
----
Start                        # [xid=14]
AddFlight,14,10,10,10
Commit,14
----
Start                        # [xid=15]
QueryFlight,15,10            # Quantity == 0
DeleteFlight,15,10
AddFlight,15,10,15,15
Commit,15
----
Start                        # [xid=16]
QueryFlight,16,10            # Quantity == 15
      */

    }

}
