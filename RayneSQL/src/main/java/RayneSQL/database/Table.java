package RayneSQL.database;

import RayneSQL.DBException;
import RayneSQL.condition.LogicalNode;
import RayneSQL.condition.Condition;
import RayneSQL.condition.ConditionNode;
import RayneSQL.token.TokenType;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Table {
    private final String storageFolderPath;
    private final String tableName;
    private ArrayList<String> attributes;
    private ArrayList<TableRow> tableRows;
    private int nextID;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    public Table(String storageFolderPath, String tableName) {
        this.storageFolderPath = storageFolderPath;
        this.tableName = tableName;
        this.nextID = 1;
    }

    public <T> T executeUnderReadLock(CheckedRunnable.TypeRunnable<T> task) throws Exception {
        return CheckedRunnable.executeUnderLock(readLock, task);
    }

    public void executeUnderWriteLock(CheckedRunnable.VoidRunnable task) throws Exception {
        CheckedRunnable.executeUnderLock(writeLock, task);
    }

    public <T> T executeUnderWriteLock(CheckedRunnable.TypeRunnable<T> task) throws Exception {
        return CheckedRunnable.executeUnderLock(writeLock, task);
    }

    public Lock getReadLock() {
        return readLock;
    }

    public ArrayList<String> getAttributes(ArrayList<String> attributes) throws Exception {

        if (attributes == null) return this.attributes;
        ArrayList<String> selectedAttributes = new ArrayList<>();

        for (String attribute : attributes) {
            int index = getAttributeIndex(attribute);
            if (index < 0) throw new RayneSQL.DBException.AttributeDoesNotExistException(attribute);
            // Preserve the original case of the attributes
            selectedAttributes.add(this.attributes.get(index));
        }
        return selectedAttributes;
    }

    public Table getDeepCopy() throws Exception {

        Table tableCopy = new Table(storageFolderPath, tableName);
        tableCopy.setAttributes(new ArrayList<>(attributes));
        tableCopy.tableRows = new ArrayList<>();

        for (TableRow row : tableRows) {
            ArrayList<String> rowCopy = row.getDeepCopy();
            rowCopy.remove(0);
            tableCopy.addRow(rowCopy);
        }
        return tableCopy;
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

                    throw new RayneSQL.DBException.DuplicateAttributesException(attribute);
                }
            }
            tableAttributes.put(attribute, attribute);
        }
    }

    public String getNextID() {
        return Integer.toString(nextID++);
    }

    public ArrayList<TableRow> getTableRows() {
        return tableRows;
    }

    public void addAttribute(String attribute) throws Exception {

        if (getAttributeIndex(attribute) >= 0) {
            String tableAttribute = attributes.get(getAttributeIndex(attribute));
            throw new DBException.AttributeAlreadyExistsException(tableAttribute);
        }
        if (attributes == null) attributes = new ArrayList<>();
        attributes.add(attribute);

        for (TableRow row : tableRows) {
            row.addColumnEntry("NULL");
        }
    }

    public void removeAttribute(String attribute) throws Exception {

        int attributeIndex = getAttributeIndex(attribute);

        if (attributeIndex < 0) {
            throw new DBException.AttributeDoesNotExistException(attribute);
        }
        if (attributeIndex == 0) {
            throw new DBException.TryingToRemoveIdException();
        }

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

            if (index < 0) {
                throw new RayneSQL.DBException.AttributeDoesNotExistException(attribute);
            }
            else indexes.add(index);
        }
        return indexes;
    }

    public void deleteTable() throws Exception {

        try {
            Files.delete(Paths.get(getTablePath()).toAbsolutePath());
        }
        catch (Exception e) {
            throw new RayneSQL.DBException.ErrorDeletingTableException(tableName);
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

            // read table rows
            while ((line = buffReader.readLine()) != null) {
                List<String> rowData = getTableRow(line);
                tableRows.add(new TableRow(rowData));
            }
            buffReader.close();
        }
        catch (Exception e) {
            throw new RayneSQL.DBException.ErrorLoadingTableException(tableName);
        }
    }

    private ArrayList<String> getTableRow(String line) {

        String[] row = line.split("\t");

        for (int i=0; i<row.length; i++) {
            if (Objects.equals(row[i], ".")) row[i] = "";
        }
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
        // so it must be checked here if table has no rows
        if (tableRows.isEmpty()) {
            ArrayList<Condition> conditions = ConditionNode.getAllSubConditions(node);

            for (Condition condition : conditions) {
                String attribute = condition.getAttribute();

                if (getAttributeIndex(attribute) < 0 ) {
                    throw new RayneSQL.DBException.AttributeDoesNotExistException(attribute);
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
                throw new RayneSQL.DBException.AttributeDoesNotExistException(condition.getAttribute());
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
            throw new RayneSQL.DBException.AddingRowBeforeAttributesException(tableName);
        }
        if (attributes.size()-1 != rowData.size()) {
            throw new RayneSQL.DBException.RowAttributesMismatchException(tableName, rowData.size(),
                                                                 attributes.size()-1);
        }
        rowData.add(0, getNextID());
        tableRows.add(new TableRow(rowData));
    }

    public void removeRow(TableRow row) {
        tableRows.remove(row);
    }
}
