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

        TokenType type = tokens.get(index++).getType();

        return switch (type) {
            case USE -> parseUseQuery();
            case CREATE -> parseCreateQuery();
            case DROP -> parseDropQuery();
            case ALTER -> parseAlterQuery();
            case INSERT -> parseInsertQuery();
            case SELECT -> parseSelectQuery();
            case UPDATE -> parseUpdateQuery();
            case DELETE -> parseDeleteQuery();
            case JOIN -> parseJoinQuery();
            default -> throw new DBException.UnexpectedTokenException(type, TokenType.USE,
                    TokenType.CREATE, TokenType.DROP, TokenType.ALTER, TokenType.INSERT,
                    TokenType.SELECT, TokenType.UPDATE, TokenType.DELETE, TokenType.JOIN);
        };
    }

    private UseCommand parseUseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new UseCommand(databaseName.toLowerCase());
    }

    private Command parseCreateQuery() throws Exception {

        expectTokenType(TokenType.DATABASE, TokenType.TABLE);

        if (tokens.get(index++).getType() == TokenType.DATABASE) {
            return parseCreateDatabaseQuery();
        }
        else return parseCreateTableQuery();
    }

    private CreateDatabaseCommand parseCreateDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new CreateDatabaseCommand(databaseName.toLowerCase());
    }

    private CreateTableCommand parseCreateTableQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        ArrayList<String> attributes = null; // = new ArrayList<>();

        if (tokens.get(index).getType() != TokenType.SEMICOLON) {
            expectTokenType(TokenType.OPEN_BRACKET);
            index++;
            attributes = getValueList(false, TokenType.IDENTIFIER);
        }
        expectTerminalTokens();
        return new CreateTableCommand(tableName.toLowerCase(), attributes);
    }

    private Command parseDropQuery() throws Exception {

        expectTokenType(TokenType.DATABASE, TokenType.TABLE);

        if (tokens.get(index++).getType() == TokenType.DATABASE) {
            return parseDropDatabaseQuery();
        }
        else return parseDropTableQuery();
    }

    private DropDatabaseCommand parseDropDatabaseQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String databaseName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new DropDatabaseCommand(databaseName.toLowerCase());
    }

    private DropTableCommand parseDropTableQuery() throws Exception {

        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTerminalTokens();
        return new DropTableCommand(tableName.toLowerCase());
    }

    private AlterCommand parseAlterQuery() throws Exception {

        expectTokenType(TokenType.TABLE);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        expectTokenType(TokenType.DROP, TokenType.ADD);
        Token alterationType = tokens.get(index++);
        expectTokenType(TokenType.IDENTIFIER);
        String attribute = tokens.get(index++).toString();
        expectTerminalTokens();
        return new AlterCommand(tableName.toLowerCase(), alterationType, attribute);
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
        ArrayList<String> values = getValueList(false, TokenType.STRING_LITERAL, TokenType.TRUE,
                TokenType.FALSE, TokenType.FLOAT_LITERAL, TokenType.INTEGER_LITERAL, TokenType.NULL);
        expectTerminalTokens();
        return new InsertCommand(tableName.toLowerCase(), values);
    }

    private SelectCommand parseSelectQuery() throws Exception {

        expectTokenType(TokenType.WILDCARD, TokenType.IDENTIFIER);
        ArrayList<String> attributes = null;
        ConditionNode condition = null;

        if (tokens.get(index).getType() == TokenType.WILDCARD) index++;
        else {
            attributes = getValueList(true, TokenType.IDENTIFIER);
            index--;
        }

        expectTokenType(TokenType.FROM);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();

        if (tokens.get(index).getType() != TokenType.SEMICOLON) {
            condition = getCondition();
        }
        return new SelectCommand(tableName.toLowerCase(), attributes, condition);
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
        return new UpdateCommand(tableName.toLowerCase(), nameValuePairs, condition);
    }

    private DeleteCommand parseDeleteQuery() throws Exception {

        expectTokenType(TokenType.FROM);
        index++;
        expectTokenType(TokenType.IDENTIFIER);
        String tableName = tokens.get(index++).toString();
        ConditionNode condition = getCondition();
        return new DeleteCommand(tableName.toLowerCase(), condition);
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
        index++;
        String attribute2 = tokens.get(index++).toString();
        expectTerminalTokens();
        return new JoinCommand(tableName1.toLowerCase(), tableName2.toLowerCase(),
                               attribute1, attribute2);
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

        return ConditionParser.parseCondition(conditionTokens);
    }

    private ArrayList<String> getValueList(boolean select, TokenType... expect) throws Exception {

        ArrayList<String> values = new ArrayList<>();
        boolean insideList = true;

        while (insideList) {
            expectTokenType(expect);
            values.add(tokens.get(index++).toString());

            if (tokens.get(index++).getType() != TokenType.COMMA) {
                index--;
                if (!select) expectTokenType(TokenType.CLOSE_BRACKET);
                insideList = false;
            }
        }
        index++;
        return values;
    }

    private void expectTokenType(TokenType... types) throws Exception {

        for (TokenType type : types) {
            if (tokens.get(index).getType() == type) return;
        }
        System.out.println("index = " + index);
        System.out.println(tokens.get(index).toString());
        throw new DBException.UnexpectedTokenException(tokens.get(index).getType(), types);
    }

    private void expectTerminalTokens() throws Exception {
        expectTokenType(TokenType.SEMICOLON);
        index++;
        expectTokenType(TokenType.EOT);
    }
}
