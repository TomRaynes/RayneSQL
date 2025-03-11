package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;
import edu.uob.database.Table;
import edu.uob.token.Token;
import edu.uob.token.TokenType;

import java.util.Objects;

public class AlterCommand extends Command {

    private final String tableName;
    private final Token alterationType;
    private final String attribute;

    public AlterCommand(String tableName, Token alterationType, String attribute) {
        this.tableName = tableName;
        this.alterationType = alterationType;
        this.attribute = attribute;
    }

    public String execute(DBServer server) throws Exception {

        Table table = getTable(server, tableName);
        table.loadTableData();

        if (alterationType.getType() == TokenType.ADD) {
            table.addAttribute(attribute);
        }
        else table.removeAttribute(attribute);

        table.saveTable();
        return "";
    }

    public String getClassAttributes() {
        return tableName + ", " + alterationType + ", " + attribute;
    }
}
