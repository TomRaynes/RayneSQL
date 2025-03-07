package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

public class UseCommand extends Command {

    private final String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server) {
        Database database = new Database(server.getStorageFolderPath(), databaseName);

        try {
            database.loadTables();
        }
        catch (Exception e) {
            return "[ERROR]";
        }
        server.setActiveDatabase(database);
        return "[OK]";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
