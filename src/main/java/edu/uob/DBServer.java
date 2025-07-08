package edu.uob;

import edu.uob.command.Command;
import edu.uob.database.Database;
import edu.uob.token.Token;
import edu.uob.token.Tokeniser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBServer {

    private static final char END_OF_TRANSMISSION = 4;
    private final String storageFolderPath;
    private Database activeDatabase = null;

    public static void main(String[] args) throws IOException {
        DBServer server = new DBServer();
        server.listenForConnections(8888);
    }

    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        try {
            // Create the database storage folder if it doesn't already exist
            Files.createDirectories(Paths.get(storageFolderPath));
        } catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public String handleCommand(String query) {

        if (Objects.equals(query, "")) return "";
        String response;

        try {
            Tokeniser tokeniser = new Tokeniser(query);
            ArrayList<Token> tokens = tokeniser.getAllTokens();
            Parser parser = new Parser(tokens);
            Command command;
            command = parser.parseQuery();
            response = command.execute(this);
        }
        catch (Exception e) {
            return "[ERROR]" + e.getMessage();
        }
        return "[OK]" + response;
    }

    public Database getActiveDatabase() {
        return activeDatabase;
    }

    public void setActiveDatabase(Database database) {
        activeDatabase = database;
    }

    public String getStorageFolderPath() {
        return storageFolderPath;
    }

    public void listenForConnections(int portNumber) throws IOException {
        ExecutorService socketThreads = Executors.newCachedThreadPool();

        try (ServerSocket ss = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = ss.accept(); // block until new connection is established
                socketThreads.submit(() -> { // add to socket pool
                    try {
                        handleConnection(socket);
                    }
                    catch (IOException e) {
                        System.err.println("Server encountered a non-fatal IO error:");
                        e.printStackTrace();
                        System.err.println("Continuing...");
                    }
                });
            }
        }
        finally {
            socketThreads.shutdown();
        }
    }

    private void handleConnection(Socket s) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()))) {

            System.out.println("Connection established: " + s.getRemoteSocketAddress());
            String incomingCommand;

            while ((incomingCommand = reader.readLine()) != null) {
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand);
                writer.write(result);
                writer.write("\n" + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
        finally {
            System.out.println("Connection closed: " + s.getRemoteSocketAddress());
            s.close();
        }
    }
}
