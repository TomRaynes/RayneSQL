package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

import java.util.Objects;

public class DropDatabaseCommand extends Command {

    private final String databaseName;

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server) throws Exception {

        Database database = new Database(server.getStorageFolderPath(), databaseName);
        database.loadDatabase();

        Database activeDatabase = server.getActiveDatabase();

        String databaseName = database.getDatabaseName();
        String activeDatabaseName = null;

        if (activeDatabase != null) {
            activeDatabaseName = activeDatabase.getDatabaseName();
        }
        if (Objects.equals(databaseName, activeDatabaseName)) {
            server.setActiveDatabase(null);
        }
        database.deleteDatabase();
        return "";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
