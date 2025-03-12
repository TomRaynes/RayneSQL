package edu.uob.condition;

import edu.uob.database.ColumnEntry;
import edu.uob.token.Token;
import edu.uob.token.TokenType;

import java.util.Objects;

public class Condition extends ConditionNode {

    String attribute;
    Comparator comparator;
    Token value;

    public Condition(String attribute, Comparator comparator, Token value) {
        this.attribute = attribute;
        this.comparator = comparator;
        this.value = value;
    }

    public boolean assessCondition(ColumnEntry actualValue) {

        TokenType type = value.getType();

        if (type == TokenType.INTEGER_LITERAL || type == TokenType.FLOAT_LITERAL) {
            double conditionalValue = Double.parseDouble(value.toString());
            double actualValueDouble;

            try {
                actualValueDouble = Double.parseDouble(actualValue.toString());
            } catch (NumberFormatException e) {
                return false; // Condition and actual are incompatible
            }
            return assessNumberCondition(conditionalValue, actualValueDouble);
        }
        // Case-insensitive querying on all values except string literals
        String actual = processActual(actualValue), conditional = processConditional();

        // Remaining values may only be assessed on basis of equality or sub-string
        return switch (comparator.getType()) {
            case EQUAL -> Objects.equals(actual, conditional);
            case NOT_EQUAL -> !Objects.equals(actual, conditional);
            case LIKE -> containsSubString(conditional, actual);
            default -> false;
        };
    }

    private boolean assessNumberCondition(double conditionalValue, double actualValue) {

        return switch (comparator.getType()) {
            case GREATER_THAN -> actualValue > conditionalValue;
            case GREATER_THAN_OR_EQUAL -> actualValue >= conditionalValue;
            case EQUAL -> actualValue == conditionalValue;
            case LESS_THAN_OR_EQUAL -> actualValue <= conditionalValue;
            case LESS_THAN -> actualValue < conditionalValue;
            case NOT_EQUAL -> actualValue != conditionalValue;
            case LIKE -> containsSubString(Double.toString(conditionalValue),
                    Double.toString(actualValue));
        };
    }

    private String processActual(ColumnEntry actual) {

        if (value.getType() != TokenType.STRING_LITERAL) {
            return actual.toString().toLowerCase();
        }
        return actual.toString();
    }

    private String processConditional() {

        if (value.getType() != TokenType.STRING_LITERAL) {
            return value.toString().toLowerCase();
        }
        return value.toString();
    }

    private boolean containsSubString(String subString, String string) {
        return string.contains(subString);
    }

    public String getAttribute() {
        return attribute;
    }

    public String toString() {
        return attribute + comparator.toString() + value.toString();
    }
}
