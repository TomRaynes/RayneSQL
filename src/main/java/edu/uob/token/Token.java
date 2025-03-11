package edu.uob.token;

import edu.uob.DBException;

import java.util.HashSet;
import java.util.Set;

public class Token {
    private final TokenType type;
    private final String token;

    public Token(String token) throws Exception {
        this.type = characteriseToken(token);
        this.token = token;
    }

    public TokenType getType() {
        return type;
    }

    public String toString() {
        if (type == TokenType.STRING_LITERAL) return token.replaceAll("'", "");
        return token;
    }

    private static TokenType characteriseToken(String token) throws Exception {

        token = token.toUpperCase();

        return switch (token) {
            case "USE" -> TokenType.USE;
            case "CREATE" -> TokenType.CREATE;
            case "DATABASE" -> TokenType.DATABASE;
            case "TABLE" -> TokenType.TABLE;
            case "DROP" -> TokenType.DROP;
            case "ALTER" -> TokenType.ALTER;
            case "ADD" -> TokenType.ADD;
            case "INSERT" -> TokenType.INSERT;
            case "INTO" -> TokenType.INTO;
            case "VALUES" -> TokenType.VALUES;
            case "TRUE" -> TokenType.TRUE;
            case "FALSE" -> TokenType.FALSE;
            case "SELECT" -> TokenType.SELECT;
            case "FROM" -> TokenType.FROM;
            case "WHERE" -> TokenType.WHERE;
            case "UPDATE" -> TokenType.UPDATE;
            case "SET" -> TokenType.SET;
            case "DELETE" -> TokenType.DELETE;
            case "JOIN" -> TokenType.JOIN;
            case "AND" -> TokenType.AND;
            case "OR" -> TokenType.OR;
            case "ON" -> TokenType.ON;
            case "(" -> TokenType.OPEN_BRACKET;
            case ")" -> TokenType.CLOSE_BRACKET;
            case "*" -> TokenType.WILDCARD;
            case "," -> TokenType.COMMA;
            case ";" -> TokenType.SEMICOLON;
            case "=" -> TokenType.ASSIGNMENT_EQUALS;
            case ">", ">=", "==", "<=", "<", "!=", "LIKE" -> TokenType.COMPARATOR;
            case "NULL" -> TokenType.NULL;
            case "\u0004" -> TokenType.EOT;
            default -> characteriseMisc(token);
        };
    }

    private static TokenType characteriseMisc(String token) throws Exception {

        if (isIntegerLiteral(token)) return TokenType.INTEGER_LITERAL;
        if (isFloatLiteral(token)) return TokenType.FLOAT_LITERAL;
        if (isStringLiteral(token)) return TokenType.STRING_LITERAL;
        if (isIdentifier(token)) return TokenType.IDENTIFIER;
        else throw new DBException.InvalidTokenException(token);
    }

    private static boolean isIntegerLiteral(String token) {

        for (int i=0; i<token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return true;
    }

    private static boolean isFloatLiteral(String token) {

        char ch = token.charAt(0);
        int decimalPoints = 0;

        if (!Character.isDigit(ch) && ch != '+' && ch != '-') return false;

        for (int i=1; i<token.length(); i++) {
            if (!Character.isDigit(token.charAt(i)) && token.charAt(i) != '.') return false;
            if (token.charAt(i) != '.') decimalPoints++;
        }
        return decimalPoints == 1;
    }

    private static boolean isStringLiteral(String token) {

        Set<String> legalChars = new HashSet<>();

        for (char ch=' '; ch<='~'; ch++) {
            if (ch != '\'' && ch != '"' && ch != '|') legalChars.add(Character.toString(ch));
        }
        if (token.charAt(0) != '\'' || token.charAt(token.length()-1) != '\'') return false;

        for (int i=1; i<token.length()-1; i++) {
            if (!legalChars.contains(Character.toString(token.charAt(i)))) return false;
        }
        return true;
    }

    private static boolean isIdentifier(String token) {

        for (int i=0; i<token.length(); i++) {

            char ch = token.charAt(i);
            if (!Character.isLetter(ch) && !Character.isDigit(ch)) return false;
        }
        return true;
    }
}
