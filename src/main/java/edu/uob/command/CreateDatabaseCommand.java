package edu.uob.command;

public class CreateDatabaseCommand extends Command {

    private final String databaseName;

    public CreateDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
