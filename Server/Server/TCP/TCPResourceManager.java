package Server.TCP;

import java.io.*;
import java.net.*;
import Server.Common.*;


public class TCPResourceManager {

    private static int port = 1098;

    public static void main(String[] args) {
        if (args.length != 1) {
            return;
        } else {
            port = Integer.parseInt(args[0]);
        }
         
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Resource manager is listening on port " + port);
 
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");
                
                new ResourceManagerThread(clientSocket).start();
            }
 
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}