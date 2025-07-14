package RayneSQL;

import RayneSQL.token.Token;
import RayneSQL.token.TokenType;
import RayneSQL.token.Tokeniser;
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

    private void processIllegalToken(String token) throws Exception {
        Tokeniser tokeniser = new Tokeniser(token);
        tokeniser.getAllTokens();
    }

    @Test
    public void testConcreteTokens() {
        assertTrue(setExpected("USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "ADD", "INSERT",
            "INTO", "VALUES", "TRUE", "FALSE", "NULL", "SELECT", "FROM", "WHERE", "UPDATE", "SET",
                "DELETE", "JOIN", "AND", "OR", "ON", "(", ")", "*", ",", ";", "\u0004"));
        assertTrue(getActual("USE CREATE DATABASE TABLE DROP ALTER ADD INSERT INTO VALUES " +
                "TRUE FALSE NULL SELECT FROM WHERE UPDATE SET DELETE JOIN AND OR ON ( ) * , ;"));
        assertEquals(expected.toString(), actual.toString());
        TokenType type = TokenType.USE;

        for (Token token : actual) {
            assertEquals(type, token.getType());
            if (type != null) type = type.getNextType();
        }

        // Lowercase
        assertTrue(setExpected("use", "create", "database", "table", "drop", "alter", "add", "insert",
                "into", "values", "true", "false", "null", "select", "from", "where", "update", "set", "delete",
                "join", "and", "or", "on", "(", ")", "*", ",", ";", "\u0004"));
        assertTrue(getActual("use create database table drop alter add insert into values " +
                "true false null select from where update set delete join and or on ( ) * , ;"));
        assertEquals(expected.toString(), actual.toString());
        type = TokenType.USE;

        for (Token token : actual) {
            assertEquals(type, token.getType());
            if (type != null) type = type.getNextType();
        }

        // With varying case and whitespace
        assertTrue(setExpected("UsE", "CreATe", "DAtAbasE", "TAblE", "drOP", "aLTEr", "Add", "insERT",
                "inTO", "ValueS", "TRuE", "fALSe", "NUll", "SeLeCt", "fRom", "wHeRE", "UpDaTe", "sET",
                "DeLete", "JOIn", "aND", "oR", "On", "(", ")", "*", ",", ";", "\u0004"));
        assertTrue(getActual("  UsE  CreATe DAtAbasE     TAblE drOP     aLTEr  Add insERT    inTO " +
                "ValueS  TRuE                 fALSe    NUll SeLeCt     fRom  wHeRE  UpDaTe     sET DeLete " +
                "JOIn  aND      oR  On   ()*    ,;        "));
        assertEquals(expected.toString(), actual.toString());
        type = TokenType.USE;

        for (Token token : actual) {
            assertEquals(type, token.getType());
            if (type != null) type = type.getNextType();
        }
    }

    @Test
    public void testComparatorAndAssignmentTokens() {

        assertTrue(getActual("= > >= == <= < != LIKE like"));

        for (int i=0; i<actual.size()-1; i++) {
            if (i == 0) assertEquals(TokenType.ASSIGNMENT_EQUALS, actual.get(i).getType());
            else assertEquals(TokenType.COMPARATOR, actual.get(i).getType());
        }
    }

    @Test
    public void testStringLiteralTokens() {

        assertTrue(getActual("''        'ABCDEFGHIJKLMNOPQRSTUVWXYZ'" +
                                    "'abcdefghijklmnopqrstuvwxyz' " +
                                    "'0123456789'      " +
                                    "   ' !#$%&()*+,-./:;<=>?@[\\]^_`{}~'    "));

        for (int i=0; i<actual.size()-1; i++) {
            assertEquals(TokenType.STRING_LITERAL, actual.get(i).getType());
        }
    }

    @Test
    public void testFloatLiteralTokens() {

        assertTrue(getActual("123.456 +83.715 -6981.0032"));

        for (int i=0; i<actual.size()-1; i++) {
            assertEquals(TokenType.FLOAT_LITERAL, actual.get(i).getType());
        }
    }

    @Test
    public void testIntegerLiteralTokens() {

        assertTrue(getActual("0 1 2 3 4 5 6 7 8 9 23 " +
                                    "5601 3940175480 +786 -152 +1 -2"));

        for (int i=0; i<actual.size()-1; i++) {
            assertEquals(TokenType.INTEGER_LITERAL, actual.get(i).getType());
        }
    }

    @Test
    public void testIdentifierTokens() {

        assertTrue(getActual("ABCDEFGHIJKLMNOPQRSTUVWXYZ " +
                                    "abcdefghijklmnopqrstuvwxyz " +
                                    "ABCdef0123456789"));

        for (int i=0; i<actual.size()-1; i++) {
            assertEquals(TokenType.IDENTIFIER, actual.get(i).getType());
        }
    }

    @Test
    public void testIllegalTokens() {

        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("!"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("#"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("$"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("%"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("&"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("+"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("-"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("."));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("/"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken(":"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("?"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("@"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("["));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("\\"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("]"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("^"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("_"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("`"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("{"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("}"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("`"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("|"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("\""));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("abc!"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("123."));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken(".456"));

        // Some queries with syntax errors
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("UPDATE products SET price == £2000 WHERE item == 'Macbook Pro';"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("SELECT * FROM emails WHERE email == john.lennon@gmail.com;"));
        assertThrows(DBException.InvalidTokenException.class,
                () -> processIllegalToken("INSERT INTO marks VALUES (75%, 90%) WHERE id == 352;"));
    }
}
