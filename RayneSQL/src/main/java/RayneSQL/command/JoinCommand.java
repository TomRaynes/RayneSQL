package RayneSQL.command;

import RayneSQL.DBException;
import RayneSQL.database.Database;

import java.net.SocketAddress;
import java.util.ArrayList;

public class JoinCommand extends Command {

    private final String tableName1;
    private final String tableName2;
    private final String attribute1;
    private final String attribute2;

    public JoinCommand(String tableName1, String tableName2, String attribute1, String attribute2) {
        this.tableName1 = tableName1;
        this.tableName2 = tableName2;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }

    public String execute(RayneSQL.DBServer server, SocketAddress socketAddress) throws Exception {

        Database database = server.getActiveDatabase(socketAddress);

        if (database == null) {
            throw new DBException.NoActiveDatabaseException();
        }
        ArrayList<ArrayList<String>> joinedTable;
        joinedTable = database.hashJoinTables(this, database.getDatabaseName());
        return getReturnString(joinedTable);
    }

    public String getTableName1() {
        return tableName1;
    }

    public String getTableName2() {
        return tableName2;
    }

    public String getAttribute1() {
        return attribute1;
    }

    public String getAttribute2() {
        return attribute2;
    }

    public String getClassAttributes() {
        return tableName1 + ", " + tableName2 + ", " + attribute1 + ", " + attribute2;
    }
}
