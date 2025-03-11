package edu.uob;

import edu.uob.token.TokenType;

import java.io.Serial;
import java.util.ArrayList;

public abstract class DBException extends Exception {

    @Serial private static final long serialVersionUID = 1;

    public DBException(String message) {
        super("\n" + message);
    }

    public static class UnexpectedTokenException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public UnexpectedTokenException(TokenType actual, TokenType... expected) {
            super(createErrorMessage(actual, expected));
        }

        public static String createErrorMessage(TokenType actual, TokenType... expected) {

            String str = "";
            ArrayList<String> tokens = new ArrayList<>();
            for (TokenType token : expected) tokens.add(token.toString());

            if (tokens.size() == 1) {
                str = "'" + tokens.get(0) + "'";
            }
            else {
                for (int i=0; i<tokens.size(); i++) {

                    if (i < tokens.size()-2) str += "'" + tokens.get(i) + "', ";
                    else if (i == tokens.size()-2) str += "'" + tokens.get(i) + "' ";
                    else if (i == tokens.size()-1) str += "or '" + tokens.get(i) + "'";
                }
            }
            return "Expected token " + str + " but found '" + actual.toString() + "'";
        }
    }

    public static class InvalidTokenException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public InvalidTokenException(String token) {
            super("'" + token + "' is not a valid token");
        }
    }

    public static class UnpairedBracketInConditionException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public UnpairedBracketInConditionException() {
            super("Syntax error in condition from unpaired bracket");

        }
    }

    public static class ConflictingLogicalOperatorException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ConflictingLogicalOperatorException() {
            super("Conflicting logical operators within brackets");

        }
    }

    public static class MissingSubConditionException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public MissingSubConditionException(TokenType type) {
            super("Expected condition following '" + type.toString() + "' operator");

        }
    }

    public static class NoActiveDatabaseException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public NoActiveDatabaseException() {
            super("No database is currently loaded by the server\n" +
                    "An existing database can be loaded with: 'USE [database name];'");

        }
    }

    public static class AttributeAlreadyExistsException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public AttributeAlreadyExistsException(String attribute) {
            super("This table already has attribute '" + attribute + "'");

        }
    }

    public static class AttributeDoesNotExistException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public AttributeDoesNotExistException(String attribute) {
            super("This table has no attribute '" + attribute + "'");

        }
    }

    public static class ErrorSavingTableException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorSavingTableException(String tableName) {
            super("An error occurred while saving table data of table '" + tableName + "'");

        }
    }

    public static class ErrorLoadingTableException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorLoadingTableException(String tableName) {
            super("An error occurred while loading table data of table '" + tableName + "'");

        }
    }

    public static class ErrorLoadingDatabaseException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorLoadingDatabaseException(String databaseName) {
            super("An error occurred while loading database '" + databaseName + "'");

        }
    }

    public static class TryingToRemoveIdException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public TryingToRemoveIdException() {
            super("The table attribute 'id' cannot be removed");

        }
    }

    public static class TryingToChangeIdException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public TryingToChangeIdException() {
            super("The entry for attribute 'id' cannot be changed");

        }
    }

    public static class DatabaseAlreadyExistsException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public DatabaseAlreadyExistsException(String databaseName) {
            super("The database '" + databaseName + "' already exists");

        }
    }

    public static class DatabaseDoesNotExistException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public DatabaseDoesNotExistException(String databaseName) {
            super("Database '" + databaseName + "' does not exist");

        }
    }

    public static class ErrorCreatingDatabaseException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorCreatingDatabaseException(String databaseName) {
            super("An error occurred while creating database '" + databaseName + "'");

        }
    }

    public static class TableAlreadyExistsException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public TableAlreadyExistsException(String tableName, String databaseName) {
            super("The table '" + tableName + "' already exists in database '" + databaseName + "'");

        }
    }

    public static class TableDoesNotExistException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public TableDoesNotExistException(String tableName, String databaseName) {
            super("Table '" + tableName + "' does not exist in database '" + databaseName + "'\n" +
                    "Create a table with: 'CREATE [table name];'");
        }
    }

    public static class ErrorDeletingTableException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorDeletingTableException(String tableName) {
            super("An error occurred while trying to remove table '" + tableName + "'");

        }
    }

    public static class ErrorDeletingDatabaseException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public ErrorDeletingDatabaseException(String databaseName) {
            super("An error occurred while trying to remove database '" + databaseName + "'");

        }
    }

    public static class AddingRowBeforeAttributesException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public AddingRowBeforeAttributesException(String tableName) {
            super("Attributes must be added to table '" + tableName + "' before inserting data\n" +
                    "An attribute can be added with: 'ALTER TABLE " + tableName + " ADD [attribute];");

        }
    }

    public static class RowAttributesMismatchException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public RowAttributesMismatchException(String tableName, int insertCount, int attributeCount) {
            super("Attempting to insert " + insertCount + " elements into table '" +
                    tableName + "' containing " + attributeCount + " attributes");

        }
    }

    public static class DuplicateAttributesException extends DBException {

        @Serial private static final long serialVersionUID = 1;

        public DuplicateAttributesException(String attribute) {
            super("Attempting to add duplicate attributes '" + attribute + "' to table\n" +
                    "Table attributes must be unique");

        }
    }
}
