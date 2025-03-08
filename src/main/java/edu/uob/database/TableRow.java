package edu.uob.database;

import java.util.ArrayList;
import java.util.List;

public class TableRow {
    List<ColumnEntry> data;

    public TableRow(List<String> rowData) {

        data = new ArrayList<>();

        for (String datum : rowData) {
            data.add(new ColumnEntry(datum));
        }
    }

    public void addColumnEntry(String entry) {
        data.add(new ColumnEntry(entry));
    }

    public void removeColumnEntry(int index) {
        data.remove(index);
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (ColumnEntry col : data) {
            str.append(col.toString());
            str.append("\t");
        }
        return str.toString();
    }

}
