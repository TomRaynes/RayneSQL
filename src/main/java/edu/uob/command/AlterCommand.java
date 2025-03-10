package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;
import edu.uob.database.Table;

import java.util.Objects;

public class AlterCommand extends Command {

    private final String tableName;
    private final String alterationType;
    private final String attribute;

    public AlterCommand(String tableName, String alterationType, String attribute) {
        this.tableName = tableName;
        this.alterationType = alterationType;
        this.attribute = attribute;
    }

    public String execute(DBServer server) throws Exception {

        Table table = getTable(server, tableName);

        if (Objects.equals(alterationType, "ADD")) {
            table.addAttribute(attribute);
        }
        else table.removeAttribute(attribute);

        return "";
    }

    public String getClassAttributes() {
        return tableName + ", " + alterationType + ", " + attribute;
    }
}
