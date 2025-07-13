package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Table;

import java.net.SocketAddress;
import java.util.ArrayList;

public class InsertCommand extends Command {

    private final String tableName;
    private final ArrayList<String> values;

    public InsertCommand(String tableName, ArrayList<String> values) {
        this.tableName = tableName;
        this.values = values;

    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {

        Table table = getTable(server, tableName, socketAddress);
        table.loadTableData();
        table.addRow(values);
        table.saveTable();
        return "";
    }

    public String getClassAttributes() {
        return tableName + listToString(values);
    }
}
