package edu.uob;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Objects;

public class DBClient {

    private static final String END_OF_TRANSMISSION = "\u0004";


    public static void main(String[] args) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
             Socket socket = new Socket("localhost", 8888);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            Thread handleIncomingMessages = new Thread(() -> getIncomingMessages(socketReader));
            handleIncomingMessages.start();

            while (!Thread.interrupted()) {
                getOutgoingCommand(input, socketWriter);
            }
        }
        catch (IOException e) {
            System.out.println("\u001B[33m\nError initialising Client\u001B[0m");
            System.exit(1);
        }
    }

    private static void getOutgoingCommand(BufferedReader commandLine, BufferedWriter socketWriter) {
        String command = null;
        try {
            command = commandLine.readLine();
        } catch (IOException e) {
            System.out.print("\u001B[33m\nError reading from command line\u001B[0m\nSQL:> ");
        }

        if (command == null) {
            return;
        }
        try {
            socketWriter.write(command + "\n");
            socketWriter.flush();
        } catch (IOException e) {
            System.out.print("\u001B[33m\nError communicating with server\u001B[0m\nSQL:> ");
        }
    }

    private static void getIncomingMessages(BufferedReader socketReader) {
        while (!Thread.interrupted()) {
            StringBuilder response = new StringBuilder();
            String line = null;

            try {
                while ((line = socketReader.readLine()) != null) {
                    response.append(line).append("\n");

                    if (line.contains(END_OF_TRANSMISSION)) {
                        if (!Objects.equals(line, END_OF_TRANSMISSION)) { // non-empty response
                            String fullResponse = response.toString().replace(END_OF_TRANSMISSION, "");
                            System.out.println(fullResponse);
                        }
                        System.out.print("SQL:> ");
                        break;
                    }
                }
            }
            catch (IOException e) {
                System.out.print("\u001B[33m\nError communicating with server\u001B[0m\nSQL:> ");
            }
            if (line == null) {
                System.out.println("\u001B[33m\nConnection to server has been lost\u001B[0m");
                System.exit(1);
            }
        }
    }
}
