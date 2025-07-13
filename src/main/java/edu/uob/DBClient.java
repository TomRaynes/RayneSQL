package edu.uob;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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
        // BufferedReader input = new BufferedReader(new InputStreamReader(System.in))
        try (Terminal input = TerminalBuilder.builder().system(true).build();
             Socket socket = new Socket("localhost", 8888);
             BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter socketWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            LineReader inputReader = LineReaderBuilder.builder().terminal(input).build();

            Thread handleIncomingMessages = new Thread(() -> getIncomingMessages(socketReader, inputReader));
            handleIncomingMessages.start();

            while (!Thread.interrupted()) {
                getOutgoingCommand(inputReader, socketWriter);
            }
        }
        catch (IOException e) {
            System.out.println("\u001B[33m\nFiled to connect with server\u001B[0m");
            System.exit(1);
        }
    }

    private static  void getOutgoingCommand(LineReader inputReader, BufferedWriter socketWriter) {
        String command = null;
        try {
            command = inputReader.readLine("SQL:> ");
        } catch (EndOfFileException | UserInterruptException e) {
            System.exit(0);
        }
        if (command == null || command.equalsIgnoreCase("exit")) {
            System.exit(0);
        }
        try {
            socketWriter.write(command + "\n");
            socketWriter.flush();
        } catch (IOException e) {
            inputReader.printAbove("\u001B[33mError communicating with server\u001B[0m");
            //System.out.print("\u001B[33m\nError communicating with server\u001B[0m\nSQL:> ");
        }
    }

    private static void getIncomingMessages(BufferedReader socketReader, LineReader lineReader) {
        while (!Thread.interrupted()) {
            StringBuilder response = new StringBuilder();
            String line = null;

            try {
                while ((line = socketReader.readLine()) != null) {
                    response.append(line).append("\n");

                    if (line.contains(END_OF_TRANSMISSION)) {
                        if (!Objects.equals(line, END_OF_TRANSMISSION)) { // non-empty response
                            String fullResponse = response.toString().replace(END_OF_TRANSMISSION, "");
                            lineReader.printAbove(fullResponse);
                        }
                        break;
                    }
                }
            }
            catch (IOException e) {
                //System.out.print("\u001B[33m\nError communicating with server\u001B[0m\nSQL:> ");
                lineReader.printAbove("\u001B[33mError communicating with server\u001B[0m");
            }
            if (line == null) {
                //System.out.println("\u001B[33m\nConnection to server has been lost\u001B[0m");
                lineReader.printAbove("\u001B[33mConnection to server has been lost\u001B[0m");
                System.exit(1);
            }
        }
    }
}

