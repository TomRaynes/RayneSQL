package edu.uob.command;

import java.util.ArrayList;

public class CreateTableCommand extends Command {

    private final String tableName;
    private final ArrayList<String> attributes;

    public CreateTableCommand(String tableName, ArrayList<String> attributes) {
        this.tableName = tableName;
        this.attributes = attributes;
    }

    public String getClassAttributes() {
        return attributes == null ? tableName : tableName + listToString(attributes);
    }
}
