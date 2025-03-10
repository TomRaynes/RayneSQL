package edu.uob.command;

import edu.uob.DBException;
import edu.uob.DBServer;
import edu.uob.database.Database;

import java.util.ArrayList;

public class CreateTableCommand extends Command {

    private final String tableName;
    private final ArrayList<String> attributes;

    public CreateTableCommand(String tableName, ArrayList<String> attributes) {
        this.tableName = tableName;
        this.attributes = attributes;
    }

    public String execute(DBServer server) throws Exception {
        Database database = server.getActiveDatabase();
        if (database == null) throw new DBException.NoActiveDatabaseException();
        database.addTable(tableName, attributes);
        return null;
    }

    public String getClassAttributes() {
        return attributes == null ? tableName : tableName + listToString(attributes);
    }
}
