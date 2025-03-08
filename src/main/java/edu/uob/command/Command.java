package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.condition.BooleanNode;
import edu.uob.condition.Comparator;
import edu.uob.condition.Condition;
import edu.uob.condition.ConditionNode;
import edu.uob.database.*;
import edu.uob.token.Token;
import edu.uob.token.TokenType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class Command {

    public abstract String execute(DBServer server) throws Exception;
    public abstract String getClassAttributes();

    protected static ArrayList<ArrayList<String>> getSelectedData(ArrayList<String> attributes,
            ArrayList<TableRow> rows, ArrayList<Integer> attributeIndexes) {

        ArrayList<ArrayList<String>> selectedData = new ArrayList<>();
        selectedData.add(attributes);

        for (TableRow row : rows) {
            ArrayList<String> selectedValues = new ArrayList<>();

            for (int index : attributeIndexes) {
                selectedValues.add(row.getEntryFromIndex(index).getValue());
            }
            selectedData.add(selectedValues);
        }
        return selectedData;
    }

    protected static ArrayList<Integer> getMaxColWidths(ArrayList<ArrayList<String>> table) {

        ArrayList<Integer> maxColWidths = new ArrayList<>();

        for (int col=0; col<table.get(0).size(); col++) {
            int maxColWidth = 0;
            for (ArrayList<String> row : table) {
                maxColWidth = Math.max(maxColWidth, row.get(col).length());
            }
            maxColWidths.add(maxColWidth);
        }
        return maxColWidths;
    }

    protected static String getReturnString(ArrayList<ArrayList<String>> table,
                                                 ArrayList<Integer> maxColWidths) {

        StringBuilder str = new StringBuilder();

        for (ArrayList<String> row : table) {
            for (int col=0; col<table.get(0).size(); col++) {

                str.append(String.format("%-" + (maxColWidths.get(col)+3) + "s", row.get(col)));
            }
            str.append("\n");
        }
        return str.toString();
    }

    protected static String listToString(ArrayList<String> strings) {

        String str = "";
        for (String string : strings) str += ", " + string;
        return str;
    }
}

