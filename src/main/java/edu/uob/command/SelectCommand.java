package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.condition.ConditionNode;
import edu.uob.database.Database;
import edu.uob.database.Table;
import edu.uob.database.TableRow;

import java.util.ArrayList;

public class SelectCommand extends Command {

    String tableName;
    ArrayList<String> attributes;
    ConditionNode condition;

    public SelectCommand(String tableName, ArrayList<String> attributes, ConditionNode condition) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.condition = condition;
    }

    public String execute(DBServer server) throws Exception {

        Database database = server.getActiveDatabase();
        if (database == null) throw new Exception();
        Table table = database.getTable(tableName);
        if (table == null) throw new Exception();

        ArrayList<TableRow> rows = table.getRowsFromCondition(condition);
        ArrayList<Integer> attributeIndexes = table.getAttributeIndexes(attributes);

        ArrayList<ArrayList<String>> selectedData = getSelectedData(attributes,
                                                            rows, attributeIndexes);

        ArrayList<Integer> maxColWidths = getMaxColWidths(selectedData);
        return getReturnString(selectedData, maxColWidths);
    }

    public String getClassAttributes() {

        String attributeList;
        if (attributes == null) attributeList = ", *";
        else attributeList = listToString(attributes);
        return tableName + attributeList + ", " + ConditionNode.getTreeAsString(condition);
    }
}
