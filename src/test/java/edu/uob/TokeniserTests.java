package edu.uob;

import edu.uob.token.Token;
import edu.uob.token.TokenType;
import edu.uob.token.Tokeniser;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

public class TokeniserTests {

    ArrayList<Token> expected;
    ArrayList<Token> actual;

    private boolean setExpected(String... tokens) {
        ArrayList<Token> expected = new ArrayList<>();

        try {
            for (String token : tokens) {
                expected.add(new Token(token));
            }
        }
        catch (Exception e) {
            return false;
        }
        this.expected = expected;
        return true;
    }

    private boolean getActual(String query) {
        ArrayList<Token> actual;

        try {
            Tokeniser tokeniser = new Tokeniser(query);
            actual = tokeniser.getAllTokens();
        }
        catch (Exception e) {
            return false;
        }
        this.actual = actual;
        return true;
    }

    @Test
    public void testConcreteTokens() {
        assertTrue(setExpected("USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "ADD", "INSERT",
            "INTO", "VALUES", "TRUE", "FALSE", "SELECT", "FROM", "WHERE", "UPDATE", "SET", "DELETE",
            "JOIN", "AND", "OR", "ON", "(", ")", "*", ",", ";", "\u0004"));
        assertTrue(getActual("USE CREATE DATABASE TABLE DROP ALTER ADD INSERT INTO VALUES " +
                "TRUE FALSE SELECT FROM WHERE UPDATE SET DELETE JOIN AND OR ON ( ) * , ;"));
        assertEquals(expected.toString(), actual.toString());
        TokenType type = TokenType.USE;

        for (Token token : actual) {
            assertEquals(type, token.getType());
            if (type != null) type = type.getNextType();
        }

        // Lowercase
        assertTrue(setExpected("use", "create", "database", "table", "drop", "alter", "add", "insert",
                "into", "values", "true", "false", "select", "from", "where", "update", "set", "delete",
                "join", "and", "or", "on", "(", ")", "*", ",", ";", "\u0004"));
        assertTrue(getActual("use create database table drop alter add insert into values " +
                "true false select from where update set delete join and or on ( ) * , ;"));
        assertEquals(expected.toString(), actual.toString());
        type = TokenType.USE;

        for (Token token : actual) {
            assertEquals(type, token.getType());
            if (type != null) type = type.getNextType();
        }
    }
}
