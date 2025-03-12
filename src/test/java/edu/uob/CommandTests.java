package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTests {

    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    private String sendCommandToServer(String command) {
        // Try to send a command to the server - this call will timeout if it takes too long (in case the server enters an infinite loop)
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> server.handleCommand(command),
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    private String generateRandomName() {
        String randomName = "";
        for(int i=0; i<10 ;i++) randomName += (char)(97 + (Math.random() * 25.0));
        return randomName;
    }

    @Test
    public void testUseCommand() {
        String name = generateRandomName();
        String response = sendCommandToServer("USE " + name + ";");
        String expected = "[ERROR]\nDatabase '" + name + "' does not exist";
        assertEquals(expected, response, response);
        sendCommandToServer("CREATE DATABASE " + name + ";");
        response = sendCommandToServer("USE " + name + ";");
        assertEquals("[OK]", response);
    }

    @Test
    public void testCreateDatabaseCommand() {
        String name = generateRandomName();
        String response = sendCommandToServer("CREATE DATABASE " + name + ";");
        String expected = "[OK]";
        assertEquals(expected, response, response);
        response = sendCommandToServer("CREATE DATABASE " + name + ";");
        expected = "[ERROR]\nThe database '" + name + "' already exists";
        assertEquals(expected, response, response);
    }

    @Test
    public void testCreateTableCommand() {
        String databaseName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + databaseName + ";");
        sendCommandToServer("USE " + databaseName + ";");
        String tableName = generateRandomName();
        String response = sendCommandToServer("CREATE TABLE " + tableName + ";");
        assertEquals("[OK]", response);
        response = sendCommandToServer("CREATE TABLE " + tableName + ";");
        String expected = "[ERROR]\nThe table '" + tableName + "' already exists in database '"
                + databaseName + "'";
        assertEquals(expected, response);
        // Create table with attributes
        response = sendCommandToServer("CREATE TABLE newTable (a, b, c);");
        assertEquals("[OK]", response);
    }

    @Test
    public void testDropDatabaseCommand() {
        String name = generateRandomName();
        String response = sendCommandToServer("DROP DATABASE " + name + ";");
        String expected = "[ERROR]\nDatabase '" + name + "' does not exist";
        assertEquals(expected, response);
        sendCommandToServer("CREATE DATABASE " + name + ";");
        response = sendCommandToServer("DROP DATABASE " + name + ";");
        assertEquals("[OK]", response, response);
    }

    @Test
    public void testDropTableCommand() {
        String databaseName = generateRandomName();
        String tableName = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + databaseName + ";");
        sendCommandToServer("USE " + databaseName + ";");
        String response = sendCommandToServer("DROP TABLE " + tableName + ";");
        String expected = "[ERROR]\nTable '" + tableName + "' does not exist in database '"
                + databaseName + "'\nCreate a table with: 'CREATE [table name];'";
        assertEquals(expected, response);
        sendCommandToServer("CREATE TABLE " + tableName + ";");
        response = sendCommandToServer("DROP TABLE " + tableName + ";");
        assertEquals("[OK]", response);
    }

    @Test
    public void testAlterCommand() {
        String databaseName = generateRandomName();
        String tableName = generateRandomName();
        String attribute1 = generateRandomName(), attribute2 = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + databaseName + ";");
        sendCommandToServer("USE " + databaseName + ";");
        sendCommandToServer("CREATE TABLE " + tableName + ";");
        // attribute doesnt exist
        String response = sendCommandToServer("ALTER TABLE " + tableName + " DROP " + attribute1 + ";");
        assertEquals("[ERROR]\nThis table has no attribute '" + attribute1 + "'", response);
        response = sendCommandToServer("ALTER TABLE " + tableName + " ADD " + attribute1 + ";");
        assertEquals("[OK]", response);
        response = sendCommandToServer("ALTER TABLE " + tableName + " ADD " + attribute2 + ";");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + tableName + ";");
        String expected = "[OK]\nid   " + attribute1 + "   " + attribute2 + "   ";
        assertEquals(expected, response);
        // trying to remove id
        response = sendCommandToServer("ALTER TABLE " + tableName + " DROP id;");
        assertEquals("[ERROR]\nThe table attribute 'id' cannot be removed", response);
        // duplicate attributes
        tableName = generateRandomName();
        response = sendCommandToServer("CREATE TABLE " + tableName + " (name, name);");
        assertEquals("""
                            [ERROR]
                            Attempting to add duplicate attributes 'name' to table
                            Table attributes must be unique""", response);
    }

    @Test
    public void testInsertCommand() {
        String databaseName = generateRandomName();
        String tableName = generateRandomName();
        String attribute1 = generateRandomName(), attribute2 = generateRandomName();
        String value1 = generateRandomName(), value2 = generateRandomName(), value3 = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + databaseName + ";");
        sendCommandToServer("USE " + databaseName + ";");
        sendCommandToServer("CREATE TABLE " + tableName + " (" + attribute1 + ", " + attribute2 + ");");
        String response = sendCommandToServer("INSERT INTO " + tableName + " VALUES ('" + value1 + "');");
        String expected = "[ERROR]\n" +
                "Attempting to insert 1 elements into table '" + tableName + "' containing 2 attributes";
        assertEquals(expected, response);
        response = sendCommandToServer("INSERT INTO " + tableName + " VALUES ('" + value1 + "', '" +
                value2 + "', '" + value3 + "');");
        expected = "[ERROR]\n" +
                "Attempting to insert 3 elements into table '" + tableName + "' containing 2 attributes";
        assertEquals(expected, response);
        response = sendCommandToServer("INSERT INTO " + tableName + " VALUES ('" + value1 + "', '" + value2 + "');");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + tableName + ";");
        expected = "[OK]\n" +
                   "id   " + attribute1 + "   " + attribute2 + "   \n" +
                   "1    " + value1 + "   " + value2 + "   ";
        assertEquals(expected, response);

        // assert that id is not resued
        sendCommandToServer("DELETE FROM " + tableName + " WHERE id == 1;");
        sendCommandToServer("INSERT INTO " + tableName + " VALUES ('" + value1 + "', '" + value2 + "');");
        response = sendCommandToServer("SELECT * FROM " + tableName + ";");
        expected = "[OK]\n" +
                "id   " + attribute1 + "   " + attribute2 + "   \n" +
                "2    " + value1 + "   " + value2 + "   ";
        assertEquals(expected, response);
    }

    @Test
    public void testSelectCommand() {
        String database = generateRandomName();
        String table = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + database + ";");
        sendCommandToServer("USE " + database + ";");
        sendCommandToServer("CREATE TABLE " + table + " (Name, Instrument);");
        sendCommandToServer("INSERT INTO " + table + " VALUES ('John', 'Guitar');");
        sendCommandToServer("INSERT INTO " + table + " VALUES ('Paul', 'Bass');");
        sendCommandToServer("INSERT INTO " + table + " VALUES ('George', 'Guitar');");
        sendCommandToServer("INSERT INTO " + table + " VALUES ('Ringo', 'Drums');");

        String response = sendCommandToServer("SELECT * FROM " + table + ";");
        String expected = """
                [OK]
                id   Name     Instrument  \s
                1    John     Guitar      \s
                2    Paul     Bass        \s
                3    George   Guitar      \s
                4    Ringo    Drums       \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT Name, Instrument FROM " + table + ";");
        expected = """
                [OK]
                Name     Instrument  \s
                John     Guitar      \s
                Paul     Bass        \s
                George   Guitar      \s
                Ringo    Drums       \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT Instrument, Name FROM " + table + ";");
        expected = """
                [OK]
                Instrument   Name    \s
                Guitar       John    \s
                Bass         Paul    \s
                Guitar       George  \s
                Drums        Ringo   \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM " + table + " WHERE Instrument == 'Guitar';");
        expected = """
                [OK]
                id   Name     Instrument  \s
                1    John     Guitar      \s
                3    George   Guitar      \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT name FROM " + table +
                " WHERE Instrument == 'Bass' OR Instrument == 'Drums';");
        expected = """
                [OK]
                Name   \s
                Paul   \s
                Ringo  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT age FROM " + table + " WHERE Instrument == 'Guitar';");
        expected = "[ERROR]\nThis table has no attribute 'age'";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM " + table + " WHERE name == 'Dave';");
        expected = """
                [OK]
                id   Name   Instrument  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM " + table + " WHERE Name LIKE 'Geo';");
        expected = """
                [OK]
                id   Name     Instrument  \s
                3    George   Guitar      \s""";
        assertEquals(expected, response);

        // Test boolean
        sendCommandToServer("ALTER TABLE " + table + " ADD Alive;");
        sendCommandToServer("UPDATE " + table + " SET Alive = TRUE WHERE name == 'Paul';");
        sendCommandToServer("UPDATE " + table + " SET Alive = TRUE WHERE name == 'Ringo';");
        sendCommandToServer("UPDATE " + table + " SET Alive = FALSE WHERE name == 'John';");
        sendCommandToServer("UPDATE " + table + " SET Alive = FALSE WHERE name == 'George';");
        response = sendCommandToServer("SELECT name, instrument FROM " + table + " WHERE alive == false;");
        expected = """
                [OK]
                Name     Instrument  \s
                John     Guitar      \s
                George   Guitar      \s""";
        assertEquals(expected, response);

        // test big conditions
        sendCommandToServer("CREATE TABLE condition (number, colour, month, fruit);");
        sendCommandToServer("INSERT INTO condition VALUES (5, 'red', 'december', 'apple');");
        sendCommandToServer("INSERT INTO condition VALUES (10, 'blue', 'march', 'banana');");
        sendCommandToServer("INSERT INTO condition VALUES (23, 'green', 'september', 'strawberry');");
        sendCommandToServer("INSERT INTO condition VALUES (39, 'yellow', 'october', 'raspberry');");
        sendCommandToServer("INSERT INTO condition VALUES (50, 'purple', 'june', 'pear');");
        sendCommandToServer("INSERT INTO condition VALUES (70, 'orange', 'january', 'grape');");
        sendCommandToServer("INSERT INTO condition VALUES (70, 'pink', 'february', 'blueberry');");
        sendCommandToServer("INSERT INTO condition VALUES (87, 'black', 'april', 'mango');");
        sendCommandToServer("INSERT INTO condition VALUES (93, 'white', 'november', 'passion fruit');");
        sendCommandToServer("INSERT INTO condition VALUES (106, 'violet', 'may', 'dragon fruit');");
        sendCommandToServer("INSERT INTO condition VALUES (112, 'brown', 'august', 'pineapple');");
        sendCommandToServer("INSERT INTO condition VALUES (120, 'gray', 'july', 'plum');");
        response = sendCommandToServer("SELECT * FROM condition WHERE " +
                "(month LIKE 'ember' AND number <= 23) OR (fruit LIKE 'fruit' AND colour == 'violet');");
        expected = """
                [OK]
                id   number   colour   month       fruit         \s
                1    5        red      december    apple         \s
                3    23       green    september   strawberry    \s
                10   106      violet   may         dragon fruit  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM condition WHERE " +
                "((month LIKE 'ember' AND number <= 23) OR (fruit LIKE 'fruit' AND colour == 'violet')) " +
                "AND colour != 'green';");
        expected = """
                [OK]
                id   number   colour   month      fruit         \s
                1    5        red      december   apple         \s
                10   106      violet   may        dragon fruit  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM condition WHERE " +
                "(((month LIKE 'ember' AND number <= 23) OR (fruit LIKE 'fruit' AND colour == 'violet')) " +
                "AND colour != 'green') OR month LIKE 'uary';");
        expected = """
                [OK]
                id   number   colour   month      fruit         \s
                1    5        red      december   apple         \s
                6    70       orange   january    grape         \s
                7    70       pink     february   blueberry     \s
                10   106      violet   may        dragon fruit  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("SELECT * FROM condition WHERE " +
                "(((month LIKE 'ember' AND number <= 23) OR (fruit LIKE 'fruit' AND colour == 'violet')) " +
                "AND colour != 'green') OR (month LIKE 'uary' AND id > 6);");
        expected = """
                [OK]
                id   number   colour   month      fruit         \s
                1    5        red      december   apple         \s
                7    70       pink     february   blueberry     \s
                10   106      violet   may        dragon fruit  \s""";
        assertEquals(expected, response);

        // Ambiguous condition
        response = sendCommandToServer("SELECT * FROM condition WHERE " +
                "(((month LIKE 'ember' AND number <= 23) OR fruit LIKE 'fruit' AND colour == 'violet') " +
                "AND colour != 'green') OR (month LIKE 'uary' AND id > 6);");
        assertEquals("[ERROR]\nConflicting logical operators within brackets", response);

        // Unpaired bracket
        response = sendCommandToServer("SELECT * FROM condition WHERE ((id == 1) AND (colour == 'green');");
        assertEquals("[ERROR]\nSyntax error in condition from unpaired bracket", response);
    }

    @Test
    public void testUpdateCommand() {
        String database = generateRandomName();
        String table = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + database + ";");
        sendCommandToServer("USE " + database + ";");
        sendCommandToServer("CREATE TABLE " + table + " (a, b, c);");
        sendCommandToServer("INSERT INTO " + table + " VALUES (1, 2, 3);");
        String response;
        char ch = 'a';
        for (int i=4; i<7; i++, ch++) {
            response = sendCommandToServer("UPDATE " + table + " SET " + ch + " = " + i + " WHERE id == 1;");
            assertEquals("[OK]", response);
        }
        response = sendCommandToServer("SELECT * FROM " + table + ";");
        String expected = """
                        [OK]
                        id   a   b   c  \s
                        1    4   5   6  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("UPDATE " + table + " SET a=7, b= 8, c = 9 WHERE id == 1;");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + table + ";");
        expected = """
                        [OK]
                        id   a   b   c  \s
                        1    7   8   9  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("UPDATE " + table + " SET d = 5 WHERE id == 1;");
        assertEquals("[ERROR]\nThis table has no attribute 'd'", response);

        response = sendCommandToServer("UPDATE " + table + " SET a = 5 WHERE e == 1;");
        assertEquals("[ERROR]\nThis table has no attribute 'e'", response);

        // try to update id
        response = sendCommandToServer("UPDATE " + table + " SET id = 10 WHERE id == 1;");
        assertEquals("[ERROR]\nThe entry for attribute 'id' cannot be changed", response);
    }

    @Test
    public void testDeleteCommand() {
        String database = generateRandomName();
        String table = generateRandomName();
        sendCommandToServer("CREATE DATABASE " + database + ";");
        sendCommandToServer("USE " + database + ";");
        sendCommandToServer("CREATE TABLE " + table + " (a, b, c);");
        String response = sendCommandToServer("INSERT INTO " + table + " VALUES (1, 2, 3);");
        assertEquals("[OK]", response);
        response = sendCommandToServer("INSERT INTO " + table + " VALUES (4, 5, 6);");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + table + ";");
        String expected = """
                        [OK]
                        id   a   b   c  \s
                        1    1   2   3  \s
                        2    4   5   6  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("DELETE FROM " + table + " WHERE c == 3;");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + table + ";");
        expected = """
                    [OK]
                    id   a   b   c  \s
                    2    4   5   6  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("DELETE FROM " + table + " WHERE id == 2;");
        assertEquals("[OK]", response);
        response = sendCommandToServer("SELECT * FROM " + table + ";");
        expected = """
                    [OK]
                    id   a   b   c  \s""";
        assertEquals(expected, response);

        response = sendCommandToServer("DELETE FROM " + table + " WHERE d == 10;");
        assertEquals("[ERROR]\nThis table has no attribute 'd'", response);
    }

    @Test
    public void testJoinCommand() {
        sendCommandToServer("DROP DATABASE test;");
        sendCommandToServer("CREATE DATABASE test;");
        sendCommandToServer("USE test;");
        sendCommandToServer("CREATE TABLE people (name, age);");
        sendCommandToServer("CREATE TABLE dob (age, year);");
        sendCommandToServer("INSERT INTO people VALUES ('Paul', 83);");
        sendCommandToServer("INSERT INTO people VALUES ('Ringo', 85);");

        for (int year=1900; year<=2025; year++) {
            sendCommandToServer("INSERT INTO dob VALUES (" + (2025-year) + ", " + year + ");");
        }

        String response = sendCommandToServer("JOIN people AND dob ON age AND age;");
        String expected = """
                [OK]
                id   people.name   dob.year  \s
                1    Ringo         1940      \s
                2    Paul          1942      \s""";
        assertEquals(expected, response, "Actual:\n\n" + response);
    }
}
