package edu.uob.condition;

public abstract class ConditionNode {

    ConditionNode leftChild = null;
    ConditionNode rightChild = null;


    public abstract void printNode();

    public void printTree() {

        if (this instanceof LogicalNode) {
            this.printNode();
            this.leftChild.printTree();
            this.rightChild.printTree();
        }
        else if (this instanceof Condition) this.printNode();
    }

    public ConditionNode getLeftChild() {
        return leftChild;
    }
    public ConditionNode getRightChild() {
        return rightChild;
    }

    public static String getTreeAsString(ConditionNode node) {

        if (node instanceof Condition) return node.toString();

        else return "(" + getTreeAsString(node.getLeftChild()) + ") " + node +
                " (" + getTreeAsString(node.getRightChild()) + ")";

    }
}
