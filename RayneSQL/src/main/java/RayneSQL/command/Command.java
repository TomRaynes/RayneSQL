package RayneSQL.command;

import RayneSQL.DBException;
import RayneSQL.DBServer;
import RayneSQL.database.*;

import java.net.SocketAddress;
import java.util.ArrayList;

public abstract class Command {

    public abstract String execute(DBServer server, SocketAddress socketAddress) throws Exception;
    public abstract String getClassAttributes();

    protected static Table getTable(DBServer server, String tableName, SocketAddress socketAddress) throws Exception {

        Database database = server.getActiveDatabase(socketAddress);

        if (database == null) {
            throw new DBException.NoActiveDatabaseException();
        }
        database.loadDatabase();
        Table table = database.getTable(tableName);

        if (table == null) {
            throw new DBException.TableDoesNotExistException(tableName, database.getDatabaseName());
        }
        return table;
    }

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

    protected static String getReturnString(ArrayList<ArrayList<String>> table) {

        ArrayList<Integer> maxColWidths = getMaxColWidths(table);
        StringBuilder str = new StringBuilder();

        for (ArrayList<String> row : table) {
            for (int col=0; col<table.get(0).size(); col++) {

                str.append(String.format("%-" + (maxColWidths.get(col)+3) + "s", row.get(col)));
            }
            if (row != table.get(table.size()-1)) str.append("\n");
        }
        return "\n" + str;
    }

    protected static String listToString(ArrayList<String> strings) {

        StringBuilder str = new StringBuilder();
        for (String string : strings) str.append(", ").append(string);
        return str.toString();
    }
}

