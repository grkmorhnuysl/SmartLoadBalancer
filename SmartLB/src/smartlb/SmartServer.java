/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smartlb;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class SmartServer {
    private static final int SERVER_PORT = 9001;
    private static final String LOAD_BALANCER_HOST = "localhost";
    private static final int LOAD_BALANCER_PORT = 9999;

    private static boolean isBusy = false;

    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
        System.out.println("SmartServer started on port " + SERVER_PORT);

       
        new Thread(() -> {
            while (true) {
                try {
                    sendStatusToLoadBalancer();
                    Thread.sleep(3000); 
                } catch (Exception e) {
                    System.out.println("Status send error: " + e.getMessage());
                }
            }
        }).start();

        
        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void handleClient(Socket socket) {
    try (
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
    ) {
        String request = in.readLine();
        System.out.println("Received request: " + request);

        isBusy = true;

        if (request.startsWith("COMPUTE:")) {
            int seconds = Integer.parseInt(request.split(":")[1].trim());
            Thread.sleep(seconds * 1000);
            out.write("COMPUTE complete (" + seconds + "s)\n");

        } else if (request.startsWith("VIDEO:")) {
            int seconds = Integer.parseInt(request.split(":")[1].trim());
            for (int i = 1; i <= seconds; i++) {
                out.write("Streaming second " + i + "\n");
                out.flush();
                Thread.sleep(1000);
            }
            out.write("VIDEO stream complete\n");

        } else if (request.startsWith("FILE:")) {
            String fileName = request.split(":")[1].trim();
            // Simülasyon: dosya gönderimi yerine adını yazdırıyoruz
            out.write("Sending file: " + fileName + "\n");

        } else if (request.startsWith("LIST")) {
            File dir = new File(".");
            File[] files = dir.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        out.write("FILE: " + file.getName() + "\n");
                    }
                }
            } else {
                out.write("Could not access directory.\n");
            }

        } else {
            out.write("Unknown request type\n");
        }

        out.flush();
        isBusy = false;
        socket.close();

    } catch (Exception e) {
        System.out.println("Client error: " + e.getMessage());
        isBusy = false;
    }
}


    private static void sendStatusToLoadBalancer() throws IOException {
        String status = isBusy ? "BUSY" : "IDLE";
        String message = SERVER_PORT + ":" + status;

        DatagramSocket udpSocket = new DatagramSocket();
        byte[] buffer = message.getBytes();
        InetAddress address = InetAddress.getByName(LOAD_BALANCER_HOST);

        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, LOAD_BALANCER_PORT);
        udpSocket.send(packet);
        udpSocket.close();

        System.out.println("Status sent to LB: " + message);
    }
}
