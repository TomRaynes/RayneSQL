package edu.uob.condition;

import edu.uob.token.Token;

public class Condition extends ConditionNode {

    String attribute;
    Comparator comparator;
    Token value;

    public Condition(String attribute, Comparator comparator, Token value) {
        this.attribute = attribute;
        this.comparator = comparator;
        this.value = value;
    }

    public void printNode() {
        System.out.println(attribute + comparator.toString() + value.toString());
    }

    public String toString() {
        return attribute + comparator.toString() + value.toString();
    }
}
