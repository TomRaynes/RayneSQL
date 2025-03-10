package edu.uob.database;

import edu.uob.DBException;
import edu.uob.command.JoinCommand;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
        try {
            Files.delete(Paths.get(getDatabasePath()).toAbsolutePath());
        }
        catch (Exception e) {
            throw new DBException.ErrorDeletingDatabaseException(databaseName);
        }
    }

    public void addTable(String tableName, ArrayList<String> attributes) throws Exception {

        loadDatabase();

        if (getTable(tableName) != null) {
            throw new DBException.TableAlreadyExistsException(tableName, databaseName);
        }
        Table table = new Table(getDatabasePath(), tableName);
        table.initialiseIDs();

        if (attributes == null) attributes = new ArrayList<>();
        attributes.add(0, "id");
        table.setAttributes(attributes);
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
        throw new DBException.TableDoesNotExistException(tableName, databaseName);
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
        if (database.exists()) {
            throw new DBException.DatabaseAlreadyExistsException(databaseName);
        }
        boolean databaseCreated;

        try {
            databaseCreated = database.mkdir();
        }
        catch (Exception e) {
            throw new DBException.ErrorCreatingDatabaseException(databaseName);
        }
        if (!databaseCreated) {
            throw new DBException.ErrorCreatingDatabaseException(databaseName);
        }
    }

    public void loadDatabase() throws Exception {
        tables = getTablesFromDatabaseFolder();
    }

    public boolean databaseExists() {
        File database = new File(getDatabasePath());
        return database.exists();
    }

    private ArrayList<TableNamePair> getTablesFromDatabaseFolder() throws Exception {

        File[] databaseTables = getDirectoryContents();
        ArrayList<TableNamePair> tables = new ArrayList<>();

        for (File file : databaseTables) {
            String name = file.getName();

            if (name.endsWith(".tab")) {
                String tableName = name.substring(0, name.length()-4); // remove extension
                Table table = new Table(getDatabasePath(), tableName);
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

    public ArrayList<ArrayList<String>> hashJoinTables(JoinCommand command,
                                                       String databaseName) throws Exception {

        Table table1 = getTable(command.getTableName1());
        Table table2 = getTable(command.getTableName2());

        if (table1 == null) {
            throw new DBException.TableDoesNotExistException(command.getTableName1(), databaseName);
        }
        if (table2 == null) {
            throw new DBException.TableDoesNotExistException(command.getTableName2(), databaseName);
        }
        table1.loadTableData();
        table2.loadTableData();

        ArrayList<TableRow> rows1 = table1.getTableRows();
        ArrayList<TableRow> rows2 = table2.getTableRows();

        int keyIndex1 = table1.getAttributeIndex(command.getAttribute1());
        int keyIndex2 = table2.getAttributeIndex(command.getAttribute2());

        ArrayList<ArrayList<String>> joinedTable = getJoinedTableRows(rows1, keyIndex1,
                                                                      rows2, keyIndex2);

        ArrayList<String> attributes = table1.getNonKeyAttributes(keyIndex1);
        attributes.addAll(table2.getNonKeyAttributes(keyIndex2));
        joinedTable.add(0, attributes);

        // Remove id columns of joined old tables
        int indexId1 = 0, indexId2 = rows1.size()-1;

        for (ArrayList<String> row : joinedTable) {
            row.remove(indexId2);
            row.remove(indexId1);
        }

        int id = 0;

        // Add new id column
        for (ArrayList<String> row : joinedTable) {
            row.add(0, Integer.toString(id++));
        }
        joinedTable.get(0).set(0, "id");

        return joinedTable;
    }

    private ArrayList<ArrayList<String>> getJoinedTableRows(ArrayList<TableRow> rows1,
                                int keyIndex1, ArrayList<TableRow> rows2, int keyIndex2) {

        HashMap<String, ArrayList<TableRow>> hashMap = new HashMap<>();
        ArrayList<ArrayList<String>> joinedRows = new ArrayList<>();

        for (TableRow row1 : rows1) {
            String key = row1.getEntryFromIndex(keyIndex1).getValue();
            row1.removeColumnEntry(keyIndex1);

            if (hashMap.containsKey(key)) hashMap.get(key).add(row1);
            else {
                hashMap.put(key, new ArrayList<>());
                hashMap.get(key).add(row1);
            }
        }
        for (TableRow row2 : rows2) { // TODO: Include unmatched entries? Currently discarding no matched data
            String key = row2.getEntryFromIndex(keyIndex2).getValue();
            row2.removeColumnEntry(keyIndex2);
            ArrayList<TableRow> matches = hashMap.get(key);

            if (matches != null) {
                for (TableRow row1 : matches) {
                    joinedRows.add(row1.mergeRow(row2).toStringArray());
                }
            }
        }
        // remove the key columns from the joined rows
        int startTable2 = rows1.get(0).getSize();

//        for (ArrayList<String> row : joinedRows) {
//            row.remove(startTable2 + keyIndex2);
//            row.remove(startTable2);
//            row.remove(keyIndex1);
//            row.remove(0);
//        }
        return joinedRows;
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
