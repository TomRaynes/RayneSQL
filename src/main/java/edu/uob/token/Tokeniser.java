package edu.uob.token;

import java.util.ArrayList;

public class Tokeniser {

    private final String query;
    private final String[] specialTokens = {"(", ")", ",", ";", "*", ">=", "==", "<=", "!="};
    private final ArrayList<Token> tokens;

    public Tokeniser(String query) {
        this.query = query;
        this.tokens = new ArrayList<>();

    }

    public ArrayList<Token> getAllTokens() throws Exception {
        String[] fragments = query.split("'");

        for (int i=0; i<fragments.length; i++) {

            if (i%2 != 0) tokens.add(new Token("'" + fragments[i] + "'"));
            else {
                String[] nextBatchOfTokens = tokenise(fragments[i]);

                for (String token : nextBatchOfTokens) {

                    if (token.isEmpty()) continue;
                    tokens.add(new Token(token));
                }
            }
        }
        tokens.add(new Token("\u0004"));
        return tokens;
    }

    String[] tokenise(String input) {
        // Add ' ' padding around all occurrences of special tokens
        for (String specialToken : specialTokens) {
            input = input.replace(specialToken, " " + specialToken + " ");
        }
        // Occurrences of '=' which are not part of '==', '!=', '<=' or '>='
        input = input.replaceAll("(?<![=!<>])=(?!=)", " = ");
        // Occurrences of '>' which are not part of '>='
        input = input.replaceAll(">(?!=)", " > ");
        // Occurrences of '<' which are not part of '<='
        input = input.replaceAll("<(?!=)", " < ");

        // Remove any double spaces
        while (input.contains("  ")) input = input.replace("  ", " ");
        // Remove any whitespace from the beginning and end
        input = input.trim();
        // Finally split on the space char
        return input.split(" ");
    }
}
