package edu.uob;

import edu.uob.command.*;
import edu.uob.condition.ConditionNode;
import edu.uob.condition.ConditionParser;
import edu.uob.token.Token;
import edu.uob.token.TokenType;

import java.util.ArrayList;

public class Parser {

    private final ArrayList<Token> tokens;
    private int index = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public Command parseQuery() throws Exception {

        if (tokens.isEmpty()) return null;

        return switch (tokens.get(index++).getType()) {
            case USE -> parseUseQuery();
            case CREATE -> parseCreateQuery();
            case DROP -> parseDropQuery();
            case ALTER -> parseAlterQuery();
            case INSERT -> parseInsertQuery();
            case SELECT -> parseSelectQuery();
            case UPDATE -> parseUpdateQuery();
            case DELETE -> parseDeleteQuery();
            case JOIN -> parseJoinQuery();
            default -> throw new Exception();
        };
    }

    private UseCommand parseUseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        index++;
        expectTerminalTokens();
        return new UseCommand(tokens.get(index).toString());
    }

    private Command parseCreateQuery() throws Exception {

        return switch (tokens.get(index++).getType()) {
            case DATABASE -> parseCreateDatabaseQuery();
            case TABLE -> parseCreateTableQuery();
            default -> throw new Exception();
        };
    }

    private CreateDatabaseCommand parseCreateDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new CreateDatabaseCommand(databaseName);
    }

    private CreateTableCommand parseCreateTableQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        ArrayList<String> attributes = new ArrayList<>();

        if (tokens.get(index).getType() != TokenType.SEMICOLON) {
            expectTokenType(TokenType.OPEN_BRACKET);
            index++;
        }
        do {
            expectTokenType(TokenType.IDENTIFIER);
            attributes.add(tokens.get(index++).toString());
        }
        while (tokens.get(index).getType() != TokenType.CLOSE_BRACKET);

        index++;
        expectTerminalTokens();
        return new CreateTableCommand(tableName, attributes);
    }

    private Command parseDropQuery() throws Exception {

        return switch (tokens.get(index++).getType()) {
            case DATABASE -> parseDropDatabaseQuery();
            case TABLE -> parseDropTableQuery();
            default -> throw new Exception();
        };
    }

    private DropDatabaseCommand parseDropDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new DropDatabaseCommand(databaseName);
    }

    private DropTableCommand parseDropTableQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new DropTableCommand(tableName);
    }

    private AlterCommand parseAlterQuery() throws Exception {

        expectTokenType(TokenType.TABLE);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTokenType(TokenType.DROP, TokenType.ADD);
        String alterationType = tokens.get(index++).toString();
        expectTokenType(TokenType.IDENTIFIER);
        String attribute = tokens.get(index++).toString();
        expectTerminalTokens();
        return new AlterCommand(tableName, alterationType, attribute);
    }

    private InsertCommand parseInsertQuery() throws Exception {

        expectTokenType(TokenType.INTO);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTokenType(TokenType.VALUES);
        index++;
        expectTokenType(TokenType.OPEN_BRACKET);
        index++;
        ArrayList<String> values = new ArrayList<>();

        do {
            expectTokenType(TokenType.STRING_LITERAL, TokenType.TRUE, TokenType.FALSE,
                    TokenType.FLOAT_LITERAL, TokenType.INTEGER_LITERAL, TokenType.NULL);
            values.add(tokens.get(index++).toString());
        }
        while (tokens.get(index).getType() != TokenType.CLOSE_BRACKET);

        index++;
        expectTerminalTokens();
        return new InsertCommand(tableName, values);
    }

    private SelectCommand parseSelectQuery() throws Exception {

        expectTokenType(TokenType.WILDCARD, TokenType.IDENTIFIER);
        ArrayList<String> attributes = null;
        ConditionNode condition = null;

        if (tokens.get(index).getType() == TokenType.WILDCARD) index++;
        else attributes = getAttributeList();

        expectTokenType(TokenType.FROM);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();

        if (tokens.get(index).getType() != TokenType.SEMICOLON) {
            condition = getCondition();
        }
        return new SelectCommand(tableName, attributes, condition);
    }

    private UpdateCommand parseUpdateQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTokenType(TokenType.SET);
        index++;
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<>();

        do nameValuePairs.add(getNameValuePair());
        while (tokens.get(index++).getType() == TokenType.COMMA);

        index--;
        ConditionNode condition = getCondition();
        return new UpdateCommand(tableName, nameValuePairs, condition);
    }

    private DeleteCommand parseDeleteQuery() throws Exception {

        expectTokenType(TokenType.FROM);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        ConditionNode condition = getCondition();
        return new DeleteCommand(tableName, condition);
    }

    private JoinCommand parseJoinQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName1 = tokens.get(index++).toString();
        expectTokenType(TokenType.AND);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName2 = tokens.get(index++).toString();
        expectTokenType(TokenType.ON);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String attribute1 = tokens.get(index++).toString();
        expectTokenType(TokenType.AND);
        String attribute2 = tokens.get(index++).toString();
        expectTerminalTokens();
        return new JoinCommand(tableName1, tableName2, attribute1, attribute2);
    }

    private NameValuePair getNameValuePair() throws Exception {
        expectTokenType(TokenType.IDENTIFIER);
        String attribute = tokens.get(index++).toString();
        expectTokenType(TokenType.ASSIGNMENT_EQUALS);
        index++;
        expectTokenType(TokenType.STRING_LITERAL, TokenType.TRUE, TokenType.FALSE,
                TokenType.FLOAT_LITERAL, TokenType.INTEGER_LITERAL, TokenType.NULL);
        Token value = tokens.get(index++);
        return new NameValuePair(attribute, value);
    }

    private ConditionNode getCondition() throws Exception {

        expectTokenType(TokenType.WHERE);
        index++;
        ArrayList<Token> conditionTokens = new ArrayList<>();

        do conditionTokens.add(tokens.get(index));
        while (tokens.get(index++).getType() != TokenType.EOT);

        ConditionParser conditionParser = new ConditionParser();
        return conditionParser.parseCondition(conditionTokens);
    }

    private ArrayList<String> getAttributeList() throws Exception {

        ArrayList<String> attributeList = new ArrayList<>();

        do {
            expectTokenType(TokenType.IDENTIFIER);
            attributeList.add(tokens.get(index++).toString());
        }
        while (tokens.get(index).getType() != TokenType.FROM);

        return attributeList;
    }

    private void expectTokenType(TokenType... types) throws Exception {

        for (TokenType type : types) {
            if (tokens.get(index).getType() == type) return;
        }
        throw new Exception();
    }

    private void expectTerminalTokens() throws Exception {
        expectTokenType(TokenType.SEMICOLON);
        index++;
        expectTokenType(TokenType.EOT);
    }
}
