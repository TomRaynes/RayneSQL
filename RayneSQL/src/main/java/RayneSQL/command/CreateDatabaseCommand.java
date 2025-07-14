package RayneSQL.command;

import RayneSQL.database.Database;

import java.net.SocketAddress;

public class CreateDatabaseCommand extends Command {

    private final String databaseName;

    public CreateDatabaseCommand(String databaseName) {
        this.databaseName = databaseName;
    }

    public String execute(RayneSQL.DBServer server, SocketAddress socketAddress) throws Exception {
        Database database = new Database(server.getStorageFolderPath(), databaseName);
        database.createDirectory();
        return "";
    }

    public String getClassAttributes() {
        return databaseName;
    }
}
