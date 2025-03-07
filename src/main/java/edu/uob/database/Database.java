package edu.uob.database;

import java.io.File;
import java.util.ArrayList;

public class Database {

    String storageFolderPath;
    String databaseName;
    ArrayList<TableNamePair> tables;

    public Database(String storageFolderPath, String databaseName) {

        this.storageFolderPath = storageFolderPath;
        this.databaseName = databaseName;
    }

    public void loadTables() throws Exception {
        tables = getDatabaseTables();
    }

    private ArrayList<TableNamePair> getDatabaseTables() throws Exception {

        String databasePath = storageFolderPath + File.separator + databaseName;
        File database = new File(databasePath);
        if (!database.exists()) throw new Exception();
        File[] databaseTables = database.listFiles();
        if (databaseTables == null) throw new Exception();
        ArrayList<TableNamePair> tables = new ArrayList<>();

        for (File file : databaseTables) {
            String name = file.getName();

            if (name.endsWith(".tab")) {
                Table table = new Table(databasePath, name);
                String tableName = name.substring(0, name.length()-4); // remove extension
                tables.add(new TableNamePair(name, table));
            }
        }
        return tables;
    }
}
