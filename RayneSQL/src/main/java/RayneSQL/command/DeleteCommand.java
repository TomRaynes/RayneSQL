package RayneSQL.command;

import RayneSQL.condition.ConditionNode;
import RayneSQL.database.Table;
import RayneSQL.database.TableRow;

import java.net.SocketAddress;
import java.util.ArrayList;

public class DeleteCommand extends Command {

    private final String tableName;
    private final ConditionNode condition;

    public DeleteCommand(String tableName, ConditionNode condition) {
        this.tableName = tableName;
        this.condition = condition;
    }

    public String execute(RayneSQL.DBServer server, SocketAddress socketAddress) throws Exception {

        Table table = getTable(server, tableName, socketAddress);
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
