package RayneSQL.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableRow {
    ArrayList<ColumnEntry> data;

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

    public void setEntryFromIndex(int index, String value) {
        data.get(index).setValue(value);
    }

    public TableRow mergeRow(TableRow row) {

        ArrayList<String> mergedRow = toStringArray();
        mergedRow.addAll(row.toStringArray());
        return new TableRow(mergedRow);
    }

    public ArrayList<String> toStringArray() {

        ArrayList<String> rowData = new ArrayList<>();
        for (ColumnEntry entry : data) rowData.add(entry.getValue());
        return rowData;
    }

    public ColumnEntry getEntryFromIndex(int index) {
        return data.get(index);
    }

    public ArrayList<String> getDeepCopy() {
        ArrayList<String> rowCopy = new ArrayList<>();

        for (ColumnEntry datum : data) {
            rowCopy.add(datum.toString());
        }
        return rowCopy;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();

        for (ColumnEntry col : data) {
            String entry = col.toString();
            if (Objects.equals(entry, "")) str.append(".");
            else str.append(entry);
            str.append("\t");
        }
        return str.toString();
    }

}
