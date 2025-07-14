package RayneSQL.command;

import RayneSQL.DBException;
import RayneSQL.database.Database;

import java.net.SocketAddress;
import java.util.ArrayList;

public class CreateTableCommand extends Command {

    private final String tableName;
    private final ArrayList<String> attributes;

    public CreateTableCommand(String tableName, ArrayList<String> attributes) {
        this.tableName = tableName;
        this.attributes = attributes;
    }

    public String execute(RayneSQL.DBServer server, SocketAddress socketAddress) throws Exception {
        Database database = server.getActiveDatabase(socketAddress);
        if (database == null) throw new DBException.NoActiveDatabaseException();
        database.executeUnderReadLock(database::loadDatabase);
        database.executeUnderWriteLock(() -> database.addTable(tableName, attributes));
        return "";
    }

    public String getClassAttributes() {
        return attributes == null ? tableName : tableName + listToString(attributes);
    }
}
