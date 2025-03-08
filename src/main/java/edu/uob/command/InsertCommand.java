package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;
import edu.uob.database.Table;

import java.util.ArrayList;

public class InsertCommand extends Command {

    private final String tableName;
    private final ArrayList<String> values;

    public InsertCommand(String tableName, ArrayList<String> values) {
        this.tableName = tableName;
        this.values = values;

    }

    public String execute(DBServer server) throws Exception {

        Database database = server.getActiveDatabase();
        if (database == null) throw new Exception();
        Table table = database.getTable(tableName);
        if (table == null) throw new Exception();
        table.addRow(values);
        return null;
    }

    public String getClassAttributes() {
        return tableName + listToString(values);
    }
}
