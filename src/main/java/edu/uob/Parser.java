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
//            case UPDATE -> parseUpdateQuery();
//            case DELETE -> parseDeleteQuery();
//            case JOIN -> parseJoinQuery();
            default -> throw new Exception();
        };
    }

    private UseCommand parseUseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        index++;
        expectTokenType(TokenType.SEMICOLON);
        return new UseCommand(tokens.get(index).toString());
    }

    private Command parseCreateQuery() throws Exception {

        expectToken();

        return switch (tokens.get(index++).getType()) {
            case DATABASE -> parseCreateDatabaseQuery();
            case TABLE -> parseCreateTableQuery();
            default -> throw new Exception();
        };
    }

    private CreateDatabaseCommand parseCreateDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTokenType(TokenType.SEMICOLON);
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
        expectTokenType(TokenType.SEMICOLON);
        return new CreateTableCommand(tableName, attributes);
    }

    private Command parseDropQuery() throws Exception {

        expectToken();

        return switch (tokens.get(index++).getType()) {
            case DATABASE -> parseDropDatabaseQuery();
            case TABLE -> parseDropTableQuery();
            default -> throw new Exception();
        };
    }

    private DropDatabaseCommand parseDropDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTokenType(TokenType.SEMICOLON);
        return new DropDatabaseCommand(databaseName);
    }

    private DropTableCommand parseDropTableQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTokenType(TokenType.SEMICOLON);
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
        expectTokenType(TokenType.SEMICOLON);
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
        expectTokenType(TokenType.SEMICOLON);
        return new InsertCommand(tableName, values);
    }

    private SelectCommand parseSelectQuery() throws Exception {

        expectToken();
        ArrayList<String> attributes = null;
        ConditionNode condition = null;

        if (tokens.get(index).getType() == TokenType.WILDCARD) index++;
        else attributes = getAttributeList();

        expectTokenType(TokenType.FROM);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();

        if (tokens.get(index).getType() != TokenType.EOT) {
            ArrayList<Token> conditionTokens = new ArrayList<>();
            do conditionTokens.add(tokens.get(index++));
            while (tokens.get(index).getType() != TokenType.EOT);
            ConditionParser conditionParser = new ConditionParser();
            condition = conditionParser.parseCondition(conditionTokens);
        }
        return new SelectCommand(tableName, attributes, condition);
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

    private void expectToken() throws Exception {

        if (tokens.get(index).getType() == TokenType.EOT) {
            throw new Exception();
        }
    }

    private void expectTokenType(TokenType... types) throws Exception {

        for (TokenType type : types) {
            if (tokens.get(index).getType() == type) return;
        }
        throw new Exception();
    }

    private boolean followingTokenExists() {
        return tokens.get(index).getType() != TokenType.EOT;
    }
}
