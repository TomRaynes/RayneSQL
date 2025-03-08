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

        Database database = server.getActiveDatabase();
        if (database == null) throw new Exception();
        Table table = database.getTable(tableName);
        if (table == null) throw new Exception();

        if (Objects.equals(alterationType, "ADD")) {
            table.addAttribute(attribute);
        }
        else table.removeAttribute(attribute);

        return null;
    }

    public String getClassAttributes() {
        return tableName + ", " + alterationType + ", " + attribute;
    }
}
