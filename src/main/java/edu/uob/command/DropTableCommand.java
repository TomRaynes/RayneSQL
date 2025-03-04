package edu.uob.command;

public class DropTableCommand extends Command {

    private final String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }
}
