package RayneSQL;

import RayneSQL.token.Token;

public class NameValuePair {

    String attribute;
    Token value;

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
