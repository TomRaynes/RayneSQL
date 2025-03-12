package edu.uob.command;

import edu.uob.DBException;
import edu.uob.DBServer;
import edu.uob.database.Database;

import java.util.ArrayList;

public class JoinCommand extends Command {

    String tableName1;
    String tableName2;
    String attribute1;
    String attribute2;

    public JoinCommand(String tableName1, String tableName2, String attribute1, String attribute2) {
        this.tableName1 = tableName1;
        this.tableName2 = tableName2;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }

    public String execute(DBServer server) throws Exception {

        Database database = server.getActiveDatabase();

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
