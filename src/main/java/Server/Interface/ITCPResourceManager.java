package Server.Interface;

import java.io.*;
import java.net.*;
import Server.Common.*;
import java.util.*;

public interface ITCPResourceManager 
{
    /**
     * Add seats to a flight.
     *
     * In general this will be used to create a new
     * flight, but it should be possible to add seats to an existing flight.
     * Adding to an existing flight should overwrite the current price of the
     * available seats.
     *
     * @return Success
     */
    public boolean addFlight(int id, int flightNum, int flightSeats, int flightPrice);
    
    /**
     * Add car at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addCars(int id, String location, int numCars, int price); 
   
    /**
     * Add room at a location.
     *
     * This should look a lot like addFlight, only keyed on a string location
     * instead of a flight number.
     *
     * @return Success
     */
    public boolean addRooms(int id, String location, int numRooms, int price); 			    
			    
    /**
     * Add customer.
     *
     * @return Unique customer identifier
     */
    public int newCustomer(int id); 
    
    /**
     * Add customer with id.
     *
     * @return Success
     */
    public boolean newCustomer(int id, int cid);

    /**
     * Delete the flight.
     *
     * deleteFlight implies whole deletion of the flight. If there is a
     * reservation on the flight, then the flight cannot be deleted
     *
     * @return Success
     */   
    public boolean deleteFlight(int id, int flightNum); 
    
    /**
     * Delete all cars at a location.
     *
     * It may not succeed if there are reservations for this location
     *
     * @return Success
     */		    
    public boolean deleteCars(int id, String location); 

    /**
     * Delete all rooms at a location.
     *
     * It may not succeed if there are reservations for this location.
     *
     * @return Success
     */
    public boolean deleteRooms(int id, String location); 
    
    /**
     * Delete a customer and associated reservations.
     *
     * @return Success
     */
    public boolean deleteCustomer(int id, int customerID); 

    /**
     * Query the status of a flight.
     *
     * @return Number of empty seats
     */
    public int queryFlight(int id, int flightNumber); 

    /**
     * Query the status of a car location.
     *
     * @return Number of available cars at this location
     */
    public int queryCars(int id, String location); 

    /**
     * Query the status of a room location.
     *
     * @return Number of available rooms at this location
     */
    public int queryRooms(int id, String location); 

    /**
     * Query the customer reservations.
     *
     * @return A formatted bill for the customer
     */
    public String queryCustomerInfo(int id, int customerID); 
    
    /**
     * Query the status of a flight.
     *
     * @return Price of a seat in this flight
     */
    public int queryFlightPrice(int id, int flightNumber); 

    /**
     * Query the status of a car location.
     *
     * @return Price of car
     */
    public int queryCarsPrice(int id, String location); 

    /**
     * Query the status of a room location.
     *
     * @return Price of a room
     */
    public int queryRoomsPrice(int id, String location); 

    /**
     * Reserve a seat on this flight.
     *
     * @return Success
     */
    public boolean reserveFlight(int id, int customerID, int flightNumber); 

    /**
     * Reserve a car at this location.
     *
     * @return Success
     */
    public boolean reserveCar(int id, int customerID, String location); 

    /**
     * Reserve a room at this location.
     *
     * @return Success
     */
    public boolean reserveRoom(int id, int customerID, String location); 

    /**
     * Reserve a bundle for the trip.
     *
     * @return Success
     */
    public boolean bundle(int id, int customerID, Vector<String> flightNumbers, String location, boolean car, boolean room); 

    /**
     * Convenience for probing the resource manager.
     *
     * @return Name
     */
    public String getName();
}
