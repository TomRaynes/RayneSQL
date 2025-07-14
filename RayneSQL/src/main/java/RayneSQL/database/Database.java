package RayneSQL.database;

import RayneSQL.command.JoinCommand;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Database {

    private final String storageFolderPath;
    private final String databaseName;
    private ArrayList<TableNamePair> tables;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public Database(String storageFolderPath, String databaseName) {

        this.storageFolderPath = storageFolderPath;
        this.databaseName = databaseName;
    }

    public void executeUnderWriteLock(CheckedRunnable.VoidRunnable task) throws Exception {
        CheckedRunnable.executeUnderLock(writeLock, task);
    }

    public void executeUnderReadLock(CheckedRunnable.VoidRunnable task) throws Exception {
        CheckedRunnable.executeUnderLock(readLock, task);
    }

    public <T> T executeUnderWriteLock(CheckedRunnable.TypeRunnable<T> task) throws Exception {
        return CheckedRunnable.executeUnderLock(writeLock, task);
    }

    public void deleteDatabase() throws Exception {

        for (TableNamePair tableNamePair : tables) {
            tableNamePair.getTable().deleteTable();
        }
        try {
            Files.delete(Paths.get(getDatabasePath()).toAbsolutePath());
        }
        catch (Exception e) {
            throw new RayneSQL.DBException.ErrorDeletingDatabaseException(databaseName);
        }
    }

    public void addTable(String tableName, ArrayList<String> attributes) throws Exception {

        if (getTable(tableName) != null) {
            throw new RayneSQL.DBException.TableAlreadyExistsException(tableName, databaseName);
        }
        Table table = new Table(getDatabasePath(), tableName);

        if (attributes == null) {
            attributes = new ArrayList<>();
        }
        attributes.add(0, "id");
        table.setAttributes(attributes);
        table.checkForDuplicateAttributes();
        TableNamePair tableNamePair = new TableNamePair(tableName, table);
        tables.add(tableNamePair);
        table.saveTable();
    }

    public void removeTable(String tableName) throws Exception {

        for (TableNamePair tableNamePair : tables) {

            if (Objects.equals(tableNamePair.getTableName(), tableName)) {
                tableNamePair.getTable().deleteTable();
                tables.remove(tableNamePair);
                return;
            }
        }
        throw new RayneSQL.DBException.TableDoesNotExistException(tableName, databaseName);
    }

    public Table getTable(String tableName) {

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
            throw new RayneSQL.DBException.DatabaseAlreadyExistsException(databaseName);
        }
        boolean databaseCreated;

        try {
            databaseCreated = database.mkdir();
        }
        catch (Exception e) {
            throw new RayneSQL.DBException.ErrorCreatingDatabaseException(databaseName);
        }
        if (!databaseCreated) {
            throw new RayneSQL.DBException.ErrorCreatingDatabaseException(databaseName);
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

        if (!database.exists()) {
            throw new RayneSQL.DBException.DatabaseDoesNotExistException(databaseName);
        }
        File[] databaseFiles = database.listFiles();

        if (databaseFiles == null) {
            throw new RayneSQL.DBException.ErrorLoadingDatabaseException(databaseName);
        }
        return databaseFiles;
    }

    public ArrayList<ArrayList<String>> hashJoinTables(JoinCommand command,
                                                       String databaseName) throws Exception {

        Table table1 = getTable(command.getTableName1());
        Table table2 = getTable(command.getTableName2());
        boolean tablesAreDistinct = true;

        if (table1 == null) {
            throw new RayneSQL.DBException.TableDoesNotExistException(command.getTableName1(), databaseName);
        }
        if (table2 == null) {
            throw new RayneSQL.DBException.TableDoesNotExistException(command.getTableName2(), databaseName);
        }
        table1.getReadLock().lock();
        table2.getReadLock().lock();

        try {
            table1.loadTableData();
            table2.loadTableData();

            if (Objects.equals(table1, table2)) {
                tablesAreDistinct = false;
                table2 = table1.getDeepCopy();
            }

            ArrayList<TableRow> rows1 = table1.getTableRows();
            ArrayList<TableRow> rows2 = table2.getTableRows();

            // Indexes of the rows that the tables are join on
            int keyIndex1 = table1.getAttributeIndex(command.getAttribute1());
            int keyIndex2 = table2.getAttributeIndex(command.getAttribute2());

            if (keyIndex1 < 0) {
                throw new RayneSQL.DBException.AttributeDoesNotExistException(command.getAttribute1());
            }
            if (keyIndex2 < 0) {
                throw new RayneSQL.DBException.AttributeDoesNotExistException(command.getAttribute2());
            }

            ArrayList<ArrayList<String>> joinedTable = getJoinedTableRows(rows1, keyIndex1,
                    rows2, keyIndex2);

            // Add attributes to first row
            ArrayList<String> attributes = table1.getNonKeyAttributes(keyIndex1);
            attributes.addAll(table2.getNonKeyAttributes(keyIndex2));
            joinedTable.add(0, attributes);

            int id = 0;
            // Add new id column
            for (ArrayList<String> row : joinedTable) {
                row.add(0, Integer.toString(id++));
            }
            joinedTable.get(0).set(0, "id");

            return joinedTable;
        }
        finally {
            if (tablesAreDistinct) {
                table2.getReadLock().unlock();
            }
            table1.getReadLock().unlock();
        }
    }

    private ArrayList<ArrayList<String>> getJoinedTableRows(ArrayList<TableRow> rows1,
                                int keyIndex1, ArrayList<TableRow> rows2, int keyIndex2) {

        HashMap<String, ArrayList<TableRow>> hashMap = new HashMap<>();
        ArrayList<ArrayList<String>> joinedRows = new ArrayList<>();

        for (TableRow row1 : rows1) {
            String key = row1.getEntryFromIndex(keyIndex1).getValue();
            row1.removeColumnEntry(keyIndex1); // remove key
            if (keyIndex1 != 0) row1.removeColumnEntry(0); // remove id

            if (hashMap.containsKey(key)) hashMap.get(key).add(row1);
            else {
                hashMap.put(key, new ArrayList<>());
                hashMap.get(key).add(row1);
            }
        }
        for (TableRow row2 : rows2) {
            String key = row2.getEntryFromIndex(keyIndex2).getValue();
            row2.removeColumnEntry(keyIndex2); // remove key
            if (keyIndex2 != 0) row2.removeColumnEntry(0); // remove id
            ArrayList<TableRow> matches = hashMap.get(key);

            if (matches != null) {
                for (TableRow row1 : matches) {
                    joinedRows.add(row1.mergeRow(row2).toStringArray());
                }
            }
        }
        return joinedRows;
    }

    private String getDatabasePath() {
        return storageFolderPath + File.separator + databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }
}
