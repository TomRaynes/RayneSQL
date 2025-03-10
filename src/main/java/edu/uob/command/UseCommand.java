package edu.uob.command;

import edu.uob.DBException;
import edu.uob.DBServer;
import edu.uob.database.Database;

public class UseCommand extends Command {

    private final String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server) throws Exception
    {
        Database database = new Database(server.getStorageFolderPath(), databaseName);

        if (!database.databaseExists()) {
            throw new DBException.DatabaseDoesNotExistException(databaseName);
        }
        database.loadDatabase();
        server.setActiveDatabase(database);
        return null;
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
