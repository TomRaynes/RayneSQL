package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;

import java.util.ArrayList;

public abstract class Command {

    public abstract String execute(DBServer server);
    public abstract String getClassAttributes();

    protected static String listToString(ArrayList<String> strings) {

        String str = "";
        for (String string : strings) str += ", " + string;
        return str;
    }
}

