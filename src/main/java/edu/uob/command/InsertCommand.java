package edu.uob.command;

import java.util.ArrayList;

public class InsertCommand extends Command {

    private final String tableName;
    private final ArrayList<String> values;

    public InsertCommand(String tableName, ArrayList<String> values) {
        this.tableName = tableName;
        this.values = values;

    }

    public String getClassAttributes() {
        return tableName + listToString(values);
    }
}
