package edu.uob.condition;

import edu.uob.token.TokenType;

public abstract class ConditionNode {

    ConditionNode leftChild = null;
    ConditionNode rightChild = null;


    public abstract void printNode();

    public void printTree() {

        if (this instanceof BooleanNode) {
            this.printNode();
            this.leftChild.printTree();
            this.rightChild.printTree();
        }
        else if (this instanceof Condition) this.printNode();
    }
}
