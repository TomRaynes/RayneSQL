package edu.uob.condition;

import edu.uob.token.Token;
import edu.uob.token.TokenType;

import java.util.ArrayList;

public class ConditionParser {

    public ConditionNode parseCondition(ArrayList<Token> tokens) throws Exception {
        removeSemicolon(tokens);
        return buildConditionTree(tokens);
    }

    private void removeSemicolon(ArrayList<Token> tokens) throws Exception {

        int semicolonIndex = tokens.size()-2;

        if (tokens.get(semicolonIndex).getType() == TokenType.SEMICOLON) {
            tokens.remove(semicolonIndex);
        }
        else throw new Exception();
    }

    private ConditionNode buildConditionTree(ArrayList<Token> tokens) throws Exception {

        while (removeOuterBrackets(tokens));
        BooleanNode node = createBooleanNode(tokens);
        if (node != null) return node;
        return createCondition(tokens);
    }

    private Condition createCondition(ArrayList<Token> tokens) throws Exception {

        int index = 0;
        expectTokenType(tokens, index, TokenType.IDENTIFIER);
        String attribute = tokens.get(index++).toString();

        expectTokenType(tokens, index, TokenType.COMPARATOR);
        Comparator comparator = new Comparator(tokens.get(index++));

        expectTokenType(tokens, index, TokenType.STRING_LITERAL, TokenType.TRUE, TokenType.FALSE,
                TokenType.FLOAT_LITERAL, TokenType.INTEGER_LITERAL, TokenType.NULL);
        Token value = tokens.get(index++);

        expectTokenType(tokens, index, TokenType.EOT);
        return new Condition(attribute, comparator, value);
    }

    private BooleanNode createBooleanNode(ArrayList<Token> tokens) throws Exception {

        int nestingLevel = 0;

        for (int index=0; index<tokens.size(); index++) {
            TokenType type = tokens.get(index).getType();

            if (type == TokenType.OPEN_BRACKET) nestingLevel++;
            else if (type == TokenType.CLOSE_BRACKET) nestingLevel--;
            else if (nestingLevel == 0 && (type == TokenType.AND || type == TokenType.OR)) {

                if (tokens.get(index+1).getType() == TokenType.EOT) throw new Exception();

                ArrayList<Token> leftChild = new ArrayList<>(tokens.subList(0, index));
                ArrayList<Token> rightChild = new ArrayList<>(tokens.subList(index+1, tokens.size()));
                leftChild.add(new Token("\u0004"));

                BooleanNode node = new BooleanNode(type);
                node.setLeftChild(buildConditionTree(leftChild));
                node.setRightChild(buildConditionTree(rightChild));
                return node;
            }
        }
        return null;
    }

    private boolean removeOuterBrackets(ArrayList<Token> tokens) throws Exception {

        int index = 0;
        if (tokens.get(index++).getType() != TokenType.OPEN_BRACKET) return false;

        boolean insideBracket = false;
        int nestingLevel = 0;

        for (; index<tokens.size()-2; index++) {

            if (tokens.get(index).getType() == TokenType.OPEN_BRACKET) {
                nestingLevel++;
                insideBracket = true;
            }
            else if (tokens.get(index).getType() == TokenType.CLOSE_BRACKET) {
                if (--nestingLevel == 0) insideBracket = false;
            }
        }
        if (tokens.get(index).getType() == TokenType.CLOSE_BRACKET) {

            if (!insideBracket) {
                tokens.remove(0);
                tokens.remove(--index);
                return true;
            }
            else if (nestingLevel != 0) throw new Exception();
        }
        return false;
    }

    private void expectTokenType(ArrayList<Token> tokens, int index,
                                 TokenType... types) throws Exception {

        for (TokenType type : types) {
            if (tokens.get(index).getType() == type) return;
        }
        throw new Exception();
    }
}
