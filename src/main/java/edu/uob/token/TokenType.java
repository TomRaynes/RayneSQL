package edu.uob.token;

public enum TokenType {
    USE,
    CREATE,
    DATABASE,
    TABLE,
    DROP,
    ALTER,
    ADD,
    INSERT,
    INTO,
    VALUES,
    TRUE,
    FALSE,
    SELECT,
    FROM,
    WHERE,
    UPDATE,
    SET,
    DELETE,
    JOIN,
    AND,
    OR,
    ON,
    OPEN_BRACKET,
    CLOSE_BRACKET,
    WILDCARD,
    COMMA,
    SEMICOLON,
    EOT,
    COMPARATOR,
    ASSIGNMENT_EQUALS,
    STRING_LITERAL,
    FLOAT_LITERAL,
    INTEGER_LITERAL,
    NULL,
    IDENTIFIER;

    public TokenType getNextType() {
        if (ordinal() == values().length - 1) return null;
        return values()[ordinal() + 1];
    }
}
