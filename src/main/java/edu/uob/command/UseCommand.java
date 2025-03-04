package edu.uob.command;

public class UseCommand extends Command {

    private final String databaseName;

    public UseCommand(String databaseName) {
        this.databaseName = databaseName;
    }
}
