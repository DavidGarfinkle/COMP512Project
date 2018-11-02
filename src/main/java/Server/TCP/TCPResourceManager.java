package Server.TCP;

import java.io.*;
import java.net.*;
import Server.Common.*;


public class TCPResourceManager {

    private static int port = 1098;
    private static String name = "Resource";

    public static void main(String[] args) {
        if (args.length > 1) {
            name = args[0];
            port = Integer.parseInt(args[0]);
        } else {
            return;
        }
         
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println(name + " RM is listening on port " + port);
 
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                
                Thread RMThread = new Thread( new ResourceManagerThread(clientSocket, name));
                RMThread.start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}