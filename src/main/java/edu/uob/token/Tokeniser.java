package edu.uob.token;
import java.util.ArrayList;

public class Tokeniser {

    String query;
    String[] specialCharacters = {"(",")",",",";"};
    ArrayList<Token> tokens = new ArrayList<>();
    private static final char END_OF_TRANSMISSION = 4;

    public Tokeniser(String query) {
        this.query = query;

    }

    public ArrayList<Token> getAllTokens() {
        // Split the query on single quotes (to separate out query text from string literals)
        String[] fragments = query.split("'");
        for (int i=0; i<fragments.length; i++) {
            // Every other fragment is a string literal, so just add it straight to "result" token list
            if (i%2 != 0) {

                try {
                    tokens.add(new Token("'" + fragments[i] + "'"));
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
                // If it's not a string literal, it must be query text (which needs further processing)
            else {
                // Tokenise the fragment into an array of strings - this is the "clever" bit !
                String[] nextBatchOfTokens = tokenise(fragments[i]);
                // Then copy all the tokens into the "result" list (needs a bit of conversion)
                //tokens.addAll(Arrays.asList(nextBatchOfTokens));

                for (String token : nextBatchOfTokens) {
                    try {
                        tokens.add(new Token(token));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        // Finally, loop through the result array list, printing out each token a line at a time

        try {
            tokens.add(new Token("\u0004"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tokens;
    }

    String[] tokenise(String input) {
        // Add in some extra padding spaces either side of the "special characters"...
        // so we can be SURE that they are separated by AT LEAST one space (possibly more)
        for(int i=0; i<specialCharacters.length; i++) {
            input = input.replace(specialCharacters[i], " " + specialCharacters[i] + " ");
        }
        // Remove any double spaces (the previous padding activity might have introduced some of these)
        while (input.contains("  ")) input = input.replace("  ", " "); // Replace two spaces by one
        // Remove any whitespace from the beginning and the end that might have been introduced
        input = input.trim();
        // Finally split on the space char (since there will now ALWAYS be a SINGLE space between tokens)
        return input.split(" ");
    }
}
