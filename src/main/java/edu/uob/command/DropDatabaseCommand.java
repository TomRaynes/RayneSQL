package edu.uob.command;

public class DropDatabaseCommand extends Command {

    private final String databaseName;

    public DropDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
