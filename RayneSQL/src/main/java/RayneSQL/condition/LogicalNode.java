package RayneSQL.condition;

import RayneSQL.token.TokenType;

public class LogicalNode extends ConditionNode {
   private final TokenType type;


    public LogicalNode(TokenType type) {
        this.type = type;
    }

    public TokenType getType() {
        return type;
    }

    public void setLeftChild(ConditionNode leftChild) {
        this.leftChild = leftChild;
    }

    public void setRightChild(ConditionNode rightChild) {
        this.rightChild = rightChild;
    }

    public String toString() {
        return type.toString();
    }
}
