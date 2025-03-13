package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.condition.ConditionNode;
import edu.uob.database.Table;
import edu.uob.database.TableRow;

import java.util.ArrayList;

public class SelectCommand extends Command {

    private final String tableName;
    private ArrayList<String> attributes;
    private final ConditionNode condition;

    public SelectCommand(String tableName, ArrayList<String> attributes, ConditionNode condition) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.condition = condition;
    }

    public String execute(DBServer server) throws Exception {

        Table table = getTable(server, tableName);
        table.loadTableData();
        ArrayList<TableRow> rows;

        attributes = table.getAttributes(attributes);
        if (condition == null) rows = table.getTableRows();
        else rows = table.getRowsFromCondition(condition);

        ArrayList<Integer> attributeIndexes = table.getAttributeIndexes(attributes);
        ArrayList<ArrayList<String>> selectedData = getSelectedData(attributes,
                                                            rows, attributeIndexes);
        return getReturnString(selectedData);
    }

    public String getClassAttributes() {

        String attributeList;
        if (attributes == null) attributeList = ", *";
        else attributeList = listToString(attributes);
        return tableName + attributeList + ", " + ConditionNode.getTreeAsString(condition);
    }
}
