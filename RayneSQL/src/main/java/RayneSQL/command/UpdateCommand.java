package RayneSQL.command;

import RayneSQL.DBServer;
import RayneSQL.NameValuePair;
import RayneSQL.condition.ConditionNode;
import RayneSQL.database.Table;
import RayneSQL.database.TableRow;

import java.net.SocketAddress;
import java.util.ArrayList;

public class UpdateCommand extends Command {

    private final String tableName;
    private final ArrayList<RayneSQL.NameValuePair> nameValuePairs;
    private final ConditionNode condition;

    public UpdateCommand(String tableName, ArrayList<RayneSQL.NameValuePair> nameValuePairs,
                         ConditionNode condition) {
        this.tableName = tableName;
        this.nameValuePairs = nameValuePairs;
        this.condition = condition;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {

        Table table = getTable(server, tableName, socketAddress);
        table.loadTableData();
        ArrayList<TableRow> rows = table.getRowsFromCondition(condition);

        for (NameValuePair pair : nameValuePairs) {
            int index = table.getAttributeIndex(pair.getAttribute());

            if (index < 0) {
                throw new RayneSQL.DBException.AttributeDoesNotExistException(pair.getAttribute());
            }
            if (index == 0) {
                throw new RayneSQL.DBException.TryingToChangeIdException();
            }
            for (TableRow row : rows) {
                row.setEntryFromIndex(index, pair.getValue().toString());
            }
        }
        table.saveTable();
        return "";
    }

    public String getClassAttributes() {

        StringBuilder nameValList = new StringBuilder();
        for (NameValuePair pair : nameValuePairs) nameValList.append(", ").append(pair.toString());
        return tableName + nameValList + ", " + ConditionNode.getTreeAsString(condition);
    }
}
