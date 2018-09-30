package Server.Common;

import Server.Interface.*;

import java.util.*;
import java.rmi.RemoteException;
import java.io.*;

public class Middleware implements IResourceManager{

  protected String s_flightServerName = "FlightServer";
  protected String s_carServerName = "CarServer";
  protected String s_roomServerName = "RoomServer";
  protected String s_customerServerName = "CustomerServer";
  
  public Middleware(flightServer, carServer, roomServer, customerServer){
    s_flightServerName = flightServer;
    s_carServerName = carServer;
    s_roomServerName = roomServer;
    s_customerServerName = customerServer;
  }

}