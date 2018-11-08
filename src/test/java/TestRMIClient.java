package Tests;

import org.junit.jupiter.api.*;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.*;

import java.util.concurrent.TimeUnit;

import Server.RMI.*;
import Client.*;

public class TestRMIClient {

    InputStream original;
    RMIClient client;
    Thread t_client;

    @BeforeEach
    public void setUp() {
      System.out.println("=-=-=-=-=-=-=-=-==------------=-=-=-=-=-=-=-=-=-=-=------------=-=-=-=-=-=");
      System.out.println("setUp()");
      RMIResourceManager.main(new String[]{"Flight"});
      RMIResourceManager.main(new String[]{"Room"});
      RMIResourceManager.main(new String[]{"Car"});
      RMIMiddleware.main(new String[]{"localhost", "localhost", "localhost"});

      client = new RMIClient(new String[]{"localhost"});
      t_client = new Thread(client);
      t_client.start();

      original = System.in;
    }

    @AfterEach
    public void tearDown() throws RemoteException{
      System.out.println("=-=-=-=-=-=-=-=-==------------=-=-=-=-=-=-=-=-=-=-=------------=-=-=-=-=-=");
      System.out.println("tearDown()");
			Registry registry = LocateRegistry.getRegistry(1099);
      try {
        registry.unbind("group28Flight");
        registry.unbind("group28Room");
        registry.unbind("group28Car");
        registry.unbind("group28Server");
			}
      catch (NotBoundException | RemoteException e) {
        System.out.println("Failed to unbind servers");
        System.out.println(e.getLocalizedMessage());
      }

      t_client.stop();

      System.setIn(original);
    }

    @Test
    public void testSetUp(){
      System.out.println("===========================================================");
      System.out.println("testSetUp()");

      System.setIn(new ByteArrayInputStream("Quit".getBytes()));
    }

    @Test
    public void testReserveFlight() throws InterruptedException {
      System.out.println("===========================================================");
      System.out.println("testReserveFlight()");

      String test =
        "start\n" +
        "addCustomerID,1,0\n" +
        "addFlight,1,0,100,100\n" +
        "reserveFlight,1,0,0\n";
      System.setIn(new ByteArrayInputStream(test.getBytes()));

      TimeUnit.SECONDS.sleep(5);
    }
}
