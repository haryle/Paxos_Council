package utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class WriteServer {
    public static void main(String[] args) {
        final int PORT = 8080;

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                // Wait for a client to connect
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress());

                // Create PrintWriter to send data to the client
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Create Scanner to read data from the server console
                Scanner scanner = new Scanner(System.in);

                // Send messages to the client based on server console input
                while (true) {
                    System.out.print("Enter a message to send to the client (or type " +
                                     "'exit' to stop): ");
                    String message = scanner.nextLine();

                    // Send the message to the client
                    out.println(message);
                    System.out.println("Sent to client: " + message);

                    // Check for exit command
                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }

                // Close resources after serving the client
                out.close();
                scanner.close();
                clientSocket.close();

                // Client has been served, wait for the next client
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
