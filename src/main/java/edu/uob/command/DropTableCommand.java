package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

public class DropTableCommand extends Command {

    private final String tableName;

    public DropTableCommand(String tableName) {
        this.tableName = tableName;
    }

    public String execute(DBServer server) throws Exception {
        Database database = server.getActiveDatabase();
        if (database == null) throw new Exception();
        database.removeTable(tableName);
        return null;
    }

    public String getClassAttributes() {
        return tableName;
    }
}
