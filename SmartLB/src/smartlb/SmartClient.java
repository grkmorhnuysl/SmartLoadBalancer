/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartlb;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class SmartClient {
    private static final String LB_HOST = "localhost";
    private static final int LB_PORT = 8888;

    public static void main(String[] args) {
        try {
           
            Socket lbSocket = new Socket(LB_HOST, LB_PORT);
            BufferedReader lbIn = new BufferedReader(new InputStreamReader(lbSocket.getInputStream()));

            String serverPortStr = lbIn.readLine();
            int serverPort = Integer.parseInt(serverPortStr.trim());
            lbSocket.close();

            System.out.println("Connected to server at port: " + serverPort);

          
            Socket serverSocket = new Socket(LB_HOST, serverPort);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));

         
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter request (e.g., COMPUTE:5, VIDEO:3, FILE:abc.txt): ");
            String request = scanner.nextLine();

            out.write(request + "\n");
            out.flush();

            
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("SERVER: " + line);
            }

            serverSocket.close();

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
