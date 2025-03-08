package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

public class DropDatabaseCommand extends Command {

    private final String databaseName;

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server) throws Exception {

        Database database = new Database(server.getStorageFolderPath(), databaseName);
        database.deleteDatabase();
        return null;
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
