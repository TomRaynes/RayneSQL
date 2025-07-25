package RayneSQL.condition;

import RayneSQL.DBException;
import RayneSQL.token.Token;
import RayneSQL.token.TokenType;

import java.util.ArrayList;

public final class ConditionParser {

    public static ConditionNode parseCondition(ArrayList<Token> tokens) throws Exception {
        removeSemicolon(tokens);
        return buildConditionTree(tokens);
    }

    private static void removeSemicolon(ArrayList<Token> tokens) throws Exception {

        // last token == EOT, therefore second last should be semicolon
        int semicolonIndex = tokens.size()-2;
        TokenType type = tokens.get(semicolonIndex).getType();

        if (type == TokenType.SEMICOLON) tokens.remove(semicolonIndex);
        else throw new DBException.UnexpectedTokenException(type, TokenType.SEMICOLON);
    }

    private static ConditionNode buildConditionTree(ArrayList<Token> tokens) throws Exception {

        while (removeOuterBrackets(tokens));
        LogicalNode node = findLogicalTokens(tokens);
        if (node != null) return node;
        return createCondition(tokens);
    }

    private static Condition createCondition(ArrayList<Token> tokens) throws Exception {

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

    private static LogicalNode findLogicalTokens(ArrayList<Token> tokens) throws Exception {
// (population>300000 AND population<500000) OR (region=='South East' OR (name LIKE 'ingham' AND region!='West Midlands'))
        int nestingLevel = 0;
        Token logicalToken = null;

        for (Token token : tokens) {
            TokenType type = token.getType();

            if (type == TokenType.OPEN_BRACKET) nestingLevel++;
            else if (type == TokenType.CLOSE_BRACKET) nestingLevel--;
            else if (nestingLevel == 0 && (type == TokenType.AND || type == TokenType.OR)) {

                if (logicalToken == null) logicalToken = token;
                else if (logicalToken.getType() != token.getType()) {
                    throw new DBException.ConflictingLogicalOperatorException();
                }
            }
        }
        return logicalToken == null ? null : createLogicalNode(tokens, logicalToken);
    }

    private static LogicalNode createLogicalNode(ArrayList<Token> tokens, Token operator) throws Exception {

        int index = tokens.indexOf(operator);
        if (tokens.get(index+1).getType() == TokenType.EOT) {
            throw new DBException.MissingSubConditionException(operator.getType());
        }

        ArrayList<Token> leftChild = new ArrayList<>(tokens.subList(0, index));
        ArrayList<Token> rightChild = new ArrayList<>(tokens.subList(index+1, tokens.size()));
        leftChild.add(new Token("\u0004"));

        LogicalNode node = new LogicalNode(operator.getType());
        node.setLeftChild(buildConditionTree(leftChild));
        node.setRightChild(buildConditionTree(rightChild));
        return node;
    }

    private static boolean removeOuterBrackets(ArrayList<Token> tokens) throws Exception {
        if (tokens.size() < 3) { // i.e. '(', ')', 'EOT'
            return false;
        }

        int lastTokenIndex = tokens.size() - 1; // EOT
        TokenType firstType = tokens.get(0).getType();
        TokenType lastType = tokens.get(lastTokenIndex - 1).getType();

        if (firstType != TokenType.OPEN_BRACKET || lastType != TokenType.CLOSE_BRACKET) {
            return false;
        }
        int nestingLevel = 0;

        // Check bracket balance
        for (int i = 1; i < lastTokenIndex - 1; i++) {
            TokenType type = tokens.get(i).getType();

            if (type == TokenType.OPEN_BRACKET) {
                nestingLevel++;
            }
            else if (type == TokenType.CLOSE_BRACKET) {
                if (nestingLevel == 0) {
                    return false;
                }
                nestingLevel--;
            }
        }
        if (nestingLevel != 0) {
            throw new DBException.UnpairedBracketInConditionException();
        }
        tokens.remove(lastTokenIndex - 1); // remove last close bracket
        tokens.remove(0); // remove first open bracket
        return true;
    }

    private static void expectTokenType(ArrayList<Token> tokens, int index,
                                 TokenType... types) throws Exception {

        for (TokenType type : types) {
            if (tokens.get(index).getType() == type) return;
        }
        throw new DBException.UnexpectedTokenException(tokens.get(index).getType(), types);
    }
}
