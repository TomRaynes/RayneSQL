package edu.uob.command;

import edu.uob.condition.ConditionNode;

public class DeleteCommand extends Command {

    String tableName;
    ConditionNode condition;

    public DeleteCommand(String tableName, ConditionNode condition) {
        this.tableName = tableName;
        this.condition = condition;
    }
}
