package RayneSQL.database;

public class TableNamePair {

    String tableName;
    Table table;

    public TableNamePair(String tableName, Table table) {
        this.tableName = tableName;
        this.table = table;
    }

    public String getTableName() {
        return tableName;
    }

    public Table getTable() {
        return table;
    }
}
