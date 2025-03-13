package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.condition.ConditionNode;
import edu.uob.database.Table;
import edu.uob.database.TableRow;

import java.util.ArrayList;

public class DeleteCommand extends Command {

    private final String tableName;
    private final ConditionNode condition;

    public DeleteCommand(String tableName, ConditionNode condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    public String execute(DBServer server) throws Exception {

        Table table = getTable(server, tableName);
        table.loadTableData();
        ArrayList<TableRow> rows = table.getRowsFromCondition(condition);

        for (TableRow row : rows) {
            table.removeRow(row);
        }
        table.saveTable();
        return "";
    }

    public String getClassAttributes() {
        return tableName + ", " + ConditionNode.getTreeAsString(condition);
    }
}
