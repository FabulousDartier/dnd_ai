package com.id013754.lan_coop_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {

    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 12345;

        System.out.println("ChatClient: Trying to connect to " + hostName + ":" + portNumber);

        try {
            Socket socket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

            // Start a new thread to continuously listen for messages from the server.
            ServerListener listener = new ServerListener(in);
            new Thread(listener).start();

            // The main thread will now handle sending user input to the server
            String userInput;
            while (true) {
                userInput = stdIn.readLine();
                if (userInput != null) {
                    out.println(userInput); // Send message to the server
                    if ("/quit".equalsIgnoreCase(userInput)) {
                        break;
                    }
                }
            }

            // Clean up
            socket.close();
            System.out.println("ChatClient: Disconnect.");
        } catch (UnknownHostException e) {
            System.err.println("Client error: Can't recognize the host " + hostName);
        } catch (IOException e) {
            System.err.println("Client error: Couldn't connect to " + hostName);
        }
    }

    // This class runs on a separate thread to listen for server messsages.
    // This prevents the client from getting stuck waiting for user input.
    private static class ServerListener implements Runnable {
        private final BufferedReader serverReader;

        public ServerListener(BufferedReader serverReader) {
            this.serverReader = serverReader;
        }

        @Override
        public void run() {
            try {
                String serverMessage;
                // Continously read messages from the server and print them.
                while ((serverMessage = serverReader.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }
}
