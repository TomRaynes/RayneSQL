package edu.uob;

import edu.uob.command.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class QueryTests {

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
    public void testUseQuery() {
        sendCommandToServer("");
    }

    @Test
    public void testJoinQuery() {
        sendCommandToServer("DROP DATABASE test;");
        sendCommandToServer("CREATE DATABASE test;");
        sendCommandToServer("USE test;");
        sendCommandToServer("CREATE TABLE people (name, age);");
        sendCommandToServer("CREATE TABLE dob (age, year);");
        sendCommandToServer("INSERT INTO people VALUES ('john', 25);");
        sendCommandToServer("INSERT INTO people VALUES ('paul', 26);");
        sendCommandToServer("INSERT INTO dob VALUES (25, 2000);");
        sendCommandToServer("INSERT INTO dob VALUES (26, 1999);");
        String response = sendCommandToServer("JOIN people AND dob ON age AND age;");
        String expected = "[OK]\n" +
                          "id   people.name   dob.year   \n" +
                          "1    'john'        2000       \n" +
                          "2    'paul'        1999       ";
        assertEquals(expected, response, "Actual:\n\n" + response);
    }
}
