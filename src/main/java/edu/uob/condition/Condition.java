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
            //System.out.println(attribute + " " + comparator.getType() + " " + value.toString());
            return assessNumberCondition(conditionalValue, actualValueDouble);
        }
        // Remaining values may only be assessed on basis of equality or sub-string
        if (comparator.getType() == ComparatorType.EQUAL) {
            return Objects.equals(actualValue.toString(), value.toString());
        }
        if (comparator.getType() == ComparatorType.NOT_EQUAL) {
            return !Objects.equals(actualValue.toString(), value.toString());
        }
        if (comparator.getType() == ComparatorType.LIKE) {
            return containsSubString(value.toString(), actualValue.getValue());
        }
        return false;
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

    private boolean containsSubString(String subString, String string) {
        return string.contains(subString);
    }

    public String getAttribute() {
        return attribute;
    }

    public Comparator getComparator() {
        return comparator;
    }

    public Token getValue() {
        return value;
    }

    public void printNode() {
        System.out.println(attribute + comparator.toString() + value.toString());
    }

    public String toString() {
        return attribute + comparator.toString() + value.toString();
    }
}
