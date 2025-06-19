package com.id013754.lan_coop_test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class SimpleClient {
    public static void main(String[] args) {
        String hostName = "localhost";
        int portNumber = 12345;

        System.out.println("SimpleClient: Trying to connect to " + hostName + ":" + portNumber);

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));) {
            System.out.println("SimpleClient: Successfully connected to the server.");
            String serverMessage = in.readLine();
            System.out.println("SimpleClient: Message from server: \"" + serverMessage + "\"");
            System.out.println("SimpleClient: Type your message for the server: ");
            String userInput = stdIn.readLine();

            if (userInput != null) {
                out.println(userInput);
                System.out.println("SimpleClient: Sent message: \"" + userInput + "\"");

                String serverResponse = in.readLine();
                System.out.println("SimpleClient: Response from server: \"" + serverResponse + "\"");
            }

            System.out.println("SimpleClient: Closing connection.");
        } catch (UnknownHostException e) {
            System.err.println("SimpleClient: Can't recognize the host " + hostName);
        } catch (IOException e) {
            System.err.println("SimpleClient");
        }

        System.out.println("SimpleClient: Client has finished.");
    }
}