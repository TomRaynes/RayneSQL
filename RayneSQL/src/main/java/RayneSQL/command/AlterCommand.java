package RayneSQL.command;

import RayneSQL.DBServer;
import RayneSQL.database.Table;
import RayneSQL.token.Token;
import RayneSQL.token.TokenType;

import java.net.SocketAddress;

public class AlterCommand extends Command {

    private final String tableName;
    private final Token alterationType;
    private final String attribute;

    public AlterCommand(String tableName, Token alterationType, String attribute) {
        this.tableName = tableName;
        this.alterationType = alterationType;
        this.attribute = attribute;
    }

    public String execute(DBServer server, SocketAddress socketAddress) throws Exception {

        Table table = getTable(server, tableName, socketAddress);
        return table.executeUnderWriteLock(() -> {
            table.loadTableData();

            if (alterationType.getType() == TokenType.ADD) {
                table.addAttribute(attribute);
            }
            else table.removeAttribute(attribute);

            table.saveTable();
            return "";
        });
    }

    public String getClassAttributes() {
        return tableName + ", " + alterationType + ", " + attribute;
    }
}
