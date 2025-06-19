package com.id013754.lan_coop_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleServer {
    public static void main(String[] args) {
        int portNumber = 12345;

        System.out.println("SimpleServer: Starting server on port " + portNumber);
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
                Socket clientSocket = serverSocket.accept();
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));) {
            System.out.println("SimpleServer: Client connected from " + clientSocket.getRemoteSocketAddress());
            out.println("Welcome to the SimpleServer! Type a message and press Enter.");
            String inputLine;
            if ((inputLine = in.readLine()) != null) {
                System.out.println("SimpleServer: Received from client: \"" + inputLine + "\"");
                out.println("SimpleServer: Sent response back to client.");
            }

            System.out.println("SimpleServer: Closing connection with this client.");
        } catch (IOException e) {
            System.err.println(
                    "SimpleServer: Could not listen on port " + portNumber + ". Another program might be using it.");
            System.err.println(e.getMessage());
        }

        System.out.println("SimpleServer: Server has shut down.");
    }
}
