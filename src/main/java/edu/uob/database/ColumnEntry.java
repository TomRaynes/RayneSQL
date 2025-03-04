package edu.uob.database;

public class ColumnEntry {
    String entry;

    public ColumnEntry(String entry) {
        this.entry = entry;
    }

    public void setValue(String entry) {
        this.entry = entry;
    }

    public String toString() {
        return entry;
    }
}
