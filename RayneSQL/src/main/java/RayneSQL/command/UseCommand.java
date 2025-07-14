package RayneSQL.command;

import RayneSQL.DBServer;
import RayneSQL.database.Database;

import java.net.SocketAddress;

public class UseCommand extends Command {

    private final String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {
        Database oldDatabase = server.getActiveDatabase(socketAddress);
        Database database = server.getLoadedDatabase(databaseName);

        if (database == null) { // database not loaded
            database = new Database(server.getStorageFolderPath(), databaseName);
        }
        if (!database.databaseExists()) {
            throw new RayneSQL.DBException.DatabaseDoesNotExistException(databaseName);
        }
        database.loadDatabase();

        if (server.databaseInactive(database.getDatabaseName())) {
            server.trackDatabase(database);
        }
        server.setActiveDatabase(database, socketAddress);

        if (oldDatabase != null && server.databaseInactive(oldDatabase.getDatabaseName())) {
            server.untrackDatabase(oldDatabase);
        }
        return "";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
