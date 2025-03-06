package edu.uob;

import edu.uob.token.Token;

public class NameValuePair {

    String attribute;
    Token value;

    public NameValuePair(String attribute, Token value) {
        this.attribute = attribute;
        this.value = value;
    }

    public String toString() {
        return attribute + "=" + value.toString();
    }
}
