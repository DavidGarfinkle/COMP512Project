package Server.Common;

import java.util.*;
import java.io.*;

public class Middleware {

  ResourceManager m_flightResourceManager;
  ResourceManager m_roomResourceManager;
  ResourceManager m_carResourceManager;
  ResourceManager m_customerResourceManager;

  public Middleware(ResourceManager flightRM, ResourceManager customerRM, ResourceManager roomRM, ResourceManager carRM)
  {
    m_flightResourceManager = flightRM;
    m_roomResourceManager = roomRM;
    m_customerResourceManager = customerRM;
    m_carResourceManager = carRM;
  }

  public boolean addFlight(int xid, int flightnumber, int flightSeats, int flightPrice) {
  	Trace.info("RM::addFlight(" + xid + ", " + flightnumber + ", " + flightSeats + ", $"
  		+ flightPrice + ") called");
  	return m_flightResourceManager.addFlight(xid, flightnumber, flightSeats, flightPrice);
  }

  public boolean addCars(int xid, String location, int numCars, int price) {
  	Trace.info("RM::addCars(" + xid + ", " + location + ", " + numCars + ", $" + price
  		+ ") called");
  	return m_carResourceManager.addCars(xid, location, numCars, price);
  }

  public boolean addRooms(int xid, String location, int numRooms, int price) {
  	Trace.info("RM::addRooms(" + xid + ", " + location + ", " + numRooms + ", $" + price
  		+ ") called");
  	return m_roomResourceManager.addRooms(xid, location, numRooms, price);
  }

  public int newCustomer(int xid) {
  	Trace.info("RM::newCustomer(" + xid + ") called");
  	return m_customerResourceManager.newCustomer(xid);
  }

  public boolean newCustomer(int xid, int cid) {
  	Trace.info("RM::newCustomer(" + xid + ", " + cid + ") called");
  	return m_customerResourceManager.newCustomer(xid, cid);
  }

  public boolean deleteFlight(int xid, int flightnumber) {
  	Trace.info("RM::deleteFlight(" + xid + ", " + flightnumber + ") called");
  	return m_flightResourceManager.deleteFlight(xid, flightnumber);
  }

  public boolean deleteCars(int xid, String location) {
  	Trace.info("RM::deleteCars(" + xid + ", " + location + ") called");
  	return m_carResourceManager.deleteCars(xid, location);
  }

  public boolean deleteRooms(int xid, String location) {
  	Trace.info("RM::deleteRooms(" + xid + ", " + location + ") called");
  	return m_roomResourceManager.deleteRooms(xid, location);
  }

  public boolean deleteCustomer(int xid, int cid) {
  	Trace.info("RM::deleteCustomer(" + xid + ", " + cid + ") called");
  	return m_customerResourceManager.deleteCustomer(xid, cid);
  }

  public int queryFlight(int xid, int flightNumber) {
  	Trace.info("RM::queryFlight(" + xid + ", " + flightNumber + ") called");
  	return m_flightResourceManager.queryFlight(xid, flightNumber);
  }

  public int queryCars(int xid, String location) {
	Trace.info("RM::queryCars(" + xid + ", " + location + ") called");
	return m_carResourceManager.queryCars(xid, location);
  }

  public int queryRooms(int xid, String location) {
  	Trace.info("RM::queryRooms(" + xid + ", " + location + ") called");
  	return m_roomResourceManager.queryRooms(xid, location);
  }

  public String queryCustomerInfo(int xid, int cid) {
  	Trace.info("RM::queryCustomerInfo(" + xid + ", " + cid + ") called");
  	return m_customerResourceManager.queryCustomerInfo(xid, cid);
  }

  public int queryFlightPrice(int xid, int flightNumber) {
  	Trace.info("RM::queryFlightPrice(" + xid + ", " + flightNumber + ") called");
  	return m_flightResourceManager.queryFlightPrice(xid, flightNumber);
  }

  public int queryCarsPrice(int xid, String location) {
	Trace.info("RM::queryCarsPrice(" + xid + ", " + location + ") called");
	return m_carResourceManager.queryCarsPrice(xid, location);
  }

  public int queryRoomsPrice(int xid, String location) {
  	Trace.info("RM::queryRoomsPrice(" + xid + ", " + location + ") called");
  	return m_roomResourceManager.queryRoomsPrice(xid, location);
  }

  public boolean reserveFlight(int xid, int cid, int flightNumber) {
  	Trace.info("RM::reserveFlight(" + xid + ", " + cid + ", " + flightNumber + ") called");
  	return m_flightResourceManager.reserveFlight(xid, cid, flightNumber);
  }

  public boolean reserveCar(int xid, int cid, String location) {
  	Trace.info("RM::reserveCar(" + xid + ", " + cid + ", " + location + ") called");
  	return m_carResourceManager.reserveCar(xid, cid, location);
  }

  public boolean reserveRoom(int xid, int cid, String location) {
  	Trace.info("RM::reserveRoom(" + xid + ", " + cid + ", " + location + ") called");
  	return m_roomResourceManager.reserveRoom(xid, cid, location);
  }

  public boolean bundle(int xid, int cid, Vector<String> flightNumbers, String location, boolean car, boolean room) {
  	String flights = "";
  	for (String flightNumber : flightNumbers) {
  	  flights = flights + flightNumber + " ";
  	}
  	Trace.info("RM::bundle(" + xid + ", " + cid + ", " + flights + ", " + location + ", " + car
  		+ ", " + room + ") called");
    return false;
  }

  public String getName() {
  	return null;
  }
}
