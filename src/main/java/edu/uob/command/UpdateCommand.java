package edu.uob.command;

import edu.uob.NameValuePair;
import edu.uob.condition.ConditionNode;

import java.util.ArrayList;

public class UpdateCommand extends Command {

    String tableName;
    ArrayList<NameValuePair> nameValuePairs;
    ConditionNode condition;

    public UpdateCommand(String tableName, ArrayList<NameValuePair> nameValuePairs,
                         ConditionNode condition) {
        this.tableName = tableName;
        this.nameValuePairs = nameValuePairs;
        this.condition = condition;
    }

    public String getClassAttributes() {

        String nameValList = "";
        for (NameValuePair pair : nameValuePairs) nameValList += ", " + pair.toString();
        return tableName + nameValList + ", " + ConditionNode.getTreeAsString(condition);
    }
}
