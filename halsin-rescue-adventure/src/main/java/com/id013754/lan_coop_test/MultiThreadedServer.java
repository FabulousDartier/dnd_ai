package com.id013754.lan_coop_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MultiThreadedServer {
    private static List<PrintWriter> clientWriters = new ArrayList<>();

    public static void main(String[] args) {
        int portNumber = 12345;
        System.out.println("MultiThreadedServer: Starting server on port: " + portNumber);

        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);) {
            while (true) {
                // Wait for the client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: New client connected: " + clientSocket.getRemoteSocketAddress());

                ClientHandler clientThread = new ClientHandler(clientSocket);
                new Thread(clientThread).start();
            }
        } catch (IOException e) {
            System.err.println("Server error:  + " + e.getMessage());
        }
    }

    // This method sends a message to every connected client.
    private static void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    // Each instance of this class will handle one client connection on a separate
    // thread
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter writer;
        private BufferedReader reader;
        private String clientName;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // Set up input and output streams for this client
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // Add this client's writer to the shared list so it can receive broadcasts.
                clientWriters.add(writer);

                // Ask for client's name and annouce their arrival
                writer.println("Welcome! Please enter your name:");
                clientName = reader.readLine();
                if (clientName == null || clientName.trim().isEmpty()) {
                    clientName = "Anonymous-" + (int) (Math.random() * 100);
                }
                broadcastMessage(clientName + " has joined the chat.");
                System.out.println("Server: " + clientName + " has join");

                String clientMessage;
                while ((clientMessage = reader.readLine()) != null) {
                    // Check if the client wants to quit
                    if ("/quit".equalsIgnoreCase(clientMessage)) {
                        break;
                    }

                    // Create the message to be broadcasted to everyone
                    String broadcastString = clientName + ": " + clientMessage;

                    // Print to the server's console for logging purposes
                    System.out.println("Server: Broadcasting from " + broadcastString);

                    // Send the message to all connected clients
                    broadcastMessage(broadcastString);
                }
            } catch (IOException e) {
                System.out.println("Server: Error with client " + clientName + ": " + e.getMessage());
            } finally {
                // When the client disconnects, clean up
                if (clientName != null) {
                    System.out.println("Server: " + clientName + " has disconnected.");
                    broadcastMessage(clientName + " has left the chat.");
                }
                if (writer != null) {
                    clientWriters.remove(writer); // Remove from broadcast list
                }
                try {
                    socket.close(); // Close the connection to this client
                } catch (IOException e) {
                }
            }
        }
    }
}
