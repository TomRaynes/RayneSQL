package edu.uob.database;
import edu.uob.condition.BooleanNode;
import edu.uob.condition.Condition;
import edu.uob.condition.ConditionNode;
import edu.uob.token.TokenType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Table {
    String storageFolderPath;
    String tableName;
    List<String> attributes;
    List<TableRow> tableRows;
    int nextID;

    public Table(String storageFolderPath, String tableName) {
        this.storageFolderPath = storageFolderPath;
        this.tableName = tableName;
        this.tableRows = new ArrayList<>();
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
        this.attributes.add(0, "id");
    }

    public void initialiseIDs() {
        nextID = 1;
    }

    public String getNextID() {
        return Integer.toString(nextID++);
    }

    public void addAttribute(String attribute) throws Exception {
        loadTableData();
        if (getAttributeIndex(attribute) >= 0) throw new Exception();
        attributes.add(attribute);

        for (TableRow row : tableRows) {
            row.addColumnEntry("null");
        }
        saveTable();
    }

    public void removeAttribute(String attribute) throws Exception {

        loadTableData();
        int attributeIndex = getAttributeIndex(attribute);
        if (attributeIndex < 0) throw new Exception(); // does not exist
        if (attributeIndex == 0) throw new Exception(); // cant remove id

        attributes.remove(attributeIndex);

        for (TableRow row : tableRows) {
            row.removeColumnEntry(attributeIndex);
        }
        saveTable();
    }

    public int getAttributeIndex(String attribute) {

        for (int i=0; i<attributes.size(); i++) {
            if (Objects.equals(attributes.get(i), attribute)) return i;
        }
        return -1;
    }

    public ArrayList<Integer> getAttributeIndexes(ArrayList<String> attributeList)
                                                                    throws Exception {

        ArrayList<Integer> indexes = new ArrayList<>();

        for (String attribute : attributeList) {
            int index = getAttributeIndex(attribute);

            if (index < 0) throw new Exception();
            else indexes.add(index);
        }
        return indexes;
    }

    public void deleteTable() throws IOException {
        Files.delete(Paths.get(getTablePath()).toAbsolutePath());
    }

    private String getTablePath() {
        return storageFolderPath + File.separator + tableName;
    }

    public void loadTableData() throws IOException {
        File table = new File(storageFolderPath + File.separator + tableName);
        if (!table.exists()) return;

        FileReader reader = new FileReader(table);
        BufferedReader buffReader = new BufferedReader(reader);
        String line = buffReader.readLine();
        nextID = Integer.parseInt(line);
        line = buffReader.readLine();
        attributes = getTableRow(line);

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

    public void saveTable() throws Exception {

        File table = new File(storageFolderPath + File.separator + tableName + ".tab");

        if (!table.exists()) {
            if (!table.createNewFile()) throw new Exception();
        }

        FileWriter writer = new FileWriter(table);
        BufferedWriter buffWriter = new BufferedWriter(writer);
        buffWriter.write(Integer.toString(nextID));
        buffWriter.newLine();

        for (String str : attributes) {
            buffWriter.write(str + "\t");
        }
        buffWriter.newLine();

        for (TableRow row : tableRows) {
            buffWriter.write(row.toString());
            buffWriter.newLine();
        }
        buffWriter.close();
    }

    public ArrayList<TableRow> getRowsFromCondition(ConditionNode node) throws Exception {

        ArrayList<TableRow> rows = new ArrayList<>();

        for (TableRow row : tableRows) {
            if (conditionSatisfied(row, node)) rows.add(row);
        }
        return rows;
    }

    private boolean conditionSatisfied(TableRow row, ConditionNode node) throws Exception {

        if (node instanceof Condition condition) {

            int attributeIndex = getAttributeIndex(condition.getAttribute());
            if (attributeIndex < 0) throw new Exception(); // doesnt exist
            if (attributeIndex == 0) throw new Exception(); // id
            ColumnEntry actualValue = row.getEntryFromIndex(attributeIndex);
            return condition.assessCondition(actualValue);
        }

        boolean leftCondition = conditionSatisfied(row, node.getLeftChild());
        boolean rightCondition = conditionSatisfied(row, node.getRightChild());
        TokenType booleanType = ((BooleanNode) node).getType();

        if (booleanType == TokenType.AND) {
            return leftCondition && rightCondition;
        }
        return leftCondition || rightCondition;
    }

    public void addRow(ArrayList<String> rowData) throws Exception {

        loadTableData();
        rowData.add(0, getNextID());
        tableRows.add(new TableRow(rowData));
        saveTable();
    }
}
