package RayneSQL.command;

import RayneSQL.DBServer;
import RayneSQL.database.Database;

import java.net.SocketAddress;

public class UseCommand extends Command {

    private final String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception
    {
        Database database = new Database(server.getStorageFolderPath(), databaseName);

        if (!database.databaseExists()) {
            throw new RayneSQL.DBException.DatabaseDoesNotExistException(databaseName);
        }
        database.loadDatabase();
        server.setActiveDatabase(database, socketAddress);
        return "";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
