package edu.uob.command;

public class JoinCommand extends Command {

    String tableName1;
    String tableName2;
    String attribute1;
    String attribute2;

    public JoinCommand(String tableName1, String tableName2, String attribute1, String attribute2) {
        this.tableName1 = tableName1;
        this.tableName2 = tableName2;
        this.attribute1 = attribute1;
        this.attribute2 = attribute2;
    }
}
