package edu.uob.command;

import java.util.ArrayList;

public abstract class Command {
    public abstract String getClassAttributes();

    public static String listToString(ArrayList<String> strings) {

        String str = "";
        for (String string : strings) str += ", " + string;
        return str;
    }
}

