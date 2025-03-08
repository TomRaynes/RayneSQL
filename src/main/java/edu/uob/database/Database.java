package edu.uob.database;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class Database {

    String storageFolderPath;
    String databaseName;
    ArrayList<TableNamePair> tables;

    public Database(String storageFolderPath, String databaseName) {

        this.storageFolderPath = storageFolderPath;
        this.databaseName = databaseName;
    }

    public void deleteDatabase() throws Exception {

        loadDatabase();

        for (TableNamePair tableNamePair : tables) {
            tableNamePair.getTable().deleteTable();
        }
        Files.delete(Paths.get(getDatabasePath()).toAbsolutePath());
    }

    public void addTable(String tableName, ArrayList<String> attributes) throws Exception {

        loadDatabase();

        if (getTable(tableName) != null) throw new Exception();
        Table table = new Table(getDatabasePath(), tableName);
        if (attributes != null) table.setAttributes(attributes);
        TableNamePair tableNamePair = new TableNamePair(tableName, table);
        tables.add(tableNamePair);
        table.saveTable();
    }

    public void removeTable(String tableName) throws Exception {

        loadDatabase();

        for (TableNamePair tableNamePair : tables) {

            if (Objects.equals(tableNamePair.getTableName(), tableName)) {
                tableNamePair.getTable().deleteTable();
                return;
            }
        }
        throw new Exception();
    }

    public Table getTable(String tableName) { // TODO: is this method necessary

        for (TableNamePair tableNamePair : tables) {

            if (Objects.equals(tableNamePair.getTableName(), tableName)) {
                return tableNamePair.getTable();
            }
        }
        return null;
    }

    public void createDirectory() throws Exception {

        File database = new File(getDatabasePath());
        if (database.exists()) throw new Exception();
        if (!database.mkdir()) throw new Exception();
    }

    public void loadDatabase() throws Exception {
        tables = getTablesFromDatabaseFolder();
    }

    private ArrayList<TableNamePair> getTablesFromDatabaseFolder() throws Exception {

        File[] databaseTables = getDirectoryContents();
        ArrayList<TableNamePair> tables = new ArrayList<>();

        for (File file : databaseTables) {
            String name = file.getName();

            if (name.endsWith(".tab")) {
                Table table = new Table(getDatabasePath(), name);
                String tableName = name.substring(0, name.length()-4); // remove extension
                tables.add(new TableNamePair(tableName, table));
            }
        }
        return tables;
    }

    public File[] getDirectoryContents() throws Exception {

        File database = new File(getDatabasePath());
        if (!database.exists()) throw new Exception();
        File[] databaseFiles = database.listFiles();
        if (databaseFiles == null) throw new Exception();
        return databaseFiles;
    }

    public String getStorageFolderPath() {
        return storageFolderPath;
    }

    private String getDatabasePath() {
        return storageFolderPath + File.separator + databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
