package smartlb;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class SmartLoadBalancer {
    private static final int STATUS_PORT = 9999;  
    private static final int LISTEN_PORT = 8888;  
    private static final Set<Integer> serverPorts = new HashSet<>(Arrays.asList(9001, 9002, 9003));


    private static final Map<Integer, String> serverStatus = new ConcurrentHashMap<>();
    private static Iterator<Integer> rrIterator = serverPorts.iterator();

    public static void main(String[] args) throws IOException {
       
        for (int port : serverPorts) {
            serverStatus.put(port, "IDLE");
        }

        
        new Thread(SmartLoadBalancer::startStatusListener).start();

        
        ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);
        System.out.println("LoadBalancer running on port " + LISTEN_PORT);

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket)).start();
        }
    }

    private static void startStatusListener() {
        try (DatagramSocket socket = new DatagramSocket(STATUS_PORT)) {
            byte[] buffer = new byte[1024];
            System.out.println("Listening for server status updates on UDP " + STATUS_PORT);

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                String[] parts = message.split(":");
                int port = Integer.parseInt(parts[0]);
                String status = parts[1];

                serverStatus.put(port, status);
                System.out.println("Server " + port + " is now " + status);
            }
        } catch (IOException e) {
            System.out.println("Error in status listener: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))
        ) {
            int selectedPort = selectAvailableServer();

            out.write(String.valueOf(selectedPort));
            out.newLine();
            out.flush();

            System.out.println("Client directed to server: " + selectedPort);
            socket.close();
        } catch (IOException e) {
            System.out.println("Client handler error: " + e.getMessage());
        }
    }

    private static synchronized int selectAvailableServer() {
        
        for (int port : serverPorts) {
            if ("IDLE".equalsIgnoreCase(serverStatus.get(port))) {
                return port;
            }
        }

       
        if (!rrIterator.hasNext()) rrIterator = serverPorts.iterator();
        return rrIterator.next();
    }
}