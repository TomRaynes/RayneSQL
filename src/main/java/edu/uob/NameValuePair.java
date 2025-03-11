package edu.uob;

import edu.uob.token.Token;

public class NameValuePair {

    String attribute;
    Token value;
    int index;

    public NameValuePair(String attribute, Token value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String getAttribute() {
        return attribute;
    }

    public Token getValue() {
        return value;
    }

    public String toString() {
        return attribute + "=" + value.toString();
    }
}
