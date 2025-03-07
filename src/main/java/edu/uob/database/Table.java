package edu.uob.database;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Table {
    String storageFolderPath;
    String tableName;
    List<String> colNames;
    List<TableRow> tableRows;

    public Table(String storageFolderPath, String tableName) {
        this.storageFolderPath = storageFolderPath;
        this.tableName = tableName;
        this.tableRows = new ArrayList<>();
    }

    public void loadTableData() throws IOException {
        File table = new File(storageFolderPath + File.separator + tableName);
        if (!table.exists()) return;

        FileReader reader = new FileReader(table);
        BufferedReader buffReader = new BufferedReader(reader);
        String line = buffReader.readLine();
        colNames = getTableRow(line);

        while ((line = buffReader.readLine()) != null) {
            List<String> rowData = getTableRow(line);
            tableRows.add(new TableRow(rowData));
        }
        buffReader.close();
    }

    private List<String> getTableRow(String line) {

        String[] columnNames = line.split("\t");
        return Arrays.asList(columnNames);
    }

    public void saveTable() throws IOException {
        File table = new File(storageFolderPath + File.separator + tableName + ".tab");
        if (!table.exists()) table.createNewFile();

        FileWriter writer = new FileWriter(table);
        BufferedWriter buffWriter = new BufferedWriter(writer);

        for (String str : colNames) {
            buffWriter.write(str + "\t");
        }
        buffWriter.newLine();

        for (TableRow row : tableRows) {
            buffWriter.write(row.toString());
            buffWriter.newLine();
        }
        buffWriter.close();
    }

    public void addRow(String[] rowData) {
        tableRows.add(new TableRow(Arrays.asList(rowData)));
    }
}
