package edu.uob.condition;

import edu.uob.token.Token;

public class Comparator {
    private final ComparatorType type;
    private final String str;

    public Comparator(Token token) {
        this.type = characteriseComparator(token);
        this.str = token.toString();
    }

    public String toString() {
        return str;
    }

    public ComparatorType getType() {
        return type;
    }

    private static ComparatorType characteriseComparator(Token token) {

        return switch (token.toString()) {
            case ">" -> ComparatorType.GREATER_THAN;
            case ">=" -> ComparatorType.GREATER_THAN_OR_EQUAL;
            case "==" -> ComparatorType.EQUAL;
            case "<=" -> ComparatorType.LESS_THAN_OR_EQUAL;
            case "<" -> ComparatorType.LESS_THAN;
            case "!=" -> ComparatorType.NOT_EQUAL;
            default -> ComparatorType.LIKE;
        };
    }
}
