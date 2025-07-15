package RayneSQL;

import RayneSQL.command.Command;
import RayneSQL.database.Database;
import RayneSQL.token.Token;
import RayneSQL.token.Tokeniser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DBServer {
    private static int port = 8888;
    private static final String END_OF_TRANSMISSION = "\u0004";
    private final String storageFolderPath;
    private final ExecutorService socketThreads = Executors.newCachedThreadPool();
    private final Map<String, Database> loadedDatabases;
    private final HashMap<SocketAddress, Database> activeDatabases;
    private final ConcurrentHashMap<SocketAddress, BufferedWriter> socketWriters;
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BOLD = "\u001B[1m";
    private static final String RESET = "\u001B[0m";
    private static final String asciiArtLogo = """
                \u001B[1m\u001B[31m ____                        \u001B[97m____   ___  _    \s
                \u001B[31m|  _ \\ __ _ _   _ _ __   ___\u001B[97m/ ___| / _ \\| |   \s
                \u001B[31m| |_) / _` | | | | '_ \\ / _ \u001B[97m\\___ \\| | | | |   \s
                \u001B[31m|  _ < (_| | |_| | | | |  __/\u001B[97m___) | |_| | |___\s
                \u001B[31m|_| \\_\\__,_|\\__, |_| |_|\\___\u001B[97m|____/ \\__\\_\\_____|
                \u001B[31m            |___/                              \u001B[0m
            \s""";

    public static void main(String[] args) throws IOException {
        DBServer server = new DBServer();

        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        server.listenForConnections(port);
    }

    public DBServer() {
        storageFolderPath = Paths.get("databases").toAbsolutePath().toString();
        loadedDatabases = new HashMap<>();
        activeDatabases = new HashMap<>();
        socketWriters = new ConcurrentHashMap<>();

        try {
            // Create the database storage folder if it doesn't already exist
            Files.createDirectories(Paths.get(storageFolderPath));
        }
        catch(IOException ioe) {
            System.out.println("Can't seem to create database storage folder " + storageFolderPath);
        }
    }

    public String handleCommand(String query, SocketAddress socketAddress) {

        if (Objects.equals(query, "")) return "";
        String response;

        try {
            Tokeniser tokeniser = new Tokeniser(query);
            ArrayList<Token> tokens = tokeniser.getAllTokens();
            Parser parser = new Parser(tokens);
            Command command;
            command = parser.parseQuery();
            response = command.execute(this, socketAddress);
        }
        catch (Exception e) {
            return "[" + RED + BOLD + "ERROR" + RESET + "]" + e.getMessage();
        }
        return "[" + GREEN + BOLD + "OK" + RESET + "]" + response;
    }

    public HashMap<SocketAddress, Database> getActiveDatabases() {
        return activeDatabases;
    }

    public Database getActiveDatabase(SocketAddress socketAddress) {
        return activeDatabases.get(socketAddress);
    }

    public Database getLoadedDatabase(String databaseName) {
        return loadedDatabases.get(databaseName);
    }

    public void trackDatabase(Database database) {
        loadedDatabases.put(database.getDatabaseName(), database);
    }

    public void untrackDatabase(Database database) {
        loadedDatabases.remove(database.getDatabaseName());
    }

    public boolean databaseInactive(String targetName) {
        return activeDatabases.values().stream().filter(Objects::nonNull)
                .anyMatch(db -> targetName.equals(db.getDatabaseName()));
    }

    public void setActiveDatabase(Database database, SocketAddress socketAddress) {
        if (activeDatabases.containsKey(socketAddress)) {
            activeDatabases.replace(socketAddress, database);
        }
        else activeDatabases.put(socketAddress, database);
    }

    public String getStorageFolderPath() {
        return storageFolderPath;
    }

    public void listenForConnections(int portNumber) throws IOException {
        try (ServerSocket ss = new ServerSocket(portNumber)) {
            System.out.println("Server listening on port " + portNumber);

            while (!Thread.currentThread().isInterrupted()) {
                Socket socket = ss.accept(); // block until new connection is established
                // client starts with no active database
                setActiveDatabase(null, socket.getRemoteSocketAddress());
                socketThreads.submit(() -> { // add to execution pool
                    try {
                        handleConnection(socket);
                    }
                    catch (IOException e) {
                        System.err.println("Server encountered a non-fatal error while serving a client");
                    }
                });
            }
        }
        finally {
            socketThreads.shutdown();
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        SocketAddress socketAddress = socket.getRemoteSocketAddress();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             socket) {

            System.out.println("Connection established: " + socketAddress);
            socketWriters.put(socketAddress, writer);
            writer.write("\n" + asciiArtLogo + END_OF_TRANSMISSION + "\n");
            writer.flush();
            String incomingCommand;

            while ((incomingCommand = reader.readLine()) != null) {
                System.out.println("Received message: " + incomingCommand);
                String result = handleCommand(incomingCommand, socketAddress);
                writer.write(result + END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
        }
        finally {
            System.out.println("Connection closed: " + socketAddress);
            socketWriters.remove(socketAddress);
        }
    }

    public void writeToSocket(SocketAddress socketAddress, String message) {
        socketThreads.submit(() -> {
            BufferedWriter writer = socketWriters.get(socketAddress);

            try {
                writer.write(YELLOW + message + RESET);
                writer.write(END_OF_TRANSMISSION + "\n");
                writer.flush();
            }
            catch (IOException e) {
                System.out.println("Unable to write to socket: " + socketAddress);
            }
        });
    }
}

