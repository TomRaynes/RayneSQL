package edu.uob.command;

import edu.uob.condition.ConditionNode;

import java.util.ArrayList;

public class SelectCommand extends Command {

    String tableName;
    ArrayList<String> attributes;
    ConditionNode condition;

    public SelectCommand(String tableName, ArrayList<String> attributes, ConditionNode condition) {
        this.tableName = tableName;
        this.attributes = attributes;
        this.condition = condition;
    }
}
