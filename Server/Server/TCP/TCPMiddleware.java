package Server.TCP;

import java.io.*;
import java.net.*;
import Server.Common.*;

public class TCPMiddleware {

    protected static String flightServer;
    protected static String carServer;
    protected static String roomServer;
    protected static String customerServer;

    protected static int middlewarePort = 1098;
    protected static int flightPort = 1098;
    protected static int carPort = 1098;
    protected static int roomPort = 1098;
    protected static int customerPort = 1098;
    public static void main(String[] args) {
        if (args.length == 3) {
            middlewarePort = Integer.parseInt(args[0]);
            flightServer = args[1];
            flightPort = Integer.parseInt(args[2]);
            carServer = flightServer;
            carPort = flightPort;
            roomServer = flightServer;
            roomPort = flightPort;;
            customerServer = flightServer;
            customerPort = flightPort;
            routeRequests();

        } else if (args.length == 9) {
            middlewarePort = Integer.parseInt(args[0]);
            flightServer = args[1];
            flightPort = Integer.parseInt(args[2]);
            carServer = args[3];
            carPort = Integer.parseInt(args[4]);
            roomServer = args[5];
            roomPort = Integer.parseInt(args[6]);
            customerServer = args[7];
            customerPort = Integer.parseInt(args[8]);
            routeRequests();

        } else {
            return;
        }
    }

    private static void routeRequests() {

            try (ServerSocket serverSocket = new ServerSocket(middlewarePort)) {
 
                System.out.println("Middleware is listening on port " + middlewarePort);
     
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected");
     
                    new MiddlewareThread(clientSocket).start();
                }
     
            } catch (IOException ex) {
                System.out.println("Server exception: " + ex.getMessage());
                ex.printStackTrace();
            }
    }
}