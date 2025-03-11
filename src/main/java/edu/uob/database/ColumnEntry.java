package edu.uob.database;

public class ColumnEntry {
    String entry;

    public ColumnEntry(String entry) {
        this.entry = entry;
    }

    public void setValue(String entry) {
        this.entry = entry;
    }

    public String getValue() {

        if (entry.startsWith("'") && entry.endsWith("'")) {
            return entry.substring(1, entry.length()-1);
        }
        return entry;
    }

    public String toString() {
        return entry;
    }
}
