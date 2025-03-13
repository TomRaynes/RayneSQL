package edu.uob.database;

import edu.uob.DBException;
import edu.uob.condition.LogicalNode;
import edu.uob.condition.Condition;
import edu.uob.condition.ConditionNode;
import edu.uob.token.TokenType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Table {
    private final String storageFolderPath;
    private final String tableName;
    private ArrayList<String> attributes;
    private ArrayList<TableRow> tableRows;
    private int nextID;

    public Table(String storageFolderPath, String tableName) {
        this.storageFolderPath = storageFolderPath;
        this.tableName = tableName;
    }

    public ArrayList<String> getAttributes(ArrayList<String> attributes) throws Exception {

        if (attributes == null) return this.attributes;
        ArrayList<String> selectedAttributes = new ArrayList<>();

        for (String attribute : attributes) {
            int index = getAttributeIndex(attribute);
            if (index < 0) throw new DBException.AttributeDoesNotExistException(attribute);
            selectedAttributes.add(this.attributes.get(index));
        }
        return selectedAttributes;
    }

    public ArrayList<String> getNonKeyAttributes(int keyIndex) {

        ArrayList<String> nonKeyAttributes = new ArrayList<>();

        for (int i=1; i<attributes.size(); i++) {

            if (i != keyIndex) nonKeyAttributes.add(tableName + "." + attributes.get(i));
        }
        return nonKeyAttributes;
    }

    public void setAttributes(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    public void checkForDuplicateAttributes() throws Exception {

        Map<String, String> tableAttributes = new HashMap<>();

        for (String attribute : attributes) {

            if (tableAttributes.containsKey(attribute.toLowerCase())) {

                if (Objects.equals(tableAttributes.get(attribute.toLowerCase()),
                                   attribute.toLowerCase())) {

                    throw new DBException.DuplicateAttributesException(attribute);
                }
            }
            tableAttributes.put(attribute, attribute);
        }
    }

    public void initialiseIDs() {
        nextID = 1;
    }

    public String getNextID() {
        return Integer.toString(nextID++);
    }

    public ArrayList<TableRow> getTableRows() {
        return tableRows;
    }

    public void addAttribute(String attribute) throws Exception {

        if (getAttributeIndex(attribute) >= 0) {
            throw new DBException.AttributeAlreadyExistsException(attribute);
        }
        if (attributes == null) attributes = new ArrayList<>();
        attributes.add(attribute);

        for (TableRow row : tableRows) {
            row.addColumnEntry("NULL");
        }
    }

    public void removeAttribute(String attribute) throws Exception {

        int attributeIndex = getAttributeIndex(attribute);
        if (attributeIndex < 0) throw new DBException.AttributeDoesNotExistException(attribute);
        if (attributeIndex == 0) throw new DBException.TryingToRemoveIdException();

        attributes.remove(attributeIndex);

        for (TableRow row : tableRows) {
            row.removeColumnEntry(attributeIndex);
        }
    }

    public int getAttributeIndex(String attribute) {

        if (attributes == null) return -1;

        for (int i=0; i<attributes.size(); i++) {

            if (Objects.equals(attributes.get(i).toLowerCase(),
                               attribute.toLowerCase())) return i;
        }
        return -1;
    }

    public ArrayList<Integer> getAttributeIndexes(ArrayList<String> attributeList)
                                                                    throws Exception {
        ArrayList<Integer> indexes = new ArrayList<>();

        for (String attribute : attributeList) {
            int index = getAttributeIndex(attribute);

            if (index < 0) throw new DBException.AttributeDoesNotExistException(attribute);
            else indexes.add(index);
        }
        return indexes;
    }

    public void deleteTable() throws Exception {

        try {
            Files.delete(Paths.get(getTablePath()).toAbsolutePath());
        }
        catch (Exception e) {
            throw new DBException.ErrorDeletingTableException(tableName);
        }
    }

    private String getTablePath() {
        return storageFolderPath + File.separator + tableName + ".tab";
    }

    public void loadTableData() throws Exception {

        try {
            File table = new File(storageFolderPath + File.separator + tableName + ".tab");
            if (!table.exists()) return;

            FileReader reader = new FileReader(table);
            BufferedReader buffReader = new BufferedReader(reader);
            String line = buffReader.readLine();
            nextID = Integer.parseInt(line);
            line = buffReader.readLine();

            if (line == null) {
                buffReader.close();
                return;
            }
            attributes = getTableRow(line);
            tableRows = new ArrayList<>();

            while ((line = buffReader.readLine()) != null) {
                List<String> rowData = getTableRow(line);
                tableRows.add(new TableRow(rowData));
            }
            buffReader.close();
        }
        catch (Exception e) {
            throw new DBException.ErrorLoadingTableException(tableName);
        }
    }

    private ArrayList<String> getTableRow(String line) {

        String[] row = line.split("\t");
        return new ArrayList<>(Arrays.asList(row));
    }

    public void saveTable() throws Exception {

        try {
            File table = new File(storageFolderPath + File.separator + tableName + ".tab");

            if (!table.exists()) table.createNewFile();

            FileWriter writer = new FileWriter(table);
            BufferedWriter buffWriter = new BufferedWriter(writer);
            buffWriter.write(Integer.toString(nextID));
            buffWriter.newLine();

            if (attributes == null) {
                buffWriter.close();
                return;
            }
            for (String str : attributes) {
                buffWriter.write(str + "\t");
            }
            if (tableRows == null) {
                buffWriter.close();
                return;
            }
            buffWriter.newLine();

            for (TableRow row : tableRows) {
                buffWriter.write(row.toString());
                buffWriter.newLine();
            }
            buffWriter.close();
        }
        catch (Exception e) {
            throw new DBException.ErrorSavingTableException(tableName);
        }
    }

    public ArrayList<TableRow> getRowsFromCondition(ConditionNode node) throws Exception {

        ArrayList<TableRow> rows = new ArrayList<>();

        // attribute existence is checked inside conditionSatisfied()
        // so must check here if table has no rows
        if (tableRows.isEmpty()) {
            ArrayList<Condition> conditions = ConditionNode.getAllSubConditions(node);

            for (Condition condition : conditions) {
                String attribute = condition.getAttribute();

                if (getAttributeIndex(attribute) < 0 ) {
                    throw new DBException.AttributeDoesNotExistException(attribute);
                }
            }
        }
        for (TableRow row : tableRows) {
            if (conditionSatisfied(row, node)) rows.add(row);
        }
        return rows;
    }

    private boolean conditionSatisfied(TableRow row, ConditionNode node) throws Exception {

        if (node instanceof Condition condition) {

            int attributeIndex = getAttributeIndex(condition.getAttribute());

            if (attributeIndex < 0) {
                throw new DBException.AttributeDoesNotExistException(condition.getAttribute());
            }
            ColumnEntry actualValue = row.getEntryFromIndex(attributeIndex);
            return condition.assessCondition(actualValue);
        }

        boolean leftCondition = conditionSatisfied(row, node.getLeftChild());
        boolean rightCondition = conditionSatisfied(row, node.getRightChild());
        TokenType booleanType = ((LogicalNode) node).getType();

        if (booleanType == TokenType.AND) {
            return leftCondition && rightCondition;
        }
        return leftCondition || rightCondition;
    }

    public void addRow(ArrayList<String> rowData) throws Exception {

        if (attributes == null) {
            throw new DBException.AddingRowBeforeAttributesException(tableName);
        }
        if (attributes.size()-1 != rowData.size()) {
            throw new DBException.RowAttributesMismatchException(tableName, rowData.size(),
                                                                 attributes.size()-1);
        }
        rowData.add(0, getNextID());
        tableRows.add(new TableRow(rowData));
    }

    public void removeRow(TableRow row) {
        tableRows.remove(row);
    }
}
