package edu.uob.condition;

import java.util.ArrayList;

public abstract class ConditionNode {

    protected ConditionNode leftChild = null;
    protected ConditionNode rightChild = null;

    public ConditionNode getLeftChild() {
        return leftChild;
    }
    public ConditionNode getRightChild() {
        return rightChild;
    }

    public static ArrayList<Condition> getAllSubConditions(ConditionNode node) {

        ArrayList<Condition> conditions = new ArrayList<>();

        if (node instanceof Condition condition) {
            conditions.add(condition);
            return conditions;
        }
        conditions.addAll(getAllSubConditions(node.getLeftChild()));
        conditions.addAll(getAllSubConditions(node.getRightChild()));
        return conditions;
    }

    public static String getTreeAsString(ConditionNode node) {

        if (node instanceof Condition) return node.toString();

        else return "(" + getTreeAsString(node.getLeftChild()) + ") " + node +
                " (" + getTreeAsString(node.getRightChild()) + ")";

    }
}
