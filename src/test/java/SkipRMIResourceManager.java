package Tests;

import org.junit.jupiter.api.*;
import java.rmi.*;

import Server.RMI.*;

public class SkipRMIResourceManager {

    @Test
    public void testInit() {
      RMIResourceManager rm = new RMIResourceManager("testResourceServer");
    }

    @Test
    public void testBind() throws RemoteException {
      RMIResourceManager rm = new RMIResourceManager("testResourceServer");
      rm.bind();
      rm.unbind();
    }
}
