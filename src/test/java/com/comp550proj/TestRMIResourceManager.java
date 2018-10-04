package Tests;

import org.junit.jupiter.api.*;

import Server.RMI.*;

public class TestRMIResourceManager {

    @Test
    public void testInit() {
        RMIResourceManager rm = new RMIResourceManager("testResourceServer");
    }
}
