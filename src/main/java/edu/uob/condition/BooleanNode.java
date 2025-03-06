package edu.uob.condition;

import edu.uob.token.TokenType;

public class BooleanNode extends ConditionNode {
    TokenType type;


    public BooleanNode(TokenType type) {
        this.type = type;
    }

    public void setLeftChild(ConditionNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(ConditionNode rightChild) {
        this.rightChild = rightChild;
    }

    public void printNode() {
        System.out.println(type);
    }

    public String toString() {
        return type.toString();
    }
}
