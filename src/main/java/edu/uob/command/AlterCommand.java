package edu.uob.command;

public class AlterCommand extends Command {

    private final String tableName;
    private final String alterationType;
    private final String attribute;

    public AlterCommand(String tableName, String alterationType, String attribute) {
        this.tableName = tableName;
        this.alterationType = alterationType;
        this.attribute = attribute;
    }
}
