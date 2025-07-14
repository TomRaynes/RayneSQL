package RayneSQL;

import RayneSQL.command.*;
import RayneSQL.command.SelectCommand;
import org.junit.jupiter.api.Test;

import RayneSQL.token.Token;
import RayneSQL.token.Tokeniser;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {

    public <T extends Command> void testQuery(String input, Class<T> returnType, String expectedOutput) throws Exception {
        Tokeniser tokeniser = new Tokeniser(input);
        ArrayList<Token> tokens = tokeniser.getAllTokens();
        Parser parser = new Parser(tokens);
        Command command = parser.parseQuery();
        assertInstanceOf(returnType, command);
        assertEquals(expectedOutput, command.getClassAttributes());
    }

    @Test
    public void testParseUseQuery() throws Exception {

        testQuery("USE employees;",
                UseCommand.class,
                "employees");
    }

    @Test
    public void testParseCreateQuery() throws Exception {

        testQuery("CREATE DATABASE employees;",
                CreateDatabaseCommand.class,
                "employees");

        testQuery("CREATE TABLE salaries;",
                CreateTableCommand.class,
                "salaries");

        testQuery("CREATE TABLE people (height, weight, age);",
                CreateTableCommand.class,
                "people, height, weight, age");
    }

    @Test
    public void testParseDropQuery() throws Exception {

        testQuery("DROP DATABASE data;",
                DropDatabaseCommand.class,
                "data");

        testQuery("DROP TABLE tab;",
                DropTableCommand.class,
        "tab");
    }

    @Test
    public void testParseAlterQuery() throws Exception {

        testQuery("ALTER TABLE people ADD height;",
                AlterCommand.class,
        "people, ADD, height");

        testQuery("ALTER TABLE people DROP height;",
                AlterCommand.class,
                "people, DROP, height");
    }

    @Test
    public void testParseInsertQuery() throws Exception {

        testQuery("INSERT INTO people VALUES ('john', 30, 'john@john.com');",
                InsertCommand.class,
                "people, john, 30, john@john.com");
    }

    @Test
    public void testParseSelectQuery() throws Exception {

        testQuery("SELECT * FROM marks WHERE (pass==FALSE AND mark > 35) OR name == 'tom';",
                SelectCommand.class,
                "marks, *, ((pass==FALSE) AND (mark>35)) OR (name==tom)");

        testQuery("SELECT name, email FROM marks WHERE (pass==FALSE AND mark > 35);",
                SelectCommand.class,
                "marks, name, email, (pass==FALSE) AND (mark>35)");
    }

    @Test
    public void testParseUpdateQuery() throws Exception {

        testQuery("UPDATE marks SET age=35 WHERE name == 'Simon';",
                UpdateCommand.class,
                "marks, age=35, name==Simon");

        testQuery("UPDATE marks SET age=35 WHERE name != 'Simon';",
                UpdateCommand.class,
                "marks, age=35, name!=Simon");
    }

    @Test
    public void testParseDeleteQuery() throws Exception {

        testQuery("DELETE FROM marks WHERE name == 'Simon';",
                DeleteCommand.class,
                "marks, name==Simon");

        testQuery("DELETE FROM marks WHERE name == 'Simon' OR (mark<20);",
                DeleteCommand.class,
                "marks, (name==Simon) OR (mark<20)");
    }

    @Test
    public void testParseJoinQuery() throws Exception {

        testQuery("JOIN table1 AND table2 ON attribute1 AND attribute2;",
                JoinCommand.class,
                "table1, table2, attribute1, attribute2");
    }
}
