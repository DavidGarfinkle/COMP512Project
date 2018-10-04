package Client;

import Server.Interface.*;

import java.net.*;
import java.util.*;
import java.io.*;

public class TCPClient {

    private static String hostname = "localhost";
    private static int port = 1098;
    public static void main(String[] args) {
        if (args.length < 1) {
            return;
        } else if (args.length < 2){
            port = Integer.parseInt(args[0]);
        } else {
            hostname = args[0];
            port = Integer.parseInt(args[1]);
        }
 
        try (Socket socket = new Socket(hostname, port)) {
 
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
 
            String time = reader.readLine();
 
            System.out.println(time);
 
        } catch (UnknownHostException ex) {
 
            System.out.println("Server not found: " + ex.getMessage());
 
        } catch (IOException ex) {
 
            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}