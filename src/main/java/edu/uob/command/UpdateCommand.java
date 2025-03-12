package edu.uob.command;

import edu.uob.DBException;
import edu.uob.DBServer;
import edu.uob.NameValuePair;
import edu.uob.condition.ConditionNode;
import edu.uob.database.Table;
import edu.uob.database.TableRow;

import java.util.ArrayList;

public class UpdateCommand extends Command {

    String tableName;
    ArrayList<NameValuePair> nameValuePairs;
    ConditionNode condition;

    public UpdateCommand(String tableName, ArrayList<NameValuePair> nameValuePairs,
                         ConditionNode condition) {
        this.tableName = tableName;
        this.nameValuePairs = nameValuePairs;
        this.condition = condition;
    }

    public String execute(DBServer server) throws Exception {

        Table table = getTable(server, tableName);
        table.loadTableData();
        ArrayList<TableRow> rows = table.getRowsFromCondition(condition);

        for (NameValuePair pair : nameValuePairs) {
            int index = table.getAttributeIndex(pair.getAttribute());

            if (index < 0) {
                throw new DBException.AttributeDoesNotExistException(pair.getAttribute());
            }
            if (index == 0) {
                throw new DBException.TryingToChangeIdException();
            }
            for (TableRow row : rows) {
                row.setEntryFromIndex(index, pair.getValue().toString());
            }
        }
        table.saveTable();
        return "";
    }

    public String getClassAttributes() {

        String nameValList = "";
        for (NameValuePair pair : nameValuePairs) nameValList += ", " + pair.toString();
        return tableName + nameValList + ", " + ConditionNode.getTreeAsString(condition);
    }
}
