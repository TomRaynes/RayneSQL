package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;

public class DropDatabaseCommand extends Command {

    private final String databaseName;

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {

        Database database = new Database(server.getStorageFolderPath(), databaseName);
        database.loadDatabase();

        Database activeDatabase = server.getActiveDatabase(socketAddress);

        String databaseName = database.getDatabaseName();
        String activeDatabaseName = null;

        if (activeDatabase != null) {
            activeDatabaseName = activeDatabase.getDatabaseName();
        }
        if (Objects.equals(databaseName, activeDatabaseName)) {
            server.setActiveDatabase(null, socketAddress);
        }
        for (Map.Entry<SocketAddress, Database> entry : server.getActiveDatabases().entrySet()) {
            if (entry.getKey() == socketAddress || entry.getValue() == null) {
                continue;
            }
            // Warn all other clients using the database that it was deleted
            if (Objects.equals(entry.getValue().getDatabaseName(), database.getDatabaseName())) {
                server.setActiveDatabase(null, entry.getKey());
                server.writeToSocket(entry.getKey(),
                        "Active database '" + database.getDatabaseName()
                                + "' was deleted by another user");
            }
        }
        database.deleteDatabase();
        return "";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
