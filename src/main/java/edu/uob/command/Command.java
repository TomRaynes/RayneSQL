package edu.uob.command;

import edu.uob.DBServer;
import edu.uob.database.Database;
import edu.uob.database.TableNamePair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class Command {

    public abstract String execute(DBServer server) throws Exception;
    public abstract String getClassAttributes();

    protected static String listToString(ArrayList<String> strings) {

        String str = "";
        for (String string : strings) str += ", " + string;
        return str;
    }
}

