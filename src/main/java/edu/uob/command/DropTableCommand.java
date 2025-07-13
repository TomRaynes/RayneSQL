package edu.uob.command;

import edu.uob.DBException;
import edu.uob.DBServer;
import edu.uob.database.Database;

import java.net.SocketAddress;

public class DropTableCommand extends Command {

    private final String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {

        Database database = server.getActiveDatabase(socketAddress);

        if (database == null) {
            throw new DBException.NoActiveDatabaseException();
        }
        database.loadDatabase();
        database.removeTable(tableName);
        return "";
    }

    public String getClassAttributes() {
        return tableName;
    }
}
